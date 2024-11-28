package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import Modelos.Event;
import es.ucm.fdi.viva_tu_pueblo.R;

public class Events_Adapter extends RecyclerView.Adapter<Events_Adapter.EventViewHolder> {

    private final List<Event> eventList;
    private final OnEventClickListener onEventClickListener;

    // Interfaz para manejar clics en los eventos
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    // Constructor del adaptador
    public Events_Adapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.onEventClickListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño item_evento
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evento, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        Event event = eventList.get(position);

        // Obtener el correo del usuario actual
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Comprobar si el usuario ha reservado el evento
        if (event.getUsers().contains(userEmail)) {
            // Si el usuario ha reservado, mostrar el ícono de reserva
            holder.ivReservedIcon.setVisibility(View.VISIBLE);
        } else {
            // Si no ha reservado, ocultar el ícono
            holder.ivReservedIcon.setVisibility(View.GONE);
        }

        // Configurar datos del evento en las vistas
        holder.tvTituloEvento.setText(event.getEventName());
        holder.tvPrecioEvento.setText("Precio: " + event.getPrice());
        holder.tvUbicacionEvento.setText("Ubicación: " + event.getEventLocation());
        holder.tvFechaHoraEvento.setText("Día: " + event.getEventDate() + " - Hora: " + event.getEventTime());

        // Aquí puedes cargar la imagen si tienes URLs, por ejemplo, con Glide o Picasso
        // Glide.with(holder.itemView.getContext()).load(event.getImageUrl()).into(holder.ivImagenEvento);

        // Manejar clic en el elemento
        holder.itemView.setOnClickListener(v -> onEventClickListener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // ViewHolder para los elementos del RecyclerView
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloEvento, tvPrecioEvento, tvUbicacionEvento, tvFechaHoraEvento;
        ImageView ivImagenEvento, ivReservedIcon;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloEvento = itemView.findViewById(R.id.tvTituloEvento);
            tvPrecioEvento = itemView.findViewById(R.id.tvPrecioEvento);
            tvUbicacionEvento = itemView.findViewById(R.id.tvUbicacionEvento);
            tvFechaHoraEvento = itemView.findViewById(R.id.tvFechaHoraEvento);
            ivImagenEvento = itemView.findViewById(R.id.ivImagenEvento);
            ivReservedIcon = itemView.findViewById(R.id.ivReservedIcon);
        }
    }
}
