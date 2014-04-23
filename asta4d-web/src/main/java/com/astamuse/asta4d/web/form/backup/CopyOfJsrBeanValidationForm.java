package com.astamuse.asta4d.web.form.backup;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;

public class CopyOfJsrBeanValidationForm extends AbstractValidatableForm {

    private final static ValidatorFactory defaultFactory = Validation.buildDefaultValidatorFactory();
    private final static Validator defaultValidator = defaultFactory.getValidator();

    private Validator customizedValidator = null;

    private Validator retrieveValidator(Field clsField, ValidatableFormField formField) {
        if (customizedValidator == null) {
            customizedValidator = buildCustomizedValidatorForCertainFormField(clsField, formField);
        }
        return customizedValidator;
    }

    private Validator buildCustomizedValidatorForCertainFormField(Field clsField, ValidatableFormField formField) {
        BeanDescriptor beanDescriptor = defaultValidator.getConstraintsForClass(this.getClass());

        PropertyDescriptor propDesc = beanDescriptor.getConstraintsForProperty(clsField.getName());

        Set<ConstraintDescriptor<?>> constraints = propDesc.getConstraintDescriptors();

        HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class).configure();

        ConstraintMapping constraintMapping = configuration.createConstraintMapping();

        PropertyConstraintMappingContext pc = constraintMapping.type(formField.getClass()).property("fieldValue", ElementType.METHOD);

        ConstraintDef def;

        for (ConstraintDescriptor cd : constraints) {
            final Map<String, Object> attrMap = cd.getAttributes();
            def = new ConstraintDef(cd.getAnnotation().annotationType()) {
                {
                    for (Entry<String, Object> item : attrMap.entrySet()) {
                        addParameter(item.getKey(), item.getValue());
                    }
                }
            };

            def.groups(convertClsSetToArray(cd.getGroups()));
            def.message(cd.getMessageTemplate());
            def.payload(convertClsSetToArray(cd.getPayload()));
            pc.constraint(def);
        }

        Validator validator = configuration.addMapping(constraintMapping).buildValidatorFactory().getValidator();
        return validator;
    }

    private Class[] convertClsSetToArray(Set clsSet) {
        Set<Class> cs = clsSet;
        return cs.toArray(new Class[cs.size()]);
    }

    protected boolean isValid(List<Field> fieldList) {
        Set<ConstraintViolation<ValidatableFormField>> result = new LinkedHashSet<>();
        Set<ConstraintViolation<ValidatableFormField>> tmp;
        try {
            for (Field field : fieldList) {

                Type type = field.getGenericType();

                ValidatableFormField formField;

                formField = (ValidatableFormField) FieldUtils.readField(field, this, true);

                tmp = validate(field, formField);
                for (ConstraintViolation<ValidatableFormField> cv : tmp) {
                    addMessage(formField.getName(), cv.getMessage());
                }
                result.addAll(tmp);
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return result.isEmpty();
    }

    protected Set<ConstraintViolation<ValidatableFormField>> validate(Field clsField, ValidatableFormField formField) {
        Validator validator = retrieveValidator(clsField, formField);
        return validator.validateValue((Class) formField.getClass(), "fieldValue", formField.getFieldValue());
    }

    @Override
    protected boolean validateValues(List<Field> fieldList) {
        // TODO Auto-generated method stub
        return false;
    }
}
