package com.rustedbrain.diploma.travelvisualizer;

public class HttpUtils {

    private static final String TRAVEL_URL = "/travel";
    public static final String PLACE_ADD_URL = TRAVEL_URL + "/place/add";
    public static final String PLACE_GET_BOUNDS_URL = TRAVEL_URL + "/place/get/bounds";
    private static final String BASE_URL = "http://192.168.154.102:8080";
    private static final String LOGIN_URL = "/login";
    public static final String AUTHENTICATE_URL = LOGIN_URL + "/authenticate";
    public static final String REGISTER_URL = LOGIN_URL + "/register";

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
