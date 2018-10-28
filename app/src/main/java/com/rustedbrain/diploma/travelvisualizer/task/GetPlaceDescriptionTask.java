package com.rustedbrain.diploma.travelvisualizer.task;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDescriptionDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.GetPlaceDescriptionDTORequest;

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

public class GetPlaceDescriptionTask extends AsyncTask<Void, Void, PlaceDescriptionDTO> {

    private final List<Listener> taskListeners = new ArrayList<>();
    private final UserDTO userDTO;
    private final double placeLatitude;
    private final double placeLongitude;

    public GetPlaceDescriptionTask(double placeLatitude, double placeLongitude, UserDTO userDTO) {
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.userDTO = userDTO;
    }

    public void addListener(Listener listener) {
        this.taskListeners.add(listener);
    }

    @Override
    protected PlaceDescriptionDTO doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            GetPlaceDescriptionDTORequest request = new GetPlaceDescriptionDTORequest(new LatLngDTO(placeLatitude, placeLongitude));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<GetPlaceDescriptionDTORequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForObject(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.PLACE_GET_MAP_DESCRIPTION_URL)),
                    entity, PlaceDescriptionDTO.class);
        } catch (HttpClientErrorException e) {
            Log.e(LoginActivity.class.getName(), e.getMessage(), e);
            try {
                return new ObjectMapper().readValue(e.getResponseBodyAsString(), PlaceDescriptionDTO.class);
            } catch (IOException e1) {
                return null;
            }
        } catch (ResourceAccessException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final PlaceDescriptionDTO placeDescriptionDTO) {
        for (Listener listener : taskListeners) {
            listener.setGetPlaceDescriptionTask(null);
            listener.showProgress(false);
        }

        if (placeDescriptionDTO == null) {
            for (Listener listener : taskListeners) {
                listener.showGetPlaceDescriptionDTOTaskError();
            }
        } else if (HttpStatus.OK.equals(placeDescriptionDTO.getStatus())) {
            for (Listener listener : taskListeners) {
                listener.showPlaceDescriptionDTO(placeDescriptionDTO);
            }
        } else {
            for (Listener listener : taskListeners) {
                listener.showGetPlaceDescriptionDTOTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (Listener listener : taskListeners) {
            listener.setGetPlaceDescriptionTask(null);
            listener.showProgress(false);
        }
    }

    public interface Listener {

        void setGetPlaceDescriptionTask(GetPlaceDescriptionTask getPlaceDescriptionTask);

        void showProgress(boolean show);

        void showGetPlaceDescriptionDTOTaskError();

        void showPlaceDescriptionDTO(PlaceDescriptionDTO placeDescriptionDTO);
    }
}