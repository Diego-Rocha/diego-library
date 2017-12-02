package io.diego.lib.spring.data.service.generic.repository;

import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class GenericRepositoryImpl<T, ID extends Serializable> extends QueryDslJpaRepository<T, ID> implements GenericRepository<T, ID> {

	protected final EntityManager entityManager;
	protected final JpaEntityInformation entityInformation;
	private PathBuilder<T> pathBuilder;
	private boolean cleanIdStateOnSave = true;

	public GenericRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
		this.entityInformation = entityInformation;
	}

	public GenericRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager, EntityPathResolver resolver) {
		super(entityInformation, entityManager, resolver);
		this.entityManager = entityManager;
		this.entityInformation = entityInformation;
	}

	@Override
	public EntityManager getEntityMananger() {
		return entityManager;
	}

	@Override
	public void setCleanIdStateOnSave(boolean cleanIdStateOnSave) {
		this.cleanIdStateOnSave = cleanIdStateOnSave;
	}

	@Override
	public boolean isCleanIdStateOnSave() {
		return cleanIdStateOnSave;
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public <S extends T> S save(S entity) {
		if (isCleanIdStateOnSave()) {
			cleanIdState(entity);
		}
		return super.save(entity);
	}

	private void cleanIdState(T entity) {
		ID id = (ID) entityInformation.getId(entity);
		if (id == null) {
			return;
		}
		if (entityInformation.hasCompositeId()) {
			return;
		}
		/**
		 * BUG do Spring? entityInformation.hasCompositeId() == false para @EmbeddedId! a solução "ótima" seria realizar reflection buscando pela anotação @EmbeddedId porém seria muito custosa essa verificação por isto foi optado por verificar se o mesmo é uma instancia de Number ou primitivo (int,long, etc...) pois é mais rápido
		 */
		if (!(id instanceof Number) && !(entityInformation.getIdType().isPrimitive())) {
			return;
		}
		if (!exists(id)) {
			Object nullValue = null;
			if (entityInformation.getIdType().equals(int.class)) {
				nullValue = 0;
			}
			if (entityInformation.getIdType().equals(long.class)) {
				nullValue = 0l;
			}
			setEntityId(entity, nullValue);
		}
	}

	private void setEntityId(T entity, Object id) {
		DirectFieldAccessFallbackBeanWrapper beanWrapper = new DirectFieldAccessFallbackBeanWrapper(entity);
		beanWrapper.setPropertyValue(entityInformation.getIdAttribute().getName(), id);
	}

	@Override
	public <Y> void applyPagination(JPQLQuery<Y> query, Pageable pageable) {
		Querydsl querydsl = new Querydsl(entityManager, getPathBuilder());
		querydsl.applyPagination(pageable, query);
	}

	@Override
	public <Y> Page<Y> getPage(JPQLQuery<Y> query, Pageable pageable) {
		long count = query.fetchCount();
		applyPagination(query, pageable);
		return new PageImpl<>(query.fetch(), pageable, count);
	}

	private PathBuilder<T> getPathBuilder() {
		if (pathBuilder == null) {
			pathBuilder = new PathBuilderFactory().create(entityInformation.getJavaType());
		}
		return pathBuilder;
	}

	@Override
	@Transactional(
			rollbackFor = Throwable.class)
	public void delete(T entity) {
		ID id = (ID) entityInformation.getId(entity);
		entity = findOne(id);
		super.delete(entity);
	}
}
