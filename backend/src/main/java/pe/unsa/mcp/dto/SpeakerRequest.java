package pe.unsa.mcp.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record SpeakerRequest(
    @NotBlank String fullName,
    String orgName,
    String jobTitle,
    String bio,
    String profileImageUrl,
    Map<String, Object> contacts
) {
}

