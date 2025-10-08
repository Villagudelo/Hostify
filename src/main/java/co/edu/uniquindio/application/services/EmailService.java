package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.exceptions.ValidationException;

public interface EmailService {
    void sendResetCodeEmail(String email, String code) throws ValidationException;

    void sendNewCommentNotification(String hostEmail, String guestName, String placeTitle, String commentText, int rating);
}
