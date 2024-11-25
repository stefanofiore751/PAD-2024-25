package Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import es.ucm.fdi.viva_tu_pueblo.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Events_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Events_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Events_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Events_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Events_Fragment newInstance(String param1, String param2) {
        Events_Fragment fragment = new Events_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        // Botón del calendario
        ImageButton btnCalendar = rootView.findViewById(R.id.btnCalendar);

        // Configurar el RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // Dos columnas
        recyclerView.setHasFixedSize(true);

        // Configurar el adapter
        List<Event> eventList = getDummyEvents(); // Generar o cargar la lista de eventos
        EventAdapter adapter = new EventAdapter(eventList);
        recyclerView.setAdapter(adapter);

        // Listener del botón calendario
        btnCalendar.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    R.style.CustomDatePickerDialog,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Acción al seleccionar la fecha
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        return rootView;
    }

    // Generar una lista de eventos de prueba
    private List<Event> getDummyEvents() {
        List<Event> events = new ArrayList<>();
        events.add(new Event("Evento 1", "25€", "Madrid", "25/11/2024"));
        events.add(new Event("Evento 2", "Gratis", "Barcelona", "26/11/2024"));
        // Agrega más eventos según sea necesario
        return events;
    }
}