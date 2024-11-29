package viva_tu_pueblo;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.ucm.fdi.viva_tu_pueblo.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etFullName, etAdress, etCity;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etAdress = findViewById(R.id.etAdress);
        etCity = findViewById(R.id.etCity);

        Button btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String address = etAdress.getText().toString().trim();
        String city = etCity.getText().toString().trim();


        // Verificar que los campos no estén vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(fullName) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(city)) {
            Toast.makeText(SignUpActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo usuario en Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si la creación de usuario es exitosa, obtén el usuario
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Crear un mapa con los datos del usuario
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullName", fullName);
                            user.put("address", address);
                            user.put("city", city);
                            user.put("email", email);
                            user.put("userId", firebaseUser.getUid());  // Agregar el ID de Firebase Auth

                            // Crear el documento del usuario en Firestore
                            db.collection("usuarios")
                                    .document(firebaseUser.getUid()) // Usamos el UID del usuario como ID del documento
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        finish(); // Cerrar la actividad después del registro exitoso
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUpActivity.this, "Error al guardar datos del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Si la creación de usuario falla
                        Toast.makeText(SignUpActivity.this, "Error al registrar usuario: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
