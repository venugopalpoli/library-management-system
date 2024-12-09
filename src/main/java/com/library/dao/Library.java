package com.library.dao;

import com.library.exception.BookAlreadyExistException;
import com.library.exception.BookNotFoundException;
import com.library.exception.NoCopiesAvailableException;
import com.library.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Library {

    private final Map<String, Book> books;

    public void addBook(final Book book) {
        if (books.put(book.getIsbn(), book) != null) {
            throw new BookAlreadyExistException("Book already exists with this isbn : " + book.getIsbn());
        }
    }

    public void removeBook(final String iSBN) {
        final Book book = books.remove(iSBN);
        if (book == null) {
            throw new BookNotFoundException("No book is available with this isbn : " + iSBN);
        }
    }

    public Book findBookByISBN(final String iSBN) {
        final Book book = books.get(iSBN);
        if (book == null) {
            throw new BookNotFoundException("No book is available with this isbn : " + iSBN);
        }
        return book;
    }

    public List<Book> findBooksByAuthor(final String author) {
        final List<Book> bookList = books.values().stream().filter(book -> book.getAuthor().equalsIgnoreCase(author)).toList();
        if (ObjectUtils.isEmpty(bookList)) {
            throw new BookNotFoundException("No book is available with this author : " + author);
        }
        return bookList;
    }

    public void borrowBook(final String iSBN) {
        Book book = books.get(iSBN);
        if (book == null) {
            throw new BookNotFoundException("No book is available with this isbn : " + iSBN);
        }

        if (book.getAvailableCopies() > 0) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
        } else {
            throw new NoCopiesAvailableException("No copies of the book is available");
        }
    }

    public void returnBook(final String iSBN) {
        final Book book = books.get(iSBN);
        if (book == null) {
            throw new BookNotFoundException("No book is available with this isbn : " + iSBN);
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }
}
