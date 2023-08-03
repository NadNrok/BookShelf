package com.limethecoder.util.converter;

import com.limethecoder.data.domain.*;
import com.limethecoder.data.repository.UserRepository;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ReadingConverter
public class BookReadConverter implements Converter<DBObject, Book> {
    private UserRepository userRepository;

    public BookReadConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Book convert(DBObject source) {
        Book book = new Book();

        book.setId(((ObjectId) source.get("_id")).toString());
        book.setTitle((String) source.get("title"));
        book.setPublishYear((Integer)source.get("publishYear"));
        book.setPagesCnt((Integer)source.get("pagesCnt"));
        book.setDescription((String)source.get("description"));

        Object obj = source.get("coverUrl");
        if(obj != null) {
            book.setCoverUrl((String)obj);
        }

        BasicDBList dbList = (BasicDBList)source.get("genres");

        if (dbList != null && !dbList.isEmpty()) {
            List<String> genres = dbList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            book.setGenres(genres);
        } else {
            book.setGenres(new ArrayList<>());
        }

        Object publisher = source.get("publisher");
        book.setPublisher(convertToPublisher(publisher));

        dbList = (BasicDBList) source.get("authors");

        if(dbList != null) {
            List<Author> authors = dbList.stream()
                    .map(this::convertToAuthor)
                    .collect(Collectors.toList());
            book.setAuthors(authors);
        } else {
            book.setAuthors(new ArrayList<>());
        }

        if(source.get("reviews") != null) {
            dbList = (BasicDBList) source.get("reviews");

            if (dbList != null) {
                List<Review> reviews = dbList.stream()
                        .map(this::convertToReview)
                        .collect(Collectors.toList());
                book.setReviews(reviews);
            } else {
                book.setReviews(new ArrayList<>());
            }
        }

        return book;
    }

    private Publisher convertToPublisher(Object object) {
        if(object == null) {
            return null;
        }

        DBObject source = (DBObject) object;

        Publisher publisher = new Publisher();
        publisher.setName((String)source.get("name"));

        DBObject addressObject = (DBObject) source.get("address");

        if(addressObject != null) {
            Address address = new Address();
            address.setBuilding((String) addressObject.get("building"));
            address.setStreet((String) addressObject.get("street"));
            address.setCity((String) addressObject.get("city"));
            address.setCountry((String) addressObject.get("country"));
            address.setZip((String) addressObject.get("zip"));

            publisher.setAddress(address);
        }

        return publisher;
    }

    private Author convertToAuthor(Object object) {
        if(object == null) {
            return null;
        }

        DBObject source = (DBObject) object;

        Author author = new Author();
        author.setName((String)source.get("name"));
        author.setSurname((String)source.get("surname"));
        author.setBirthDate((Date)source.get("birthdate"));

        return author;
    }

    private Review convertToReview(Object object) {
        if(object == null) {
            return null;
        }

        DBObject source = (DBObject) object;

        Review review = new Review();
        review.setText((String)source.get("text"));
        review.setRate((int)source.get("rate"));

        if(source.get("date") != null) {
            review.setDate((Date) source.get("date"));
        }

        Object userLogin = source.get("user");
        if(userLogin != null) {
            User user = userRepository.findOne((String)userLogin);
            review.setUser(user);
        }

        return review;
    }
}
