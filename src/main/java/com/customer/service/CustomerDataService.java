package com.customer.service;

import com.customer.dto.*;
import com.customer.persistence.model.Book;
import com.customer.persistence.model.Customer;
import com.customer.persistence.model.LendingHistory;
import com.customer.persistence.repository.BookRepository;
import com.customer.persistence.repository.CustomerRepository;
import com.customer.persistence.repository.LendingHistoryRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.hibernate.query.sqm.tree.SqmNode.log;


@Service
public class CustomerDataService {
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;
    private final LendingHistoryRepository lendingHistoryRepository;

    public CustomerDataService(CustomerRepository customerRepository, BookRepository bookRepository,
                               LendingHistoryRepository lendingHistoryRepository) {
        this.customerRepository = customerRepository;
        this.bookRepository = bookRepository;
        this.lendingHistoryRepository = lendingHistoryRepository;
    }

    @Transactional
    public ResponseDto addDataToDatabase(CustomerDataRequestDto customerDataRequestDto) {
        try {
            customerRepository.getCustomerByCustomerIdAndCustomerName(customerDataRequestDto.customerId(),
                            customerDataRequestDto.customerName())
                    .ifPresentOrElse(
                            customer -> log.info("Customer already exists"),
                            () -> addCustomerData(customerDataRequestDto.customerId(),
                                    customerDataRequestDto.customerName()));

            Optional.ofNullable(customerDataRequestDto.listOfBooks())
                    .filter(list -> !list.isEmpty())
                    .ifPresent(list -> {
                        addBookData(list);
                        addLendingHistoryData(customerDataRequestDto);
                    });

            return new ResponseDto(true, "Data added successfully");
        } catch (Exception e) {
            log.error("Error while adding data to database", e);
            return new ResponseDto(false, "Error while adding data to database");
        }
    }

    private void addLendingHistoryData(@NotNull CustomerDataRequestDto customerDataRequestDto) {
        customerDataRequestDto.listOfBooks().stream()
                .map(bookDetailDto -> createLendingHistory(bookDetailDto, customerDataRequestDto.customerId()))
                .filter(lendingHistory -> !lendingHistoryRepository.existsByBookIdAndCustomerId(lendingHistory.getBookId()
                        , lendingHistory.getCustomerId()))
                .forEach(lendingHistoryRepository::saveAndFlush);
    }

    private LendingHistory createLendingHistory(BookDetailDto bookDetailDto, Integer customerId) {
        LendingHistory lendingHistory = new LendingHistory();
        lendingHistory.setBookId(bookDetailDto.bookId());
        lendingHistory.setLendDate(bookDetailDto.lendDate());
        lendingHistory.setCustomerId(customerId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(bookDetailDto.lendDate());
        calendar.add(Calendar.DAY_OF_MONTH, bookDetailDto.daysToReturn());

        lendingHistory.setLendTill(calendar.getTime());

        return lendingHistory;
    }

    private void addBookData(List<BookDetailDto> bookDetailDtoList) {
        bookDetailDtoList.stream()
                .map(this::createBookFromDto)
                .filter(book -> !bookRepository.existsByBookId(book.getBookId()))
                .forEach(bookRepository::saveAndFlush);
    }

    private Book createBookFromDto(BookDetailDto bookDetailDto) {
        Book book = new Book();
        book.setBookId(bookDetailDto.bookId());
        book.setBookName(bookDetailDto.bookName());
        book.setAuthorName(bookDetailDto.authorName());
        return book;
    }

    private void addCustomerData(Integer customerId, String customerName) {
        Customer customer = createCustomer(customerId, customerName);
        customerRepository.saveAndFlush(customer);
    }

    private Customer createCustomer(Integer customerId, String customerName) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerName(customerName);
        return customer;
    }

    public ReturnBooksResponseDto calculateReturnCharges(ReturnBooksRequestDto returnBooksRequestDto) {
        double totalCharges = returnBooksRequestDto.bookIds().stream()
                .mapToDouble(bookId -> calculateChargeForBook(bookId, returnBooksRequestDto.customerId()))
                .sum();

        return new ReturnBooksResponseDto(totalCharges);
    }

    double calculateChargeForBook(UUID bookId, Integer customerId) {
        return lendingHistoryRepository.findLendingHistoryByBookIdAndCustomerId(bookId, customerId)
                .map(lendingHistory -> {
                    long days = DAYS.between(lendingHistory.getLendDate().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate(), LocalDate.now());

                    Book book = bookRepository.findBookByBookId(bookId)
                            .orElseThrow(() -> new RuntimeException("Book not found"));

                    return calculateCharge(book.getType(), days);
                })
                .orElse(0.0);
    }

    private double calculateCharge(String bookType, long days) {
        switch (bookType) {
            case "Regular" -> {
                if (days < 2) {
                    return 2.0;
                } else {
                    return 2.0 + (days - 2) * 1.5;
                }
            }
            case "Novel" -> {
                if (days < 3) {
                    return 4.5;
                } else {
                    return (days * 1.5);
                }
            }
            case "Fiction" -> {
                return days * 3.0;
            }
            default -> throw new RuntimeException("Invalid book type");
        }
    }
}
