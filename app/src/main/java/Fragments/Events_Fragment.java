package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import Adapters.Events_Adapter;
import Modelos.Event;
import es.ucm.fdi.viva_tu_pueblo.R;
import viva_tu_pueblo.LoginActivity;

public class Events_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private Events_Adapter adapter;
    private List<Event> eventList;
    private androidx.appcompat.widget.SearchView searchView;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inicializar Firestore y lista de eventos
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();

        // Cargar eventos si el usuario está logueado
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


        // Verificar si el usuario está logueado
        /*if (auth.getCurrentUser() == null) {
            showNotLoggedInDialog();
        } else {
            // Cargar eventos si el usuario está logueado
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

        }*/


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
                            event.setEventId(document.getId());
                            eventList.add(event);
                            Log.d("Firestore", "Evento cargado: " + event.getEventName());
                        } catch (Exception e) {
                            Log.e("Firestore", "Error al mapear el documento a un objeto Event: ", e);
                        }
                    }

                    // Configurar adaptador con los datos cargados
                    adapter = new Events_Adapter(eventList, Events_Fragment.this::openEventDetail);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                );
    }

    private void searchEvents(String query) {
        db.collection("events")
                .orderBy("eventName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        Log.w("Firestore", "No se encontraron eventos que coincidan con la búsqueda.");
                        eventList.clear();
                        if (adapter != null) adapter.notifyDataSetChanged();
                        return;
                    }

                    // Vaciar la lista para evitar duplicados
                    eventList.clear();

                    // Iterar sobre los documentos recibidos
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // Mapea el documento a la clase Event
                            Event event = document.toObject(Event.class);
                            event.setEventId(document.getId());
                            eventList.add(event);
                            Log.d("Firestore", "Evento encontrado: " + event.getEventName());
                        } catch (Exception e) {
                            Log.e("Firestore", "Error al mapear el documento a un objeto Event: ", e);
                        }
                    }

                    // Notificar al adaptador que los datos han cambiado
                    if (adapter != null) adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al buscar eventos", Toast.LENGTH_SHORT).show()
                );
    }

    private void openEventDetail(Event event) {
        // Crear el fragmento de detalle y pasar los argumentos necesarios
        EventDetail_Fragment eventDetailFragment = new EventDetail_Fragment();
        Bundle args = new Bundle();
        args.putString("eventId", event.getEventId());
        eventDetailFragment.setArguments(args);

        // Reemplazar el fragmento actual por el de detalles
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.Frame_Layout_NavView, eventDetailFragment)
                .addToBackStack(null)
                .commit();
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


}
