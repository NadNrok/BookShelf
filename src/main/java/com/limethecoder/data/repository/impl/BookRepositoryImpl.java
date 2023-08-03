package com.limethecoder.data.repository.impl;


import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.User;
import com.limethecoder.data.repository.interfaces.BookOperations;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.query.Criteria.*;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.core.query.Update;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BookRepositoryImpl implements BookOperations {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void deleteReviewsByUser(User user) {
        Query query = new Query(where("reviews.user").is(user.getLogin()));
        Update update = new Update().pull("reviews", new BasicDBObject("user", user.getLogin()));
        mongoTemplate.updateFirst(query, update, Book.class);
    }

    @Override
    public List<Book> findReviewedBooks(User user) {
        Query query = new Query(where("reviews.user").is(user.getLogin()));
        return mongoTemplate.find(query, Book.class);
    }

    @Override
    public Page<Book> fullTextSearch(String text, Pageable pageable) {
        Query query = TextQuery.queryText(new TextCriteria().matchingPhrase(text)).sortByScore();
        long total = mongoTemplate.count(query, Book.class);
        List<Book> content = mongoTemplate.find(query.with(pageable), Book.class);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<String> findMostRated(int cnt) {
        Aggregation aggregation = newAggregation(
                unwind("reviews"),
                group("_id").avg("reviews.rate").as("rate_avg")
                        .count().as("reviews_cnt"),
                sort(Sort.Direction.DESC, "rate_avg", "reviews_cnt"),
                limit(cnt),
                project("_id")
        );
        System.out.println(aggregation.toString());
        AggregationResults<String> res = mongoTemplate
                .aggregate(aggregation, "book", String.class);
        List<String> ids = res.getMappedResults();

        if(ids != null && !ids.isEmpty()) {
            return ids.stream().map((x) -> {
                String[] part = x.split("\"");
                return part[part.length - 2];
            }
            ).collect(Collectors.toList());
        }

        return null;
    }
}
