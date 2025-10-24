package pe.unsa.mcp.dto.mcp;

import java.util.UUID;

public record SearchResultResponse(
    UUID sessionId,
    String title,
    String snippet,
    Double score
) {}

