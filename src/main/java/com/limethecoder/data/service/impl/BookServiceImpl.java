package com.limethecoder.data.service.impl;

import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.Like;
import com.limethecoder.data.domain.User;
import com.limethecoder.data.repository.BookRepository;
import com.limethecoder.data.repository.LikeRepository;
import com.limethecoder.data.service.BookService;
import com.limethecoder.data.service.CacheService;
import com.limethecoder.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class BookServiceImpl extends AbstractMongoService<Book, String>
        implements BookService {

    private static final Logger logger = LoggerFactory
            .getLogger(BookServiceImpl.class);

    private final static String DEFAULT_COVER = "default.jpg";
    private final static String BOOKS_CNT = "books_cnt";

    private BookRepository repository;
    private LikeRepository likeRepository;
    private CacheService cacheService;

    @Autowired
    public BookServiceImpl(BookRepository repository,
                           LikeRepository likeRepository,
                           CacheService cacheService) {
        this.repository = repository;
        this.likeRepository = likeRepository;
        this.cacheService = cacheService;
    }

    @Override
    public Book add(Book book) {
        saveCover(book);
        cacheService.onBookInsert(book);
        cacheService.invalidate(BOOKS_CNT);
        return repository.save(book);
    }

    @Override
    public Book update(Book book) {
        saveCover(book);
        cacheService.onBookUpdate(book);
        return repository.save(book);
    }

    @Override
    public void delete(String bookId) {
        Book book = repository.findOne(bookId);
        FileUtil.removeFileIfExists(book.getCoverUrl());

        List<Like> likes = likeRepository.findByBookId(bookId);
        if(likes != null && !likes.isEmpty()) {
            likeRepository.delete(likes);
        }

        cacheService.onBookDelete(book);
        cacheService.invalidate(BOOKS_CNT);

        repository.delete(bookId);
    }

    private void saveCover(Book book) {
        final String COVER_PREFIX = "_cover";
        if(book.getCover() != null && !book.getCover().isEmpty()) {
            String[] parts = book.getCover().getOriginalFilename()
                    .split("\\.");

            String fileExtension = parts[parts.length - 1];
            String filename = book.getId() + COVER_PREFIX +
                    "." + fileExtension;

            FileUtil.saveFile(book.getCover(), filename);
            book.setCoverUrl(filename);
        }
    }

    @Override
    protected MongoRepository<Book, String> getRepository() {
        return repository;
    }

    @Override
    public byte[] loadCover(Book book) {
        String cover = book.getCoverUrl();
        if(FileUtil.isExists(cover)) {
            return FileUtil.loadImage(cover);
        }

        byte[] image = FileUtil.loadImage(DEFAULT_COVER);
        cacheService.addImage(DEFAULT_COVER, image);
        return image;
    }

    @Override
    public List<Book> findReviewedBooks(User user) {
        String key = new StringBuilder(CacheService.USER_KEY)
                .append(CacheService.SEPARATOR)
                .append(user.getLogin())
                .append(CacheService.SEPARATOR)
                .append(CacheService.RATED).toString();

        if(cacheService.exists(key)) {
            logger.info("Cache hit for reviewed books");
            return cacheService.getBooks(key);
        } else {
            List<Book> rated = repository.findReviewedBooks(user);
            cacheService.addBooks(key, rated);
            logger.info("Database hit for reviewed books");
            return rated;
        }
    }

    @Override
    public Page<Book> fullTextSearch(String text, Pageable pageable) {
        StringBuilder cntKey = new StringBuilder(CacheService.BOOKS_KEY);
        cntKey.append("_cnt");
        cntKey.append(CacheService.SEPARATOR);
        cntKey.append(CacheService.QUERY);
        cntKey.append(text);

        if(cacheService.exists(CacheService.BOOKS_KEY,
                pageable.getPageNumber() + 1, text)) {
            List<Book> books = cacheService.getBooks(
                    pageable.getPageNumber() + 1, text);
            long total = Long.valueOf(cacheService.get(cntKey.toString()));
            logger.info("Cache hit for books full text search");
            return new PageImpl<>(books, pageable, total);

        }

        Page<Book> page = repository.fullTextSearch(text, pageable);

        if(page.getTotalElements() != 0) {
            cacheService.addBooks(page.getContent(), pageable.getPageNumber() + 1, text);
            cacheService.add(cntKey.toString(),
                    String.valueOf(page.getTotalElements()));
        }

        logger.info("Database hit for books full text search");
        return page;
    }

    @Override
    public List<Book> findMostRated(int cnt) {
        List<String> ids = repository.findMostRated(cnt);
        if(ids == null) {
            return new ArrayList<>();
        }

        return ids.stream().map(this::findOne).
                collect(Collectors.toList());
    }

    @Override
    public Long count() {
        if(cacheService.exists(BOOKS_CNT)) {
            return Long.valueOf(cacheService.get(BOOKS_CNT));
        } else {
            long cnt = repository.count();
            cacheService.add(BOOKS_CNT, String.valueOf(cnt));
            return cnt;
        }
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        if(cacheService.exists(CacheService.BOOKS_KEY,
                pageable.getPageNumber() + 1, "")) {
            List<Book> books = cacheService.getBooks(
                    pageable.getPageNumber() + 1, "");
            long total = count();
            logger.info("Cache hit for books search all");
            return new PageImpl<>(books, pageable, total);
        }

        Page<Book> page = repository.findAllByOrderByIdAsc(pageable);

        if(page.getTotalElements() != 0) {
            cacheService.addBooks(page.getContent(), pageable.getPageNumber() + 1, "");
            cacheService.add(BOOKS_CNT, String.valueOf(page.getTotalElements()));
            logger.info("Database hit for books search all");
        }

        return page;
    }
}
