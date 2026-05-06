package com.assignment.domain.coupon.dto.request;

import jakarta.validation.constraints.NotNull;

public record IssueRequest(
        Long userId
) {
}
