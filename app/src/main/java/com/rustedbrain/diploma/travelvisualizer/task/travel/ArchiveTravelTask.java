package com.rustedbrain.diploma.travelvisualizer.task.travel;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.AuthorizedNamingRequest;

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

public class ArchiveTravelTask extends AsyncTask<Void, Void, ResponseEntity<TravelDTO>> {

    private final String username;
    private final String travelName;
    private final AuthUserDTO userDTO;
    private final List<Listener> travelPlaceAddListeners;

    public ArchiveTravelTask(String username, String travelName, AuthUserDTO userDTO, Listener listener) {
        this.username = username;
        this.travelName = travelName;
        this.userDTO = userDTO;
        this.travelPlaceAddListeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<TravelDTO> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            AuthorizedNamingRequest request = new AuthorizedNamingRequest(travelName, username);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<AuthorizedNamingRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.TRAVEL_ARCHIVE_URL)),
                    entity, TravelDTO.class);
        } catch (ResourceAccessException | URISyntaxException | HttpClientErrorException ex) {
            Log.d(MainActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<TravelDTO> responseEntity) {
        for (Listener listener : travelPlaceAddListeners) {
            listener.setArchiveTravelTask(null);
            listener.showArchiveTravelTaskProgress(false);
        }

        if (responseEntity == null) {
            for (Listener listener : travelPlaceAddListeners) {
                listener.showArchiveTravelTaskError();
            }
        } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            for (Listener listener : travelPlaceAddListeners) {
                listener.fireTravelArchived(responseEntity.getBody());
            }
        } else {
            for (Listener listener : travelPlaceAddListeners) {
                listener.showArchiveTravelTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (Listener listener : travelPlaceAddListeners) {
            listener.setArchiveTravelTask(null);
            listener.showArchiveTravelTaskProgress(false);
        }
    }

    public interface Listener {

        void setArchiveTravelTask(ArchiveTravelTask archiveTravelTask);

        void showArchiveTravelTaskProgress(boolean show);

        void showArchiveTravelTaskError();

        void fireTravelArchived(TravelDTO travelDTO);
    }
}