package com.limethecoder.util.converter;

import com.limethecoder.data.domain.Author;
import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.Publisher;
import com.limethecoder.data.domain.Review;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.List;
import java.util.stream.Collectors;

@WritingConverter
public class BookWriteConverter implements Converter<Book, DBObject> {

    @Override
    public DBObject convert(Book book) {
        DBObject dbo = new BasicDBObject();
        if(book.getId() != null && !book.getId().isEmpty()) {
            dbo.put("_id", new ObjectId(book.getId()));
        }

        dbo.put("title", book.getTitle());
        dbo.put("genres", book.getGenres());
        dbo.put("publishYear", book.getPublishYear());
        dbo.put("pagesCnt", book.getPagesCnt());
        dbo.put("_class", "com.limethecoder.data.domain.Book");
        dbo.put("description", book.getDescription());
        dbo.put("publisher", convertPublisher(book.getPublisher()));

        List<DBObject> authors = book.getAuthors().stream()
                .map(this::convertAuthor)
                .collect(Collectors.toList());
        dbo.put("authors", authors);

        if(book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            dbo.put("coverUrl", book.getCoverUrl());
        }

        if(book.getReviews() != null && !book.getReviews().isEmpty()) {
            List<DBObject> reviews = book.getReviews().stream()
                    .map(this::convertReview)
                    .collect(Collectors.toList());
            dbo.put("reviews", reviews);
        }

        return dbo;
    }

    private DBObject convertAuthor(Author author) {
        DBObject dbo = new BasicDBObject();
        dbo.put("name", author.getName());
        dbo.put("surname", author.getSurname());
        dbo.put("birthdate", author.getBirthDate());
        return dbo;
    }

    private DBObject convertPublisher(Publisher publisher) {
        DBObject dbo = new BasicDBObject();
        dbo.put("name", publisher.getName());

        DBObject address = new BasicDBObject();
        address.put("building", publisher.getAddress().getBuilding());
        address.put("city", publisher.getAddress().getCity());
        address.put("street", publisher.getAddress().getStreet());
        address.put("country", publisher.getAddress().getCountry());
        address.put("zip", publisher.getAddress().getZip());

        dbo.put("address", address);

        return dbo;
    }

    private DBObject convertReview(Review review) {
        DBObject dbo = new BasicDBObject();
        dbo.put("text", review.getText());
        dbo.put("date", review.getDate());
        dbo.put("user", review.getUser().getLogin());
        dbo.put("rate", review.getRate());
        return dbo;
    }
}
