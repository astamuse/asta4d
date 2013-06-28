package com.astamuse.asta4d.web.builtin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.BinaryDataProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfo;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfoProvider;
import com.astamuse.asta4d.web.util.BinaryDataUtil;

public class StaticResourceHandler extends AbstractGenericPathHandler {

    public final static String VAR_CONTENT_TYPE = "content_type";

    public final static String VAR_CONTENT_CACHE = "content_cache";

    private final static Logger logger = LoggerFactory.getLogger(StaticResourceHandler.class);

    private final static long DefaultLastModified = getCurrentSystemTimeInGMT();
    // one hour
    private final static long DefaultCacheTime = 1000 * 60 * 60;

    private final static class StaticFileInfoHolder {
        String contentType;
        String path;
        long lastModified;
        SoftReference<byte[]> content;
        InputStream firstTimeInput;
    }

    private final static StaticFileInfoHolder NoContent = new StaticFileInfoHolder();

    private final static ConcurrentHashMap<String, StaticFileInfoHolder> StaticFileInfoMap = new ConcurrentHashMap<>();

    public StaticResourceHandler() {
        super();
    }

    public StaticResourceHandler(String basePath) {
        super(basePath);
    }

    private final static long getCurrentSystemTimeInGMT() {
        DateTime current = DateTime.now();
        return DateTimeZone.getDefault().convertLocalToUTC(current.getMillis(), false);
    }

    private HeaderInfoProvider createSimpleHeaderResponse(int status) {
        HeaderInfo header = new HeaderInfo(status);
        HeaderInfoProvider provider = new HeaderInfoProvider(header);
        provider.setContinuable(false);
        return provider;
    }

    @RequestHandler
    public Object handler(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext,
            UrlMappingRule currentRule) throws FileNotFoundException, IOException {
        String path = convertPath(request, currentRule);

        if (path == null) {
            return createSimpleHeaderResponse(404);
        }

        boolean cacheEnable = Configuration.getConfiguration().isCacheEnable();

        StaticFileInfoHolder info = cacheEnable ? StaticFileInfoMap.get(path) : null;

        if (info == null) {
            Context context = Context.getCurrentThreadContext();
            Boolean contentCache = context.getData(WebApplicationContext.SCOPE_PATHVAR, VAR_CONTENT_CACHE);
            if (contentCache == null) {
                contentCache = Boolean.TRUE;
            }
            info = createInfo(servletContext, path, contentCache);
            StaticFileInfoMap.put(path, info);
        }

        if (info == NoContent) {
            return createSimpleHeaderResponse(404);
        }

        if (cacheEnable) {
            long clientTime = retrieveClientCachedTime(request);

            if (clientTime >= info.lastModified) {
                return createSimpleHeaderResponse(304);
            }
        }

        // our header provider is not convenient for date header... hope we can
        // improve it in future
        response.setStatus(200);
        response.setHeader("Content-Type", info.contentType);
        response.setDateHeader("Expires", getCurrentSystemTimeInGMT() + DefaultCacheTime);
        response.setDateHeader("Last-Modified", info.lastModified);
        response.setHeader("Cache-control", "max-age=" + DefaultCacheTime);

        // here we do not synchronize threads because we do not matter

        if (info.content == null && info.firstTimeInput != null) {
            // no cache, and we have opened it at first time
            InputStream input = info.firstTimeInput;
            info.firstTimeInput = null;
            return new BinaryDataProvider(input);
        } else if (info.content == null && info.firstTimeInput == null) {
            // no cache
            return new BinaryDataProvider(servletContext, this.getClass().getClassLoader(), info.path);
        } else {
            // should cache
            byte[] data = null;
            data = info.content.get();
            if (data == null) {
                InputStream input = BinaryDataUtil.retrieveInputStreamByPath(servletContext, this.getClass().getClassLoader(), path);
                data = retrieveBytesFromInputStream(input);
                info.content = new SoftReference<byte[]>(data);
            }
            return new BinaryDataProvider(data);
        }
    }

    private long retrieveClientCachedTime(HttpServletRequest request) {
        try {
            long time = request.getDateHeader("If-Modified-Since");
            return DateTimeZone.getDefault().convertLocalToUTC(time, false);
        } catch (Exception e) {
            logger.debug("retrieve If-Modified-Since failed", e);
            return -1;
        }
    }

    private StaticFileInfoHolder createInfo(ServletContext servletContext, String path, boolean cache) throws FileNotFoundException,
            IOException {

        InputStream input = BinaryDataUtil.retrieveInputStreamByPath(servletContext, this.getClass().getClassLoader(), path);

        if (input == null) {
            return NoContent;
        }

        StaticFileInfoHolder info = new StaticFileInfoHolder();
        info.contentType = judgContentType(path);
        info.path = path;
        if (path.startsWith("file://")) {
            info.lastModified = new File(path).lastModified();
        } else {
            info.lastModified = DefaultLastModified;
        }

        // cut the milliseconds
        info.lastModified = info.lastModified / 1000 * 1000;

        if (cache) {
            try {
                info.content = new SoftReference<byte[]>(retrieveBytesFromInputStream(input));
            } finally {
                input.close();
            }
            info.firstTimeInput = null;
        } else {
            info.content = null;
            info.firstTimeInput = input;
        }

        return info;
    }

    private final static Map<String, String> MimeTypeMap = new HashMap<>();
    static {
        MimeTypeMap.put("js", "application/javascript");
        MimeTypeMap.put("css", "text/css");
    }

    protected String judgContentType(String path) {

        Context context = Context.getCurrentThreadContext();

        String forceContentType = context.getData(WebApplicationContext.SCOPE_PATHVAR, VAR_CONTENT_TYPE);
        if (forceContentType != null) {
            return forceContentType;
        }

        String fileName = FilenameUtils.getName(path);

        // guess the type by file name extension
        String type = URLConnection.guessContentTypeFromName(fileName);

        if (type == null) {
            type = MimeTypeMap.get(FilenameUtils.getExtension(fileName));
        }
        return type;
    }

    private byte[] retrieveBytesFromInputStream(InputStream input) throws IOException {
        int bufferSize = 4096;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize);
        byte[] b = new byte[bufferSize];
        int len;
        while ((len = input.read(b)) != -1) {
            bos.write(b, 0, len);
        }
        return bos.toByteArray();
    }
}
