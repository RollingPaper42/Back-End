package com.strcat.service;

public enum OAuthProviderEnum {
    GOOGLE("kakao"),
    KAKAO("google");

    private String providerName;

    OAuthProviderEnum(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public static int changeCode(String provider) {
        if (provider.equals("kakao")) {
            return KAKAO.ordinal();
        } else if (provider.equals("google")) {
            return GOOGLE.ordinal();
        }
        throw new IllegalArgumentException("Unknown OAuth Provider");
    }
}
