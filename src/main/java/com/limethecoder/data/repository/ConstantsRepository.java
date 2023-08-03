package com.limethecoder.data.repository;


import com.limethecoder.data.domain.Constants;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ConstantsRepository extends MongoRepository<Constants, String> {;
}
