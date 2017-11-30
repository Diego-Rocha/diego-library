package io.diego.lib.spring.data.service.generic.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import io.diego.lib.spring.data.service.generic.repository.GenericRepository;
import io.diego.lib.spring.service.MessageService;
import io.diego.lib.spring.validator.ValidationException;
import io.diego.lib.spring.validator.Validator;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.util.List;

public abstract class GenericServiceImpl<T, ID extends Serializable> implements GenericService<T, ID> {

	private final GenericRepository<T, ID> repository;

	public GenericServiceImpl(GenericRepository<T, ID> repository) {
		this.repository = repository;
	}

	protected GenericRepository<T, ID> getRepository() {
		return repository;
	}

	public Validator<T> getValidator() {
		return null;
	}

	@Autowired
	@Getter(onMethod = @__(@Override))
	MessageService messageService;

	@Override
	public void validate(T t) throws ValidationException {
		Validator<T> validator = getValidator();
		if (validator == null) {
			return;
		}
		BindingResult errors = validator.validate(t);
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
	}

	@Override
	public void validate(T t, BindingResult errors) throws ValidationException {
		Validator<T> validator = getValidator();
		if (validator == null) {
			return;
		}
		BindingResult validationErrors = validator.validate(t);
		errors.addAllErrors(validationErrors);
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
	}

	@Override
	public <S extends T> void validate(Iterable<S> entities) throws ValidationException {
		validate(entities, null);
	}

	@Override
	public <S extends T> void validate(Iterable<S> entities, BindingResult errors) throws ValidationException {
		for (S entity : entities) {
			try {
				validate(entity);
			} catch (ValidationException ex) {
				BindingResult exErrors = ex.getErrors();
				if (errors == null) {
					errors = exErrors;
				} else {
					errors.addAllErrors(exErrors);
				}
			}
		}
		if (errors != null && errors.hasErrors()) {
			throw new ValidationException(errors);
		}
	}

	@Override
	public List<T> findAll() {
		return getRepository().findAll();
	}

	@Override
	public List<T> findAll(Sort sort) {
		return getRepository().findAll(sort);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		return getRepository().findAll(pageable);
	}

	@Override
	public List<T> findAll(Iterable<ID> ids) {
		return getRepository().findAll(ids);
	}

	@Override
	public long count() {
		return getRepository().count();
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public void delete(ID id) {
		getRepository().delete(id);
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public void delete(T t) {
		getRepository().delete(t);
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public void delete(Iterable<? extends T> iterable) {
		getRepository().delete(iterable);
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public void deleteAll() {
		getRepository().deleteAll();
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public <S extends T> S save(S s) throws ValidationException {
		validate(s);
		return getRepository().save(s);
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public <S extends T> List<S> save(Iterable<S> entities) throws ValidationException {
		validate(entities);
		return getRepository().save(entities);
	}

	@Override
	public T findOne(ID id) {
		return getRepository().findOne(id);
	}

	@Override
	public boolean exists(ID id) {
		return getRepository().exists(id);
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public void flush() {
		getRepository().flush();
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public <S extends T> S saveAndFlush(S entity) throws ValidationException {
		validate(entity);
		return getRepository().saveAndFlush(entity);
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public void deleteInBatch(Iterable<T> entities) {
		getRepository().deleteInBatch(entities);
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public void deleteAllInBatch() {
		getRepository().deleteAllInBatch();
	}

	@Override
	public T getOne(ID id) {
		return getRepository().getOne(id);
	}

	@Override
	public T findOne(Predicate predicate) {
		return getRepository().findOne(predicate);
	}

	@Override
	public Iterable<T> findAll(Predicate predicate) {
		return getRepository().findAll(predicate);
	}

	@Override
	public Iterable<T> findAll(Predicate predicate, Sort sort) {
		return getRepository().findAll(predicate, sort);
	}

	@Override
	public Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orderSpecifiers) {
		return getRepository().findAll(predicate, orderSpecifiers);
	}

	@Override
	public Iterable<T> findAll(OrderSpecifier<?>... orderSpecifiers) {
		return getRepository().findAll(orderSpecifiers);
	}

	@Override
	public Page<T> findAll(Predicate predicate, Pageable pageable) {
		return getRepository().findAll(predicate, pageable);
	}

	@Override
	public long count(Predicate predicate) {
		return getRepository().count(predicate);
	}

	@Override
	public boolean exists(Predicate predicate) {
		return getRepository().exists(predicate);
	}

}
