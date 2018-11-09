package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import com.rustedbrain.diploma.travelvisualizer.model.dto.HttpDTO;

import org.springframework.http.HttpStatus;

import java.util.List;

public class CommentDTOList extends HttpDTO {

    private List<CommentDTO> commentDTOList;

    public CommentDTOList(HttpStatus status, List<CommentDTO> commentDTOList) {
        super(status);
        this.commentDTOList = commentDTOList;
    }

    public List<CommentDTO> getCommentDTOList() {
        return commentDTOList;
    }

    public void setCommentDTOList(List<CommentDTO> commentDTOList) {
        this.commentDTOList = commentDTOList;
    }
}
