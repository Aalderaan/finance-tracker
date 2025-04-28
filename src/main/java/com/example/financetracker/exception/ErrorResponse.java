package com.example.financetracker.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(example = "2025-04-26T23:00:00", description = "Timestamp of the error")
    private LocalDateTime timestamp;

    @Schema(example = "404", description = "HTTP Status Code")
    private int status;

    @Schema(example = "Not Found", description = "Error description")
    private String error;

    @Schema(example = "Transaction not found", description = "Detailed error message")
    private String message;

    @Schema(example = "/api/transactions/1", description = "Path where error occurred")
    private String path;
}
