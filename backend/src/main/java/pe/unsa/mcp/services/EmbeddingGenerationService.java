package pe.unsa.mcp.services;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import pe.unsa.mcp.model.EntityEmbedding;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingField;
import pe.unsa.mcp.model.EntityEmbedding.EntityType;
import pe.unsa.mcp.model.Session;
import pe.unsa.mcp.model.Speaker;
import pe.unsa.mcp.repository.EntityEmbeddingRepository;

@Service
@Transactional
public class EmbeddingGenerationService {

  private static final Logger log = LoggerFactory.getLogger(EmbeddingGenerationService.class);

  private final EmbeddingModel embeddingModel;
  private final EntityEmbeddingRepository embeddingRepository;

  public EmbeddingGenerationService(
      EmbeddingModel embeddingModel, EntityEmbeddingRepository embeddingRepository) {
    this.embeddingModel = embeddingModel;
    this.embeddingRepository = embeddingRepository;
  }

  /** Generate embeddings for a speaker's bio */
  public void generateSpeakerEmbeddings(Speaker speaker) {
    try {
      // Combine bio with job title and organization for richer context
      StringBuilder textBuilder = new StringBuilder();

      if (speaker.getBio() != null && !speaker.getBio().isEmpty()) {
        textBuilder.append(speaker.getBio());
      }

      if (speaker.getJobTitle() != null && !speaker.getJobTitle().isEmpty()) {
        if (textBuilder.length() > 0) textBuilder.append(" ");
        textBuilder.append(speaker.getJobTitle());
      }

      if (speaker.getOrgName() != null && !speaker.getOrgName().isEmpty()) {
        if (textBuilder.length() > 0) textBuilder.append(" ");
        textBuilder.append(speaker.getOrgName());
      }

      String text = textBuilder.toString().trim();

      if (text.isEmpty()) {
        log.warn("No text to generate embedding for speaker: {}", speaker.getId());
        return;
      }

      // Generate embedding using OpenAI
      float[] vector = generateEmbedding(text);

      // Delete existing bio embedding if exists
      embeddingRepository
          .findByEntityTypeAndEntityIdAndField(
              EntityType.speaker, speaker.getId(), EmbeddingField.bio)
          .ifPresent(embeddingRepository::delete);

      // Save new embedding
      EntityEmbedding embedding = new EntityEmbedding();
      embedding.setEntityType(EntityType.speaker);
      embedding.setEntityId(speaker.getId());
      embedding.setField(EmbeddingField.bio);
      embedding.setEmbedding(vector);
      embedding.setModel(EntityEmbedding.EmbeddingModel.TEXT_EMBEDDING_3_SMALL);
      embedding.setDim(1536);

      embeddingRepository.save(embedding);

      log.info("Generated embedding for speaker: {} ({})", speaker.getFullName(), speaker.getId());

    } catch (Exception e) {
      log.error("Failed to generate embedding for speaker: " + speaker.getId(), e);
      // Don't throw - embedding generation is optional
    }
  }

  /** Generate embeddings for a session (title, abstract, and combined 'all') */
  public void generateSessionEmbeddings(Session session) {
    try {
      // Generate embedding for title
      if (session.getTitle() != null && !session.getTitle().isEmpty()) {
        generateAndSaveSessionEmbedding(session, EmbeddingField.title, session.getTitle());
      }

      // Generate embedding for abstract
      if (session.getAbstractText() != null && !session.getAbstractText().isEmpty()) {
        generateAndSaveSessionEmbedding(
            session, EmbeddingField.abstract_, session.getAbstractText());
      }

      // Generate combined embedding (title + abstract + speaker info)
      StringBuilder allText = new StringBuilder();

      if (session.getTitle() != null) {
        allText.append(session.getTitle());
      }

      if (session.getAbstractText() != null) {
        if (allText.length() > 0) allText.append(" ");
        allText.append(session.getAbstractText());
      }

      if (session.getSpeaker() != null) {
        if (session.getSpeaker().getFullName() != null) {
          if (allText.length() > 0) allText.append(" ");
          allText.append(session.getSpeaker().getFullName());
        }
        if (session.getSpeaker().getOrgName() != null) {
          if (allText.length() > 0) allText.append(" ");
          allText.append(session.getSpeaker().getOrgName());
        }
      }

      if (allText.length() > 0) {
        generateAndSaveSessionEmbedding(session, EmbeddingField.all, allText.toString());
      }

      log.info("Generated embeddings for session: {} ({})", session.getTitle(), session.getId());

    } catch (Exception e) {
      log.error("Failed to generate embeddings for session: " + session.getId(), e);
      // Don't throw - embedding generation is optional
    }
  }

  private void generateAndSaveSessionEmbedding(Session session, EmbeddingField field, String text) {
    float[] vector = generateEmbedding(text);

    // Delete existing embedding if exists
    embeddingRepository
        .findByEntityTypeAndEntityIdAndField(EntityType.session, session.getId(), field)
        .ifPresent(embeddingRepository::delete);

    // Save new embedding
    EntityEmbedding embedding = new EntityEmbedding();
    embedding.setEntityType(EntityType.session);
    embedding.setEntityId(session.getId());
    embedding.setField(field);
    embedding.setEmbedding(vector);
    embedding.setModel(EntityEmbedding.EmbeddingModel.TEXT_EMBEDDING_3_SMALL);
    embedding.setDim(1536);

    embeddingRepository.save(embedding);
  }

  /** Generate embedding vector from text using OpenAI */
  private float[] generateEmbedding(String text) {
    try {
      EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));

      if (response.getResults() != null && !response.getResults().isEmpty()) {
        float[] embeddingArray = response.getResults().get(0).getOutput();
        return embeddingArray;
      }

      throw new RuntimeException("No embedding generated");

    } catch (Exception e) {
      log.error("Error calling OpenAI embedding API: " + e.getMessage(), e);
      throw new RuntimeException("Failed to generate embedding", e);
    }
  }

  /** Public helper to generate an embedding from arbitrary text */
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public float[] generateEmbeddingFromText(String text) {
    return generateEmbedding(text);
  }

  /** Delete all embeddings for a speaker */
  public void deleteSpeakerEmbeddings(Speaker speaker) {
    embeddingRepository.deleteByEntityTypeAndEntityId(EntityType.speaker, speaker.getId());
    log.info("Deleted embeddings for speaker: {}", speaker.getId());
  }

  /** Delete all embeddings for a session */
  public void deleteSessionEmbeddings(Session session) {
    embeddingRepository.deleteByEntityTypeAndEntityId(EntityType.session, session.getId());
    log.info("Deleted embeddings for session: {}", session.getId());
  }
}
