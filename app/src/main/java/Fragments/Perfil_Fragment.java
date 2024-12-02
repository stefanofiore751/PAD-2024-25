package Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapters.Events_Adapter;
import Modelos.Event;
import Modelos.User;
import es.ucm.fdi.viva_tu_pueblo.R;
import viva_tu_pueblo.LoginActivity;

public class Perfil_Fragment extends Fragment implements Events_Adapter.OnEventClickListener {

    // Variables de la interfaz
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User actualUser;

    private RecyclerView rvEventosReservados;
    private Events_Adapter eventosAdapter;
    private Button btnEditarDatos;
    private Button btnCambiarContrasena;
    private Button btnLogOut;
    private ImageButton btnSalir;
    private View cardFront, cardBack;
    private Button tabMisDatos, tabMisEventos;
    private boolean isBackVisible = false;

    // Método constructor
    public Perfil_Fragment() {
        // Requiere un constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el layout
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializamos las variables
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicialización de la interfaz
        btnCambiarContrasena = view.findViewById(R.id.btnChangePassword);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnSalir = view.findViewById(R.id.salirButton);
        btnEditarDatos = view.findViewById(R.id.btnEditPersonalInfo);

        // Inicializar vistas
        cardFront = view.findViewById(R.id.cardFront);
        cardBack = view.findViewById(R.id.cardBack);
        rvEventosReservados = cardBack.findViewById(R.id.rvEventosReservados);
        tabMisDatos = view.findViewById(R.id.tabMisDatos);
        tabMisEventos = view.findViewById(R.id.tabMisEventos);

        // Configurar clics en los botones de las pestañas
        tabMisDatos.setOnClickListener(v -> flipCard(false));
        tabMisEventos.setOnClickListener(v -> flipCard(true));

        // Configurar colores iniciales de las pestañas
        updateTabStyles(false);

        loadUserData(view);


        // Cargar los datos del usuario
        btnLogOut.setOnClickListener(v -> logout());
        btnCambiarContrasena.setOnClickListener(v -> cambiarContrasena());
        btnSalir.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        btnEditarDatos.setOnClickListener(v -> showEditDialog());

        return view;
    }

