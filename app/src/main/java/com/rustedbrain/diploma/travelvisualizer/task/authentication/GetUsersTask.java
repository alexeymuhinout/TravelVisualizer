package com.rustedbrain.diploma.travelvisualizer.task.authentication;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTOList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class GetUsersTask extends AsyncTask<Void, Void, ResponseEntity<UserDTOList>> {

    private List<Listener> listeners;

    public GetUsersTask(Listener listener) {
        this.listeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<UserDTOList> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            return restTemplate.getForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.TRAVEL_USERNAMES_URL)),
                    UserDTOList.class);
        } catch (ResourceAccessException | URISyntaxException | HttpClientErrorException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<UserDTOList> responseEntity) {
        for (Listener listener : listeners) {
            listener.setGetUsersTask(null);
            listener.showGetUsersTaskProgress(false);
            if (responseEntity == null) {
                listener.showUserLoginTaskUnknownError();
            } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                listener.fireUsersRetrieved(responseEntity.getBody());
            } else {
                listener.showUserLoginTaskUnknownError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (Listener listener : listeners) {
            listener.setGetUsersTask(null);
            listener.showGetUsersTaskProgress(false);
        }
    }

    public interface Listener {

        void setGetUsersTask(GetUsersTask getUsersTask);

        void showGetUsersTaskProgress(boolean show);

        void showUserLoginTaskUnknownError();

        void fireUsersRetrieved(UserDTOList user);
    }
}