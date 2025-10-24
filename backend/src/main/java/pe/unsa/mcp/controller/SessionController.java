package pe.unsa.mcp.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.unsa.mcp.dto.SessionRequest;
import pe.unsa.mcp.dto.SessionResponse;
import pe.unsa.mcp.services.SessionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        List<SessionResponse> sessions = sessionService.findAll();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSessionById(@PathVariable UUID id) {
        try {
            SessionResponse session = sessionService.findById(id);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByEvent(@PathVariable UUID eventId) {
        List<SessionResponse> sessions = sessionService.findByEventId(eventId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/speaker/{speakerId}")
    public ResponseEntity<List<SessionResponse>> getSessionsBySpeaker(@PathVariable UUID speakerId) {
        List<SessionResponse> sessions = sessionService.findBySpeakerId(speakerId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest request) {
        try {
            SessionResponse created = sessionService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionResponse> updateSession(@PathVariable UUID id,
                                                          @Valid @RequestBody SessionRequest request) {
        try {
            SessionResponse updated = sessionService.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID id) {
        try {
            sessionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

