package com.astamuse.asta4d.web.builtin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.BinaryDataProvider;
import com.astamuse.asta4d.web.util.BinaryDataUtil;

public class StaticFileHandler extends AbstractGenericPathHandler {

    public final static String VAR_CONTENT_TYPE = "content_type";

    public final static String VAR_CONTENT_CACHE = "content_cache";

    private final static Logger logger = LoggerFactory.getLogger(StaticFileHandler.class);

    @SuppressWarnings("deprecation")
    private final static long Year_1970 = new Date(1970 - 1900, 0, 1).getTime();

    @SuppressWarnings("deprecation")
    private final static long Year_3000 = new Date(3000 - 1900, 0, 1).getTime();

    // system start up
    private final static long defaultLastModified = System.currentTimeMillis();
    private final static long defaultExpires = Year_3000;

    private final static class StaticFileInfoHolder {
        String contentType;
        String path;
        long lastModified;
        long expires;
        SoftReference<byte[]> content;
        InputStream firstTimeInput;
    }

    private final static StaticFileInfoHolder NoContent = new StaticFileInfoHolder();

    private final static ConcurrentHashMap<String, StaticFileInfoHolder> StaticFileInfoMap = new ConcurrentHashMap<>();

    @RequestHandler
    public Object handler(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext,
            UrlMappingRule currentRule) throws FileNotFoundException, IOException {
        String path = convertPath(request, currentRule);

        StaticFileInfoHolder info = StaticFileInfoMap.get(path);

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
            response.setStatus(404);
            return null;
        }

        long clientTime = retrieveClientCachedTime(request);

        if (clientTime >= info.lastModified) {
            response.setStatus(304);
            return null;
        }

        response.setStatus(200);
        response.setHeader("Content-Type", info.contentType);
        response.setDateHeader("Expires", info.expires - Year_1970);
        response.setDateHeader("Last-Modified", info.lastModified - Year_1970);
        // one day
        response.setHeader("Cache-control", "max-age=" + (60 * 60 * 24));

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

    private boolean fileNameSecurityCheck(String path) {
        if (path.startsWith("file://")) {
            // we do not allow any unnormalized file name for security reason
            String name = path.substring("file://".length());
            String normalizedName = FilenameUtils.normalize(name);
            return name.equals(normalizedName);
        } else {
            return true;
        }
    }

    private long retrieveClientCachedTime(HttpServletRequest request) {
        try {
            return request.getDateHeader("If-Modified-Since");
        } catch (Exception e) {
            logger.debug("retrieve If-Modified-Since failed", e);
            return -1;
        }
    }

    private StaticFileInfoHolder createInfo(ServletContext servletContext, String path, boolean cache) throws FileNotFoundException,
            IOException {

        if (!fileNameSecurityCheck(path)) {
            return NoContent;
        }

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
            info.lastModified = defaultLastModified;
        }
        info.expires = defaultExpires;

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

    protected String judgContentType(String path) {

        Context context = Context.getCurrentThreadContext();

        String forceContentType = context.getData(WebApplicationContext.SCOPE_PATHVAR, VAR_CONTENT_TYPE);
        if (forceContentType != null) {
            return forceContentType;
        }

        // guess the type by file name extension
        return URLConnection.guessContentTypeFromName(path);
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
