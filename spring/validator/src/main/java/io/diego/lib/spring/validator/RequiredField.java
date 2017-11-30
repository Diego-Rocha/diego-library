package io.diego.lib.spring.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public class RequiredField<T> {

	private Validator<T> validator;
	private String field;
	private String code;

	public RequiredField(Validator<T> validator, String field) {
		this(validator, field, null);
	}

	public RequiredField(Validator validator, String field, String code) {
		this.validator = validator;
		this.field = field;
		this.code = code;
	}

	public String getField() {
		return field;
	}

	public String getCode() {
		if (code != null && !code.isEmpty()) {
			return code;
		}
		code = String.format("%s.%s.%s.%s", getValidator().getErrorCodPrefix(), getValidator().getBeanName(), getField(), getValidator().getErrorCodeRequired());
		return code;
	}

	public Validator getValidator() {
		return validator;
	}

	protected void validate(Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, getField(), getCode());
	}
}
