package pe.unsa.mcp.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionResponse(
    UUID id,
    UUID eventId,
    UUID speakerId,
    String title,
    String abstractText,
    LocalDate day,
    OffsetDateTime startsAt,
    OffsetDateTime endsAt,
    Integer seq,
    String track,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}

