package com.limethecoder.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface Service<T, ID> {
    T findOne(ID id);
    Page<T> findAll(Pageable pageable);
    T add(T entity);
    void delete(ID id);
    T update(T entity);
    Long count();
    List<T> findAll();
    boolean exists(ID id);
}
