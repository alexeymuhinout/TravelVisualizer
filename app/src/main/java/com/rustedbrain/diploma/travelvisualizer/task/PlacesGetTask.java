package com.rustedbrain.diploma.travelvisualizer.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.rustedbrain.diploma.travelvisualizer.HttpUtils;
import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.model.dto.LatLngBoundsDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTOList;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.GetPlacesRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PlacesGetTask extends AsyncTask<Void, Void, PlaceMapDTOList> {

    private final List<PlacesGetTaskListener> placesGetTaskListeners = new ArrayList<>();
    private final UserDTO userDTO;
    private final LatLngBoundsDTO latLngBoundsDTO;

    public PlacesGetTask(LatLngBounds bounds, UserDTO userDTO) {
        this.latLngBoundsDTO = new LatLngBoundsDTO(bounds);
        this.userDTO = userDTO;
    }

    public void addShowplacesGetTaskListener(PlacesGetTaskListener placesGetTaskListener) {
        this.placesGetTaskListeners.add(placesGetTaskListener);
    }

    @Override
    protected PlaceMapDTOList doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            GetPlacesRequest request = new GetPlacesRequest(latLngBoundsDTO);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<GetPlacesRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForObject(new URI(HttpUtils.getAbsoluteUrl(HttpUtils.PLACE_GET_BOUNDS_URL)),
                    entity, PlaceMapDTOList.class);
        } catch (HttpClientErrorException e) {
            Log.e(LoginActivity.class.getName(), e.getMessage(), e);
            try {
                return new ObjectMapper().readValue(e.getResponseBodyAsString(), PlaceMapDTOList.class);
            } catch (IOException e1) {
                return null;
            }
        } catch (ResourceAccessException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final PlaceMapDTOList placeMapDTOList) {
        for (PlacesGetTaskListener listener : placesGetTaskListeners) {
            listener.setPlacesGetTask(null);
            listener.showProgress(false);
        }

        if (placeMapDTOList == null) {
            for (PlacesGetTaskListener listener : placesGetTaskListeners) {
                listener.showPlacesGetTaskError();
            }
        } else if (HttpStatus.OK.equals(placeMapDTOList.getStatus())) {
            for (PlacesGetTaskListener listener : placesGetTaskListeners) {
                listener.showPlaceMapDTOList(placeMapDTOList);
            }
        } else {
            for (PlacesGetTaskListener listener : placesGetTaskListeners) {
                listener.showPlacesGetTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (PlacesGetTaskListener listener : placesGetTaskListeners) {
            listener.setPlacesGetTask(null);
            listener.showProgress(false);
        }
    }

    public interface PlacesGetTaskListener {

        void setPlacesGetTask(PlacesGetTask placesGetTask);

        void showProgress(boolean show);

        void showPlacesGetTaskError();

        void showPlaceMapDTOList(PlaceMapDTOList placeMapDTOList);
    }
}