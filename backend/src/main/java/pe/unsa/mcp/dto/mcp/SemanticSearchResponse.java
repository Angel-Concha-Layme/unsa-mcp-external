package pe.unsa.mcp.dto.mcp;

import java.util.UUID;

public record SemanticSearchResponse(
    UUID sessionId,
    String title,
    String abstractText,
    Double score,
    SpeakerInfo speaker
) {
    public record SpeakerInfo(String name) {}
}

