package com.limethecoder.controller;


import com.limethecoder.data.domain.*;
import com.limethecoder.data.service.BookService;
import com.limethecoder.data.service.CacheService;
import com.limethecoder.data.service.LikeService;
import com.limethecoder.data.service.UserService;
import com.limethecoder.util.generator.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@PropertySource("classpath:app.properties")
@RequestMapping("/")
public class MainController {

    private final static int PAGE_SIZE = 18;
    private final static int PAGES_ON_VIEW = 5;

    private static final Logger logger = LoggerFactory
            .getLogger(MainController.class);

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private Generator generator;

    @Resource
    private Environment env;

    @RequestMapping(method = GET)
    public String home(@RequestParam(name = "page", defaultValue = "1") int pageNumber,
                       @RequestParam(name = "q", defaultValue = "") String query,
                       Model model) {

        if(pageNumber > 0) {
            Pageable pageable = new PageRequest(pageNumber - 1, PAGE_SIZE);
            Page<Book> page = query.isEmpty() ? bookService.findAll(pageable) :
                    bookService.fullTextSearch(query, pageable);

            if(!query.isEmpty()) {
                model.addAttribute("query", query);
            }

            if(page.getTotalElements() == 0) {
                model.addAttribute("error", "No books for your request in database");
                return "home";
            }

            int begin = Math.max(1, pageNumber - PAGES_ON_VIEW / 2);
            int end = Math.min(begin + PAGES_ON_VIEW - 1, page.getTotalPages());

            if(pageNumber > end) {
                model.addAttribute("message", "Page number out of range");
                return "error";
            }

            if(end - pageNumber < PAGES_ON_VIEW / 2) {
                begin = Math.max(1, end - PAGES_ON_VIEW + 1);
            }

            model.addAttribute("current", pageNumber);
            model.addAttribute("begin", begin);
            model.addAttribute("end", end);
            model.addAttribute("books", page);

        } else {
            model.addAttribute("message", "Page number can't be less that 1");
            return "error";
        }

        return "home";
    }

    @RequestMapping(value = "gen", method = GET)
    public String generate(@RequestParam(name = "size", defaultValue = "25") int size,
                           Model model) {
        generator.generateBunchOfBooks(size);
        model.addAttribute("message", "Books inserted");
        return "error";
    }

    @RequestMapping(value = "user/{login}", method = GET)
    public String userPage(@PathVariable String login, Model model) {
        User user = userService.findOne(login);
        if(user == null) {
            model.addAttribute("message", "No user with login " + login);
            return "error";
        }
        List<Book> liked = likeService.findLikedBooks(user.getLogin());
        if(liked != null) {
            model.addAttribute("liked", liked);
        }

        List<Book> reviewed = bookService.findReviewedBooks(user);
        if(reviewed != null) {
            model.addAttribute("reviewed", reviewed);
        }

        model.addAttribute("user", user);
        return "user_page";
    }

    @RequestMapping(value = "book/{id}", method = GET)
    public String bookPage(@PathVariable String id, Model model,
                           Principal principal) {
        Book book = bookService.findOne(id);
        model.addAttribute(book);
        model.addAttribute("newReview", new Review());
        if(principal != null) {
            model.addAttribute("isLiked", likeService.isLiked(
                    principal.getName(), id));
        }
        return "book_page";
    }

    @RequestMapping(value = "book/{id}", method = POST)
    public String addReview(@PathVariable String id,
                            @ModelAttribute("newReview") Review review,
                            Model model, Principal principal) {
        Book book = bookService.findOne(id);
        User user = userService.findOne(principal.getName());
        review.setUser(user);
        review.setDate(new Date());

        if(book.getReviews() == null) {
            book.setReviews(new ArrayList<>());
        }

        book.getReviews().add(review);
        bookService.update(book);
        model.addAttribute(book);

        return "book_page";
    }

    @RequestMapping(value = "top", method = GET)
    public String topRated(Model model) {
        model.addAttribute("books", bookService.findMostRated(PAGE_SIZE));
        return "top_rated";
    }

    @RequestMapping(value = "book/{id}/like", method = GET)
    public @ResponseBody String likeHandler(@PathVariable String id,
                                            Principal principal) {
        if(principal == null) {
            return "";
        }

        if(likeService.isLiked(principal.getName(), id)) {
            likeService.delete(principal.getName(), id);
        } else {
            likeService.like(principal.getName(), id);
        }

        return "{'response' : 'success'}";
    }

    @RequestMapping(value = "admin/stats", method = GET)
    public String statsPage(Model model, Principal principal,
                            @RequestParam(name = "clear", defaultValue = "")
                                    String clear,
                            @RequestParam(name="dump", defaultValue = "")
                            String dump) {
        if(!clear.isEmpty()) {
            cacheService.invalidateCache();
            return "redirect:/admin/stats";
        }

        if(!dump.isEmpty()) {
            try {
                String command = String.format("mongodump --host %s --port %s --db %s --out %s",
                        env.getProperty("mongo.host"),
                        env.getProperty("mongo.port"),
                        env.getProperty("mongo.db"),
                        env.getProperty("dump.storage"));
                logger.info("Command: " + command);
                Process process = Runtime.getRuntime().exec(command);
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    logger.error("In db dump", e);
                }
            } catch (IOException e) {
                logger.error("Error occurred during db dump", e);
            }
            logger.info("Database dumped successfully");
            return "redirect:/admin/stats";
        }

        model.addAttribute("cache", cacheService.getCache());
        testCacheSpeed(principal.getName(), model);
        return "stats";
    }

    private void testCacheSpeed(String login, Model model) {
        if(cacheService.exists("dbTime") && cacheService.exists("cacheTime")) {
            model.addAttribute("dbTime", cacheService.get("dbTime"));
            model.addAttribute("cacheTime", cacheService.get("cacheTime"));
            return;
        }

        cacheService.invalidateUserKeys(login);
        User user = userService.findOne(login);

        long startTime = System.currentTimeMillis();
        bookService.findReviewedBooks(user);
        likeService.findLikedBooks(login);
        long dbEndTime = System.currentTimeMillis();
        bookService.findReviewedBooks(user);
        likeService.findLikedBooks(login);
        long cacheEndTime = System.currentTimeMillis();

        model.addAttribute("dbTime", dbEndTime - startTime);
        model.addAttribute("cacheTime", cacheEndTime - dbEndTime);

        cacheService.add("dbTime",
                String.valueOf(dbEndTime - startTime));
        cacheService.add("cacheTime",
                String.valueOf(cacheEndTime - dbEndTime));
    }

    @RequestMapping(value = "getCover/{id}")
    @ResponseBody
    public byte[] getBookCover(@PathVariable String id) {
        Book book = bookService.findOne(id);
        if(book == null) {
            return new byte[]{};
        }
        return bookService.loadCover(book);
    }

    @RequestMapping(value = "getIcon/{login}")
    @ResponseBody
    public byte[] getUserIcon(@PathVariable String login) {
        User user = userService.findOne(login);
        if(user == null) {
            return new byte[]{};
        }
        return userService.loadImage(user);
    }
}
