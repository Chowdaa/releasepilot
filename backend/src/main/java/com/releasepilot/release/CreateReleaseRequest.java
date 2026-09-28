package com.releasepilot.release;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateReleaseRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull @FutureOrPresent LocalDate targetDate
) {}
