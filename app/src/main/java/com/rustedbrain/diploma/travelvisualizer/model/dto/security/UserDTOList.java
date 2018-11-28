package com.rustedbrain.diploma.travelvisualizer.model.dto.security;

import java.util.Collection;

public class UserDTOList {

    private Collection<UserDTO> userDTOList;

    public UserDTOList() {
    }

    public UserDTOList(Collection<UserDTO> userDTOList) {
        this.userDTOList = userDTOList;
    }

    public Collection<UserDTO> getUserDTOList() {
        return userDTOList;
    }

    public void setUserDTOList(Collection<UserDTO> userDTOList) {
        this.userDTOList = userDTOList;
    }

    @Override
    public String toString() {
        return "UserDTOList{" +
                "userDTOList=" + userDTOList +
                '}';
    }
}
