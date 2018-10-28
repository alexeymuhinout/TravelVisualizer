package com.rustedbrain.diploma.travelvisualizer;

public class TravelAppUtils {

    private static final String TRAVEL_URL = "/travel";
    public static final String PLACE_ADD_URL = TRAVEL_URL + "/place/add";
    public static final String PLACE_GET_MAP_DESCRIPTION_URL = TRAVEL_URL + "/place/description/get";
    public static final String PLACE_GET_BOUNDS_URL = TRAVEL_URL + "/place/get/bounds";
    private static final String BASE_URL = "http://10.0.3.2:8080";
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
