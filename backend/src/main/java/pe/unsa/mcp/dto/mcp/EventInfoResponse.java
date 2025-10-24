package pe.unsa.mcp.dto.mcp;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record EventInfoResponse(
    UUID id,
    String name,
    Integer year,
    String description,
    String venue,
    String tz,
    EventDates dates,
    String websiteUrl,
    Map<String, Object> contacts
) {
    public record EventDates(LocalDate start, LocalDate end) {}
}
