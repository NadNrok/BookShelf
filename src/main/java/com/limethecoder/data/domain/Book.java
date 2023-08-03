package com.limethecoder.data.domain;

import com.limethecoder.util.DisplayUtil;
import com.limethecoder.util.validation.ValidImage;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Document
public class Book {
    @Id
    private String id;

    @NotNull
    @NotEmpty
    @Size(min = 4, max=50)
    private String title;

    @NotNull
    @NotEmpty
    @Size(min = 4, max=250)
    private String description;

    @Range
    private int pagesCnt;

    @Range(min = 500, max = 2016)
    private int publishYear;

    private String coverUrl;

    @NotNull
    private Publisher publisher;

    @NotNull
    @NotEmpty
    private List<Author> authors;

    private List<Review> reviews;

    @NotNull
    @NotEmpty
    private List<String> genres;

    @Transient
    @ValidImage
    private MultipartFile cover;

    public MultipartFile getCover() {
        return cover;
    }

    public void setCover(MultipartFile cover) {
        this.cover = cover;
    }

    public double getAverageRate(){
        if(reviews == null) {
            return 0;
        }

        return reviews.stream().mapToInt(Review::getRate)
                .average().getAsDouble();
    }

    public int findRatePercent(int rate) {
        if(reviews == null) {
            return 0;
        }
        long cnt = reviews.stream().filter((x) -> x.getRate() == rate).count();
        return (int)((double)cnt / reviews.size() * 100);
    }

    public int getReviewsCnt() {
        if(reviews == null) {
            return 0;
        }

        return reviews.size();
    }

    public boolean isReviewed(User user) {
        return isReviewed(user.getLogin());
    }

    public boolean isReviewed(String login) {
        if(reviews == null || reviews.isEmpty()) {
            return false;
        }

        for(Review review : reviews) {
            if(review.getUser().getLogin().equals(login)) {
                return true;
            }
        }

        return false;
    }

    public void addReview(Review review) {
        if(review != null && review.getUser() != null
                &&!isReviewed(review.getUser().getLogin())) {
            reviews.add(review);
        }
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPagesCnt() {
        return pagesCnt;
    }

    public void setPagesCnt(int pagesCnt) {
        this.pagesCnt = pagesCnt;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String printAuthors() {
        return DisplayUtil.printList(authors);
    }

    public String printGenres() {
        return DisplayUtil.printList(genres);
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
