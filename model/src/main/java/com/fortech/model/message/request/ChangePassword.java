package com.fortech.model.message.request;

import javax.validation.constraints.NotNull;

public class ChangePassword {

    @NotNull
    private String currentPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmPassword;


    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isValid() {
        return newPassword != null && confirmPassword != null;
    }
}
