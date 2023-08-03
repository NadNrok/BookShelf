package com.limethecoder.data.service.impl;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.User;
import com.limethecoder.data.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl implements CacheService {

    private static final Logger logger = LoggerFactory
            .getLogger(CacheServiceImpl.class);

    private JedisPool jedisPool;
    private Gson gson = new Gson();

    @Autowired
    public CacheServiceImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void addBooks(List<Book> books, int page, String query) {
        String data = gson.toJson(books);
        String key = buildKey(BOOKS_KEY, page, query);

        try (Jedis jedis = jedisPool.getResource()) {
            if (query.isEmpty()) {
                String rangeKey = RANGE + key;
                String range = books.get(0).getId() + SEPARATOR +
                        books.get(books.size() - 1).getId();
                jedis.set(rangeKey, range);
                jedis.expire(rangeKey, EXPIRE_TIME);
            }

            jedis.set(key, data);
            jedis.expire(key, EXPIRE_TIME);
        }
    }

    @Override
    public void addBooks(String key, List<Book> books) {
        String data = gson.toJson(books);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, data);
            jedis.expire(key, EXPIRE_TIME);
        }
    }

    @Override
    public void addUser(User user) {
        String data = gson.toJson(user);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(USER_KEY + SEPARATOR + user.getLogin(), data);
            jedis.expire(USER_KEY + SEPARATOR + user.getLogin(), EXPIRE_TIME);
        }
    }

    @Override
    public void addImage(String key, byte[] image) {
        String data = gson.toJson(image);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, data);
            jedis.expire(key, EXPIRE_TIME);
        }
    }

    @Override
    public void add(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            jedis.expire(key, EXPIRE_TIME);
        }
    }

    @Override
    public List<Book> getBooks(int page, String query) {
        String key = buildKey(BOOKS_KEY, page, query);
        return getBooks(key);
    }

    @Override
    public List<Book> getBooks(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String data = jedis.get(key);
            Type type = new TypeToken<List<Book>>() {
            }.getType();

            jedis.expire(key, EXPIRE_TIME);
            return gson.fromJson(data, type);
        }
    }

    @Override
    public User getUser(String login) {
        try (Jedis jedis = jedisPool.getResource()) {
            String data = jedis.get(USER_KEY + SEPARATOR + login);
            return gson.fromJson(data, User.class);
        }
    }

    @Override
    public byte[] getImage(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String data = jedis.get(key);
            return gson.fromJson(data, byte[].class);
        }
    }

    @Override
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, EXPIRE_TIME);
            return jedis.get(key);
        }
    }

    @Override
    public boolean exists(String typeKey, int page, String query) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = buildKey(typeKey, page, query);
            return jedis.exists(key);
        }
    }

    @Override
    public boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    @Override
    public boolean userExists(String login) {
        String key = USER_KEY + SEPARATOR + login;
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("*");
            if (keys == null || keys.isEmpty()) {
                return false;
            }

            keys = keys.stream().filter((x) -> x.startsWith(USER_KEY + SEPARATOR + login))
                    .collect(Collectors.toSet());

            if (keys == null || keys.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void invalidateCache() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    @Override
    public void invalidate(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    @Override
    public void invalidateUserKeys(String login) {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("*");
            if (keys == null || keys.isEmpty()) {
                return;
            }

            keys = keys.stream().filter((x) -> x.startsWith(USER_KEY + SEPARATOR + login))
                    .collect(Collectors.toSet());

            for(String key : keys) {
                jedis.del(key);
            }
        }
    }


    @Override
    public void onBookUpdate(Book book) {
        invalidateBooksQueryCache();
        invalidateUsersBooks(book);

        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("*");
            if (keys == null || keys.isEmpty()) {
                return;
            }

            keys = keys.stream().filter((x) -> x.startsWith("books&"))
                    .collect(Collectors.toSet());

            String pageKey = null;

            for (String key : keys) {
                if(!jedis.exists(RANGE + key)) {
                    continue;
                }

                String data = jedis.get(RANGE + key);
                String[] range = data.split(SEPARATOR);
                if (book.getId().compareTo(range[0]) >= 0 &&
                        book.getId().compareTo(range[1]) <= 0) {
                    pageKey = key;
                    break;
                }
            }


            logger.info("Update " + pageKey);

            if (pageKey == null) {
                return;
            }

            invalidate(pageKey);
            invalidate(RANGE + pageKey);
        }
    }

    @Override
    public void onBookDelete(Book book) {
        invalidateBooksQueryCache();
        invalidateUsersBooks(book);

        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> all = jedis.keys("*");
            if (all == null || all.isEmpty()) {
                return;
            }

            List<String> keys = all.stream().filter((x) -> x.startsWith("books&"))
                    .sorted()
                    .collect(Collectors.toList());


            boolean isFound = false;
            for (String key : keys) {
                if(!jedis.exists(RANGE + key)) {
                    continue;
                }

                String data = jedis.get(RANGE + key);
                String[] range = data.split(SEPARATOR);

                if (!isFound) {
                    if (book.getId().compareTo(range[0]) >= 0 &&
                            book.getId().compareTo(range[1]) <= 0) {
                        isFound = true;
                    }
                }

                if (isFound) {
                    invalidate(key);
                    invalidate(RANGE + key);

                    logger.info("Delete " + key);
                }
            }
        }
    }

    private void invalidateUsersBooks(Book book) {
        invalidateUsersBooks(book, "liked");
        invalidateUsersBooks(book, "rated");
    }

    private void invalidateUsersBooks(Book book, String type) {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> all = jedis.keys("*");
            if (all == null || all.isEmpty()) {
                return;
            }

            List<String> userKeys = all.stream()
                    .filter((x) -> x.matches("^user&(.)+&" + type + "$"))
                    .collect(Collectors.toList());

            if (userKeys != null && !userKeys.isEmpty()) {
                for (String key : userKeys) {
                    List<Book> books = getBooks(key);
                    if (books.contains(book)) {
                        invalidateUserKeys(key.split(SEPARATOR)[1]);
                    }
                }
            }
        }
    }

    @Override
    public void onBookInsert(Book book) {
        invalidateBooksQueryCache();

        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> all = jedis.keys("*");
            if (all == null || all.isEmpty()) {
                return;
            }

            List<String> keys = all.stream()
                    .filter((x) -> x.startsWith("books&"))
                    .sorted()
                    .collect(Collectors.toList());

            if (keys == null || keys.isEmpty()) {
                return;
            }

            String key = keys.get(keys.size() - 1);

            logger.info("Insertion key: " + key);
            invalidate(key);
            invalidate(RANGE + key);
        }
    }

    @Override
    public Map<String, String> getCache() {
        Map<String, String> map = new HashMap<>();

        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("*");
            if(keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    map.put(key, jedis.get(key));
                }
            }
        }

        return map;
    }


    private String buildKey(String typeKey, int page, String query) {
        StringBuilder key = new StringBuilder(typeKey)
                .append(SEPARATOR)
                .append(PAGE)
                .append(page);

        if(!query.isEmpty()) {
            key.append(SEPARATOR);
            key.append(QUERY);
            key.append(query);
        }

        return key.toString();
    }

    private void invalidateBooksQueryCache() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("*");
            Predicate<String> predicate = (x) -> x.startsWith("books")
                    && x.contains("q");
            if (keys != null && !keys.isEmpty()) {
                keys = keys.stream().filter(predicate)
                        .collect(Collectors.toSet());
                for (String key : keys) {
                    jedis.del(key);
                }
            }
        }
    }

    private int keyToPage(String key) {
        String [] parts = key.split("=");
        return Integer.valueOf(parts[parts.length - 1]);
    }
}
