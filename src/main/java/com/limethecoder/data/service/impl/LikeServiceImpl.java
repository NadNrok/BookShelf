package com.limethecoder.data.service.impl;

import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.Like;
import com.limethecoder.data.repository.BookRepository;
import com.limethecoder.data.repository.LikeRepository;
import com.limethecoder.data.repository.UserRepository;
import com.limethecoder.data.service.CacheService;
import com.limethecoder.data.service.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class LikeServiceImpl extends AbstractMongoService<Like, String>
        implements LikeService {

    private static final Logger logger = LoggerFactory
            .getLogger(LikeServiceImpl.class);

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CacheService cacheService;

    @Override
    public Like findLike(String userId, String bookId) {
        return likeRepository.findOne(userId, bookId);
    }

    @Override
    public boolean isLiked(String userId, String bookId) {
        return likeRepository.exists(userId, bookId);
    }

    @Override
    public void delete(String userId, String bookId) {
        if(!userRepository.exists(userId) ||
                !bookRepository.exists(bookId)) {
            return;
        }

        if(cacheService.userExists(userId)) {
            cacheService.invalidateUserKeys(userId);
        }

        likeRepository.delete(userId, bookId);
    }

    @Override
    public Like like(String userId, String bookId) {
        if(!userRepository.exists(userId) ||
                !bookRepository.exists(bookId) ||
                isLiked(userId, bookId)) {
            return null;
        }

        if(cacheService.userExists(userId)) {
            cacheService.invalidateUserKeys(userId);
        }

        return likeRepository.insert(new Like(userId, bookId));
    }

    @Override
    public List<Like> findByUserId(String userId) {
        return likeRepository.findByUserId(userId);
    }

    @Override
    public List<Like> findByBookId(String bookId) {
        return likeRepository.findByBookId(bookId);
    }

    @Override
    public List<Book> findLikedBooks(String userId) {
        String key = new StringBuilder(CacheService.USER_KEY)
                .append(CacheService.SEPARATOR)
                .append(userId)
                .append(CacheService.SEPARATOR)
                .append(CacheService.LIKED).toString();

        if(cacheService.exists(key)) {
            logger.info("Cache hit for liked books");
            return cacheService.getBooks(key);
        } else {
            List<Like> likes = findByUserId(userId);
            if (likes == null || likes.isEmpty()) {
                return null;
            }

            List<Book> liked = likes.stream().map((x) -> bookRepository
                    .findOne(x.getBookId()))
                    .collect(Collectors.toList());
            cacheService.addBooks(key, liked);
            logger.info("Database hit for liked books");
            return liked;
        }
    }

    @Override
    protected MongoRepository<Like, String> getRepository() {
        return likeRepository;
    }
}
