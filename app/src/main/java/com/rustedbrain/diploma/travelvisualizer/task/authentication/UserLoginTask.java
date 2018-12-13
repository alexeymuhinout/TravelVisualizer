package com.rustedbrain.diploma.travelvisualizer.task.authentication;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthenticationRequest;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;

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

public class UserLoginTask extends AsyncTask<Void, Void, ResponseEntity<AuthUserDTO>> {

    private final String email;
    private final String password;
    private List<Listener> listeners;

    public UserLoginTask(String email, String password, Listener listener) {
        this.email = email;
        this.password = password;
        this.listeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<AuthUserDTO> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            AuthenticationRequest request = new AuthenticationRequest(email, password);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.AUTHENTICATE_URL)),
                    request, AuthUserDTO.class);
        } catch (ResourceAccessException | URISyntaxException | HttpClientErrorException ex) {
            Log.d(LoginActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<AuthUserDTO> responseEntity) {
        for (Listener listener : listeners) {
            listener.setUserLoginTask(null);
            listener.showUserLoginTaskProgress(false);
            if (responseEntity == null) {
                listener.showUserLoginTaskUnknownError();
                //listener.showUserLoginTaskWrongCredentialsError();
            } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                listener.fireAuthSuccess(responseEntity.getBody());
            } else if (HttpStatus.NOT_FOUND.equals(responseEntity.getStatusCode())) {
                listener.showUserLoginTaskNotExistentUserError();
            } else {
                listener.showUserLoginTaskWrongCredentialsError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (Listener listener : listeners) {
            listener.setUserLoginTask(null);
            listener.showUserLoginTaskProgress(false);
        }
    }

    public interface Listener {

        void setUserLoginTask(UserLoginTask userLoginTask);

        void showUserLoginTaskProgress(boolean show);

        void showUserLoginTaskWrongCredentialsError();

        void showUserLoginTaskNotExistentUserError();

        void showUserLoginTaskUnknownError();

        void fireAuthSuccess(UserDTO user);
    }
}