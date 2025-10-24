package pe.unsa.mcp.dto.mapper;

import pe.unsa.mcp.dto.EntityEmbeddingRequest;
import pe.unsa.mcp.dto.EntityEmbeddingResponse;
import pe.unsa.mcp.model.EntityEmbedding;

public class EntityEmbeddingMapper {

    public static EntityEmbedding toEntity(EntityEmbeddingRequest request) {
        EntityEmbedding embedding = new EntityEmbedding();
        embedding.setEntityType(request.entityType());
        embedding.setEntityId(request.entityId());
        embedding.setField(request.field());
        embedding.setEmbedding(request.embedding());
        embedding.setModel(request.model());
        embedding.setDim(request.dim());
        return embedding;
    }

    public static EntityEmbeddingResponse toResponse(EntityEmbedding embedding) {
        return new EntityEmbeddingResponse(
            embedding.getId(),
            embedding.getEntityType(),
            embedding.getEntityId(),
            embedding.getField(),
            embedding.getEmbedding(),
            embedding.getModel(),
            embedding.getDim(),
            embedding.getCreatedAt()
        );
    }

    public static void updateEntity(EntityEmbedding embedding, EntityEmbeddingRequest request) {
        embedding.setEntityType(request.entityType());
        embedding.setEntityId(request.entityId());
        embedding.setField(request.field());
        embedding.setEmbedding(request.embedding());
        embedding.setModel(request.model());
        embedding.setDim(request.dim());
    }
}

