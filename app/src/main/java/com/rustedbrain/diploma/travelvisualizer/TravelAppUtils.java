package com.rustedbrain.diploma.travelvisualizer;

public class TravelAppUtils {


    private static final String TRAVEL_URL = "/travel";
    public static final String TRAVEL_GET_BY_USERNAME_URL = TRAVEL_URL + "/travel/username/get";
    public static final String TRAVEL_SHARED_SET_URL = TRAVEL_URL + "/travel/shared/set";
    public static final String TRAVEL_SEND_USER_COORDINATES_URL = TRAVEL_URL + "/travel/user/coordinates/set";
    public static final String TRAVEL_SHARED_USER_ADD_REMOVE_URL = TRAVEL_URL + "/travel/shared/modify";
    public static final String TRAVEL_ADD_URL = TRAVEL_URL + "/travel/add";
    public static final String TRAVEL_USERNAMES_URL = TRAVEL_URL + "/users/get";
    public static final String TRAVEL_ARCHIVE_URL = TRAVEL_URL + "/travel/archive";
    public static final String TRAVEL_PLACE_MODIFY_URL = TRAVEL_URL + "/travel/place/modify";
    public static final String PLACE_ADD_URL = TRAVEL_URL + "/place/add";
    public static final String PLACE_IGNORE_URL = TRAVEL_URL + "/place/ignore";
    public static final String PLACE_GET_MAP_DESCRIPTION_URL = TRAVEL_URL + "/place/description/get";
    public static final String PLACE_GET_BOUNDS_URL = TRAVEL_URL + "/place/get/bounds";
    public static final String COMMENT_ADD_URL = TRAVEL_URL + "/comment/add";
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static final String LOGIN_URL = "/login";
    public static final String AUTHENTICATE_URL = LOGIN_URL + "/authenticate";
    public static final String REGISTER_URL = LOGIN_URL + "/register";

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(TravelAppUtils.deg2rad(lat1)) * Math.sin(TravelAppUtils.deg2rad(lat2)) + Math.cos(TravelAppUtils.deg2rad(lat1)) * Math.cos(TravelAppUtils.deg2rad(lat2)) * Math.cos(TravelAppUtils.deg2rad(theta));
        dist = Math.acos(dist);
        dist = TravelAppUtils.rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
