package com.ltl.mpmp_lab3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ltl.mpmp_lab3.constants.IntentExtra;
import com.ltl.mpmp_lab3.databinding.ActivityGameResultBinding;
import com.ltl.mpmp_lab3.utility.EmailPreferenceHandler;
import com.ltl.mpmp_lab3.utility.MailSender;

public class GameResultActivity extends AppCompatActivity {
    public static final String RECORD_FILE_NAME = "record.txt";
//    private final FileHandler fileHandler = FileHandler.getInstance(Constants.RECORD_FILE_NAME, this);

    private ActivityGameResultBinding binding;
    private String displayName, userEmail;
    private int points, record;
    private boolean isEmailSent = false;
//    MailSender mailSender = new MailSender(this);
    MailSender mailSender = MailSender.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (savedInstanceState != null){
            isEmailSent = savedInstanceState.getBoolean("isEmailSent");
            mailSender = (MailSender) savedInstanceState.getSerializable("mailSender");
        }

        init();

        binding.backButton.setOnClickListener(view1 -> backToGame());

        boolean isEmailOn = EmailPreferenceHandler.get(this);
        if (isEmailOn){
            mailSender.sendInNotSent(userEmail, displayName, points);
        }
    }

    private void init(){
        Bundle arguments = getIntent().getExtras();
        points = arguments.getInt(IntentExtra.POINTS_EXTRA.getValue());
        record = arguments.getInt(IntentExtra.RECORD_EXTRA.getValue());
        displayName = arguments.getString(IntentExtra.DISPLAY_NAME_EXTRA.getValue());
        userEmail = arguments.getString(IntentExtra.USER_EMAIL_EXTRA.getValue());

        binding.displayNameTextView.setText(String.format(getString(R.string.username_gained), displayName));
        binding.pointsTextView.setText(getResources().getQuantityString(R.plurals.point_plurals, points, points));
        binding.recordTextView.setText(String.format(getString(R.string.current_record_text), record));
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("isEmailSent", mailSender.isEmailSent());
        outState.putSerializable("mailSender", mailSender);
        super.onSaveInstanceState(outState);
    }
}