package com.limethecoder.data.repository.interfaces;


import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookOperations {
    void deleteReviewsByUser(User user);
    List<Book> findReviewedBooks(User user);
    Page<Book> fullTextSearch(String text, Pageable pageable);
    List<String> findMostRated(int cnt);
}
