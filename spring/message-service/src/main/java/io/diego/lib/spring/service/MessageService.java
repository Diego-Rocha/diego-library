package io.diego.lib.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service("msg")
public class MessageService implements Serializable {

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private LocaleContext localeContext;

	public String get(String id, String... parametros) {
		List<String> param = new ArrayList<>(parametros.length);
		String parametroTraduzido;
		for (String parametro : parametros) {
			try {
				parametroTraduzido = get(parametro);
			} catch (NoSuchMessageException e) {
				parametroTraduzido = parametro;
			}
			param.add(parametroTraduzido);
		}
		return messageSource.getMessage(id, param.toArray(), localeContext.getLocale());
	}

	public String get(String id) {
		return messageSource.getMessage(id, null, localeContext.getLocale());
	}
}
