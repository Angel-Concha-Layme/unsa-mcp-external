package pe.unsa.mcp.dto.mcp;

import java.util.List;

public record HealthStatusResponse(
    String db,
    String embeddings,
    List<Integer> eventYears
) {}
