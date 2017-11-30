package io.diego.lib.spring.data.service.generic.service;

import io.diego.lib.spring.service.MessageService;
import org.springframework.context.NoSuchMessageException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface ShowMessageValidator {

	MessageService getMessageService();

	default List<String> parseValidationErros(Errors errors) {
		List<String> list = new ArrayList<>();
		for (ObjectError error : errors.getAllErrors()) {
			String code = "";
			String beanCode = "";
			try {
				if (error.getArguments() != null && error.getArguments().length > 0) {
					List<String> params = Arrays.stream(error.getArguments()).map(Object::toString).collect(Collectors.toList());
					code = getMessageService().get(error.getCode(), params.toArray(new String[0]));
				} else {
					code = getMessageService().get(error.getCode());
				}
			} catch (NoSuchMessageException e) {
				String patternRequired = "^error\\.(.*)\\.required$";
				String patternMaxLength = "^error\\.(.*)\\.maxLength";
				if (error.getCode().matches(patternRequired)) {
					try {
						String[] split = error.getCode().split("\\.");
						beanCode = split[split.length - 2];
						String beanTraduzido = getMessageService().get(beanCode);
						code = getMessageService().get("msg.requiredField", beanTraduzido);
					} catch (NoSuchMessageException e2) {
						throw new NoSuchMessageException(String.format("Implemente o código '%s' ou '%s' no arquivo de mensagens", beanCode, error.getCode()));
					}
				} else if (error.getCode().matches(patternMaxLength)) {
					try {
						String[] split = error.getCode().split("\\.");
						beanCode = split[split.length - 2];
						List<String> params = Arrays.stream(error.getArguments()).map(Object::toString).collect(Collectors.toList());
						params.add(0, getMessageService().get(beanCode));
						code = getMessageService().get("msg.maxLengthField", params.toArray(new String[0]));
					} catch (NoSuchMessageException e2) {
						throw new NoSuchMessageException(String.format("Implemente o código '%s' ou '%s' no arquivo de mensagens", beanCode, error.getCode()));
					}
				} else {
					throw e;
				}
			}
			list.add(code);
		}
		return list;
	}

}
