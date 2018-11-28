package com.rustedbrain.diploma.travelvisualizer.task.place;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDescriptionDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.TravelPlaceAddRequest;

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

public class TravelPlaceModifyTask extends AsyncTask<Void, Void, ResponseEntity<PlaceDescriptionDTO>> {

    private final String travelName;
    private final AuthUserDTO userDTO;
    private final LatLngDTO placeLatLng;
    private final List<Listener> travelPlaceAddListeners;

    public TravelPlaceModifyTask(String travelName, LatLngDTO placeLatLng, AuthUserDTO userDTO, Listener listener) {
        this.travelName = travelName;
        this.placeLatLng = placeLatLng;
        this.userDTO = userDTO;
        this.travelPlaceAddListeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<PlaceDescriptionDTO> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            TravelPlaceAddRequest request = new TravelPlaceAddRequest(travelName, placeLatLng, userDTO.getUsername());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<TravelPlaceAddRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.TRAVEL_PLACE_MODIFY_URL)),
                    entity, PlaceDescriptionDTO.class);
        } catch (ResourceAccessException | URISyntaxException | HttpClientErrorException ex) {
            Log.d(MainActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<PlaceDescriptionDTO> responseEntity) {
        for (Listener listener : travelPlaceAddListeners) {
            listener.setTravelPlaceModifyTask(null);
            listener.showTravelPlaceAddTaskProgress(false);
        }

        if (responseEntity == null) {
            for (Listener listener : travelPlaceAddListeners) {
                listener.showTravelPlaceAddTaskError();
            }
        } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            for (Listener listener : travelPlaceAddListeners) {
                listener.fireTravelPlaceAdded(responseEntity.getBody());
            }
        } else {
            for (Listener listener : travelPlaceAddListeners) {
                listener.showTravelPlaceAddTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (Listener listener : travelPlaceAddListeners) {
            listener.setTravelPlaceModifyTask(null);
            listener.showTravelPlaceAddTaskProgress(false);
        }
    }

    public interface Listener {

        void setTravelPlaceModifyTask(TravelPlaceModifyTask travelPlaceModifyTask);

        void showTravelPlaceAddTaskProgress(boolean show);

        void showTravelPlaceAddTaskError();

        void fireTravelPlaceAdded(PlaceDescriptionDTO placeDescriptionDTO);
    }
}
