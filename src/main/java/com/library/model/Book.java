package com.library.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private int availableCopies;
}
