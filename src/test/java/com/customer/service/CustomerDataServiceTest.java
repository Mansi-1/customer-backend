package com.customer.service;

import com.customer.dto.*;
import com.customer.persistence.model.Book;
import com.customer.persistence.model.Customer;
import com.customer.persistence.model.LendingHistory;
import com.customer.persistence.repository.BookRepository;
import com.customer.persistence.repository.CustomerRepository;
import com.customer.persistence.repository.LendingHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class CustomerDataServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private LendingHistoryRepository lendingHistoryRepository;

	@InjectMocks
	private CustomerDataService customerDataService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldAddDataCustomerDataIfBooksIsEmpty() {
		CustomerDataRequestDto customerDataRequestDto = new CustomerDataRequestDto(1, "John Doe",
				Collections.emptyList());
		when(customerRepository.getCustomerByCustomerIdAndCustomerName(anyInt(), anyString())).thenReturn(
				Optional.empty());

		ResponseDto responseDto = customerDataService.addDataToDatabase(customerDataRequestDto);

		verify(customerRepository, times(1)).saveAndFlush(any());
		verify(bookRepository, times(0)).saveAndFlush(any());
		verify(lendingHistoryRepository, times(0)).saveAndFlush(any());
		assert responseDto.success();
		assert "Data added successfully".equals(responseDto.message());
	}

	@Test
	void shouldNotAddCustomerIfAlreadyExists() {
		CustomerDataRequestDto customerDataRequestDto = new CustomerDataRequestDto(1, "John Doe",
				Collections.emptyList());
		when(customerRepository.getCustomerByCustomerIdAndCustomerName(anyInt(), anyString())).thenReturn(Optional.of
				(new Customer()));

		ResponseDto responseDto = customerDataService.addDataToDatabase(customerDataRequestDto);

		verify(customerRepository, times(0)).saveAndFlush(any());
		verify(bookRepository, times(0)).saveAndFlush(any());
		verify(lendingHistoryRepository, times(0)).saveAndFlush(any());
		assert responseDto.success();
		assert "Data added successfully".equals(responseDto.message());
	}

	@Test
	void shouldAddAllDataToDb() {
		UUID bookId = UUID.randomUUID();
		List<BookDetailDto> books = Collections.singletonList(new BookDetailDto(bookId, "Book Name",
				"Author Name", new Date(), 7));
		CustomerDataRequestDto customerDataRequestDto = new CustomerDataRequestDto(1, "John Doe",
				books);
		when(customerRepository.getCustomerByCustomerIdAndCustomerName(anyInt(), anyString())).thenReturn(
				Optional.empty());

		ResponseDto responseDto = customerDataService.addDataToDatabase(customerDataRequestDto);

		verify(customerRepository, times(1)).saveAndFlush(any());
		verify(bookRepository, times(1)).saveAndFlush(any());
		verify(lendingHistoryRepository, times(1)).saveAndFlush(any());
		assert responseDto.success();
		assert "Data added successfully".equals(responseDto.message());
	}

	@Test
	void shouldThrowException() {
		CustomerDataRequestDto customerDataRequestDto = new CustomerDataRequestDto(1, "John Doe",
				Collections.emptyList());
		when(customerRepository.getCustomerByCustomerIdAndCustomerName(anyInt(), anyString()))
				.thenThrow(new RuntimeException());

		ResponseDto responseDto = customerDataService.addDataToDatabase(customerDataRequestDto);

		verify(customerRepository, times(0)).saveAndFlush(any());
		verify(bookRepository, times(0)).saveAndFlush(any());
		verify(lendingHistoryRepository, times(0)).saveAndFlush(any());
		assert !responseDto.success();
		assert "Error while adding data to database".equals(responseDto.message());
	}

	@Test
	void shouldCalculateReturnCharges() {
		UUID bookId = UUID.randomUUID();
		ReturnBooksRequestDto returnBooksRequestDto = new ReturnBooksRequestDto(1, Arrays.asList(bookId));

		LendingHistory lendingHistory = new LendingHistory();
		lendingHistory.setLendDate(Date.from(LocalDate.now().minusDays(5).atStartOfDay(
				ZoneId.systemDefault()).toInstant()));
		when(lendingHistoryRepository.findLendingHistoryByBookIdAndCustomerId(bookId, returnBooksRequestDto.customerId()))
				.thenReturn(Optional.of(lendingHistory));

		Book book = new Book();
		book.setType("Regular");
		when(bookRepository.findBookByBookId(bookId)).thenReturn(Optional.of(book));

		ReturnBooksResponseDto response = customerDataService.calculateReturnCharges(returnBooksRequestDto);

		assert(7.5 == response.totalCharges());
	}

	@Test
	void shouldCalculateReturnChargesAs0WhenLendingHistoryNotFound() {
		UUID bookId = UUID.randomUUID();
		ReturnBooksRequestDto returnBooksRequestDto = new ReturnBooksRequestDto(1, Collections.singletonList(bookId));

		when(lendingHistoryRepository.findLendingHistoryByBookIdAndCustomerId(bookId, returnBooksRequestDto.customerId()))
				.thenReturn(Optional.empty());

		ReturnBooksResponseDto response = customerDataService.calculateReturnCharges(returnBooksRequestDto);

		assert(0.0 == response.totalCharges());
	}


	@Test
	void shouldThrowRuntimeExceptionWhenBookNotFound() {
		UUID bookId = UUID.randomUUID();
		ReturnBooksRequestDto returnBooksRequestDto = new ReturnBooksRequestDto(1, List.of(bookId));

		LendingHistory lendingHistory = new LendingHistory();
		lendingHistory.setLendDate(Date.from(LocalDate.now().minusDays(5).atStartOfDay(
				ZoneId.systemDefault()).toInstant()));
		when(lendingHistoryRepository.findLendingHistoryByBookIdAndCustomerId(bookId, returnBooksRequestDto.customerId()))
				.thenReturn(Optional.of(lendingHistory));

		when(bookRepository.findBookByBookId(bookId)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> customerDataService.calculateReturnCharges(returnBooksRequestDto));
	}
}