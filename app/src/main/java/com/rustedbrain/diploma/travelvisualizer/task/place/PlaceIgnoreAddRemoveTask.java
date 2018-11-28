package com.rustedbrain.diploma.travelvisualizer.task.place;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceIgnoredDTO;
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

public class PlaceIgnoreAddRemoveTask extends AsyncTask<Void, Void, ResponseEntity<PlaceIgnoredDTO>> {

    private final List<PlaceIgnoreAddRemoveTask.Listener> taskListeners;
    private final AuthUserDTO userDTO;
    private final double placeLatitude;
    private final double placeLongitude;

    public PlaceIgnoreAddRemoveTask(double placeLatitude, double placeLongitude, AuthUserDTO userDTO, Listener listener) {
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.userDTO = userDTO;
        this.taskListeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<PlaceIgnoredDTO> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            NamingPlaceRequest request = new NamingPlaceRequest(userDTO.getUsername(), new LatLngDTO(placeLatitude, placeLongitude));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<NamingPlaceRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.PLACE_IGNORE_URL)),
                    entity, PlaceIgnoredDTO.class);
        } catch (ResourceAccessException | HttpClientErrorException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<PlaceIgnoredDTO> responseEntity) {
        for (PlaceIgnoreAddRemoveTask.Listener listener : taskListeners) {
            listener.setPlaceIgnoreAddRemoveTask(null);
            listener.showPlaceIgnoreTaskProgress(false);
        }

        if (responseEntity == null) {
            for (PlaceIgnoreAddRemoveTask.Listener listener : taskListeners) {
                listener.showGetPlaceDescriptionDTOTaskError();
            }
        } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            for (PlaceIgnoreAddRemoveTask.Listener listener : taskListeners) {
                listener.showPlaceIgnore(responseEntity.getBody().isPlaceIgnored());
            }
        } else {
            for (PlaceIgnoreAddRemoveTask.Listener listener : taskListeners) {
                listener.showGetPlaceDescriptionDTOTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (PlaceIgnoreAddRemoveTask.Listener listener : taskListeners) {
            listener.setPlaceIgnoreAddRemoveTask(null);
            listener.showPlaceIgnoreTaskProgress(false);
        }
    }

    public interface Listener {

        void setPlaceIgnoreAddRemoveTask(PlaceIgnoreAddRemoveTask placeIgnoreAddRemoveTask);

        void showPlaceIgnoreTaskProgress(boolean show);

        void showGetPlaceDescriptionDTOTaskError();

        void showPlaceIgnore(boolean placeIgnored);
    }
}
