package com.ltl.mpmp_lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ltl.mpmp_lab3.databinding.ActivityGameResultBinding;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GameResultActivity extends AppCompatActivity {

//    private final FileHandler fileHandler = FileHandler.getInstance(Constants.RECORD_FILE_NAME, this);

    private ActivityGameResultBinding binding;
    private String displayName, userEmail;
    private int points, record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

        SharedPreferences settings = getSharedPreferences(Constants.IS_EMAIL_ENABLED_EXTRA, 0);
        boolean isEmailOn = settings.getBoolean("switchkey", false);
        if (isEmailOn){
            sendEmail();
        }

        binding.backButton.setOnClickListener(view1 -> backToGame());
    }

    private void init(){
        Bundle arguments = getIntent().getExtras();
        points = arguments.getInt(Constants.POINTS_EXTRA);
        record = arguments.getInt(Constants.RECORD_EXTRA);
        displayName = arguments.getString(Constants.DISPLAY_NAME_EXTRA);
        userEmail = arguments.getString(Constants.USER_EMAIL_EXTRA);

        binding.displayNameTextView.setText(String.format(getString(R.string.username_gained), displayName));
        binding.pointsTextView.setText(getResources().getQuantityString(R.plurals.point_plurals, points, points));
        binding.recordTextView.setText(String.format(getString(R.string.current_record_text), record));
    }

    protected void sendEmail() {
        Log.d("game_result_activity", "sendEmail() started");

        try {
            String senderEmail = "logicgame06@gmail.com";
            String passwordSenderEmail = "yusdkruttrfolcmx";
            String receiverEmail = userEmail;

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, passwordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            mimeMessage.setSubject("Game results");
            mimeMessage.setText(String.format("Hello %s, \nYou gained %d points this time!", displayName, points));

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(mimeMessage);
                    Log.d("game_result_activity", "email sent");
                } catch (MessagingException e) {
                    e.printStackTrace();
                    Log.d("game_result_activity", "email was not sent");
                }
            });
            thread.start();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void backToGame() {
        finish();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}