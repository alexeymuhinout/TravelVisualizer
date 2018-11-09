package com.rustedbrain.diploma.travelvisualizer.task;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelsDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.UserRequest;

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

public class GetUserTravelsTask extends AsyncTask<Void, Void, TravelsDTO> {

    private final List<GetUserTravelsTask.Listener> taskListeners;
    private final UserDTO userDTO;

    public GetUserTravelsTask(UserDTO userDTO, Listener listener) {
        this.userDTO = userDTO;
        taskListeners = Collections.singletonList(listener);
    }

    @Override
    protected TravelsDTO doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            UserRequest request = new UserRequest(userDTO.getUsername());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<UserRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForObject(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.TRAVEL_GET_BY_USERNAME_URL)),
                    entity, TravelsDTO.class);
        } catch (HttpClientErrorException e) {
            Log.e(LoginActivity.class.getName(), e.getMessage(), e);
            try {
                return new ObjectMapper().readValue(e.getResponseBodyAsString(), TravelsDTO.class);
            } catch (IOException e1) {
                return null;
            }
        } catch (ResourceAccessException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final TravelsDTO travelsDTO) {
        for (GetUserTravelsTask.Listener listener : taskListeners) {
            listener.setGetUserTravelsTask(null);
            listener.showGetUserTravelsTaskProgress(false);
        }

        if (travelsDTO == null) {
            for (GetUserTravelsTask.Listener listener : taskListeners) {
                listener.showGetUserTravelsTaskError();
            }
        } else if (HttpStatus.OK.equals(travelsDTO.getStatus())) {
            for (GetUserTravelsTask.Listener listener : taskListeners) {
                listener.showTravels(travelsDTO.getTravelsNames());
            }
        } else {
            for (GetUserTravelsTask.Listener listener : taskListeners) {
                listener.showGetUserTravelsTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (GetUserTravelsTask.Listener listener : taskListeners) {
            listener.setGetUserTravelsTask(null);
            listener.showGetUserTravelsTaskProgress(false);
        }
    }

    public interface Listener {

        void setGetUserTravelsTask(GetUserTravelsTask getUserTravelsTask);

        void showGetUserTravelsTaskProgress(boolean show);

        void showGetUserTravelsTaskError();

        void showTravels(List<String> travels);
    }
}