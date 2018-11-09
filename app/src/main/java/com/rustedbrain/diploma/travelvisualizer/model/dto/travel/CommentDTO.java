package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import com.rustedbrain.diploma.travelvisualizer.model.dto.HttpDTO;

import org.springframework.http.HttpStatus;

public class CommentDTO extends HttpDTO {

    private String authorLogin;
    private float rating;
    private String text;

    public CommentDTO() {
    }

    public CommentDTO(HttpStatus status, String authorLogin, float rating, String text) {
        super(status);
        this.authorLogin = authorLogin;
        this.rating = rating;
        this.text = text;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
