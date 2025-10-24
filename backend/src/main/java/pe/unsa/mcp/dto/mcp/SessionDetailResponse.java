package pe.unsa.mcp.dto.mcp;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionDetailResponse(
    UUID id,
    String title,
    String abstractText,
    OffsetDateTime startsAt,
    OffsetDateTime endsAt,
    Integer seq,
    SpeakerDetail speaker
) {
    public record SpeakerDetail(
        UUID id,
        String name,
        String org,
        String jobTitle,
        String profileImageUrl
    ) {}
}
