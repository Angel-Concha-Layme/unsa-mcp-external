package pe.unsa.mcp.dto.mcp;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record AgendaItemResponse(
    Integer seq,
    UUID sessionId,
    String title,
    String abstractText,
    String startsAt,
    String endsAt,
    SpeakerSummary speaker
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM HH:mm", Locale.of("es", "ES"));

    public static AgendaItemResponse fromSession(
            Integer seq,
            UUID sessionId,
            String title,
            String abstractText,
            OffsetDateTime startsAt,
            OffsetDateTime endsAt,
            SpeakerSummary speaker) {
        return new AgendaItemResponse(
                seq,
                sessionId,
                title,
                abstractText,
                startsAt != null ? startsAt.format(FORMATTER) : null,
                endsAt != null ? endsAt.format(FORMATTER) : null,
                speaker
        );
    }

    public record SpeakerSummary(
        UUID id,
        String name,
        String org,
        String jobTitle,
        String profileImageUrl
    ) {}
}
