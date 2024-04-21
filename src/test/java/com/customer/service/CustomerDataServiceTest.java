package com.customer.service;

import com.customer.dto.BookDetailDto;
import com.customer.dto.CustomerDataRequestDto;
import com.customer.dto.ResponseDto;
import com.customer.persistence.model.Customer;
import com.customer.persistence.repository.BookRepository;
import com.customer.persistence.repository.CustomerRepository;
import com.customer.persistence.repository.LendingHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
}