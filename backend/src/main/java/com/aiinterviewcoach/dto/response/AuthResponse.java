package com.aiinterviewcoach.dto.response;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {
    @Override
    public String toString() {
        return "AuthResponse[accessToken=[REDACTED], tokenType=" + tokenType
                + ", expiresIn=" + expiresIn + ", user=" + user + "]";
    }
}
