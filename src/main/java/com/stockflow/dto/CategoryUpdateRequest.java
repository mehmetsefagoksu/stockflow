package com.stockflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(

        @NotBlank(message = "Category name cannot be blank")
        @Size(max = 255, message = "Category name must be at most 255 characters")
        String name

) {
}