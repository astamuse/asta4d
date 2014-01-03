package com.astamuse.asta4d.web.form;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.reflect.FieldUtils;

public class JsrBeanValidationForm extends AbstractValidationForm {

    private final static ValidatorFactory defaultFactory = Validation.buildDefaultValidatorFactory();
    private final static Validator defaultValidator = defaultFactory.getValidator();

    @Override
    protected boolean isValid(List<Field> fieldList) {
        return false;
    }

    public boolean isValid() {
        try {

            Set<ConstraintViolation> result = new LinkedHashSet<>();

            Object magicInstance = createValidationMagicInstance();

            List<Field> originalFieldList = retrieveValidationFieldList();
            for (Field field : originalFieldList) {
                Object value = FieldUtils.readField(field, this, true);
                Object validateValue = ((FormField) value).getFieldValue();
                Set tmp = defaultValidator.validateValue(magicInstance.getClass(), field.getName(), validateValue);
                for (Object object : tmp) {
                    ConstraintViolation cv = (ConstraintViolation) object;
                    addMessage(field.getName(), cv.getMessage());
                }
                result.addAll(tmp);
            }

            return result.isEmpty();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object createValidationMagicInstance() {
        try {
            Class magicCls = getValidationMagicCls();
            Object magicInstance = magicCls.newInstance();

            return magicInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Class getValidationMagicCls() {
        try {
            String originalClassName = this.getClass().getName();
            String className = originalClassName + "_$JsrBeanValidationForm$_";
            List<Field> originalFieldList = retrieveValidationFieldList();

            ClassPool cpool = ClassPool.getDefault();
            CtClass ct = cpool.makeClass(this.getClass().getPackage().getName() + "." + className);
            ClassFile ctFile = ct.getClassFile();
            ConstPool constpool = ctFile.getConstPool();

            CtClass stringType = cpool.get("java.lang.String");

            CtField cf_original_classname_holder = new CtField(stringType, "_original_classname_holder", ct);
            ct.addField(cf_original_classname_holder, CtField.Initializer.constant(originalClassName));

            for (Field originalField : originalFieldList) {

                Class originalType = originalField.getType();
                String name = originalField.getName();
                if (FormField.class.isAssignableFrom(originalType)) {
                    originalType = (Class) ((ParameterizedType) originalField.getGenericType()).getActualTypeArguments()[0];
                }

                CtClass fieldValueType = cpool.get(originalType.getName());

                CtField cf_value = new CtField(fieldValueType, name, ct);

                AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);

                Annotation[] originalAnnotations = originalField.getAnnotations();
                for (Annotation anno : originalAnnotations) {
                    javassist.bytecode.annotation.Annotation annot = new javassist.bytecode.annotation.Annotation(anno.annotationType()
                            .getName(), constpool);
                    Method[] ams = anno.annotationType().getDeclaredMethods();
                    for (Method am : ams) {
                        Object value = am.invoke(anno);
                        MemberValue mv = createMemberValue(constpool, cpool.get(value.getClass().getName()), value);
                        annot.addMemberValue(am.getName(), mv);
                    }
                    attr.addAnnotation(annot);
                }
                cf_value.getFieldInfo().addAttribute(attr);

                ct.addField(cf_value);

                CtField cf_name_holder = new CtField(stringType, name + "_name_holder", ct);
                ct.addField(cf_name_holder);

            }

            return ct.toClass();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static MemberValue createMemberValue(ConstPool cp, CtClass type, Object value) throws NotFoundException {
        if (type.isArray()) {
            CtClass arrayType = type.getComponentType();
            int len = Array.getLength(value);
            MemberValue[] mvArray = new MemberValue[len];
            for (int i = 0; i < len; i++) {
                MemberValue member = javassist.bytecode.annotation.Annotation.createMemberValue(cp, arrayType);
                mvArray[i] = member;
            }
            ArrayMemberValue amv = new ArrayMemberValue(cp);
            amv.setValue(mvArray);
            return amv;
        } else if (type.getName().equals("java.lang.Boolean"))
            return new BooleanMemberValue(((Boolean) value).booleanValue(), cp);
        else if (type.getName().equals("java.lang.Byte"))
            return new ByteMemberValue(((Byte) value).byteValue(), cp);
        else if (type.getName().equals("java.lang.Character"))
            return new CharMemberValue(((Character) value).charValue(), cp);
        else if (type.getName().equals("java.lang.Short"))
            return new ShortMemberValue(((Short) value).shortValue(), cp);
        else if (type.getName().equals("java.lang.Integer"))
            return new IntegerMemberValue(((Integer) value).intValue(), cp);
        else if (type.getName().equals("java.lang.Long"))
            return new LongMemberValue(((Long) value).longValue(), cp);
        else if (type.getName().equals("java.lang.Float"))
            return new FloatMemberValue(((Float) value).floatValue(), cp);
        else if (type.getName().equals("java.lang.Double"))
            return new DoubleMemberValue(((Double) value).doubleValue(), cp);
        else if (type.getName().equals("java.lang.Class"))
            return new ClassMemberValue(((Class) value).getName(), cp);
        else if (type.getName().equals("java.lang.String"))
            return new StringMemberValue((String) value, cp);
        else if (type.isInterface()) {
            javassist.bytecode.annotation.Annotation info = new javassist.bytecode.annotation.Annotation(cp, type);
            return new AnnotationMemberValue(info, cp);
        } else {
            // treat as enum. I know this is not typed,
            // but JBoss has an Annotation Compiler for JDK 1.4
            // and I want it to work with that. - Bill Burke
            EnumMemberValue emv = new EnumMemberValue(cp);
            emv.setType(type.getName());
            emv.setValue(((Enum) value).name());
            return emv;
        }
    }
}
