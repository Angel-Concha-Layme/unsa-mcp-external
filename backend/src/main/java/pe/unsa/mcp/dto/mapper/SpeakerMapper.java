package pe.unsa.mcp.dto.mapper;

import pe.unsa.mcp.dto.SpeakerRequest;
import pe.unsa.mcp.dto.SpeakerResponse;
import pe.unsa.mcp.model.Speaker;

import java.util.Map;

public class SpeakerMapper {

    public static Speaker toEntity(SpeakerRequest request) {
        Speaker speaker = new Speaker();
        speaker.setFullName(request.fullName());
        speaker.setOrgName(request.orgName());
        speaker.setJobTitle(request.jobTitle());
        speaker.setBio(request.bio());
        speaker.setProfileImageUrl(request.profileImageUrl());
        speaker.setContacts(request.contacts() != null ? request.contacts() : Map.of());
        return speaker;
    }

    public static SpeakerResponse toResponse(Speaker speaker) {
        return new SpeakerResponse(
            speaker.getId(),
            speaker.getFullName(),
            speaker.getOrgName(),
            speaker.getJobTitle(),
            speaker.getBio(),
            speaker.getProfileImageUrl(),
            speaker.getContacts(),
            speaker.getCreatedAt(),
            speaker.getUpdatedAt()
        );
    }

    public static void updateEntity(Speaker speaker, SpeakerRequest request) {
        speaker.setFullName(request.fullName());
        speaker.setOrgName(request.orgName());
        speaker.setJobTitle(request.jobTitle());
        speaker.setBio(request.bio());
        speaker.setProfileImageUrl(request.profileImageUrl());
        if (request.contacts() != null) {
            speaker.setContacts(request.contacts());
        }
    }
}

