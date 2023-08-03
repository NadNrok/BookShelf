package com.limethecoder.data.repository.interfaces;


public interface CustomOperations <T> {
    T findOne(String userId, String bookId);
    boolean exists(String userId, String bookId);
    void delete(String userId, String bookId);
}
