package com.bootcamp19.gatewayservice.dto.constant;

public class ConstantVariable {
    private ConstantVariable() {}

    public static final String HEADER_API_KEY = "APIKey";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";

    // endpoint yang boleh diakses TANPA token
    public static final String[] PUBLIC_ENDPOINT = {
            "/gateway/user/rest/v1/auth/login",
            "/gateway/user/rest/v1/users/register"
    };
}
