package pe.unsa.mcp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.unsa.mcp.model.EntityEmbedding;
import pe.unsa.mcp.model.EntityEmbedding.EntityType;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingField;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntityEmbeddingRepository extends JpaRepository<EntityEmbedding, UUID> {

    Optional<EntityEmbedding> findByEntityTypeAndEntityIdAndField(EntityType entityType, UUID entityId, EmbeddingField field);

    List<EntityEmbedding> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    @Query(value = "WITH q AS (SELECT (:queryEmbedding)::vector AS v) " +
                   "SELECT e.* FROM entity_embeddings e, q WHERE e.entity_type = :entityType " +
                   "ORDER BY e.embedding <=> q.v LIMIT CAST(:limit AS int)",
           nativeQuery = true)
    List<EntityEmbedding> findSimilarEmbeddings(@Param("entityType") String entityType,
                                                 @Param("queryEmbedding") String queryEmbedding,
                                                 @Param("limit") int limit);
    interface SimilarEmbeddingResult {
        UUID getEntityId();
        Double getDistance();
    }

    @Query(value = "WITH q AS (SELECT (:queryEmbedding)::vector AS v) " +
                   "SELECT e.entity_id AS entityId, MIN(e.embedding <=> q.v) AS distance " +
                   "FROM entity_embeddings e, q WHERE e.entity_type = :entityType " +
                   "GROUP BY e.entity_id " +
                   "ORDER BY distance ASC LIMIT CAST(:limit AS int)",
           nativeQuery = true)
    List<SimilarEmbeddingResult> findSimilarEntityIds(@Param("entityType") String entityType,
                                                      @Param("queryEmbedding") String queryEmbedding,
                                                      @Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM entity_embeddings e WHERE e.entity_type = :entityType", nativeQuery = true)
    long countByEntityType(@Param("entityType") String entityType);

    void deleteByEntityTypeAndEntityId(EntityType entityType, UUID entityId);
}

