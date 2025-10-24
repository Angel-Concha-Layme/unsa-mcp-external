package pe.unsa.mcp.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unsa.mcp.dto.EventRequest;
import pe.unsa.mcp.dto.EventResponse;
import pe.unsa.mcp.dto.mapper.EventMapper;
import pe.unsa.mcp.model.Event;
import pe.unsa.mcp.repository.EventRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventResponse> findAll() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toResponse)
                .collect(Collectors.toList());
    }

    public EventResponse findById(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));
        return EventMapper.toResponse(event);
    }

    public EventResponse create(EventRequest request) {
        if (eventRepository.existsByNameAndYear(request.name(), request.year())) {
            throw new IllegalArgumentException("Event already exists for name: " + request.name() + " and year: " + request.year());
        }
        Event event = EventMapper.toEntity(request);
        Event saved = eventRepository.save(event);
        return EventMapper.toResponse(saved);
    }

    public EventResponse update(UUID id, EventRequest request) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        eventRepository.findByNameAndYear(request.name(), request.year())
                .ifPresent(event -> {
                    if (!event.getId().equals(id)) {
                        throw new IllegalArgumentException("Another event already exists with this name and year");
                    }
                });

        EventMapper.updateEntity(existing, request);
        Event updated = eventRepository.save(existing);
        return EventMapper.toResponse(updated);
    }

    public void delete(UUID id) {
        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }
}

