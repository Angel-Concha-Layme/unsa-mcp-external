package pe.unsa.mcp.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record EventResponse(
    UUID id,
    String name,
    Integer year,
    String description,
    String venue,
    String tz,
    LocalDate startsOn,
    LocalDate endsOn,
    String websiteUrl,
    Map<String, Object> contacts,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}

