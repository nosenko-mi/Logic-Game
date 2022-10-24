package com.ltl.mpmp_lab3;

import android.content.Context;
import android.util.Log;

import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    boolean isEmailSent = false;
    Context context;

    public MailSender(Context context) {
        this.context = context;
    }

    public boolean sendEmail(String userEmail, String displayName, int points) {
        Log.d("game_result_activity", "sendEmail() started");

        try {
            String senderEmail = Config.getConfigValue(context, "game_email");
            String passwordSenderEmail = Config.getConfigValue(context, "email_password");
//            String stringHost = "smtp.gmail.com";
//            Properties properties = System.getProperties();
//            properties.put("mail.smtp.host", stringHost);
//            properties.put("mail.smtp.port", "465");
//            properties.put("mail.smtp.ssl.enable", "true");
//            properties.put("mail.smtp.auth", "true");
            Properties properties = setProperties("smtp.gmail.com");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, passwordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));

            mimeMessage.setSubject("Game results");
            mimeMessage.setText(
                    String.format(
                            Locale.ENGLISH,
                            "Hello %s, \nYou gained %d points!", displayName, points));

            send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return isEmailSent;
    }

    private Properties setProperties(String  host){
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        return properties;
    }

    private void send(MimeMessage message){
        Thread thread = new Thread(() -> {
            try {
                isEmailSent = true;
                Transport.send(message);
                Log.d("game_result_activity", "email sent");
            } catch (MessagingException e) {
                isEmailSent = false;
                e.printStackTrace();
                Log.d("game_result_activity", "email was not sent");
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
