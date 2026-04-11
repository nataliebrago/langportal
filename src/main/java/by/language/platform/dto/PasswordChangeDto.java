package by.language.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeDto {

    @NotBlank(message = "Текущий пароль не может быть пустым")
    private String currentPassword;

    @NotBlank(message = "Новый пароль не может быть пустым")
    @Size(min = 6, message = "Новый пароль должен быть не короче 6 символов")
    private String newPassword;

    public PasswordChangeDto() {
    }

    public PasswordChangeDto(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

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


}