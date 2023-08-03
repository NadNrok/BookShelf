package com.limethecoder.controller;

import com.limethecoder.data.domain.Address;
import com.limethecoder.data.domain.Author;
import com.limethecoder.data.domain.Book;
import com.limethecoder.data.domain.Publisher;
import com.limethecoder.data.service.BookService;
import com.limethecoder.data.service.ConstantsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Controller
@RequestMapping("/admin/books")
public class BookController {

    private final static int PAGE_SIZE = 18;
    private final static int PAGES_ON_VIEW = 5;

    private BookService bookService;
    private ConstantsService constantsService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));

    }

    @Autowired
    public BookController(BookService bookService,
                          ConstantsService constantsService) {
        this.bookService = bookService;
        this.constantsService = constantsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String bookList(@RequestParam(name = "page", defaultValue = "1")
                                   int pageNumber, Model model,
                           @RequestParam(name = "q", defaultValue = "") String query) {
        if(pageNumber > 0) {
            Pageable pageable = new PageRequest(pageNumber - 1, PAGE_SIZE);

            Page<Book> page = query.isEmpty() ? bookService.findAll(pageable) :
                    bookService.fullTextSearch(query, pageable);

            if(!query.isEmpty()) {
                model.addAttribute("query", query);
            }

            if(page.getTotalElements() == 0) {
                model.addAttribute("error", "No books for your request in database");
                return "books";
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

        return "books";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String bookDetail(@PathVariable String id, Model model) {
        Book book = bookService.findOne(id);
        if(book.getAuthors().size() < 2 ) {
            book.getAuthors().add(new Author());
        }

        model.addAttribute("editType", true);
        model.addAttribute("book", book);
        model.addAttribute("genres", constantsService.
                getConstantsByType(ConstantsService.GENRE_TYPES));
        return "book_form";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String showBookForm(Model model) {
        Book book = new Book();
        Publisher publisher = new Publisher();
        publisher.setAddress(new Address());
        book.setPublisher(publisher);
        book.setAuthors(Arrays.asList(new Author(), new Author()));

        model.addAttribute("book", book);
        model.addAttribute("genres", constantsService.
                getConstantsByType(ConstantsService.GENRE_TYPES));
        return "book_form";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String addBook(@ModelAttribute("book") @Valid Book book, Model model,
                          BindingResult result) {
        if (book.getAuthors().get(0).getName().isEmpty()) {
            result.rejectValue("authors", "", "No authors provided");
        } else if(book.getAuthors().get(1).getName().isEmpty()) {
            book.getAuthors().remove(1);
        }

        if(!result.hasErrors()) {
            bookService.add(book);
            return "redirect:/admin/books";
        }

        model.addAttribute("book", book);
        model.addAttribute("genres", constantsService.
                getConstantsByType(ConstantsService.GENRE_TYPES));

        return "book_form";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = "delete_btn")
    public String deleteUser(@PathVariable String id, Model model) {
        if (bookService.findOne(id) == null) {
            model.addAttribute("message", "No book with id " + id);
            return "error";
        }

        bookService.delete(id);
        return "redirect:/admin/books";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = "submit_btn")
    public String editBook(@ModelAttribute("book") @Valid Book book, Model model,
                          BindingResult result) {
        if (book.getAuthors().get(0).getName().isEmpty()) {
            result.rejectValue("authors", "", "No authors provided");
        } else if(book.getAuthors().get(1).getName().isEmpty()) {
            book.getAuthors().remove(1);
        }

        if(!result.hasErrors()) {
            Book old = bookService.findOne(book.getId());
            book.setReviews(old.getReviews());
            bookService.update(book);
            return "redirect:/admin/books";
        }

        model.addAttribute("book", book);
        model.addAttribute("editType", true);
        model.addAttribute("genres", constantsService.
                getConstantsByType(ConstantsService.GENRE_TYPES));

        return "book_form";
    }
}
