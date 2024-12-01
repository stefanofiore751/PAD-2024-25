package viva_tu_pueblo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Fragments.Events_Fragment;
import Fragments.Home_Fragment;
import Fragments.Incidents_Fragment;
import Fragments.Notices_Fragment;
import Fragments.Perfil_Fragment;
import Fragments.Routes_Fragment;
import es.ucm.fdi.viva_tu_pueblo.R;

public class MainActivity extends AppCompatActivity {

    private final Notices_Fragment noticesFragment = new Notices_Fragment();
    private final Incidents_Fragment incidentsFragment = new Incidents_Fragment();
    private final Home_Fragment homeFragment = new Home_Fragment();
    private final Routes_Fragment routesFragment = new Routes_Fragment();
    private final Events_Fragment eventsFragment = new Events_Fragment();

    private ImageButton btnGoToLogin, btnProfile;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Inicializar botones
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnProfile = findViewById(R.id.btnProfile);

        // Configurar botones según estado de autenticación
        configureButtons();

        // Configurar barra de navegación inferior
        BottomNavigationView navBarBottom = findViewById(R.id.bottomNavigationView);
        navBarBottom.setSelectedItemId(R.id.nav_home);
        navBarBottom.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(homeFragment);

    }


    private void configureButtons() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isAnonymous()) {
                // Usuario invitado: mostrar botón de login
                btnProfile.setVisibility(View.INVISIBLE);
                btnGoToLogin.setVisibility(View.VISIBLE);

                btnGoToLogin.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
            } else {
                // Usuario autenticado: mostrar botón de perfil
                btnGoToLogin.setVisibility(View.INVISIBLE);
                btnProfile.setVisibility(View.VISIBLE);

                btnProfile.setOnClickListener(v -> {
                    loadFragment(new Perfil_Fragment());
                });
            }
        } else {
            // No hay usuario autenticado: redirigir al LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private final BottomNavigationView.OnItemSelectedListener mOnNavigationItemSelectedListener =
            item -> {
                TextView headerTitle = findViewById(R.id.header_title);
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        loadFragment(homeFragment);
                        // Obtener el TextView de la cabecera
                        headerTitle.setText("Home");
                        return true;

                    case R.id.nav_news:
                        headerTitle.setText("Noticias");
                        loadFragment(noticesFragment);
                        return true;

                    case R.id.nav_incidents:
                        headerTitle.setText("Incidetes");
                        loadFragment(incidentsFragment);
                        return true;

                    case R.id.nav_routes:
                        headerTitle.setText("Rutas");
                        loadFragment(routesFragment);
                        return true;

                    case R.id.nav_events:
                        headerTitle.setText("Eventos");
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