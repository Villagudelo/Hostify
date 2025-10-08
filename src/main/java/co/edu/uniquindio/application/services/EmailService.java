package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.model.entity.Booking;

public interface EmailService {
    void sendResetCodeEmail(String email, String code) throws ValidationException;

    void sendNewCommentNotification(String hostEmail, String guestName, String placeTitle, String commentText, int rating);

    void sendBookingConfirmation(String userEmail, Booking booking);
}
