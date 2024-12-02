package Fragments;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Modelos.User;
import es.ucm.fdi.viva_tu_pueblo.R;
import viva_tu_pueblo.LoginActivity;
import viva_tu_pueblo.MainActivity;

public class EventDetail_Fragment extends Fragment {

    private TextView eventTitle, eventPrice, eventLocation, eventDateTime, eventDescription, eventReservas;
    private ImageView eventImage;
    private Button reserveButton;
    private ImageButton backArrow;
    private User actualUsr;
    private FirebaseFirestore db;
    private String eventId; // Identificador único del evento en Firestore

    private String totalSeats, reservedSeats;

    public EventDetail_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_detail_fragment, container, false);

        // Inicializar vistas
        eventTitle = view.findViewById(R.id.event_title);
        eventPrice = view.findViewById(R.id.event_price);
        eventReservas = view.findViewById(R.id.event_reservedSeats);
        eventLocation = view.findViewById(R.id.event_location);
        eventDateTime = view.findViewById(R.id.event_date_time);
        eventDescription = view.findViewById(R.id.event_description);
        eventImage = view.findViewById(R.id.event_image);
        reserveButton = view.findViewById(R.id.reserve_button);
        //backArrow = view.findViewById(R.id.back_arrow);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setBackArrowVisibility(View.VISIBLE);
        }

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Recuperar argumentos pasados al fragmento
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            loadEventDetails(eventId);
        }

        /*backArrow.setOnClickListener(v -> {
            // Llamar al método onBackPressed() de la actividad para retroceder
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onBackPressed();  // Vuelve a la pantalla anterior
            }
        });*/

        return view;
    }

    private void loadEventDetails(String eventId) {
        try {
            DocumentReference eventRef = db.collection("events").document(eventId);

            eventRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Configurar detalles del evento
                    String title = documentSnapshot.getString("eventName") != null ? documentSnapshot.getString("eventName") : "Sin título";
                    String price = "Precio: " + (documentSnapshot.getString("price") != null ? documentSnapshot.getString("price") : "No disponible");
                    String location = "Ubicación: " + (documentSnapshot.getString("eventLocation") != null ? documentSnapshot.getString("eventLocation") : "No disponible");
                    String dateTime = "Fecha y Hora: " + (documentSnapshot.getString("eventDate") != null ? documentSnapshot.getString("eventDate") : "No disponible");
                    String description = documentSnapshot.getString("description") != null ? documentSnapshot.getString("description") : "Sin descripción";

                    totalSeats = documentSnapshot.getString("capacity") != null ? documentSnapshot.getString("capacity") : "No hay limite de aforo";
                    reservedSeats = documentSnapshot.getString("reservedSeats") != null ? documentSnapshot.getString("reservedSeats") : "0";

                    // Actualizar la UI
                    eventTitle.setText(title);
                    eventPrice.setText(price);
                    eventReservas.setText("Reservas:  " + reservedSeats);
                    eventLocation.setText(location);
                    eventDateTime.setText(dateTime);
                    eventDescription.setText(description);

                    // Verificar si el usuario ya reservó
                    checkReservationStatus(eventRef);
                } else {
                    Toast.makeText(getContext(), "El evento no existe", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar el evento", Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            Log.e("EventDetail_Fragment", "Error cargando detalles del evento", e);
            Toast.makeText(getContext(), "Error al cargar los detalles del evento", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkReservationStatus(DocumentReference eventRef) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> reservedUsers = (List<String>) documentSnapshot.get("users");

                if (reservedUsers != null && reservedUsers.contains(userEmail)) {
                    // Usuario ya ha reservado
                    updateReserveButton("Cancelar Reserva", android.R.color.holo_red_dark, v -> cancelReservation(eventRef, userEmail));
                } else {
                    // Usuario no ha reservado
                    updateReserveButton("Reservar", android.R.color.black, v -> reserveSeat(eventRef, userEmail));
                }
            }
        });
    }

    private void updateReserveButton(String text, int colorResId, View.OnClickListener onClickListener) {
        reserveButton.setText(text);
        reserveButton.setBackgroundTintList(getResources().getColorStateList(colorResId, null));
        reserveButton.setOnClickListener(onClickListener);
    }

    private void reserveSeat(DocumentReference eventRef, String userEmail) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null || FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            showNotLoggedInDialog();
        } else {
            showReserveDialog(eventRef, userEmail);
        }
    }

    private void cancelReservation(DocumentReference eventRef, String userEmail) {
        showCancelReservaDialog(eventRef, userEmail);
    }


    private void showNotLoggedInDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Acceso Restringido")
                .setMessage("Debes iniciar sesión para visualizar los eventos.")
                .setIcon(R.drawable.ic_aviso)
                .setCancelable(false) // Evita que el diálogo se cierre tocando fuera de él
                .setPositiveButton("Iniciar Sesión", (dialog, which) -> {
                    // Redirigir al usuario al login
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Atrás", (dialog, which) -> {
                    // Cierra el diálogo y regresa a la pestaña anterior
                    dialog.dismiss();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .show();
    }

    private void showCancelReservaDialog(DocumentReference eventRef, String userEmail) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancelación de Reserva")
                .setMessage("¿Seguro que deseas cancelar tu reserva?")
                .setIcon(R.drawable.ic_aviso)
                .setCancelable(false)
                .setPositiveButton("Sí", (dialog, which) -> {
                    int reserved = Integer.parseInt(reservedSeats);

                    // Actualizar el documento del evento
                    eventRef.update(
                            "reservedSeats", Integer.toString(reserved - 1),
                            "users", FieldValue.arrayRemove(userEmail)
                    ).addOnSuccessListener(aVoid -> {
                        // Actualizar el documento del usuario
                        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        DocumentReference userRef = db.collection("user").document(userId);

                        userRef.update("reservedEvents", FieldValue.arrayRemove(eventId))
                                .addOnSuccessListener(userVoid -> {
                                    if (isAdded()) {
                                        Toast.makeText(requireContext(), "Reserva cancelada", Toast.LENGTH_SHORT).show();
                                    }
                                    updateReserveButton("Reservar", android.R.color.black, v -> reserveSeat(eventRef, userEmail));
                                    // Volver atrás
                                    if (isAdded()) {
                                        getParentFragmentManager().popBackStack();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (isAdded()) {
                                        Toast.makeText(requireContext(), "Error al eliminar la reserva de tu perfil", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }).addOnFailureListener(e -> {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Error al cancelar la reserva del evento", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showReserveDialog(DocumentReference eventRef, String userEmail) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reserva Evento")
                .setMessage("¿Seguro que deseas reservar plaza para el evento?")
                .setIcon(R.drawable.ic_aviso)
                .setCancelable(false)
                .setPositiveButton("Sí", (dialog, which) -> {
                    int reserved = Integer.parseInt(reservedSeats);

                    if (totalSeats.equals("No hay limite de aforo") || totalSeats.isEmpty() || reserved < Integer.parseInt(totalSeats)) {
                        eventRef.update(
                                "reservedSeats", Integer.toString(reserved + 1),
                                "users", FieldValue.arrayUnion(userEmail)
                        ).addOnSuccessListener(aVoid -> {
                            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            DocumentReference userRef = db.collection("user").document(userId);

                            userRef.update("reservedEvents", FieldValue.arrayUnion(eventId))
                                    .addOnSuccessListener(userVoid -> {
                                        if (isAdded()) {
                                            Toast.makeText(requireContext(), "Reserva confirmada", Toast.LENGTH_SHORT).show();
                                        }
                                        updateReserveButton("Cancelar Reserva", android.R.color.holo_red_dark, v -> cancelReservation(eventRef, userEmail));
                                        // Volver atrás
                                        if (isAdded()) {
                                            getParentFragmentManager().popBackStack();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (isAdded()) {
                                            Toast.makeText(requireContext(), "Error al registrar la reserva en tu perfil", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }).addOnFailureListener(e -> {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Error al realizar la reserva del evento", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "No quedan plazas disponibles", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }




}
