package com.astamuse.asta4d.web.form.validation;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.util.i18n.pattern.CharsetResourceBundleFactory;
import com.astamuse.asta4d.util.i18n.pattern.ResourceBundleFactory;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;

public class JsrValidator extends CommonValidatorBase implements FormValidator {

    protected static class Asta4DResourceBundleFactoryAdapter implements ResourceBundleLocator {

        private String baseName;

        private ResourceBundleFactory resourceBundleFactory;

        public Asta4DResourceBundleFactoryAdapter(String baseName) {
            this(baseName, new CharsetResourceBundleFactory(StandardCharsets.UTF_8));
        }

        public Asta4DResourceBundleFactoryAdapter(String baseName, ResourceBundleFactory resourceBundleFactory) {
            this.baseName = baseName;
            this.resourceBundleFactory = resourceBundleFactory;
        }

        @Override
        public ResourceBundle getResourceBundle(Locale locale) {
            try {
                return resourceBundleFactory.retrieveResourceBundle(baseName, locale);
            } catch (MissingResourceException mre) {
                return null;
            }
        }

    }

    protected static class Asta4DIntegratedResourceBundleInterpolator extends ResourceBundleMessageInterpolator {

        public Asta4DIntegratedResourceBundleInterpolator() {
            super();
        }

        public Asta4DIntegratedResourceBundleInterpolator(ResourceBundleLocator userResourceBundleLocator, boolean cacheMessages) {
            super(userResourceBundleLocator, cacheMessages);
        }

        public Asta4DIntegratedResourceBundleInterpolator(ResourceBundleLocator userResourceBundleLocator) {
            super(userResourceBundleLocator);
        }

        protected Locale retrieveLocalFromAsta4d() {
            Locale loc = WebApplicationContext.getCurrentThreadWebApplicationContext().getCurrentLocale();
            if (loc == null) {
                return Locale.getDefault();
            } else {
                return loc;
            }
        }

        @Override
        public String interpolate(String message, Context context) {
            return super.interpolate(message, context, retrieveLocalFromAsta4d());
        }

        @Override
        public String interpolate(String message, Context context, Locale locale) {
            return super.interpolate(message, context, retrieveLocalFromAsta4d());
        }

    }

    protected static class ValidationPropertyInfo {
        Path path;
        AnnotatedPropertyInfo field;
        int arrayIndex;

        public ValidationPropertyInfo(Path path, AnnotatedPropertyInfo field, int arrayIndex) {
            super();
            this.path = path;
            this.field = field;
            this.arrayIndex = arrayIndex;
        }

    }

    protected boolean addFieldLablePrefixToMessage;

    protected Validator validator;

    public JsrValidator() {
        this(retrieveDefaultValidator());
    }

    public JsrValidator(Validator validator) {
        super();
        this.validator = validator;
    }

    public JsrValidator(Validator validator, boolean addFieldLablePrefixToMessage) {
        super(addFieldLablePrefixToMessage);
        this.validator = validator;
    }

    private static Validator defaultValidator = null;

    protected static Validator retrieveDefaultValidator() {
        // as an out-of-box default implementation, we do not mind we may create the instance many times.
        if (defaultValidator == null) {
            defaultValidator = Validation.byDefaultProvider().configure()
                    .messageInterpolator(new Asta4DIntegratedResourceBundleInterpolator()).buildValidatorFactory().getValidator();
        }
        return defaultValidator;
    }

    @Override
    public List<FormValidationMessage> validate(final Object form) {
        Set<ConstraintViolation<Object>> cvs = validator.validate(form);
        return ListConvertUtil.transform(cvs, new RowConvertor<ConstraintViolation<Object>, FormValidationMessage>() {

            @Override
            public FormValidationMessage convert(int rowIndex, ConstraintViolation<Object> cv) {

                ValidationPropertyInfo vp = retrieveValidationPropertyInfo(form.getClass(), cv.getPropertyPath());

                String fieldName;
                String msg;

                if (vp.field == null) {
                    // which means we cannot retrieve the annotated form field information, thus we got a unpredicated validation error
                    fieldName = vp.path.toString();
                    msg = cv.getMessage();
                } else {
                    fieldName = retrieveFieldName(vp.field, vp.arrayIndex);
                    String fieldLabel = retrieveFieldLabel(vp.field, vp.arrayIndex);
                    String annotatedMsg = retrieveFieldAnnotatedMessage(vp.field);

                    if (StringUtils.isNotEmpty(annotatedMsg)) {
                        msg = createAnnotatedMessage(vp.field.getType(), fieldName, fieldLabel, annotatedMsg);
                    } else {
                        String fieldTypeName = retrieveFieldTypeName(vp.field);
                        msg = createMessage(vp.field.getType(), fieldName, fieldLabel, fieldTypeName, cv.getMessage());
                    }
                }
                return new FormValidationMessage(fieldName, msg);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    protected String createMessage(Class formCls, String fieldName, String fieldLabel, String fieldTypeName, String cvMsg) {
        if (addFieldLablePrefixToMessage) {
            String msgTemplate = "%s: %s";
            return String.format(msgTemplate, fieldLabel, cvMsg);
        } else {
            return cvMsg;
        }
    }

    protected ValidationPropertyInfo retrieveValidationPropertyInfo(Class formCls, Path path) {
        Iterator<Node> it = path.iterator();
        Class cls = formCls;
        try {
            while (it.hasNext()) {
                Node node = it.next();
                if (node.getKind() != ElementKind.PROPERTY) {
                    // we cannot handle this case
                    return new ValidationPropertyInfo(path, null, -1);
                }
                String name = node.getName();
                if (it.hasNext()) {// not the last
                    AnnotatedPropertyInfo field = AnnotatedPropertyUtil.retrievePropertyByBeanPropertyName(cls, name);
                    CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
                    if (cff == null) {
                        // regular fields
                        cls = field.getType();
                    } else if (StringUtils.isEmpty(cff.arrayLengthField())) {
                        // simple cascading
                        cls = field.getType();
                    } else {
                        // array cascading
                        cls = field.getType().getComponentType();
                    }

                    continue;
                } else {// the last
                    AnnotatedPropertyInfo field = AnnotatedPropertyUtil.retrievePropertyByBeanPropertyName(cls, name);
                    if (field == null) {
                        // it seems we got a unexpected error
                        return new ValidationPropertyInfo(path, null, -1);
                    } else {
                        if (node.getIndex() == null) {
                            // regular fields or simple cascading
                            return new ValidationPropertyInfo(path, field, -1);
                        } else {
                            return new ValidationPropertyInfo(path, field, node.getIndex());
                        }
                    }
                }
            }
            // it seems impossible
            return new ValidationPropertyInfo(path, null, -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String retrieveFieldDisplayName(String fieldName) {
        return fieldName;
    }

}
