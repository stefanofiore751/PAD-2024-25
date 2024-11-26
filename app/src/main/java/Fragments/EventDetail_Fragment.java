package Fragments;

import android.os.Bundle;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.viva_tu_pueblo.R;

public class EventDetail_Fragment extends Fragment {

    private TextView eventTitle, eventPrice, eventLocation, eventDateTime, eventDescription;
    private ImageView eventImage;
    private Button reserveButton;
    private ImageButton backArrow;

    private FirebaseFirestore db;
    private String eventId; // Identificador único del evento en Firestore

    private int totalSeats, reservedSeats;

    public EventDetail_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_detail_fragment, container, false);

        // Inicializar vistas
        eventTitle = view.findViewById(R.id.event_title);
        eventPrice = view.findViewById(R.id.event_price);
        eventLocation = view.findViewById(R.id.event_location);
        eventDateTime = view.findViewById(R.id.event_date_time);
        eventDescription = view.findViewById(R.id.event_description);
        eventImage = view.findViewById(R.id.event_image);
        reserveButton = view.findViewById(R.id.reserve_button);
        backArrow = view.findViewById(R.id.back_arrow);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Recuperar argumentos pasados al fragmento
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            loadEventDetails(eventId);
        }

        // Configurar botón de reserva
        reserveButton.setOnClickListener(v -> reserveSeat());

        // Configurar botón de retroceso
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void loadEventDetails(String eventId) {
        // Referencia al documento del evento
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Obtener los datos del evento
                String title = documentSnapshot.getString("eventName");
                String price = "Precio: " + documentSnapshot.getString("price");
                String location = "Ubicación: " + documentSnapshot.getString("eventLocation");
                String dateTime = "Fecha y Hora: " + documentSnapshot.getString("eventDate");
                String description = documentSnapshot.getString("description");

                totalSeats = documentSnapshot.getLong("totalSeats").intValue();
                reservedSeats = documentSnapshot.getLong("reservedSeats").intValue();

                // Mostrar los datos en la UI
                eventTitle.setText(title);
                eventPrice.setText(price);
                eventLocation.setText(location);
                eventDateTime.setText(dateTime);
                eventDescription.setText(description);

                // Aquí puedes cargar la imagen si la tienes como URL
                // Picasso.get().load(imageUrl).into(eventImage);
            } else {
                Toast.makeText(getContext(), "El evento no existe", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar el evento", Toast.LENGTH_SHORT).show());
    }

    private void reserveSeat() {
        if (reservedSeats < totalSeats) {
            // Incrementar el número de plazas reservadas
            reservedSeats++;

            DocumentReference eventRef = db.collection("events").document(eventId);
            eventRef.update("reservedSeats", reservedSeats)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Reserva confirmada", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al realizar la reserva", Toast.LENGTH_SHORT).show();
                        reservedSeats--; // Revertir si falla
                    });
        } else {
            Toast.makeText(getContext(), "No quedan plazas disponibles", Toast.LENGTH_SHORT).show();
        }
    }
}
