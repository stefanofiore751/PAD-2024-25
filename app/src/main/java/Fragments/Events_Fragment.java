package Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Adapters.Events_Adapter;
import Modelos.Event;
import es.ucm.fdi.viva_tu_pueblo.R;

public class Events_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private Events_Adapter adapter;
    private List<Event> eventList;
    private androidx.appcompat.widget.SearchView searchView;
    private String selectedDate = null; // Variable para almacenar la fecha seleccionada

    public Events_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerViewEventos);
        searchView = view.findViewById(R.id.searchView);
        ImageButton btnCalendar = view.findViewById(R.id.btnCalendar); // Botón para seleccionar fecha

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inicializar Firestore y lista de eventos
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();

        // Cargar todos los eventos al inicio
        loadEvents();

        // Configurar el listener del SearchView
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchEvents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadEvents(); // Volver a cargar todos los eventos si el texto está vacío
                } else {
                    searchEvents(newText);
                }
                return true;
            }
        });

        // Configurar el listener para abrir el selector de fecha
        btnCalendar.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        // Establecer Locale en español
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);

        // Configurar el calendario en español
        Calendar calendar = Calendar.getInstance(locale);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear el selector de fecha
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1; // Formato dd/MM/yyyy
                    searchEvents(null); // Filtrar solo por fecha seleccionada
                }, year, month, day);

        // Mostrar en español
        datePickerDialog.show();

        // Listener para manejar el botón de cancelar
        datePickerDialog.setOnCancelListener(dialog -> {
            selectedDate = null; // Restablecer filtro de fecha
            loadEvents();
        });
    }

    // Cargar eventos desde Firestore
    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        Log.w("Firestore", "No se encontraron documentos en la colección 'events'.");
                        return;
                    }
                    eventList.clear(); // Evitar duplicados

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Event event = document.toObject(Event.class);
                            event.setEventId(document.getId());
                            eventList.add(event);
                            Log.d("Firestore", "Evento cargado: " + event.getEventName());
                        } catch (Exception e) {
                            Log.e("Firestore", "Error al mapear el documento a un objeto Event: ", e);
                        }
                    }

                    adapter = new Events_Adapter(eventList, Events_Fragment.this::openEventDetail);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar eventos", Toast.LENGTH_SHORT).show());
    }


    // Buscar eventos por nombre, fecha o ambos criterios
    private void searchEvents(String query) {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        Log.w("Firestore", "No se encontraron eventos que coincidan con la búsqueda.");
                        eventList.clear();
                        if (adapter != null) adapter.notifyDataSetChanged();
                        return;
                    }
                    eventList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Event event = document.toObject(Event.class);
                            String eventName = event.getEventName();
                            String eventDate = event.getEventDate(); // Fecha almacenada en Firestore

                            // Convertir las fechas a un formato controlado para comparar
                            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                            // Fecha seleccionada y fecha del evento en formato uniforme
                            String normalizedSelectedDate = selectedDate != null ? outputFormat.format(inputFormat.parse(selectedDate)) : null;
                            String normalizedEventDate = outputFormat.format(inputFormat.parse(eventDate));

                            boolean matchesName = query != null && eventName.toLowerCase().contains(query.toLowerCase());
                            boolean matchesDate = normalizedSelectedDate != null && normalizedEventDate.equals(normalizedSelectedDate);

                            if (matchesName || matchesDate) { // Si cumple cualquier criterio
                                event.setEventId(document.getId());
                                eventList.add(event);
                            }
                        } catch (Exception e) {
                            Log.e("Firestore", "Error al mapear el documento a un objeto Event o al procesar fecha: ", e);
                        }
                    }

                    if (adapter != null) adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al buscar eventos", Toast.LENGTH_SHORT).show());
    }



    private void openEventDetail(Event event) {
        EventDetail_Fragment eventDetailFragment = new EventDetail_Fragment();
        Bundle args = new Bundle();
        args.putString("eventId", event.getEventId());
        eventDetailFragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.Frame_Layout_NavView, eventDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
