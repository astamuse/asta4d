package com.astamuse.asta4d.sample.util.persondb;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.astamuse.asta4d.sample.handler.form.PersonForm;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;

public class Person implements Cloneable {

    public static enum BloodType {
        A, B, O, AB;

        public static final OptionValueMap asOptionValueMap = OptionValueMap.build(BloodType.values(),
                new RowConvertor<BloodType, OptionValuePair>() {
                    @Override
                    public OptionValuePair convert(int rowIndex, BloodType obj) {
                        return new OptionValuePair(obj.name(), obj.name());
                    }
                });
    }

    public static enum SEX {
        Male, Female;

        public static final OptionValueMap asOptionValueMap = OptionValueMap.build(SEX.values(), new RowConvertor<SEX, OptionValuePair>() {
            @Override
            public OptionValuePair convert(int rowIndex, SEX obj) {
                return new OptionValuePair(obj.name(), obj.name());
            }
        });
    }

    public static enum Language {
        English, Japanese, Chinese;

        public static final OptionValueMap asOptionValueMap = OptionValueMap.build(Language.values(),
                new RowConvertor<Language, OptionValuePair>() {
                    @Override
                    public OptionValuePair convert(int rowIndex, Language obj) {
                        return new OptionValuePair(obj.name(), obj.name());
                    }
                });
    }

    private String name;

    private Integer age;

    private BloodType bloodType = BloodType.AB;

    private SEX sex;

    private Language[] language;

    private String memo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public SEX getSex() {
        return sex;
    }

    public void setSex(SEX sex) {
        this.sex = sex;
    }

    public Language[] getLanguage() {
        return language;
    }

    public void setLanguage(Language[] language) {
        this.language = language;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public Person clone() throws CloneNotSupportedException {
        return (Person) super.clone();
    }

    public static Person createByForm(PersonForm form) {
        Person p = new Person();
        try {
            BeanUtils.copyProperties(p, form);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return p;
    }
}
