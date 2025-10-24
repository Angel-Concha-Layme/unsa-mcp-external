package pe.unsa.mcp.dto.mapper;

import pe.unsa.mcp.dto.SessionRequest;
import pe.unsa.mcp.dto.SessionResponse;
import pe.unsa.mcp.model.Event;
import pe.unsa.mcp.model.Session;
import pe.unsa.mcp.model.Speaker;

public class SessionMapper {

    public static Session toEntity(SessionRequest request, Event event, Speaker speaker) {
        Session session = new Session();
        updateEntity(session, request, event, speaker);
        return session;
    }

    public static SessionResponse toResponse(Session session) {
        return new SessionResponse(
            session.getId(),
            session.getEvent().getId(),
            session.getSpeaker().getId(),
            session.getTitle(),
            session.getAbstractText(),
            session.getDay(),
            session.getStartsAt(),
            session.getEndsAt(),
            session.getSeq(),
            session.getTrack(),
            session.getCreatedAt(),
            session.getUpdatedAt()
        );
    }

    public static void updateEntity(Session session, SessionRequest request, Event event, Speaker speaker) {
        session.setEvent(event);
        session.setSpeaker(speaker);
        session.setTitle(request.title());
        session.setAbstractText(request.abstractText());
        session.setDay(request.day());
        session.setStartsAt(request.startsAt());
        session.setEndsAt(request.endsAt());
        session.setSeq(request.seq());
        session.setTrack(request.track());
    }
}

