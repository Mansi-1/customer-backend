package com.customer.controller;

import com.customer.dto.CustomerDataRequestDto;
import com.customer.dto.ResponseDto;
import com.customer.dto.ReturnBooksRequestDto;
import com.customer.dto.ReturnBooksResponseDto;
import com.customer.service.CustomerDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerDataController.class)
class CustomerDataControllerTest {
	private MockMvc mockMvc;
	private static final String POST_URL = "/api/v1/customer/add";

	@MockBean
	private CustomerDataService customerDataService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(
				new CustomerDataController(customerDataService)).build();
	}

	@Test
	void shouldStoreDetailsInDb() throws Exception {
		CustomerDataRequestDto customerDataRequestDto = new CustomerDataRequestDto(1, "John Doe", null);
		ResponseDto expectedResponse = new ResponseDto(true, "Data added successfully");
		when(customerDataService.addDataToDatabase(any(CustomerDataRequestDto.class))).thenReturn(expectedResponse);

		mockMvc.perform(post(POST_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(customerDataRequestDto)))
				.andExpect(status().isOk())
				.andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));
	}

	@Test
	void shouldReturnBooks() throws Exception {
		ReturnBooksRequestDto returnBooksRequestDto = new ReturnBooksRequestDto(2, List.of(UUID.fromString(
				"6a0c37f0-8ad7-4afc-a99c-ebcd284fb953")));
		ReturnBooksResponseDto expectedResponse = new ReturnBooksResponseDto(20.0);
		when(customerDataService.calculateReturnCharges(any(ReturnBooksRequestDto.class))).thenReturn(expectedResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customer/return")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(returnBooksRequestDto)))
				.andExpect(status().isOk())
				.andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));
	}
}
