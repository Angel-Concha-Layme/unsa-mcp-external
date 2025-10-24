package pe.unsa.mcp.dto.mcp;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public record SpeakerDetailResponse(
    UUID id,
    String fullName,
    String orgName,
    String jobTitle,
    String bio,
    String profileImageUrl,
    Map<String, Object> contacts,
    SessionInfo session
) {
    public record SessionInfo(
        UUID id,
        String title,
        String startsAt,
        String endsAt
    ) {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM HH:mm", Locale.of("es", "ES"));

        public static SessionInfo fromDateTime(UUID id, String title, OffsetDateTime startsAt, OffsetDateTime endsAt) {
            return new SessionInfo(
                id,
                title,
                startsAt != null ? startsAt.format(FORMATTER) : null,
                endsAt != null ? endsAt.format(FORMATTER) : null
            );
        }
    }
}

