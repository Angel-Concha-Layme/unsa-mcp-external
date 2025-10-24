package pe.unsa.mcp.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unsa.mcp.dto.SpeakerRequest;
import pe.unsa.mcp.dto.SpeakerResponse;
import pe.unsa.mcp.dto.mapper.SpeakerMapper;
import pe.unsa.mcp.model.Speaker;
import pe.unsa.mcp.repository.SpeakerRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SpeakerService {

    private final SpeakerRepository speakerRepository;
    private final EmbeddingGenerationService embeddingService;

    public SpeakerService(SpeakerRepository speakerRepository,
                          EmbeddingGenerationService embeddingService) {
        this.speakerRepository = speakerRepository;
        this.embeddingService = embeddingService;
    }

    public List<SpeakerResponse> findAll() {
        return speakerRepository.findAll()
                .stream()
                .map(SpeakerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SpeakerResponse findById(UUID id) {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with id: " + id));
        return SpeakerMapper.toResponse(speaker);
    }

    public List<SpeakerResponse> findByNameSimilarity(String name, int limit) {
        return speakerRepository.findByNameSimilarity(name, limit)
                .stream()
                .map(SpeakerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SpeakerResponse create(SpeakerRequest request) {
        Speaker speaker = SpeakerMapper.toEntity(request);
        Speaker saved = speakerRepository.save(speaker);
        
        // Generate embeddings asynchronously after save
        embeddingService.generateSpeakerEmbeddings(saved);
        
        return SpeakerMapper.toResponse(saved);
    }

    public SpeakerResponse update(UUID id, SpeakerRequest request) {
        Speaker existing = speakerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with id: " + id));

        SpeakerMapper.updateEntity(existing, request);
        Speaker updated = speakerRepository.save(existing);
        
        // Regenerate embeddings after update
        embeddingService.generateSpeakerEmbeddings(updated);
        
        return SpeakerMapper.toResponse(updated);
    }

    public void delete(UUID id) {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with id: " + id));
        
        // Delete embeddings first
        embeddingService.deleteSpeakerEmbeddings(speaker);
        
        speakerRepository.deleteById(id);
    }
}

