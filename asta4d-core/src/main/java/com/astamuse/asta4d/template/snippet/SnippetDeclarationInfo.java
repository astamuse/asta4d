package com.astamuse.asta4d.template.snippet;

public class SnippetDeclarationInfo {

    private String snippetName;

    private String snippetHandler;

    private int _hashcode;

    public SnippetDeclarationInfo(String snippetName, String snippetHandler) {
        super();
        this.snippetName = snippetName;
        this.snippetHandler = snippetHandler;

        final int prime = 31;
        _hashcode = 1;
        _hashcode = prime * _hashcode + ((snippetHandler == null) ? 0 : snippetHandler.hashCode());
        _hashcode = prime * _hashcode + ((snippetName == null) ? 0 : snippetName.hashCode());

    }

    public String getSnippetName() {
        return snippetName;
    }

    public String getSnippetHandler() {
        return snippetHandler;
    }

    @Override
    public int hashCode() {
        return _hashcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SnippetDeclarationInfo other = (SnippetDeclarationInfo) obj;
        if (snippetHandler == null) {
            if (other.snippetHandler != null)
                return false;
        } else if (!snippetHandler.equals(other.snippetHandler))
            return false;
        if (snippetName == null) {
            if (other.snippetName != null)
                return false;
        } else if (!snippetName.equals(other.snippetName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s:%s]", snippetName, snippetHandler);
    }

}
