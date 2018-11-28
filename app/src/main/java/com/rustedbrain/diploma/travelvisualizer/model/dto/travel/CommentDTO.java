package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

public class CommentDTO {

    private String authorLogin;
    private float rating;
    private String text;

    public CommentDTO() {
    }

    public CommentDTO(String authorLogin, float rating, String text) {
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
