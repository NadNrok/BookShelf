package com.limethecoder.data.service;


import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService extends Service<Book, String> {
    byte[] loadCover(Book book);
    List<Book> findReviewedBooks(User user);
    Page<Book> fullTextSearch(String text, Pageable pageable);
    List<Book> findMostRated(int cnt);
}
