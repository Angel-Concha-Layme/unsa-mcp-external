package pe.unsa.mcp.dto.mcp;

import java.util.List;
import java.util.stream.Collectors;

public record McpToolResponse(
        String toolName,
        ResponseType type,
        Object data,
        String textFallback
) {
    public enum ResponseType {
        TEXT,
        WIDGET_EVENT_INFO,
        WIDGET_AGENDA_LIST,
        WIDGET_SESSION_DETAIL,
        WIDGET_SEARCH_RESULTS,
        WIDGET_SEMANTIC_SEARCH,
        WIDGET_SPEAKER_DETAIL,
        WIDGET_SPEAKER_SEARCH,
        WIDGET_SPEAKER_SEARCH_SEMANTIC,
        WIDGET_CONTACT,
        WIDGET_AGENDA_NOW,
        WIDGET_HEALTH_STATUS,
        ERROR
    }

    public static McpToolResponse text(String toolName, String message) {
        return new McpToolResponse(toolName, ResponseType.TEXT, message, message);
    }

    public static McpToolResponse error(String toolName, String errorMessage) {
        return new McpToolResponse(toolName, ResponseType.ERROR, errorMessage, errorMessage);
    }

    public static McpToolResponse eventInfo(String toolName, EventInfoResponse event) {
        String fallback = String.format(
                "%s (%d)\n%s\nVenue: %s\nDates: %s to %s",
                event.name(),
                event.year(),
                event.description() != null ? event.description() : "",
                event.venue() != null ? event.venue() : "",
                event.dates() != null && event.dates().start() != null ? event.dates().start() : "",
                event.dates() != null && event.dates().end() != null ? event.dates().end() : ""
        );
        return new McpToolResponse(toolName, ResponseType.WIDGET_EVENT_INFO, event, fallback);
    }

    public static McpToolResponse agendaList(String toolName, List<AgendaItemResponse> agenda) {
        String fallback = agenda.stream()
                .map(item -> String.format("[%d] %s - %s (%s)",
                        item.seq(),
                        item.startsAt(),
                        item.title(),
                        item.speaker().name()))
                .collect(Collectors.joining("\n"));
        return new McpToolResponse(toolName, ResponseType.WIDGET_AGENDA_LIST, agenda, fallback);
    }

    public static McpToolResponse sessionDetail(String toolName, SessionDetailResponse session) {
        String fallback = String.format(
                "%s\nSpeaker: %s (%s)\nTime: %s - %s\n\n%s",
                session.title(),
                session.speaker().name(),
                session.speaker().org(),
                session.startsAt(),
                session.endsAt(),
                session.abstractText() != null ? session.abstractText() : ""
        );
        return new McpToolResponse(toolName, ResponseType.WIDGET_SESSION_DETAIL, session, fallback);
    }

    public static McpToolResponse searchResults(String toolName, List<SearchResultResponse> results) {
        String fallback = results.stream()
                .map(r -> String.format("%s\n%s", r.title(), r.snippet()))
                .collect(Collectors.joining("\n\n"));
        return new McpToolResponse(toolName, ResponseType.WIDGET_SEARCH_RESULTS, results, fallback);
    }

    public static McpToolResponse semanticSearch(String toolName, List<SemanticSearchResponse> results) {
        String fallback = results.stream()
                .map(r -> String.format("%s (Speaker: %s)\n%s",
                        r.title(),
                        r.speaker().name(),
                        r.abstractText() != null ? r.abstractText() : ""))
                .collect(Collectors.joining("\n\n"));
        return new McpToolResponse(toolName, ResponseType.WIDGET_SEMANTIC_SEARCH, results, fallback);
    }

    public static McpToolResponse speakerDetail(String toolName, SpeakerDetailResponse speaker) {
        String fallback = String.format(
                "%s - %s at %s\n%s\nSession: %s",
                speaker.fullName(),
                speaker.jobTitle() != null ? speaker.jobTitle() : "",
                speaker.orgName() != null ? speaker.orgName() : "",
                speaker.bio() != null ? speaker.bio() : "",
                speaker.session() != null ? speaker.session().title() : "No session assigned"
        );
        return new McpToolResponse(toolName, ResponseType.WIDGET_SPEAKER_DETAIL, speaker, fallback);
    }

    public static McpToolResponse speakerSearch(String toolName, List<?> results) {
        String fallback = results.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        return new McpToolResponse(toolName, ResponseType.WIDGET_SPEAKER_SEARCH, results, fallback);
    }

    public static McpToolResponse speakerSearchSemantic(String toolName, List<?> results) {
        String fallback = results.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        return new McpToolResponse(toolName, ResponseType.WIDGET_SPEAKER_SEARCH_SEMANTIC, results, fallback);
    }

    public static McpToolResponse contact(String toolName, Object contactData) {
        String fallback = "Contact information: " + contactData.toString();
        return new McpToolResponse(toolName, ResponseType.WIDGET_CONTACT, contactData, fallback);
    }

    public static McpToolResponse agendaNow(String toolName, AgendaNowResponse agendaNow) {
        StringBuilder fallback = new StringBuilder("Current and Next Sessions:\n");
        if (agendaNow.current() != null) {
            fallback.append("Current: ").append(agendaNow.current().title()).append("\n");
        }
        if (agendaNow.next() != null) {
            fallback.append("Next: ").append(agendaNow.next().title()).append("\n");
        }
        return new McpToolResponse(toolName, ResponseType.WIDGET_AGENDA_NOW, agendaNow, fallback.toString());
    }

    public static McpToolResponse healthStatus(String toolName, HealthStatusResponse health) {
        String fallback = String.format(
                "Health Status:\nDatabase: %s\nEmbeddings: %s\nAvailable years: %s",
                health.db(),
                health.embeddings(),
                health.eventYears()
        );
        return new McpToolResponse(toolName, ResponseType.WIDGET_HEALTH_STATUS, health, fallback);
    }
}

