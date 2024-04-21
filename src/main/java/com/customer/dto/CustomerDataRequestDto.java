package com.customer.dto;

import java.util.List;

public record CustomerDataRequestDto(
        Integer customerId,
        String customerName,
        List<BookDetailDto> listOfBooks
) {}
