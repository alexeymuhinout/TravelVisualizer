package com.rustedbrain.diploma.travelvisualizer.task.travel;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTOList;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.NamingRequest;

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

public class GetUserTravelsTask extends AsyncTask<Void, Void, ResponseEntity<TravelDTOList>> {

    private final List<GetUserTravelsTask.Listener> taskListeners;
    private final AuthUserDTO userDTO;

    public GetUserTravelsTask(AuthUserDTO userDTO, Listener listener) {
        this.userDTO = userDTO;
        taskListeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<TravelDTOList> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            NamingRequest request = new NamingRequest(userDTO.getUsername());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<NamingRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.TRAVEL_GET_BY_USERNAME_URL)),
                    entity, TravelDTOList.class);
        } catch (ResourceAccessException | HttpClientErrorException | URISyntaxException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<TravelDTOList> responseEntity) {
        for (GetUserTravelsTask.Listener listener : taskListeners) {
            listener.setGetUserTravelsTask(null);
            listener.showGetUserTravelsTaskProgress(false);
        }

        if (responseEntity == null) {
            for (GetUserTravelsTask.Listener listener : taskListeners) {
                listener.showGetUserTravelsTaskError();
            }
        } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            for (GetUserTravelsTask.Listener listener : taskListeners) {
                listener.onTravelsLoadSuccess(responseEntity.getBody().getTravelDTOList());
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

        void onTravelsLoadSuccess(List<TravelDTO> travels);
    }
}