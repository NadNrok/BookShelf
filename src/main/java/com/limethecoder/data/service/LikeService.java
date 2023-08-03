package com.limethecoder.data.service;


import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.Like;

import java.util.List;

public interface LikeService extends Service<Like,String> {
    Like findLike(String userId, String bookId);
    boolean isLiked(String userId, String bookId);
    void delete(String userId, String bookId);
    Like like(String userId, String bookId);
    List<Like> findByUserId(String userId);
    List<Like> findByBookId(String bookId);
    List<Book> findLikedBooks(String userId);
}
