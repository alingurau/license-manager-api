package com.fortech.model.security.jwt;

public class SecurityConstants {

    private SecurityConstants(){}

    public static final String SECRET = "1234567890";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/sign-up";
    public static final String SIGN_IN_URL = "/sign-in";
}