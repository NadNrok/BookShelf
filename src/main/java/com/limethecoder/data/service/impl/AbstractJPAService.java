package com.limethecoder.data.service.impl;

import com.limethecoder.data.service.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractJPAService<T, ID extends Serializable> implements Service<T, ID> {

    @Override
    public T findOne(ID id) {
        return getRepository().findOne(id);
    }

    @Override
    public Long count() {
        return getRepository().count();
    }

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    public T add(T entity) {
        return getRepository().saveAndFlush(entity);
    }

    @Override
    public T update(T entity) {
        return getRepository().saveAndFlush(entity);
    }

    @Override
    public void delete(ID id) {
        getRepository().delete(id);
    }

    @Override
    public boolean exists(ID id) {
        return getRepository().exists(id);
    }

    protected abstract JpaRepository<T, ID> getRepository();
}

