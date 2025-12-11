package com.platzi.config;

public class EndpointConfig {

    // ============ Authentication Endpoints ============
    public static final String AUTH_LOGIN = "/auth/login";
    public static final String AUTH_PROFILE = "/auth/profile";
    public static final String AUTH_REFRESH = "/auth/refresh-token";

    // ============ Products Endpoints ============
    public static final String PRODUCTS = "/products";
    public static final String PRODUCT_BY_ID = "/products/{id}";

    // ============ Users Endpoints ============
    public static final String USERS = "/users";
    public static final String USER_BY_ID = "/users/{id}";

    // ============ Categories Endpoints ============
    public static final String CATEGORIES = "/categories";
    public static final String CATEGORY_BY_ID = "/categories/{id}";
    public static final String CATEGORY_PRODUCTS = "/categories/{id}/products";
}