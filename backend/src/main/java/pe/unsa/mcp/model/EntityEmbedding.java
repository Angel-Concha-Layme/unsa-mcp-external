package pe.unsa.mcp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pe.unsa.mcp.model.converter.EmbeddingFieldConverter;
import pe.unsa.mcp.model.converter.EmbeddingModelConverter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "entity_embeddings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"entity_type", "entity_id", "field"})
})
public class EntityEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, columnDefinition = "text")
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Convert(converter = EmbeddingFieldConverter.class)
    @Column(nullable = false, columnDefinition = "text")
    private EmbeddingField field;

    @Column(nullable = false, columnDefinition = "vector(1536)")
    private float[] embedding;

    @Convert(converter = EmbeddingModelConverter.class)
    @Column(nullable = false, columnDefinition = "text")
    private EmbeddingModel model;

    @Column(nullable = false)
    private Integer dim;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public enum EntityType {
        speaker,
        session
    }

    public enum EmbeddingField {
        bio("bio"),
        abstract_("abstract"),
        title("title"),
        all("all");

        private final String value;

        EmbeddingField(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum EmbeddingModel {
        TEXT_EMBEDDING_3_SMALL("text-embedding-3-small");

        private final String value;

        EmbeddingModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

