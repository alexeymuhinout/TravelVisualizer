package com.rustedbrain.diploma.travelvisualizer;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

public class HttpUtils {

    private static final String TRAVEL_URL = "/travel";
    public static final String PLACE_ADD_URL = TRAVEL_URL + "/place/add";
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static final String LOGIN_URL = "/login";
    public static final String AUTHENTICATE_URL = LOGIN_URL + "/authenticate";
    public static final String REGISTER_URL = LOGIN_URL + "/register";
    private static SyncHttpClient client = new SyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
