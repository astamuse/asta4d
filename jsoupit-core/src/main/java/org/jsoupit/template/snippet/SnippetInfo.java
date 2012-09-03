package org.jsoupit.template.snippet;

public class SnippetInfo {

    private String snippetName;

    private String snippetHandler;

    public SnippetInfo(String snippetName, String snippetHandler) {
        super();
        this.snippetName = snippetName;
        this.snippetHandler = snippetHandler;
    }

    public String getSnippetName() {
        return snippetName;
    }

    public String getSnippetHandler() {
        return snippetHandler;
    }

    @Override
    public int hashCode() {
        // TODO cal it in constructor
        final int prime = 31;
        int result = 1;
        result = prime * result + ((snippetHandler == null) ? 0 : snippetHandler.hashCode());
        result = prime * result + ((snippetName == null) ? 0 : snippetName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SnippetInfo other = (SnippetInfo) obj;
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
