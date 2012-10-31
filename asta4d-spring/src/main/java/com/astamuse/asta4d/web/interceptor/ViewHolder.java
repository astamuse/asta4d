package com.astamuse.asta4d.web.interceptor;

import com.astamuse.asta4d.web.view.Asta4dView;

public class ViewHolder {

    private Asta4dView view;

    public ViewHolder() {
    }

    public ViewHolder(Asta4dView view) {
        this.view = view;
    }

    public Asta4dView getView() {
        return view;
    }

    public void setView(Asta4dView view) {
        this.view = view;
    }
}
