package pe.unsa.mcp.dto.mcp;

public record AgendaNowResponse(
    SessionDetailResponse current,
    SessionDetailResponse next
) {}
