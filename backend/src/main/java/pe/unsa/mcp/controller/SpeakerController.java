package pe.unsa.mcp.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.unsa.mcp.dto.SpeakerRequest;
import pe.unsa.mcp.dto.SpeakerResponse;
import pe.unsa.mcp.services.SpeakerService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/speakers")
public class SpeakerController {

    private final SpeakerService speakerService;

    public SpeakerController(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }

    @GetMapping
    public ResponseEntity<List<SpeakerResponse>> getAllSpeakers() {
        List<SpeakerResponse> speakers = speakerService.findAll();
        return ResponseEntity.ok(speakers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeakerResponse> getSpeakerById(@PathVariable UUID id) {
        try {
            SpeakerResponse speaker = speakerService.findById(id);
            return ResponseEntity.ok(speaker);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<SpeakerResponse>> searchSpeakersByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "10") int limit) {
        List<SpeakerResponse> speakers = speakerService.findByNameSimilarity(name, limit);
        return ResponseEntity.ok(speakers);
    }

    @PostMapping
    public ResponseEntity<SpeakerResponse> createSpeaker(@Valid @RequestBody SpeakerRequest request) {
        SpeakerResponse created = speakerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpeakerResponse> updateSpeaker(@PathVariable UUID id,
                                                          @Valid @RequestBody SpeakerRequest request) {
        try {
            SpeakerResponse updated = speakerService.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpeaker(@PathVariable UUID id) {
        try {
            speakerService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

