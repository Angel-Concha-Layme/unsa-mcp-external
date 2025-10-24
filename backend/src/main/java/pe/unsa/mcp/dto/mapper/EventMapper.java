package pe.unsa.mcp.dto.mapper;

import pe.unsa.mcp.dto.EventRequest;
import pe.unsa.mcp.dto.EventResponse;
import pe.unsa.mcp.model.Event;

import java.util.Map;

public class EventMapper {

    public static Event toEntity(EventRequest request) {
        Event event = new Event();
        event.setName(request.name());
        event.setYear(request.year());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setTz(request.tz() != null ? request.tz() : "America/Lima");
        event.setStartsOn(request.startsOn());
        event.setEndsOn(request.endsOn());
        event.setWebsiteUrl(request.websiteUrl());
        event.setContacts(request.contacts() != null ? request.contacts() : Map.of());
        return event;
    }

    public static EventResponse toResponse(Event event) {
        return new EventResponse(
            event.getId(),
            event.getName(),
            event.getYear(),
            event.getDescription(),
            event.getVenue(),
            event.getTz(),
            event.getStartsOn(),
            event.getEndsOn(),
            event.getWebsiteUrl(),
            event.getContacts(),
            event.getCreatedAt(),
            event.getUpdatedAt()
        );
    }

    public static void updateEntity(Event event, EventRequest request) {
        event.setName(request.name());
        event.setYear(request.year());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        if (request.tz() != null) {
            event.setTz(request.tz());
        }
        event.setStartsOn(request.startsOn());
        event.setEndsOn(request.endsOn());
        event.setWebsiteUrl(request.websiteUrl());
        if (request.contacts() != null) {
            event.setContacts(request.contacts());
        }
    }
}

