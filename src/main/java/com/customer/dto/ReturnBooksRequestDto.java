package com.customer.dto;

import java.util.List;
import java.util.UUID;

public record ReturnBooksRequestDto (
		Integer customerId,
		List<UUID> bookIds
) {}
