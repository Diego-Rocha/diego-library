package io.diego.lib.spring.config;

import io.diego.lib.spring.data.service.generic.repository.GenericRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
		repositoryBaseClass = GenericRepositoryImpl.class)
public class SpringDataServiceGenericConfig {
}
