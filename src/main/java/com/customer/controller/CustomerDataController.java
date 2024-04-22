package com.customer.controller;

import com.customer.dto.ResponseDto;
import com.customer.dto.ReturnBooksRequestDto;
import com.customer.dto.ReturnBooksResponseDto;
import com.customer.service.CustomerDataService;
import com.customer.dto.CustomerDataRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/v1/customer")
public class CustomerDataController {
    private final CustomerDataService customerDataService;
    public CustomerDataController(CustomerDataService customerDataService) {
        this.customerDataService = customerDataService;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseDto> storeDetailsInDb(
            @Valid @RequestBody CustomerDataRequestDto customerDataRequestDto) {
        ResponseDto response = customerDataService.addDataToDatabase(customerDataRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/return")
    public ResponseEntity<ReturnBooksResponseDto> returnBooks(
            @Valid @RequestBody ReturnBooksRequestDto returnBooksRequestDto) {
        ReturnBooksResponseDto response = customerDataService.calculateReturnCharges(returnBooksRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
