package com.customer.service;

import com.customer.dto.BookDetailDto;
import com.customer.dto.CustomerDataRequestDto;
import com.customer.dto.ResponseDto;
import com.customer.persistence.model.Book;
import com.customer.persistence.model.Customer;
import com.customer.persistence.model.LendingHistory;
import com.customer.persistence.repository.BookRepository;
import com.customer.persistence.repository.CustomerRepository;
import com.customer.persistence.repository.LendingHistoryRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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
}
