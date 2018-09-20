package com.rustedbrain.diploma.travelvisualizer.model.dto.security;

public class EditProfileRequest extends RegistrationRequest {

    private String targetEditLoginOrEmail;

    public String getTargetEditLoginOrEmail() {
        return targetEditLoginOrEmail;
    }

    public void setTargetEditLoginOrEmail(String targetEditLoginOrEmail) {
        this.targetEditLoginOrEmail = targetEditLoginOrEmail;
    }
}
