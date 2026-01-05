package org.creatorledger.event.domain;

public record ClientName(String value) {

    private static final int MAX_LENGTH = 200;

    public static ClientName of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ClientName cannot be null or blank");
        }

        String trimmed = value.trim();

        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("ClientName cannot exceed " + MAX_LENGTH + " characters");
        }

        return new ClientName(trimmed);
    }

    @Override
    public String toString() {
        return "ClientName[" + value + "]";
    }
}
