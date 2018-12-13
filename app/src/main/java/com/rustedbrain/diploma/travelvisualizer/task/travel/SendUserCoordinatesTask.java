package com.rustedbrain.diploma.travelvisualizer.task.travel;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
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

public class SendUserCoordinatesTask extends AsyncTask<Void, Void, ResponseEntity<LatLngDTO>> {

    private final List<Listener> taskListeners;
    private final LatLngDTO latLngDTO;
    private final AuthUserDTO userDTO;

    public SendUserCoordinatesTask(AuthUserDTO userDTO, LatLngDTO latLngDTO, Listener listener) {
        this.userDTO = userDTO;
        this.latLngDTO = latLngDTO;
        this.taskListeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<LatLngDTO> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            NamingPlaceRequest request = new NamingPlaceRequest(userDTO.getUsername(), latLngDTO);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<NamingPlaceRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.TRAVEL_SEND_USER_COORDINATES_URL)),
                    entity, LatLngDTO.class);
        } catch (ResourceAccessException | HttpClientErrorException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<LatLngDTO> responseEntity) {
        for (Listener listener : taskListeners) {
            listener.setSendUserCoordinatesTask(null);
            listener.showSendUserCoordinatesTaskProgress(false);
        }

        if (responseEntity == null) {
            for (Listener listener : taskListeners) {
                listener.showSendUserCoordinatesTaskError();
            }
        } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            for (Listener listener : taskListeners) {
                listener.showUserCoordinates(responseEntity.getBody());
            }
        } else {
            for (Listener listener : taskListeners) {
                listener.showSendUserCoordinatesTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (Listener listener : taskListeners) {
            listener.setSendUserCoordinatesTask(null);
            listener.showSendUserCoordinatesTaskProgress(false);
        }
    }

    public interface Listener {

        void setSendUserCoordinatesTask(SendUserCoordinatesTask sendUserCoordinatesTask);

        void showSendUserCoordinatesTaskProgress(boolean show);

        void showSendUserCoordinatesTaskError();

        void showUserCoordinates(LatLngDTO latLngDTO);
    }
}