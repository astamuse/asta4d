package com.astamuse.asta4d.web.util.message;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.data.ContextBindData;
import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class DefaultMessageRenderingHelper implements MessageRenderingHelper {

    private final static DefaultMessageRenderingHelper instance = new DefaultMessageRenderingHelper();

    private final static class MessageHolder {
        String selector;
        String alternativeSelector;
        String message;

        public MessageHolder(String selector, String alternativeSelector, String message) {
            super();
            this.selector = selector;
            this.alternativeSelector = alternativeSelector;
            this.message = message;
        }

    }

    private String defaultGlobalContainerSelector = "#global-msg-container";

    private String defaultInfoMsgSelector = "#info-msg li";

    private String defaultWarnMsgSelector = "#warn-msg li";

    private String defaultErrMsgSelector = "#err-msg li";

    private ContextBindData<List<MessageHolder>> messageList = new ContextBindData<List<MessageHolder>>(true) {
        @Override
        protected List<MessageHolder> buildData() {
            return new LinkedList<>();
        }
    };

    protected DefaultMessageRenderingHelper() {
        super();
    }

    public final static DefaultMessageRenderingHelper instance() {
        return instance;
    }

    public void setDefaultGlobalContainerSelector(String defaultGlobalContainerSelector) {
        this.defaultGlobalContainerSelector = defaultGlobalContainerSelector;
    }

    public void setDefaultInfoMsgSelector(String defaultInfoMsgSelector) {
        this.defaultInfoMsgSelector = defaultInfoMsgSelector;
    }

    public void setDefaultWarnMsgSelector(String defaultWarnMsgSelector) {
        this.defaultWarnMsgSelector = defaultWarnMsgSelector;
    }

    public void setDefaultErrMsgSelector(String defaultErrMsgSelector) {
        this.defaultErrMsgSelector = defaultErrMsgSelector;
    }

    public Renderer createMessageRenderer() {
        Renderer renderer = Renderer.create();

        final Map<String, List<MessageHolder>> msgMap = new HashMap<>();
        final Map<String, List<String>> alternativeMsgMap = new HashMap<>();
        List<MessageHolder> mhList;
        for (MessageHolder mh : messageList.get()) {
            mhList = msgMap.get(mh.selector);
            if (mhList == null) {
                mhList = new LinkedList<>();
                msgMap.put(mh.selector, mhList);
            }
            mhList.add(mh);
        }

        renderer.disableMissingSelectorWarning();

        for (final Entry<String, List<MessageHolder>> item : msgMap.entrySet()) {
            renderer.add(item.getKey(), item.getValue(), new RowConvertor<MessageHolder, String>() {
                @Override
                public String convert(int rowIndex, MessageHolder obj) {
                    return obj.message;
                }
            });
            renderer.add(item.getKey(), new ElementNotFoundHandler() {
                @Override
                public Renderer alternativeRenderer() {
                    List<String> list;
                    for (MessageHolder mh : item.getValue()) {
                        list = alternativeMsgMap.get(mh.alternativeSelector);
                        if (list == null) {
                            list = new LinkedList<>();
                            alternativeMsgMap.put(mh.alternativeSelector, list);
                        }
                        list.add(mh.message);
                    }
                    return null;
                }
            });
        }// end for loop

        renderer.eableMissingSelectorWarning();

        renderer.add(defaultGlobalContainerSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                Renderer alternativeRenderer = Renderer.create();
                for (final Entry<String, List<String>> item : alternativeMsgMap.entrySet()) {
                    alternativeRenderer.add(item.getKey(), item.getValue());
                }
                RenderUtil.apply(elem, alternativeRenderer);
            }
        });

        return renderer;

    }

    public void outputMessage(final String selector, final String alternativeSelector, final String msg) {
        messageList.get().add(new MessageHolder(selector, alternativeSelector, msg));
    }

    public void info(String msg) {
        info(null, msg);
    }

    public void info(String selector, String msg) {
        outputMessage(selector, defaultInfoMsgSelector, msg);
    }

    public void warn(String msg) {
        warn(null, msg);
    }

    public void warn(String selector, String msg) {
        outputMessage(selector, defaultWarnMsgSelector, msg);
    }

    public void err(String msg) {
        err(null, msg);
    }

    public void err(String selector, String msg) {
        outputMessage(selector, defaultErrMsgSelector, msg);
    }
}
