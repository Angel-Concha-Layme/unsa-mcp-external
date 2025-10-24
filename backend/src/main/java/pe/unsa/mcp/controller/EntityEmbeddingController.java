package pe.unsa.mcp.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.unsa.mcp.dto.EntityEmbeddingRequest;
import pe.unsa.mcp.dto.EntityEmbeddingResponse;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingField;
import pe.unsa.mcp.model.EntityEmbedding.EntityType;
import pe.unsa.mcp.services.EntityEmbeddingService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/embeddings")
public class EntityEmbeddingController {

    private final EntityEmbeddingService embeddingService;

    public EntityEmbeddingController(EntityEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @GetMapping
    public ResponseEntity<List<EntityEmbeddingResponse>> getAllEmbeddings() {
        List<EntityEmbeddingResponse> embeddings = embeddingService.findAll();
        return ResponseEntity.ok(embeddings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityEmbeddingResponse> getEmbeddingById(@PathVariable UUID id) {
        try {
            EntityEmbeddingResponse embedding = embeddingService.findById(id);
            return ResponseEntity.ok(embedding);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<EntityEmbeddingResponse>> getEmbeddingsByEntity(
            @PathVariable EntityType entityType,
            @PathVariable UUID entityId) {
        List<EntityEmbeddingResponse> embeddings = embeddingService.findByEntity(entityType, entityId);
        return ResponseEntity.ok(embeddings);
    }

    @GetMapping("/entity/{entityType}/{entityId}/{field}")
    public ResponseEntity<EntityEmbeddingResponse> getEmbeddingByEntityAndField(
            @PathVariable EntityType entityType,
            @PathVariable UUID entityId,
            @PathVariable EmbeddingField field) {
        try {
            EntityEmbeddingResponse embedding = embeddingService.findByEntityAndField(entityType, entityId, field);
            return ResponseEntity.ok(embedding);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<EntityEmbeddingResponse>> searchSimilarEmbeddings(
            @RequestParam EntityType entityType,
            @RequestBody float[] queryEmbedding,
            @RequestParam(defaultValue = "10") int limit) {
        List<EntityEmbeddingResponse> embeddings = embeddingService.findSimilar(entityType, queryEmbedding, limit);
        return ResponseEntity.ok(embeddings);
    }

    @PostMapping
    public ResponseEntity<EntityEmbeddingResponse> createEmbedding(@Valid @RequestBody EntityEmbeddingRequest request) {
        try {
            EntityEmbeddingResponse created = embeddingService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityEmbeddingResponse> updateEmbedding(@PathVariable UUID id,
                                                                    @Valid @RequestBody EntityEmbeddingRequest request) {
        try {
            EntityEmbeddingResponse updated = embeddingService.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmbedding(@PathVariable UUID id) {
        try {
            embeddingService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Void> deleteEmbeddingsByEntity(
            @PathVariable EntityType entityType,
            @PathVariable UUID entityId) {
        embeddingService.deleteByEntity(entityType, entityId);
        return ResponseEntity.noContent().build();
    }
}

