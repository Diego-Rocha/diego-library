package io.diego.lib.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
public class MessageConfig {

	@Bean
	public LocaleContext localeContext() {
		Locale locale = new Locale("pt", "BR");
		return new SimpleLocaleContext(locale);
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setBasenames("classpath:messages/locale/framework", "classpath:messages/locale/app");
		messageSource.setCacheSeconds(2);
		return messageSource;
	}


	public ResourceBundle getResourceBundle() {
		return new MessageSourceResourceBundle(messageSource(), localeContext().getLocale());
	}
}
