package com.limethecoder.data.repository;

import com.limethecoder.data.domain.Book;
import com.limethecoder.data.repository.interfaces.BookOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface BookRepository extends MongoRepository<Book, String>, BookOperations {
    Page<Book> findAllByOrderByIdAsc(Pageable pageable);
    Page<Book> findAllByOrderByIdAsc(Pageable pageable, String text);
}
