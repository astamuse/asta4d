package com.astamuse.asta4d.sample.newform;

import javax.validation.constraints.Max;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.CheckBox;
import com.astamuse.asta4d.web.form.annotation.renderable.InputBox;
import com.astamuse.asta4d.web.form.annotation.renderable.RadioBox;
import com.astamuse.asta4d.web.form.annotation.renderable.SelectBox;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;

@Form
public class MyForm {

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

    @InputBox
    @NotBlank
    private String name;

    @InputBox
    @Max(23)
    @NotEmpty
    private Integer age;

    @SelectBox(name = "bloodtype")
    private BloodType bloodType = BloodType.AB;

    @NotEmpty
    @RadioBox
    private SEX sex;

    @NotEmpty
    @CheckBox
    private Language[] language;

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

}
