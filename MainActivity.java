package com.ltl.mpmp_lab3;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ltl.mpmp_lab3.user.UserModel;
import com.ltl.mpmp_lab3.user.UserViewModel;
import com.ltl.mpmp_lab3.utility.DatabaseCallback;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the ViewModel.
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);



        // Create the observer which updates the UI.
//        final Observer<UserModel> userObserver = new Observer<UserModel>() {
//            @Override
//            public void onChanged(@Nullable final UserModel newUser) {
//                userViewModel.getCurrentUserLiveData().setValue(newUser);
//            }
//        };
//        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
//        userViewModel.getCurrentUserLiveData().observe(this, userObserver);

//        using frame layout for navigation between fragments: !(from LoginActivity)
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_fl, new LoginFragment());
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        getSupportActionBar().setTitle(currentUser.getDisplayName());
        this.optionsMenu = menu;

        menu.findItem(R.id.email_settings).setChecked(EmailPreferenceHandler.get(this));
        setPenalty(menu.findItem(R.id.game_normal_settings));
        Log.d("main_activity", "options menu created");

        return true;
    }*/

/*    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (isStared) return;

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        contextMenu = menu;
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == checkedMenuItemId) {
                item.setChecked(true);
            }
        }
        Log.d("main_activity", "context menu created");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.game_easy_settings:
            case R.id.game_normal_settings:
            case R.id.game_hard_settings:
                setPenalty(item);
                updateMenus(item);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.game_easy_settings:
            case R.id.game_normal_settings:
            case R.id.game_hard_settings:
                setPenalty(item);
                updateMenus(item);
                return true;

            case R.id.email_settings:
                item.setChecked(!item.isChecked());
                EmailPreferenceHandler.put(this, item.isChecked());
                return true;
            case R.id.exit_settings:
                signOut();
                goToLogin();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.context_menu);

        for (int i = 0; i < popup.getMenu().size(); ++i) {
            MenuItem item = popup.getMenu().getItem(i);
            if (item.getItemId() == checkedMenuItemId) {
                item.setChecked(true);
            }
        }

        popup.show();
    }

//    popup item handler
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.game_easy_settings:
            case R.id.game_normal_settings:
            case R.id.game_hard_settings:
                setPenalty(item);
                updateMenus(item);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }*/

}