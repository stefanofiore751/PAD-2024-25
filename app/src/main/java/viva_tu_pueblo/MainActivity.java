package viva_tu_pueblo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import Fragments.Events_Fragment;
import Fragments.Home_Fragment;
import Fragments.Incidents_Fragment;
import Fragments.Notices_Fragment;
import Fragments.Routes_Fragment;
import es.ucm.fdi.viva_tu_pueblo.R;

public class MainActivity extends AppCompatActivity {

    private final Notices_Fragment noticesFragment = new Notices_Fragment();
    private final Incidents_Fragment incidentsFragment = new Incidents_Fragment();
    private final Home_Fragment homeFragment = new Home_Fragment();
    private final Routes_Fragment routesFragment = new Routes_Fragment();
    private final Events_Fragment eventsFragment = new Events_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navBarBottom = findViewById(R.id.bottomNavigationView);
        navBarBottom.setSelectedItemId(R.id.nav_home);
        navBarBottom.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(homeFragment);

        // Set up login button click listener
        ImageButton btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }


    private final BottomNavigationView.OnItemSelectedListener mOnNavigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        loadFragment(homeFragment);
                        return true;

                    case R.id.nav_news:
                        loadFragment(noticesFragment);
                        return true;

                    case R.id.nav_incidents:
                        loadFragment(incidentsFragment);
                        return true;

                    case R.id.nav_routes:
                        loadFragment(routesFragment);
                        return true;

                    case R.id.nav_events:
                        loadFragment(eventsFragment);
                        return true;
                }
                return false;
            };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Frame_Layout_NavView, fragment);
        transaction.addToBackStack(null); // Navegación hacia atrás opcional
        transaction.commit();
    }
}