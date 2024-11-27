package Fragments;

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
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import Adapters.Events_Adapter;
import Modelos.Event;
import es.ucm.fdi.viva_tu_pueblo.R;

public class Events_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private Events_Adapter adapter;
    private List<Event> eventList;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inicializar Firestore y lista de eventos
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();

        // Cargar eventos desde la base de datos
        loadEvents();
    }

    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        Log.w("Firestore", "No se encontraron documentos en la colección 'events'.");
                        return;
                    }
                    // Vaciar la lista para evitar duplicados
                    eventList.clear();

                    // Iterar sobre los documentos recibidos
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // Mapea el documento a la clase Event
                            Event event = document.toObject(Event.class);
                            event.setEventId(document.getId()); // Asigna el ID del documento
                            eventList.add(event);
                            Log.d("Firestore", "Evento cargado: " + event.getEventName());
                        } catch (Exception e) {
                            Log.e("Firestore", "Error al mapear el documento a un objeto Event: ", e);
                        }
                    }

                    // Configurar adaptador con los datos cargados
                    adapter = new Events_Adapter(eventList, this::openEventDetail);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                );
    }

    private void openEventDetail(Event event) {
        // Crear el fragmento de detalle y pasar los argumentos necesarios
        EventDetail_Fragment eventDetailFragment = new EventDetail_Fragment();
        Bundle args = new Bundle();
        args.putString("eventId", event.getEventId()); // Corregido a String
        eventDetailFragment.setArguments(args);

        // Reemplazar el fragmento actual por el de detalles
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.Frame_Layout_NavView, eventDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
