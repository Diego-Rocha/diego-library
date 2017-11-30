package io.diego.lib.spring.validator;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

public class ValidationException extends BindException {

	private BindingResult errors;

	public ValidationException(BindingResult errors) {
		super(errors);
		this.errors = errors;
	}

	public BindingResult getErrors() {
		return errors;
	}
}
