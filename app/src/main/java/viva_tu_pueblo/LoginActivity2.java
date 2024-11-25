package viva_tu_pueblo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.ucm.fdi.viva_tu_pueblo.R;

public class LoginActivity2 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_prueba);

        // Referencias a los elementos del layout
        ImageView logoImage = findViewById(R.id.logo_image);
        EditText usernameField = findViewById(R.id.username_field);
        EditText passwordField = findViewById(R.id.password_field);
        Button loginButton = findViewById(R.id.login_button);
        Button guestButton = findViewById(R.id.guest_button);
        TextView descriptionText = findViewById(R.id.textViewDescription);
        TextView joinText = findViewById(R.id.textDescriptionLogi);

        // Cargar animaciones
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Aplicar animaciones
        logoImage.startAnimation(fadeIn);
        usernameField.startAnimation(fadeIn);
        passwordField.startAnimation(fadeIn);
        loginButton.startAnimation(fadeIn);
        guestButton.startAnimation(fadeIn);
        descriptionText.startAnimation(fadeIn);
        joinText.startAnimation(fadeIn);

        // Continuar como invitado
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
