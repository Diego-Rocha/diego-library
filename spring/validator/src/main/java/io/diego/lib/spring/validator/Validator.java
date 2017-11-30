package io.diego.lib.spring.validator;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;

import javax.persistence.Column;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public abstract class Validator<T> implements org.springframework.validation.Validator {

	@Getter
	@Setter
	private String errorCodeRequired = "required";

	@Getter
	@Setter
	private String errorCodeMaxLength = "maxLength";

	@Getter
	@Setter
	private String errorCodPrefix = "error";

	@Setter
	private String beanErrorCodeRequired;

	@Getter
	private boolean validateMaxLength = true;
	protected String beanName;
	private Class<T> type;

	protected abstract Collection<RequiredField<T>> requiredFields();

	protected abstract void typedValidate(T bean, Errors errors);

	private boolean rejectIfNull(T bean, Errors errors) {
		if (bean == null) {
			errors.reject(getBeanErrorCodeRequired());
			return true;
		}
		return false;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
		Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];
		return type.equals(clazz);
	}

	@Override
	public void validate(Object bean, Errors errors) {
		if (rejectIfNull((T) bean, errors)) {
			return;
		}
		boolean fail = validateRequiredFields((T) bean, errors);
		if (fail) {
			return;
		}
		if (isValidateMaxLength() && validateMaxLength(bean, errors)) {
			return;
		}
		typedValidate((T) bean, errors);
	}

	private boolean validateMaxLength(Object bean, Errors errors) {
		int initialErrorCount = errors.getErrorCount();
		for (Field field : getType().getDeclaredFields()) {
			if (!field.getType().isAssignableFrom(String.class)) {
				continue;
			}
			Column columnAnnotation = field.getAnnotation(Column.class);
			field.setAccessible(true);
			String value;
			try {
				Object o = field.get(bean);
				if (o == null) {
					continue;
				}
				value = o.toString();
			} catch (Throwable e) {
				continue;
			}
			if (columnAnnotation == null) {
				try {
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), getType());
					columnAnnotation = propertyDescriptor.getReadMethod().getAnnotation(Column.class);
				} catch (IntrospectionException e) {
					continue;
				}
			}
			if (columnAnnotation == null) {
				continue;
			}
			if (columnAnnotation.length() < value.length()) {
				String fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, field.getName());
				errors.rejectValue(fieldName, getFieldErrorCodeMaxLength(fieldName), new Object[]{columnAnnotation.length(), value.length()}, "");
			}
		}

		return errors.getErrorCount() > initialErrorCount;
	}

	protected String getFieldErrorCodeRequired(String field) {
		return getFieldErrorCode(field, getErrorCodeRequired());
	}

	protected String getFieldErrorCodeMaxLength(String field) {
		return getFieldErrorCode(field, getErrorCodeMaxLength());
	}

	private String getFieldErrorCode(String field, String suffix) {
		return String.format("%s.%s.%s.%s", getErrorCodPrefix(), getBeanName(), field, suffix);
	}

	public BindingResult validate(T bean) {
		DataBinder dataBinder = new DataBinder(bean, errorCodPrefix);
		dataBinder.setValidator(this);
		dataBinder.validate();
		return dataBinder.getBindingResult();
	}

	private boolean validateRequiredFields(T bean, Errors errors) {
		Collection<RequiredField<T>> requiredFields = requiredFields();
		if (requiredFields == null || requiredFields.isEmpty()) {
			return false;
		}
		int initialErrorsCount = errors.getErrorCount();
		for (RequiredField requiredField : requiredFields) {
			requiredField.validate(errors);
		}
		boolean hasNewFails = errors.getErrorCount() > initialErrorsCount;
		return hasNewFails;
	}

	protected Class<T> getType() {
		if (type != null) {
			return type;
		}
		ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
		type = (Class<T>) superClass.getActualTypeArguments()[0];
		return type;
	}

	protected String getBeanName() {
		if (beanName != null) {
			return beanName;
		}
		beanName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getType().getSimpleName());
		return beanName;
	}

	protected RequiredField<T> getNewRequiredField(String field) {
		return new RequiredField<>(this, field);
	}

	protected RequiredField<T> getNewRequiredField(String field, String code) {
		return new RequiredField<>(this, field, code);
	}

	public String getBeanErrorCodeRequired() {
		if (beanErrorCodeRequired == null) {
			beanErrorCodeRequired = String.format("%s.%s.%s", getErrorCodPrefix(), getBeanName(), getErrorCodeRequired());
		}
		return beanErrorCodeRequired;
	}

}
