package com.library.controller;


import com.library.model.Book;
import com.library.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @PostMapping("/books")
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        libraryService.addBook(book);
        return new ResponseEntity<String>("{\"isbn\":\"" + book.getIsbn() + "\"}", HttpStatus.CREATED);
    }

    @GetMapping("/books/book/{isbn}")
    public ResponseEntity<Book> findBookByISBN(@PathVariable String isbn) {
        return ResponseEntity.ok(libraryService.findBookByISBN(isbn));
    }

    @GetMapping("/books/{author}")
    public ResponseEntity<List<Book>> findBooksByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(libraryService.findBooksByAuthor(author));
    }

    @PatchMapping("/books/borrow/{isbn}")
    public ResponseEntity<String> borrowBook(@PathVariable String isbn) {
        libraryService.borrowBook(isbn);
        return ResponseEntity.ok("{\"isbn\":\"" + isbn + "\"}");
    }

    @PatchMapping("/books/return/{isbn}")
    public ResponseEntity<String> returnBook(@PathVariable String isbn) {
        libraryService.returnBook(isbn);
        return ResponseEntity.ok("{\"isbn\":\"" + isbn + "\"}");
    }

    @DeleteMapping("/books/remove/{isbn}")
    public ResponseEntity<String> removeBook(@PathVariable String isbn) {
        libraryService.removeBook(isbn);
        return ResponseEntity.ok("{\"isbn\":\"" + isbn + "\"}");
    }
}
