package pe.unsa.mcp.dto;

import jakarta.validation.constraints.NotNull;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingField;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingModel;
import pe.unsa.mcp.model.EntityEmbedding.EntityType;

import java.util.UUID;

public record EntityEmbeddingRequest(
    @NotNull EntityType entityType,
    @NotNull UUID entityId,
    @NotNull EmbeddingField field,
    @NotNull float[] embedding,
    @NotNull EmbeddingModel model,
    @NotNull Integer dim
) {
}

