package com.rustedbrain.diploma.travelvisualizer.task;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceIgnoredDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.UserPlaceRequest;

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
import java.util.Collections;
import java.util.List;

public class PlaceIgnoreAddRemoveTask extends AsyncTask<Void, Void, PlaceIgnoredDTO> {

    private final List<PlaceIgnoreAddRemoveTask.Listener> taskListeners;
    private final UserDTO userDTO;
    private final double placeLatitude;
    private final double placeLongitude;

    public PlaceIgnoreAddRemoveTask(double placeLatitude, double placeLongitude, UserDTO userDTO, Listener listener) {
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.userDTO = userDTO;
        this.taskListeners = Collections.singletonList(listener);
    }

    @Override
    protected PlaceIgnoredDTO doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            UserPlaceRequest request = new UserPlaceRequest(userDTO.getUsername(), new LatLngDTO(placeLatitude, placeLongitude));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<UserPlaceRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForObject(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.PLACE_IGNORE_URL)),
                    entity, PlaceIgnoredDTO.class);
        } catch (HttpClientErrorException e) {
            Log.e(LoginActivity.class.getName(), e.getMessage(), e);
            try {
                return new ObjectMapper().readValue(e.getResponseBodyAsString(), PlaceIgnoredDTO.class);
            } catch (IOException e1) {
                return null;
            }
        } catch (ResourceAccessException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final PlaceIgnoredDTO placeIgnoreDTO) {
        for (PlaceIgnoreAddRemoveTask.Listener listener : taskListeners) {
            listener.setPlaceIgnoreAddRemoveTask(null);
            listener.showPlaceIgnoreTaskProgress(false);
        }

        if (placeIgnoreDTO == null) {
            for (PlaceIgnoreAddRemoveTask.Listener listener : taskListeners) {
                listener.showGetPlaceDescriptionDTOTaskError();
            }
        } else if (HttpStatus.OK.equals(placeIgnoreDTO.getStatus())) {
            for (PlaceIgnoreAddRemoveTask.Listener listener : taskListeners) {
                listener.showPlaceIgnore(placeIgnoreDTO.isPlaceIgnored());
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
