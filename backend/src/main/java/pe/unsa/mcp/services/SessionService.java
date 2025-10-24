package pe.unsa.mcp.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unsa.mcp.dto.SessionRequest;
import pe.unsa.mcp.dto.SessionResponse;
import pe.unsa.mcp.dto.mapper.SessionMapper;
import pe.unsa.mcp.model.Event;
import pe.unsa.mcp.model.Session;
import pe.unsa.mcp.model.Speaker;
import pe.unsa.mcp.repository.EventRepository;
import pe.unsa.mcp.repository.SessionRepository;
import pe.unsa.mcp.repository.SpeakerRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final EventRepository eventRepository;
    private final SpeakerRepository speakerRepository;
    private final EmbeddingGenerationService embeddingService;

    public SessionService(SessionRepository sessionRepository,
                          EventRepository eventRepository,
                          SpeakerRepository speakerRepository,
                          EmbeddingGenerationService embeddingService) {
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
        this.speakerRepository = speakerRepository;
        this.embeddingService = embeddingService;
    }

    public List<SessionResponse> findAll() {
        return sessionRepository.findAll()
                .stream()
                .map(SessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SessionResponse findById(UUID id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + id));
        return SessionMapper.toResponse(session);
    }

    public List<SessionResponse> findByEventId(UUID eventId) {
        return sessionRepository.findByEventIdOrderBySeqAsc(eventId)
                .stream()
                .map(SessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SessionResponse> findBySpeakerId(UUID speakerId) {
        return sessionRepository.findBySpeakerId(speakerId)
                .stream()
                .map(SessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SessionResponse create(SessionRequest request) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + request.eventId()));

        Speaker speaker = speakerRepository.findById(request.speakerId())
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with id: " + request.speakerId()));

        if (sessionRepository.existsByEventIdAndSpeakerId(request.eventId(), request.speakerId())) {
            throw new IllegalArgumentException("Speaker already has a session in this event");
        }

        if (sessionRepository.existsByEventIdAndSeq(request.eventId(), request.seq())) {
            throw new IllegalArgumentException("Sequence number already exists for this event");
        }

        Session session = SessionMapper.toEntity(request, event, speaker);
        Session saved = sessionRepository.save(session);
        
        // Generate embeddings after save
        embeddingService.generateSessionEmbeddings(saved);
        
        return SessionMapper.toResponse(saved);
    }

    public SessionResponse update(UUID id, SessionRequest request) {
        Session existing = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + id));

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + request.eventId()));

        Speaker speaker = speakerRepository.findById(request.speakerId())
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with id: " + request.speakerId()));

        if (!existing.getEvent().getId().equals(request.eventId()) ||
            !existing.getSpeaker().getId().equals(request.speakerId())) {
            if (sessionRepository.existsByEventIdAndSpeakerId(request.eventId(), request.speakerId())) {
                throw new IllegalArgumentException("Speaker already has a session in this event");
            }
        }

        if (!existing.getSeq().equals(request.seq())) {
            if (sessionRepository.existsByEventIdAndSeq(request.eventId(), request.seq())) {
                throw new IllegalArgumentException("Sequence number already exists for this event");
            }
        }

        SessionMapper.updateEntity(existing, request, event, speaker);
        Session updated = sessionRepository.save(existing);
        
        // Regenerate embeddings after update
        embeddingService.generateSessionEmbeddings(updated);
        
        return SessionMapper.toResponse(updated);
    }

    public void delete(UUID id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + id));
        
        // Delete embeddings first
        embeddingService.deleteSessionEmbeddings(session);
        
        sessionRepository.deleteById(id);
    }
}

