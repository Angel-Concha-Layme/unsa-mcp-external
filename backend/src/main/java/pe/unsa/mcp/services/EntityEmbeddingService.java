package pe.unsa.mcp.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unsa.mcp.dto.EntityEmbeddingRequest;
import pe.unsa.mcp.dto.EntityEmbeddingResponse;
import pe.unsa.mcp.dto.mapper.EntityEmbeddingMapper;
import pe.unsa.mcp.model.EntityEmbedding;
import pe.unsa.mcp.model.EntityEmbedding.EntityType;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingField;
import pe.unsa.mcp.repository.EntityEmbeddingRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntityEmbeddingService {

    private final EntityEmbeddingRepository embeddingRepository;

    public EntityEmbeddingService(EntityEmbeddingRepository embeddingRepository) {
        this.embeddingRepository = embeddingRepository;
    }

    public List<EntityEmbeddingResponse> findAll() {
        return embeddingRepository.findAll()
                .stream()
                .map(EntityEmbeddingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public EntityEmbeddingResponse findById(UUID id) {
        EntityEmbedding embedding = embeddingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Embedding not found with id: " + id));
        return EntityEmbeddingMapper.toResponse(embedding);
    }

    public EntityEmbeddingResponse findByEntityAndField(EntityType entityType, UUID entityId, EmbeddingField field) {
        EntityEmbedding embedding = embeddingRepository.findByEntityTypeAndEntityIdAndField(entityType, entityId, field)
                .orElseThrow(() -> new IllegalArgumentException("Embedding not found for entity"));
        return EntityEmbeddingMapper.toResponse(embedding);
    }

    public List<EntityEmbeddingResponse> findByEntity(EntityType entityType, UUID entityId) {
        return embeddingRepository.findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .map(EntityEmbeddingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<EntityEmbeddingResponse> findSimilar(EntityType entityType, float[] queryEmbedding, int limit) {
        String vectorString = Arrays.toString(queryEmbedding);
        return embeddingRepository.findSimilarEmbeddings(entityType.name(), vectorString, limit)
                .stream()
                .map(EntityEmbeddingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public EntityEmbeddingResponse create(EntityEmbeddingRequest request) {
        embeddingRepository.findByEntityTypeAndEntityIdAndField(
                request.entityType(), 
                request.entityId(), 
                request.field()
        ).ifPresent(existing -> {
            throw new IllegalArgumentException("Embedding already exists for this entity and field");
        });

        EntityEmbedding embedding = EntityEmbeddingMapper.toEntity(request);
        EntityEmbedding saved = embeddingRepository.save(embedding);
        return EntityEmbeddingMapper.toResponse(saved);
    }

    public EntityEmbeddingResponse update(UUID id, EntityEmbeddingRequest request) {
        EntityEmbedding existing = embeddingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Embedding not found with id: " + id));

        embeddingRepository.findByEntityTypeAndEntityIdAndField(
                request.entityType(),
                request.entityId(),
                request.field()
        ).ifPresent(embedding -> {
            if (!embedding.getId().equals(id)) {
                throw new IllegalArgumentException("Another embedding already exists for this entity and field");
            }
        });

        EntityEmbeddingMapper.updateEntity(existing, request);
        EntityEmbedding updated = embeddingRepository.save(existing);
        return EntityEmbeddingMapper.toResponse(updated);
    }

    public void delete(UUID id) {
        if (!embeddingRepository.existsById(id)) {
            throw new IllegalArgumentException("Embedding not found with id: " + id);
        }
        embeddingRepository.deleteById(id);
    }

    public void deleteByEntity(EntityType entityType, UUID entityId) {
        embeddingRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
    }
}

