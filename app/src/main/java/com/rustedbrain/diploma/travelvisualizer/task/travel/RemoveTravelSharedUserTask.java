package com.rustedbrain.diploma.travelvisualizer.task.travel;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.TravelSharedUsersSetRequest;

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RemoveTravelSharedUserTask extends AsyncTask<Void, Void, ResponseEntity<TravelDTO>> {

    private final List<Listener> taskListeners;
    private final AuthUserDTO userDTO;
    private final String username;
    private final String travelName;
    private final Collection<String> usernames;

    public RemoveTravelSharedUserTask(AuthUserDTO userDTO, String username, String travelName, Collection<String> usernames, Listener listener) {
        this.userDTO = userDTO;
        this.username = username;
        this.travelName = travelName;
        this.usernames = usernames;
        this.taskListeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<TravelDTO> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            TravelSharedUsersSetRequest request = new TravelSharedUsersSetRequest(travelName, username, usernames);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<TravelSharedUsersSetRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.TRAVEL_SHARED_USER_ADD_REMOVE_URL)),
                    entity, TravelDTO.class);
        } catch (ResourceAccessException | HttpClientErrorException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<TravelDTO> responseEntity) {
        for (Listener listener : taskListeners) {
            listener.setRemoveTravelSharedUserTask(null);
            listener.showRemoveTravelSharedUserTaskProgress(false);
        }

        if (responseEntity == null) {
            for (Listener listener : taskListeners) {
                listener.showRemoveTravelSharedUserTaskError();
            }
        } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            for (Listener listener : taskListeners) {
                listener.showRemovedSharedUserTravel(responseEntity.getBody());
            }
        } else {
            for (Listener listener : taskListeners) {
                listener.showRemoveTravelSharedUserTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (Listener listener : taskListeners) {
            listener.setRemoveTravelSharedUserTask(null);
            listener.showRemoveTravelSharedUserTaskProgress(false);
        }
    }

    public interface Listener {

        void setRemoveTravelSharedUserTask(RemoveTravelSharedUserTask removeTravelSharedUserTask);

        void showRemoveTravelSharedUserTaskProgress(boolean show);

        void showRemoveTravelSharedUserTaskError();

        void showRemovedSharedUserTravel(TravelDTO travelDTO);
    }
}