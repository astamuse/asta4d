package com.astamuse.asta4d.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jsoup.nodes.Element;

public class ProxiedClassNameSet implements Set<String> {

    private Set<String> classNames = null;

    private Element originalElement;

    public ProxiedClassNameSet(Element originalElement) {
        this.originalElement = originalElement;
    }

    private void initClassNames() {
        if (classNames == null) {
            String[] names = originalElement.className().split("\\s+");
            classNames = new LinkedHashSet<String>(Arrays.asList(names));
        }
    }

    public int size() {
        initClassNames();
        return classNames.size();
    }

    public boolean isEmpty() {
        initClassNames();
        return classNames.isEmpty();
    }

    public boolean contains(Object o) {
        initClassNames();
        return classNames.contains(o);
    }

    public Iterator<String> iterator() {
        initClassNames();
        return classNames.iterator();
    }

    public Object[] toArray() {
        initClassNames();
        return classNames.toArray();
    }

    public <T> T[] toArray(T[] a) {
        initClassNames();
        return classNames.toArray(a);
    }

    public boolean add(String e) {
        initClassNames();
        return classNames.add(e);
    }

    public boolean remove(Object o) {
        initClassNames();
        return classNames.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        initClassNames();
        return classNames.containsAll(c);
    }

    public boolean addAll(Collection<? extends String> c) {
        initClassNames();
        return classNames.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        initClassNames();
        return classNames.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        initClassNames();
        return classNames.removeAll(c);
    }

    public void clear() {
        initClassNames();
        classNames.clear();
    }

    public boolean equals(Object o) {
        initClassNames();
        return classNames.equals(o);
    }

    public int hashCode() {
        initClassNames();
        return classNames.hashCode();
    }

}
