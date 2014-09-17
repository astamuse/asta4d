package com.astamuse.asta4d.sample.util.persondb;

public interface IdentifiableEntity extends Cloneable {

    public Integer getId();

    public void setId(Integer id);

    public Object clone() throws CloneNotSupportedException;
}
