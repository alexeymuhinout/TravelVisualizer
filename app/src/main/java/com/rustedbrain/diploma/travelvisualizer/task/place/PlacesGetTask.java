package com.rustedbrain.diploma.travelvisualizer.task.place;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.LatLngBoundsDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTOList;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TripsMapPlacesFilterDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.GetPlacesRequest;

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
import java.util.ArrayList;
import java.util.List;

public class PlacesGetTask extends AsyncTask<Void, Void, ResponseEntity<PlaceMapDTOList>> {

    private final List<PlacesGetTaskListener> placesGetTaskListeners = new ArrayList<>();
    private final AuthUserDTO userDTO;
    private final LatLngBoundsDTO latLngBoundsDTO;
    private final TripsMapPlacesFilterDTO tripsMapPlacesFilterDTO;

    public PlacesGetTask(TripsMapPlacesFilterDTO tripsMapPlacesFilterDTO, LatLngBounds bounds, AuthUserDTO userDTO) {
        this.tripsMapPlacesFilterDTO = tripsMapPlacesFilterDTO;
        this.latLngBoundsDTO = new LatLngBoundsDTO(bounds);
        this.userDTO = userDTO;
    }

    public void addShowplacesGetTaskListener(PlacesGetTaskListener placesGetTaskListener) {
        this.placesGetTaskListeners.add(placesGetTaskListener);
    }

    @Override
    protected ResponseEntity<PlaceMapDTOList> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            GetPlacesRequest request = new GetPlacesRequest(latLngBoundsDTO, tripsMapPlacesFilterDTO);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<GetPlacesRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.PLACE_GET_BOUNDS_URL)),
                    entity, PlaceMapDTOList.class);
        } catch (ResourceAccessException | HttpClientErrorException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<PlaceMapDTOList> responseEntity) {
        for (PlacesGetTaskListener listener : placesGetTaskListeners) {
            listener.setPlacesGetTask(null);

            listener.showProgress(false);

            if (responseEntity == null) {
                listener.showPlacesGetTaskError();
            } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                listener.showPlaceMapDTOList(responseEntity.getBody());
            } else {
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