    // Método para girar el CardView
    private void flipCard(boolean showBack) {
        if (isBackVisible == showBack) return;

        AnimatorSet flipAnimation = new AnimatorSet();
        Animator flipOut = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_out);
        Animator flipIn = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_in);

        if (showBack) {
            flipOut.setTarget(cardFront);
            flipIn.setTarget(cardBack);
            cardBack.setVisibility(View.VISIBLE);
            showReservedEvents();
        } else {
            flipOut.setTarget(cardBack);
            flipIn.setTarget(cardFront);
            cardFront.setVisibility(View.VISIBLE);
            loadUserData(getView());
        }

        flipAnimation.playSequentially(flipOut, flipIn);
        flipAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (showBack) {
                    cardFront.setVisibility(View.GONE);
                } else {
                    cardBack.setVisibility(View.GONE);
                }
                isBackVisible = showBack;
            }
        });

        flipAnimation.start();
        updateTabStyles(showBack);
    }

    // Actualizar estilos de los botones de las pestañas
    private void updateTabStyles(boolean showBack) {
        if (showBack) {
            tabMisDatos.setBackgroundColor(Color.BLACK);
            tabMisDatos.setTextColor(Color.WHITE);
            tabMisEventos.setBackgroundColor(Color.parseColor("#B8BBAA"));
            tabMisEventos.setTextColor(Color.BLACK);
        } else {
            tabMisDatos.setBackgroundColor(Color.parseColor("#B8BBAA"));
            tabMisDatos.setTextColor(Color.BLACK);
            tabMisEventos.setBackgroundColor(Color.BLACK);
            tabMisEventos.setTextColor(Color.WHITE);
        }
    }

    private void loadUserData(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            // Intentar convertir el documento a la clase User
                            actualUser = documentSnapshot.toObject(User.class);
                            if (actualUser != null) {
                                Log.d("Firebase", "Usuario cargado: " + actualUser.getFullName());
                                updateUI(view); // Actualizar la interfaz
                            } else {
                                Log.e("Firebase", "El documento no se pudo convertir a User");
                                Toast.makeText(getContext(), "Error al interpretar datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("Firebase", "Error al convertir documento a User", e);
                            Toast.makeText(getContext(), "Error al procesar datos del usuario", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("Firebase", "Documento no encontrado para el usuario: " + userId);
                        Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error al cargar el documento", e);
                    Toast.makeText(getContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI(View view) {
        if (actualUser == null) {
            Toast.makeText(getContext(), "Datos del usuario no disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar los TextViews con la información del usuario
        TextView tvFullName = view.findViewById(R.id.tvFullName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvCity = view.findViewById(R.id.tvCity);
        ImageView ivProfilePicture = view.findViewById(R.id.ivProfilePicture);

        // Setear los valores del usuario en los TextViews
        tvFullName.setText(actualUser.getFullName() != null ? actualUser.getFullName() : "Sin nombre");
        tvEmail.setText(actualUser.getEmail() != null ? actualUser.getEmail() : "Sin email");
        tvAddress.setText(actualUser.getAddress() != null ? actualUser.getAddress() : "Sin dirección");
        tvCity.setText(actualUser.getCity() != null ? actualUser.getCity() : "Sin ciudad");

        // Setear la imagen de perfil (en este caso una imagen predeterminada)
        ivProfilePicture.setImageResource(R.drawable.ic_perfil2);
    }

    private void showReservedEvents() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "Email del usuario no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events")
                .whereArrayContains("users", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Event> eventosReservados = queryDocumentSnapshots.toObjects(Event.class);
                        showInView(eventosReservados);
                    } else {
                        Toast.makeText(getContext(), "No hay eventos reservados", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener los eventos reservados", e);
                    Toast.makeText(getContext(), "Error al cargar eventos reservados", Toast.LENGTH_SHORT).show();
                });
    }

    private void showInView(List<Event> eventosReservados) {
        eventosAdapter = new Events_Adapter(eventosReservados, this);
        rvEventosReservados.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEventosReservados.setAdapter(eventosAdapter);
    }

    // Método para cambiar contraseña (puedes agregar la lógica aquí)
    private void cambiarContrasena() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "No se puede enviar un correo de restablecimiento de contraseña. Email no encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Correo de restablecimiento enviado a " + userEmail, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Firebase", "Error al enviar el correo de restablecimiento", task.getException());
                        Toast.makeText(getContext(), "Error al enviar el correo de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Cerrar sesión en FirebaseAuth
        Toast.makeText(getContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        // Redirigir al usuario a la pantalla de inicio de sesión
        // Requiere la clase LoginActivity u otra actividad de inicio
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Eliminar historial de actividades
        startActivity(intent);
        requireActivity().finish(); // Finalizar la actividad actual
    }

    @Override
    public void onEventClick(Event event) {
        Toast.makeText(getContext(), "Evento seleccionado: " + event.getEventName() , Toast.LENGTH_SHORT).show();
    }



    private void showEditDialog() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("user").document(userId);

        // Recuperar el diseño del diálogo
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_user_data, null);

        // Inicializar los campos
        EditText edtFullName = dialogView.findViewById(R.id.edtFullName);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtCity = dialogView.findViewById(R.id.edtCity);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialogInterface, which) -> {
                    // Obtener valores del usuario
                    String fullName = edtFullName.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String address = edtAddress.getText().toString().trim();
                    String city = edtCity.getText().toString().trim();

                    if (validateFields(fullName, email, address, city)) {
                        updateUserData(fullName, email, address, city);
                    } else {
                        Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();

        // Recuperar los datos del usuario desde Firebase y asignarlos al diálogo
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                edtFullName.setText(documentSnapshot.getString("fullName"));
                edtEmail.setText(documentSnapshot.getString("email"));
                edtAddress.setText(documentSnapshot.getString("address"));
                edtCity.setText(documentSnapshot.getString("city"));
            } else {
                Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Error al cargar los datos del usuario", e);
            Toast.makeText(getContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private boolean validateFields(String fullName, String email, String address, String city) {
        return !(fullName.isEmpty() || email.isEmpty() || address.isEmpty() || city.isEmpty());
    }


    // Método para actualizar campos específicos del usuario en Firebase
    private void updateUserData(String fullName, String email, String address, String city) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("user").document(userId);

        // Crear un mapa con los campos a actualizar
        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("fullName", fullName);
        updatedFields.put("email", email);
        updatedFields.put("address", address);
        updatedFields.put("city", city);

        // Actualizar los campos específicos en Firestore
        userRef.update(updatedFields)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                    loadUserData(getView()); // Recargar los datos del usuario en la interfaz
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error al actualizar los datos", e);
                    Toast.makeText(getContext(), "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                });
    }


}
