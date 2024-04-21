package com.customer.persistence.model;

import jakarta.persistence.*;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id")
    private UUID id;

    @Column(name = "book_id")
    @Setter
    private UUID bookId;

    @Column(name = "book_name")
    @Setter
    private String bookName;

    @Column(name = "author_name")
    @Setter
    private String authorName;

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
