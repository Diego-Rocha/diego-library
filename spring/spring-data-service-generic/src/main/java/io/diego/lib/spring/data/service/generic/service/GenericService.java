package io.diego.lib.spring.data.service.generic.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import io.diego.lib.spring.validator.ValidationException;
import io.diego.lib.spring.validator.Validator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.util.List;

public interface GenericService<T, ID extends Serializable> extends ShowMessageValidator {

	Validator<T> getValidator();

	void validate(T t) throws ValidationException;

	void validate(T t, BindingResult errors) throws ValidationException;

	<S extends T> void validate(Iterable<S> entities) throws ValidationException;

	<S extends T> void validate(Iterable<S> entities, BindingResult errors) throws ValidationException;

	List<T> findAll();

	List<T> findAll(Sort sort);

	Page<T> findAll(Pageable pageable);

	List<T> findAll(Iterable<ID> ids);

	long count();

	void delete(ID id);

	void delete(T t);

	void delete(Iterable<? extends T> iterable);

	void deleteAll();

	<S extends T> S save(S s) throws ValidationException;

	<S extends T> List<S> save(Iterable<S> entities) throws ValidationException;

	T findOne(ID id);

	boolean exists(ID id);

	void flush();

	<S extends T> S saveAndFlush(S entity) throws ValidationException;

	void deleteInBatch(Iterable<T> entities);

	void deleteAllInBatch();

	T getOne(ID id);

	T findOne(Predicate predicate);

	Iterable<T> findAll(Predicate predicate);

	Iterable<T> findAll(Predicate predicate, Sort sort);

	Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orderSpecifiers);

	Iterable<T> findAll(OrderSpecifier<?>... orderSpecifiers);

	Page<T> findAll(Predicate predicate, Pageable pageable);

	long count(Predicate predicate);

	boolean exists(Predicate predicate);


}
