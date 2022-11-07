package com.ltl.mpmp_lab3.utility;

import android.content.Context;
import android.util.Log;

import com.ltl.mpmp_lab3.AppConfig;

import java.io.Serializable;
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

public class MailSender implements Serializable {

    boolean isEmailSent = false;
    Context context;
    private static volatile MailSender instance;

    public static MailSender getInstance(Context context) {
        MailSender result = instance;
        if (result != null) {
            return result;
        }
        synchronized(MailSender.class) {
            if (instance == null) {
                instance = new MailSender(context);
            }
            return instance;
        }
    }

    private MailSender(Context context) {
        this.context = context;
    }

    public MailSender(boolean isEmailSent, Context context) {
        this.isEmailSent = isEmailSent;
        this.context = context;
    }

    public void sendInNotSent(String userEmail, String displayName, int points){

        if (isEmailSent) return;

        new Thread(() -> {
            Log.d("game_result_activity", "sendEmail() started");

            String senderEmail = AppConfig.getConfigValue(context, "game_email");
            String senderPassword = AppConfig.getConfigValue(context, "email_password");

            Properties properties = setProperties();

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            MimeMessage mimeMessage = createMessage(session, userEmail, displayName, points);

            sendNoThread(mimeMessage);
        }).start();
    }

    private void sendNoThread(MimeMessage message){
        try {
            isEmailSent = true;
            Transport.send(message);
            Log.d("game_result_activity", "email sent");
        } catch (MessagingException e) {
            isEmailSent = false;
            e.printStackTrace();
            Log.d("game_result_activity", "email was not sent");
        }
    }

    private MimeMessage createMessage(Session session, String userEmail, String displayName, int points) {
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            mimeMessage.setSubject("Game results");
            mimeMessage.setText(
                    String.format(
                            Locale.ENGLISH,
                            "Hello %s, \nYou gained %d points!", displayName, points));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return mimeMessage;
    }

    public boolean isEmailSent() {
        return isEmailSent;
    }

//    TODO: delete all below
    private void sendEmailIfNotSent(String userEmail, String displayName, int points) {

        if (isEmailSent) return;

        Log.d("game_result_activity", "sendEmail() started");

        try {
            String senderEmail = AppConfig.getConfigValue(context, "game_email");
            String senderPassword = AppConfig.getConfigValue(context, "email_password");

            Properties properties = setProperties();

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));

            mimeMessage.setSubject("Game results");
            mimeMessage.setText(
                    String.format(
                            Locale.ENGLISH,
                            "Hello %s, \nYou gained %d points!", displayName, points));

            sendAsync(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendAsync(MimeMessage message){
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
    }

    private boolean sendEmail(String userEmail, String displayName, int points) {

        Log.d("game_result_activity", "sendEmail() started");

        try {
            String senderEmail = AppConfig.getConfigValue(context, "game_email");
            String passwordSenderEmail = AppConfig.getConfigValue(context, "email_password");

            Properties properties = setProperties();

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

    private Properties setProperties(){
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        return properties;
    }

    private void send(MimeMessage message) throws MessagingException{
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
