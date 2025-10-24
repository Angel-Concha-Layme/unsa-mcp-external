package pe.unsa.mcp.dto;

import pe.unsa.mcp.model.EntityEmbedding.EmbeddingField;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingModel;
import pe.unsa.mcp.model.EntityEmbedding.EntityType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EntityEmbeddingResponse(
    UUID id,
    EntityType entityType,
    UUID entityId,
    EmbeddingField field,
    float[] embedding,
    EmbeddingModel model,
    Integer dim,
    OffsetDateTime createdAt
) {
}

