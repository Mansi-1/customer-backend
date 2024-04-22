package com.customer.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
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
    @Getter
    private UUID bookId;

    @Column(name = "book_name")
    @Setter
    private String bookName;

    @Column(name = "author_name")
    @Setter
    private String authorName;

    @Column(name = "type")
    @Getter
    @Setter
    private String type;

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public UUID getBookId() {
        return bookId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
