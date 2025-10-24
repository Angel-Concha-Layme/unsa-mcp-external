package pe.unsa.mcp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionRequest(
    @NotNull UUID eventId,
    @NotNull UUID speakerId,
    @NotBlank String title,
    String abstractText,
    LocalDate day,
    @NotNull OffsetDateTime startsAt,
    @NotNull OffsetDateTime endsAt,
    @NotNull Integer seq,
    String track
) {
}

