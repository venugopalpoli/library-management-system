package com.library.service;

import com.library.dao.Library;
import com.library.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final Library library;

    @CachePut(value = "books")
    public void addBook(final Book book) {
        library.addBook(book);
    }

    @CacheEvict(value = "books", key = "#isbn")
    public void removeBook(final String isbn) {
        library.removeBook(isbn);
    }

    @CachePut(value = "books", key = "#isbn")
    public Book findBookByISBN(final String isbn) {
        return library.findBookByISBN(isbn);
    }

    @CachePut(value = "books", key = "#author")
    public List<Book> findBooksByAuthor(final String author) {
        return library.findBooksByAuthor(author);
    }

    @CachePut(value = "books", key = "#isbn")
    public void borrowBook(final String isbn) {
        library.borrowBook(isbn);
    }

    @CachePut(value = "books", key = "#isbn")
    public void returnBook(final String isbn) {
        library.returnBook(isbn);
    }
}
