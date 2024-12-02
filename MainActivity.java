package viva_tu_pueblo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private static final String SELECTED_FRAGMENT = "selected_fragment";
    private Fragment currentFragment;

    private ImageButton btnGoToLogin, btnProfile, back_arrow;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnProfile = findViewById(R.id.btnProfile);
        back_arrow = findViewById(R.id.back_arrow);

        // Configurar botones según estado de autenticación
        configureButtons();

        // Configurar el listener del botón de retroceso para manejar el retroceso
        back_arrow.setOnClickListener(v -> onBackPressed());

        // Configurar barra de navegación inferior
        BottomNavigationView navBarBottom = findViewById(R.id.bottomNavigationView);
        navBarBottom.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, SELECTED_FRAGMENT);
            loadFragment(currentFragment);
            syncNavBarWithFragment(currentFragment);
        } else {
            currentFragment = homeFragment;
            loadFragment(homeFragment);
            navBarBottom = findViewById(R.id.bottomNavigationView);
            navBarBottom.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, SELECTED_FRAGMENT, currentFragment);
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
                boolean esFragmento=true;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        esFragmento=true;
                        currentFragment = homeFragment;
                        headerTitle.setText(R.string.homepage);
                        break;
                    case R.id.nav_news:
                        esFragmento=true;
                        currentFragment = noticesFragment;
                        headerTitle.setText(R.string.noticias);
                        break;
                    case R.id.nav_incidents:
                        esFragmento=false;
                        //currentFragment = incidentsFragment;
                       // headerTitle.setText(R.string.incidentes);
                        Intent incidencias=new Intent(this, IncidenciaActivity.class);
                        startActivity(incidencias);
                        break;
                    case R.id.nav_routes:
                        esFragmento=false;
                        //currentFragment = routesFragment;
                        //headerTitle.setText(R.string.rutas);
                        Intent rutas=new Intent(this, RutaActivity.class);
                        startActivity(rutas);
                        break;
                    case R.id.nav_events:
                        esFragmento=true;
                        currentFragment = eventsFragment;
                        headerTitle.setText(R.string.eventos);
                        break;
                    default:
                        return false;
                }
                if(esFragmento){
                    loadFragment(currentFragment);
                }

                return true;
            };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Frame_Layout_NavView, fragment);
        transaction.addToBackStack(null); // Navegación hacia atrás opcional
        transaction.commit();
    }

    // Método para cambiar la visibilidad del botón de retroceso
    public void setBackArrowVisibility(int visibility) {
        if (back_arrow != null) {
            back_arrow.setVisibility(visibility);
        }
    }


    private void syncNavBarWithFragment(Fragment fragment) {
        BottomNavigationView navBarBottom = findViewById(R.id.bottomNavigationView);

        if (fragment instanceof Home_Fragment) {
            navBarBottom.setSelectedItemId(R.id.nav_home);
        } else if (fragment instanceof Notices_Fragment) {
            navBarBottom.setSelectedItemId(R.id.nav_news);
        } else if (fragment instanceof Incidents_Fragment) {
            navBarBottom.setSelectedItemId(R.id.nav_incidents);
        } else if (fragment instanceof Routes_Fragment) {
            navBarBottom.setSelectedItemId(R.id.nav_routes);
        } else if (fragment instanceof Events_Fragment) {
            navBarBottom.setSelectedItemId(R.id.nav_events);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadFragment(homeFragment);
    }

    public void onBackPressed() {
        // Verifica si hay fragmentos en el back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            setBackArrowVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();  // Vuelve al fragment anterior
        } else {
            super.onBackPressed();  // Si no hay fragmentos, realiza la acción por defecto
        }
    }

}
