package com.customer.persistence.repository;

import com.customer.persistence.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
	boolean existsByBookId(UUID bookId);
	Optional<Book> findBookByBookId(UUID bookId);
}
