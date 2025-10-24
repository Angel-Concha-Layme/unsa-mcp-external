package pe.unsa.mcp.services;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.unsa.mcp.dto.mcp.*;
import pe.unsa.mcp.model.EntityEmbedding.EntityType;
import pe.unsa.mcp.model.Event;
import pe.unsa.mcp.model.Session;
import pe.unsa.mcp.model.Speaker;
import pe.unsa.mcp.repository.EntityEmbeddingRepository;
import pe.unsa.mcp.repository.EventRepository;
import pe.unsa.mcp.repository.SessionRepository;
import pe.unsa.mcp.repository.SpeakerRepository;

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class McpToolsService {

  private final EventRepository eventRepository;
  private final SessionRepository sessionRepository;
  private final SpeakerRepository speakerRepository;
  private final EntityEmbeddingRepository embeddingRepository;
  private final EmbeddingGenerationService embeddingGenerationService;

  public McpToolsService(
      EventRepository eventRepository,
      SessionRepository sessionRepository,
      SpeakerRepository speakerRepository,
      EntityEmbeddingRepository embeddingRepository,
      EmbeddingGenerationService embeddingGenerationService) {
    this.eventRepository = eventRepository;
    this.sessionRepository = sessionRepository;
    this.speakerRepository = speakerRepository;
    this.embeddingRepository = embeddingRepository;
    this.embeddingGenerationService = embeddingGenerationService;
  }

  // 1. event.info.get
  @Tool(
      name = "event.info.get",
      description = "Get basic information about an event for a specific year")
  public McpToolResponse getEventInfo(
      @ToolParam(description = "The year of the event", required = true) Integer year) {
    try {
      Event event =
          eventRepository
              .findByYear(year)
              .orElseThrow(() -> new IllegalArgumentException("Event not found for year: " + year));

      EventInfoResponse response =
          new EventInfoResponse(
              event.getId(),
              event.getName(),
              event.getYear(),
              event.getDescription(),
              event.getVenue(),
              event.getTz(),
              new EventInfoResponse.EventDates(event.getStartsOn(), event.getEndsOn()),
              event.getWebsiteUrl(),
              event.getContacts());

      return McpToolResponse.eventInfo("event.info.get", response);
    } catch (Exception e) {
      return McpToolResponse.error(
          "event.info.get", "Error retrieving event information: " + e.getMessage());
    }
  }

  // 2. event.agenda.list
  @Tool(name = "event.agenda.list", description = "Get the complete linear agenda for an event")
  public McpToolResponse getAgendaList(
      @ToolParam(description = "The year of the event", required = true) Integer year) {
    try {
      Event event =
          eventRepository
              .findByYear(year)
              .orElseThrow(() -> new IllegalArgumentException("Event not found for year: " + year));

      List<AgendaItemResponse> agenda =
          sessionRepository.findByEventIdOrderBySeqAsc(event.getId()).stream()
              .map(
                  session ->
                      AgendaItemResponse.fromSession(
                          session.getSeq(),
                          session.getId(),
                          session.getTitle(),
                          session.getAbstractText(),
                          session.getStartsAt(),
                          session.getEndsAt(),
                          new AgendaItemResponse.SpeakerSummary(
                              session.getSpeaker().getId(),
                              session.getSpeaker().getFullName(),
                              session.getSpeaker().getOrgName(),
                              session.getSpeaker().getJobTitle(),
                              session.getSpeaker().getProfileImageUrl())))
              .collect(Collectors.toList());

      return McpToolResponse.agendaList("event.agenda.list", agenda);
    } catch (Exception e) {
      return McpToolResponse.error(
          "event.agenda.list", "Error retrieving agenda: " + e.getMessage());
    }
  }

  // 3. session.get
  @Tool(name = "session.get", description = "Get details of a specific session")
  public McpToolResponse getSession(
      @ToolParam(description = "The UUID of the session", required = true) String sessionId) {
    try {
      Session session =
          sessionRepository
              .findById(UUID.fromString(sessionId))
              .orElseThrow(
                  () -> new IllegalArgumentException("Session not found with id: " + sessionId));

      return McpToolResponse.sessionDetail("session.get", toSessionDetail(session));
    } catch (Exception e) {
      return McpToolResponse.error("session.get", "Error retrieving session: " + e.getMessage());
    }
  }

  // 4. session.find.by_time
  @Tool(
      name = "session.find.by_time",
      description = "Find the session occurring at a specific time")
  public McpToolResponse findSessionByTime(
      @ToolParam(description = "The year of the event", required = true) Integer year,
      @ToolParam(description = "The time to search", required = true) OffsetDateTime at) {
    try {
      Event event =
          eventRepository
              .findByYear(year)
              .orElseThrow(() -> new IllegalArgumentException("Event not found for year: " + year));

      SessionDetailResponse session =
          sessionRepository
              .findByEventIdAndAtTime(event.getId(), at)
              .map(this::toSessionDetail)
              .orElse(null);

      if (session == null) {
        return McpToolResponse.text(
            "session.find.by_time", "No session found at the specified time");
      }

      return McpToolResponse.sessionDetail("session.find.by_time", session);
    } catch (Exception e) {
      return McpToolResponse.error(
          "session.find.by_time", "Error finding session by time: " + e.getMessage());
    }
  }

  // 5. session.next (by seq or time)
  @Tool(
      name = "session.next",
      description = "Get the next session after a given sequence number or time")
  public McpToolResponse getNextSession(
      @ToolParam(description = "The year of the event", required = true) Integer year,
      @ToolParam(description = "Sequence number to start from", required = false) Integer fromSeq,
      @ToolParam(description = "Time to start from", required = false) OffsetDateTime fromTime) {
    try {
      Event event =
          eventRepository
              .findByYear(year)
              .orElseThrow(() -> new IllegalArgumentException("Event not found for year: " + year));

      Session nextSession;
      if (fromSeq != null) {
        nextSession =
            sessionRepository
                .findNextByEventIdAndSeq(event.getId(), fromSeq)
                .orElseThrow(() -> new IllegalArgumentException("No next session found"));
      } else if (fromTime != null) {
        nextSession =
            sessionRepository
                .findNextByEventIdAndTime(event.getId(), fromTime)
                .orElseThrow(() -> new IllegalArgumentException("No next session found"));
      } else {
        throw new IllegalArgumentException("Either fromSeq or fromTime must be provided");
      }

      return McpToolResponse.sessionDetail("session.next", toSessionDetail(nextSession));
    } catch (Exception e) {
      return McpToolResponse.error(
          "session.next", "Error retrieving next session: " + e.getMessage());
    }
  }

  // 7. session.search.semantic
  @Tool(
      name = "session.search.semantic",
      description = "Search sessions by semantic similarity using embeddings")
  public McpToolResponse searchSessionsSemantic(
      @ToolParam(description = "The year of the event", required = true) Integer year,
      @ToolParam(description = "Query text", required = true) String query,
      @ToolParam(description = "Top K results", required = true) int topK) {
    try {
      Event event =
          eventRepository
              .findByYear(year)
              .orElseThrow(() -> new IllegalArgumentException("Event not found for year: " + year));

      // Generate embedding from the provided query text
      float[] queryEmbedding = embeddingGenerationService.generateEmbeddingFromText(query);
      String vectorString = Arrays.toString(queryEmbedding);
      List<EntityEmbeddingRepository.SimilarEmbeddingResult> similar =
          embeddingRepository.findSimilarEntityIds(EntityType.session.name(), vectorString, topK);

      // Preserve order and compute scores
      java.util.LinkedHashMap<UUID, Double> idToScore = new java.util.LinkedHashMap<>();
      java.util.LinkedHashSet<UUID> idSet = new java.util.LinkedHashSet<>();
      for (EntityEmbeddingRepository.SimilarEmbeddingResult r : similar) {
        if (!idSet.contains(r.getEntityId())) {
          idSet.add(r.getEntityId());
        }
        // keep best (max) score per entityId
        idToScore.merge(r.getEntityId(), 1.0 - r.getDistance(), Math::max);
      }
      java.util.List<UUID> ids = new java.util.ArrayList<>(idSet);

      List<Session> sessionsFetched =
          ids.isEmpty() ? java.util.List.of() : sessionRepository.findAllWithSpeakerByIdIn(ids);
      java.util.Map<UUID, Session> sessionMap =
          sessionsFetched.stream()
              .collect(java.util.stream.Collectors.toMap(Session::getId, s -> s));

      List<SemanticSearchResponse> results =
          ids.stream()
              .map(id -> sessionMap.get(id))
              .filter(s -> s != null && s.getEvent().getId().equals(event.getId()))
              .map(
                  s ->
                      new SemanticSearchResponse(
                          s.getId(),
                          s.getTitle(),
                          s.getAbstractText(),
                          idToScore.get(s.getId()),
                          new SemanticSearchResponse.SpeakerInfo(s.getSpeaker().getFullName())))
              .collect(Collectors.toList());

      if (results.isEmpty()) {
        return McpToolResponse.text(
            "session.search.semantic", "No sessions found for query: " + query);
      }

      return McpToolResponse.semanticSearch("session.search.semantic", results);
    } catch (Exception e) {
      return McpToolResponse.error(
          "session.search.semantic", "Error performing semantic search: " + e.getMessage());
    }
  }

  // 8. session.range.by_day
  @Tool(name = "session.range.by_day", description = "List all sessions for a specific day")
  public McpToolResponse getSessionsByDay(
      @ToolParam(description = "The year of the event", required = true) Integer year,
      @ToolParam(description = "The day to filter", required = true) LocalDate day) {
    try {
      Event event =
          eventRepository
              .findByYear(year)
              .orElseThrow(() -> new IllegalArgumentException("Event not found for year: " + year));

      List<AgendaItemResponse> agenda =
          sessionRepository.findByEventIdAndDay(event.getId(), day).stream()
              .map(
                  session ->
                      AgendaItemResponse.fromSession(
                          session.getSeq(),
                          session.getId(),
                          session.getTitle(),
                          session.getAbstractText(),
                          session.getStartsAt(),
                          session.getEndsAt(),
                          new AgendaItemResponse.SpeakerSummary(
                              session.getSpeaker().getId(),
                              session.getSpeaker().getFullName(),
                              session.getSpeaker().getOrgName(),
                              session.getSpeaker().getJobTitle(),
                              session.getSpeaker().getProfileImageUrl())))
              .collect(Collectors.toList());

      if (agenda.isEmpty()) {
        return McpToolResponse.text("session.range.by_day", "No sessions found for day: " + day);
      }

      return McpToolResponse.agendaList("session.range.by_day", agenda);
    } catch (Exception e) {
      return McpToolResponse.error(
          "session.range.by_day", "Error retrieving sessions by day: " + e.getMessage());
    }
  }

  // 9. speaker.get
  @Tool(
      name = "speaker.get",
      description =
          "Get detailed information about a speaker by partial name match using similarity search")
  public McpToolResponse getSpeaker(
      @ToolParam(
              description =
                  "Speaker name or partial name (e.g., 'Angel', 'Angel Tomas', 'Angel Concha')",
              required = true)
          String name) {

    try {
      List<Speaker> candidates = speakerRepository.findByNameTrigram(name.trim(), 0.3, 5);

      if (candidates.isEmpty()) {
        return McpToolResponse.error("speaker.get", "No speaker found matching: " + name);
      }

      if (candidates.size() > 1) {
        String candidatesList =
            candidates.stream()
                .map(
                    s ->
                        String.format(
                            "• %s (%s, %s)", s.getFullName(), s.getOrgName(), s.getJobTitle()))
                .collect(Collectors.joining("\n"));

        return McpToolResponse.text(
            "speaker.get",
            String.format(
                "Multiple speakers found matching '%s'. Please be more specific:\n\n%s",
                name, candidatesList));
      }

      Speaker speaker = candidates.get(0);
      List<Session> sessions = sessionRepository.findBySpeakerId(speaker.getId());
      SpeakerDetailResponse.SessionInfo sessionInfo =
          sessions.isEmpty()
              ? null
              : SpeakerDetailResponse.SessionInfo.fromDateTime(
                  sessions.get(0).getId(),
                  sessions.get(0).getTitle(),
                  sessions.get(0).getStartsAt(),
                  sessions.get(0).getEndsAt());

      SpeakerDetailResponse speakerDetail =
          new SpeakerDetailResponse(
              speaker.getId(),
              speaker.getFullName(),
              speaker.getOrgName(),
              speaker.getJobTitle(),
              speaker.getBio(),
              speaker.getProfileImageUrl(),
              speaker.getContacts(),
              sessionInfo);

      return McpToolResponse.speakerDetail("speaker.get", speakerDetail);

    } catch (Exception e) {
      return McpToolResponse.error(
          "speaker.get", "Error retrieving speaker information: " + e.getMessage());
    }
  }

  // 11. speaker.search.semantic
  @Tool(
      name = "speaker.search.semantic",
      description = "Search speakers by semantic similarity using embeddings")
  public McpToolResponse searchSpeakersSemantic(
      @ToolParam(description = "Query text", required = true) String query,
      @ToolParam(description = "Top K results", required = true) int topK) {
    try {
      float[] queryEmbedding = embeddingGenerationService.generateEmbeddingFromText(query);
      String vectorString = Arrays.toString(queryEmbedding);
      List<EntityEmbeddingRepository.SimilarEmbeddingResult> embeddings =
          embeddingRepository.findSimilarEntityIds(EntityType.speaker.name(), vectorString, topK);

      List<SpeakerSemanticResult> results =
          embeddings.stream()
              .map(
                  emb -> {
                    Speaker speaker = speakerRepository.findById(emb.getEntityId()).orElse(null);
                    if (speaker != null) {
                      return new SpeakerSemanticResult(
                          speaker.getId(),
                          speaker.getFullName(),
                          speaker.getOrgName(),
                          speaker.getJobTitle(),
                          1.0 - emb.getDistance());
                    }
                    return null;
                  })
              .filter(result -> result != null)
              .collect(Collectors.toList());

      if (results.isEmpty()) {
        return McpToolResponse.text(
            "speaker.search.semantic", "No speakers found for query: " + query);
      }

      return McpToolResponse.speakerSearchSemantic("speaker.search.semantic", results);
    } catch (Exception e) {
      return McpToolResponse.error(
          "speaker.search.semantic", "Error performing semantic search: " + e.getMessage());
    }
  }

  // 12. contact.get.for_speaker
  @Tool(
      name = "contact.get.for_speaker",
      description =
          "Get contact information for a speaker by partial name match using similarity search")
  public McpToolResponse getContactForSpeaker(
      @ToolParam(description = "Speaker name or partial name", required = true) String name) {

    try {
      List<Speaker> candidates = speakerRepository.findByNameTrigram(name.trim(), 0.3, 5);

      if (candidates.isEmpty()) {
        return McpToolResponse.error(
            "contact.get.for_speaker", "No speaker found matching: " + name);
      }

      if (candidates.size() > 1) {
        String candidatesList =
            candidates.stream()
                .map(
                    s ->
                        String.format(
                            "• %s (%s, %s)", s.getFullName(), s.getOrgName(), s.getJobTitle()))
                .collect(Collectors.joining("\n"));

        return McpToolResponse.text(
            "contact.get.for_speaker",
            String.format(
                "Multiple speakers found matching '%s'. Please be more specific:\n\n%s",
                name, candidatesList));
      }

      Speaker speaker = candidates.get(0);
      ContactResponse contactResponse = new ContactResponse(speaker.getContacts());

      return McpToolResponse.contact("contact.get.for_speaker", contactResponse);

    } catch (Exception e) {
      return McpToolResponse.error(
          "contact.get.for_speaker", "Error retrieving contact information: " + e.getMessage());
    }
  }

  // 13. agenda.now
  @Tool(name = "agenda.now", description = "Get what is happening now - current and next sessions")
  public McpToolResponse getAgendaNow(
      @ToolParam(description = "The year of the event", required = true) Integer year,
      @ToolParam(description = "Use 'auto' or provide ISO-8601 time", required = false)
          String now) {
    try {
      OffsetDateTime effectiveNow =
          (now == null || now.equals("auto")) ? OffsetDateTime.now() : OffsetDateTime.parse(now);

      Event event =
          eventRepository
              .findByYear(year)
              .orElseThrow(() -> new IllegalArgumentException("Event not found for year: " + year));

      SessionDetailResponse current =
          sessionRepository
              .findByEventIdAndAtTime(event.getId(), effectiveNow)
              .map(this::toSessionDetail)
              .orElse(null);

      SessionDetailResponse next =
          sessionRepository
              .findNextByEventIdAndTime(event.getId(), effectiveNow)
              .map(this::toSessionDetail)
              .orElse(null);

      AgendaNowResponse agendaNow = new AgendaNowResponse(current, next);

      return McpToolResponse.agendaNow("agenda.now", agendaNow);
    } catch (Exception e) {
      return McpToolResponse.error(
          "agenda.now", "Error retrieving current agenda: " + e.getMessage());
    }
  }

  // 14. health.status
  @Tool(name = "health.status", description = "Get server health status and available event years")
  public McpToolResponse getHealthStatus() {
    try {
      String dbStatus = "OK";
      String embeddingsStatus = "OK";

      try {
        eventRepository.count();
      } catch (Exception e) {
        dbStatus = "ERROR";
      }

      try {
        embeddingRepository.count();
      } catch (Exception e) {
        embeddingsStatus = "ERROR";
      }

      List<Integer> years = eventRepository.findAllDistinctYears();

      HealthStatusResponse health = new HealthStatusResponse(dbStatus, embeddingsStatus, years);

      return McpToolResponse.healthStatus("health.status", health);
    } catch (Exception e) {
      return McpToolResponse.error(
          "health.status", "Error retrieving health status: " + e.getMessage());
    }
  }

  // Helper methods
  private SessionDetailResponse toSessionDetail(Session session) {
    return new SessionDetailResponse(
        session.getId(),
        session.getTitle(),
        session.getAbstractText(),
        session.getStartsAt(),
        session.getEndsAt(),
        session.getSeq(),
        new SessionDetailResponse.SpeakerDetail(
            session.getSpeaker().getId(),
            session.getSpeaker().getFullName(),
            session.getSpeaker().getOrgName(),
            session.getSpeaker().getJobTitle(),
            session.getSpeaker().getProfileImageUrl()));
  }

  // Additional DTOs for speaker search
  public record SpeakerSearchResult(
      UUID speakerId, String fullName, String orgName, String jobTitle, String match) {}

  public record SpeakerSemanticResult(
      UUID speakerId, String fullName, String orgName, String jobTitle, Double score) {}

  public record ContactResponse(java.util.Map<String, Object> contacts) {}
}
