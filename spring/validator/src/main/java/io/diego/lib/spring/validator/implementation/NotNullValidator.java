package io.diego.lib.spring.validator.implementation;

import com.google.common.base.CaseFormat;
import io.diego.lib.spring.validator.RequiredField;
import io.diego.lib.spring.validator.Validator;
import org.springframework.validation.Errors;

import java.util.Collection;

public class NotNullValidator<T> extends Validator<T> {

	private final Class<T> clazz;

	public NotNullValidator(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	protected Collection<RequiredField<T>> requiredFields() {
		return null;
	}

	@Override
	protected void typedValidate(T bean, Errors errors) {
	}

	@Override
	protected String getBeanName() {
		if (beanName != null) {
			return beanName;
		}
		beanName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName());
		return beanName;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return this.clazz.equals(clazz);
	}
}
