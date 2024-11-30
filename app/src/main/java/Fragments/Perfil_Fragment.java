package Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import Adapters.Events_Adapter;
import Modelos.Event;
import Modelos.User;
import es.ucm.fdi.viva_tu_pueblo.R;

public class Perfil_Fragment extends Fragment implements Events_Adapter.OnEventClickListener {

    // Variables de la interfaz
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User actualUser;

    private RecyclerView rvEventosReservados;
    private Events_Adapter eventosAdapter;
    private Button btnMostrarEventos;
    private ImageButton btnSalir;

    // Método constructor
    public Perfil_Fragment() {
        // Requiere un constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el layout
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rvEventosReservados = view.findViewById(R.id.rvEventosReservados);
        btnMostrarEventos = view.findViewById(R.id.btnReservedEvents);
        btnSalir = view.findViewById(R.id.salirButton);

        // Cargar los datos del usuario y pasamos la vista para evitar problemas con getView()
        loadUserData(view);

        // Lógica para mostrar eventos reservados cuando se presione el botón
        btnMostrarEventos.setOnClickListener(v -> showReservedEvents());

        // Configurar el evento del botón de salida
        btnSalir.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
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

    // Mostrar los eventos reservados del usuario
    private void showReservedEvents() {
        if (actualUser == null) {
            Toast.makeText(getContext(), "Datos del usuario no disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener los eventos reservados
        List<String> eventosReservadosIds = actualUser.getReservedEvents(); // Se usa 'actualUser' en lugar de 'usuarioActual'
        if (eventosReservadosIds == null || eventosReservadosIds.isEmpty()) {
            Toast.makeText(getContext(), "No hay eventos reservados", Toast.LENGTH_SHORT).show();
            return;
        }

        /*for(int i = 0; i < eventosReservadosIds.size(); ++i){
            db.collection("events")
        }*/

        // Cargar los eventos desde Firestore usando los IDs de los eventos reservados
        db.collection("events")
                .whereIn("id", eventosReservadosIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> eventosReservados = queryDocumentSnapshots.toObjects(Event.class);
                    showInView(eventosReservados); // Mostrar los eventos en el RecyclerView
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar eventos", Toast.LENGTH_SHORT).show());
    }

    // Mostrar los eventos en el RecyclerView
    private void showInView(List<Event> eventos) {
        rvEventosReservados.setVisibility(View.VISIBLE);
        rvEventosReservados.setLayoutManager(new LinearLayoutManager(getContext()));
        //eventosAdapter = new Events_Adapter(eventos, (Events_Adapter.OnEventClickListener) getContext());
        eventosAdapter = new Events_Adapter(eventos, this);
        rvEventosReservados.setAdapter(eventosAdapter);
    }

    // Método para editar datos personales (puedes agregar la lógica aquí)
    private void editarDatosPersonales() {
        // Lógica para editar datos personales
        Toast.makeText(getContext(), "Editar datos personales", Toast.LENGTH_SHORT).show();
    }

    // Método para cambiar contraseña (puedes agregar la lógica aquí)
    private void cambiarContrasena() {
        // Lógica para cambiar contraseña
        Toast.makeText(getContext(), "Cambiar contraseña", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventClick(Event event) {
        Toast.makeText(getContext(), "Evento seleccionado ", Toast.LENGTH_SHORT).show();
    }
}
