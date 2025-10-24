package pe.unsa.mcp.dto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record SpeakerResponse(
    UUID id,
    String fullName,
    String orgName,
    String jobTitle,
    String bio,
    String profileImageUrl,
    Map<String, Object> contacts,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}

