package com.strcat.service;

public enum OAuthProviderEnum {
    GOOGLE("google"),
    KAKAO("kakao");

    private String providerName;

    OAuthProviderEnum(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public static int toEnum(String provider) {
        return switch (provider) {
            case "kakao" -> KAKAO.ordinal();
            case "google" -> GOOGLE.ordinal();
            default -> throw new IllegalArgumentException("Unknown OAuth Provider");
        };
    }
}
