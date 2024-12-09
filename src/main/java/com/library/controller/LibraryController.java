package com.library.controller;


import com.library.exception.InvalidAuthorException;
import com.library.exception.InvalidBookException;
import com.library.exception.InvalidISBNException;
import com.library.model.Book;
import com.library.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @PostMapping("/books")
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        if (!isValid(book.getIsbn()) || !isValid(book.getTitle()) || !isValid(book.getAuthor())) {
            throw new InvalidBookException("Book must have a valid ISBN, title, and author.");
        }
        libraryService.addBook(book);
        return new ResponseEntity<String>("{\"isbn\":\"" + book.getIsbn() + "\"}", HttpStatus.CREATED);
    }

    @GetMapping("/books/book/{isbn}")
    public ResponseEntity<Book> findBookByISBN(@PathVariable String isbn) {
        if (!isValid(isbn)) {
            throw new InvalidISBNException("ISBN must not be empty or null.");
        }
        return ResponseEntity.ok(libraryService.findBookByISBN(isbn));
    }

    @GetMapping("/books/{author}")
    public ResponseEntity<List<Book>> findBooksByAuthor(@PathVariable String author) {
        if (!isValid(author)) {
            throw new InvalidAuthorException("Author must not be empty or null.");
        }
        return ResponseEntity.ok(libraryService.findBooksByAuthor(author));
    }

    @PatchMapping("/books/borrow/{isbn}")
    public ResponseEntity<String> borrowBook(@PathVariable String isbn) {
        if (!isValid(isbn)) {
            throw new InvalidISBNException("ISBN must not be empty or null.");
        }
        libraryService.borrowBook(isbn);
        return ResponseEntity.ok("{\"isbn\":\"" + isbn + "\"}");
    }

    @PatchMapping("/books/return/{isbn}")
    public ResponseEntity<String> returnBook(@PathVariable String isbn) {
        if (!isValid(isbn)) {
            throw new InvalidISBNException("ISBN must not be empty or null.");
        }
        libraryService.returnBook(isbn);
        return ResponseEntity.ok("{\"isbn\":\"" + isbn + "\"}");
    }

    @DeleteMapping("/books/remove/{isbn}")
    public ResponseEntity<String> removeBook(@PathVariable String isbn) {
        if (!isValid(isbn)) {
            throw new InvalidISBNException("ISBN must not be empty or null.");
        }
        libraryService.removeBook(isbn);
        return ResponseEntity.ok("{\"isbn\":\"" + isbn + "\"}");
    }

    private boolean isValid(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
