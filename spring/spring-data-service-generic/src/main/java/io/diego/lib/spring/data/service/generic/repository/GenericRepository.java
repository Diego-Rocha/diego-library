package io.diego.lib.spring.data.service.generic.repository;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;

@NoRepositoryBean
public interface GenericRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, QueryDslPredicateExecutor<T> {

	EntityManager getEntityMananger();

	void setCleanIdStateOnSave(boolean cleanIdStateOnSave);

	boolean isCleanIdStateOnSave();

	<Y> void applyPagination(JPQLQuery<Y> query, Pageable pageable);

	<Y> Page<Y> getPage(JPQLQuery<Y> query, Pageable pageable);
}
