package com.limethecoder.data.repository;


import com.limethecoder.data.domain.Like;
import com.limethecoder.data.repository.interfaces.CustomOperations;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LikeRepository extends MongoRepository<Like, String>, CustomOperations<Like> {
    List<Like> findByUserId(String userId);
    List<Like> findByBookId(String bookId);
}
