package com.astamuse.asta4d.web.util.message;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextBindData;
import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowRenderer;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;

public class DefaultMessageRenderingHelper implements MessageRenderingHelper {

    private final static String FLASH_MSG_LIST_KEY = "FLASH_MSG_LIST_KEY#" + DefaultMessageRenderingHelper.class;

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
        List<MessageHolder> allMsgList = new LinkedList<>();

        List<MessageHolder> flashedList = Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_FLASH, FLASH_MSG_LIST_KEY);
        if (flashedList != null) {
            allMsgList.addAll(flashedList);
        }

        allMsgList.addAll(messageList.get());

        Renderer renderer = Renderer.create();

        final Map<String, List<MessageHolder>> msgMap = new HashMap<>();
        final Map<String, List<String>> alternativeMsgMap = new HashMap<>();
        List<MessageHolder> tmpList;
        for (MessageHolder mh : allMsgList) {
            tmpList = msgMap.get(mh.selector);
            if (tmpList == null) {
                tmpList = new LinkedList<>();
                msgMap.put(mh.selector, tmpList);
            }
            tmpList.add(mh);
        }

        renderer.disableMissingSelectorWarning();

        for (final Entry<String, List<MessageHolder>> item : msgMap.entrySet()) {
            if (item.getKey() == null) {
                List<String> list;
                for (MessageHolder mh : item.getValue()) {
                    list = alternativeMsgMap.get(mh.alternativeSelector);
                    if (list == null) {
                        list = new LinkedList<>();
                        alternativeMsgMap.put(mh.alternativeSelector, list);
                    }
                    list.add(mh.message);
                }
            } else {
                renderer.add(item.getKey(), item.getValue(), new RowRenderer<MessageHolder>() {
                    @Override
                    public Renderer convert(int rowIndex, MessageHolder obj) {
                        Renderer render = Renderer.create(obj.selector, obj.message);
                        render.add(obj.selector, "-class", "x-msg-stub");
                        return render;
                    }
                });
                renderer.add(new ElementNotFoundHandler(item.getKey()) {
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
                        return Renderer.create();
                    }
                });
            }
        }// end for loop

        renderer.enableMissingSelectorWarning();

        renderer.add(defaultGlobalContainerSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                Renderer alternativeRenderer = Renderer.create();
                for (final Entry<String, List<String>> item : alternativeMsgMap.entrySet()) {
                    alternativeRenderer.add(item.getKey(), item.getValue(), new RowRenderer<String>() {
                        @Override
                        public Renderer convert(int rowIndex, String msg) {
                            Renderer render = Renderer.create("*", msg);
                            render.add("*", "-class", "x-msg-stub");
                            return render;
                        }
                    });
                }
                RenderUtil.apply(elem, alternativeRenderer);
            }
        });

        renderer.add(".x-msg-stub", Clear);

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

    public void saveMessageListToFlash() {
        ArrayList<MessageHolder> list = new ArrayList<>(messageList.get());
        RedirectTargetProvider.addFlashScopeData(FLASH_MSG_LIST_KEY, list);
    }
}
