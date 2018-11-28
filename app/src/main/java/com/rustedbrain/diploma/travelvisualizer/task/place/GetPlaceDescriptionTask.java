package com.rustedbrain.diploma.travelvisualizer.task.place;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDescriptionDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.NamingPlaceRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class GetPlaceDescriptionTask extends AsyncTask<Void, Void, ResponseEntity<PlaceDescriptionDTO>> {

    private final List<Listener> taskListeners;
    private final AuthUserDTO userDTO;
    private final double placeLatitude;
    private final double placeLongitude;

    public GetPlaceDescriptionTask(double placeLatitude, double placeLongitude, AuthUserDTO userDTO, Listener listener) {
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.userDTO = userDTO;
        this.taskListeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<PlaceDescriptionDTO> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            NamingPlaceRequest request = new NamingPlaceRequest(userDTO.getUsername(), new LatLngDTO(placeLatitude, placeLongitude));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.PLACE_GET_MAP_DESCRIPTION_URL)),
                    new HttpEntity<>(request, httpHeaders), PlaceDescriptionDTO.class);
        } catch (ResourceAccessException | HttpClientErrorException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }


    @Override
    protected void onPostExecute(final ResponseEntity<PlaceDescriptionDTO> responseEntity) {
        for (Listener listener : taskListeners) {
            listener.setGetPlaceDescriptionTask(null);
            listener.showProgress(false);
        }

        if (responseEntity == null) {
            for (Listener listener : taskListeners) {
                listener.showGetPlaceDescriptionDTOTaskError();
            }
        } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            for (Listener listener : taskListeners) {
                listener.showPlaceDescriptionDTO(responseEntity.getBody());
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