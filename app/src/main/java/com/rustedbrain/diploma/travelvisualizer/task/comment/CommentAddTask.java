package com.rustedbrain.diploma.travelvisualizer.task.comment;

import android.os.AsyncTask;
import android.util.Log;

import com.rustedbrain.diploma.travelvisualizer.MainActivity;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.CommentDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request.CommentAddRequest;

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

public class CommentAddTask extends AsyncTask<Void, Void, ResponseEntity<CommentDTO>> {

    private final LatLngDTO placeLatLng;
    private final float rating;
    private final String text;
    private final AuthUserDTO userDTO;
    private final List<CommentAddTaskListener> commentAddTaskListeners;

    public CommentAddTask(LatLngDTO placeLatLng, float rating, String text, AuthUserDTO userDTO, CommentAddTaskListener listener) {
        this.placeLatLng = placeLatLng;
        this.rating = rating;
        this.text = text;
        this.userDTO = userDTO;
        this.commentAddTaskListeners = Collections.singletonList(listener);
    }

    @Override
    protected ResponseEntity<CommentDTO> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            CommentAddRequest request = new CommentAddRequest(placeLatLng, rating, text);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(MainActivity.AUTH_TOKEN_HEADER_NAME, userDTO.getToken());

            HttpEntity<CommentAddRequest> entity = new HttpEntity<>(request, httpHeaders);

            return restTemplate.postForEntity(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.COMMENT_ADD_URL)),
                    entity, CommentDTO.class);
        } catch (ResourceAccessException | URISyntaxException | HttpClientErrorException ex) {
            Log.d(MainActivity.class.getName(), ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ResponseEntity<CommentDTO> responseEntity) {
        for (CommentAddTaskListener listener : commentAddTaskListeners) {
            listener.setCommentAddTask(null);
            listener.showProgress(false);
        }

        if (responseEntity == null) {
            for (CommentAddTaskListener listener : commentAddTaskListeners) {
                listener.showCommentAddTaskError();
            }
        } else if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            for (CommentAddTaskListener listener : commentAddTaskListeners) {
                listener.addCreatedComment(responseEntity.getBody());
            }
        } else {
            for (CommentAddTaskListener listener : commentAddTaskListeners) {
                listener.showCommentAddTaskError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (CommentAddTaskListener listener : commentAddTaskListeners) {
            listener.setCommentAddTask(null);
            listener.showProgress(false);
        }
    }

    public interface CommentAddTaskListener {

        void setCommentAddTask(CommentAddTask commentAddTask);

        void showProgress(boolean show);

        void showCommentAddTaskError();

        void addCreatedComment(CommentDTO commentDTO);
    }
}