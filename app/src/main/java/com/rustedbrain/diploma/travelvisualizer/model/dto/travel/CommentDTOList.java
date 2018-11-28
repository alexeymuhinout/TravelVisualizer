package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import java.util.List;

public class CommentDTOList {

    private List<CommentDTO> commentDTOList;

    public CommentDTOList() {
    }

    public CommentDTOList(List<CommentDTO> commentDTOList) {
        this.commentDTOList = commentDTOList;
    }

    public List<CommentDTO> getCommentDTOList() {
        return commentDTOList;
    }

    public void setCommentDTOList(List<CommentDTO> commentDTOList) {
        this.commentDTOList = commentDTOList;
    }
}
