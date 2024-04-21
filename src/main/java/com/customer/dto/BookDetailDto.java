package com.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.UUID;

public record BookDetailDto(
        @JsonProperty("book_id")
        UUID bookId,
        @JsonProperty("author_name")
        String authorName,
        @JsonProperty("book_name")
        String bookName,
        @JsonProperty("lend_date")
        Date lendDate,
        @JsonProperty("days_to_return")
        Integer daysToReturn
) {}
