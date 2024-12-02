package Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Adapters.Events_Adapter;
import Modelos.Event;
import es.ucm.fdi.viva_tu_pueblo.R;

public class Home_Fragment extends Fragment {

    private TextView weatherCity, weatherCondition, weatherTemperature, weatherHumidity;
    private ImageView weatherIcon;
    private static final String WEATHER_API_KEY = "00dad7024cda0d119d8e43ce3353e84b";
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?q={CITY}&appid="
            + WEATHER_API_KEY + "&units=metric";

    private RecyclerView eventsRecyclerView;
    private Events_Adapter eventsAdapter;
    private List<Event> eventsList = new ArrayList<>();
    private FirebaseFirestore db;

    public Home_Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializa las vistas del clima
        weatherCity = view.findViewById(R.id.weather_city);
        weatherCondition = view.findViewById(R.id.weather_condition);
        weatherTemperature = view.findViewById(R.id.weather_temperature);
        weatherHumidity = view.findViewById(R.id.weather_humidity);
        weatherIcon = view.findViewById(R.id.weather_icon);

        // Configurar RecyclerView
        eventsRecyclerView = view.findViewById(R.id.recycler_events);
        setupEventsRecyclerView();

        // Llama a las funciones
        fetchWeatherData("Neila");
        loadEventsData();

        return view;
    }

    private void fetchWeatherData(String city) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = WEATHER_API_URL.replace("{CITY}", city);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        JSONObject weather = response.getJSONArray("weather").getJSONObject(0);

                        String cityName = response.getString("name");
                        String condition = weather.getString("description");
                        String iconCode = weather.getString("icon");
                        double temperature = main.getDouble("temp");
                        int humidity = main.getInt("humidity");

                        weatherCity.setText(cityName);
                        weatherCondition.setText(condition);
                        weatherTemperature.setText("Temperatura: " + temperature + "°C");
                        weatherHumidity.setText("Humedad: " + humidity + "%");

                        // Cargar el ícono del clima
                        loadWeatherIcon(iconCode);

                    } catch (JSONException e) {
                        Log.e("WeatherError", "Error procesando JSON", e);
                    }
                },
                error -> Log.e("WeatherError", "Error en la API", error));

        queue.add(jsonObjectRequest);
    }

    private void loadWeatherIcon(String iconCode) {
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

        ImageRequest imageRequest = new ImageRequest(iconUrl,
                response -> weatherIcon.setImageBitmap(response),
                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                error -> Log.e("WeatherIconError", "Error cargando el ícono", error));

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(imageRequest);
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        eventsAdapter = new Events_Adapter(eventsList, this::openEventDetail);
        eventsRecyclerView.setAdapter(eventsAdapter);
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

    private void loadEventsData() {
        db.collection("events")
                .orderBy("eventDate", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        Log.w("Firestore", "No se encontraron eventos en la colección 'events'.");
                        return;
                    }

                    eventsList.clear(); // Evita duplicati

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Event event = document.toObject(Event.class);
                            event.setEventId(document.getId()); // Imposta l'ID dell'evento
                            eventsList.add(event);
                            Log.d("Firestore", "Evento cargado: " + event.getEventName());
                        } catch (Exception e) {
                            Log.e("Firestore", "Error al mapear el documento a un objeto Event: ", e);
                        }
                    }

                    eventsAdapter.notifyDataSetChanged(); // Notifica l'adapter
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar eventos", e));
    }

}
