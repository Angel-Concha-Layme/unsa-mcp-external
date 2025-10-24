package pe.unsa.mcp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public record EventRequest(
    @NotBlank String name,
    @NotNull Integer year,
    String description,
    String venue,
    String tz,
    LocalDate startsOn,
    LocalDate endsOn,
    String websiteUrl,
    Map<String, Object> contacts
) {
}

