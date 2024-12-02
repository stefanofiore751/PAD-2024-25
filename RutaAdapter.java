package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Modelos.Ruta;
import es.ucm.fdi.viva_tu_pueblo.R;

public class RutaAdapter extends RecyclerView.Adapter<RutaAdapter.ViewHolder>{
    private List<Ruta> mRutaData;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView wordItemView;
        public ImageView portada;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wordItemView = (TextView) itemView.findViewById(R.id.word);
        }
        public TextView getTextView() {
            return wordItemView;
        }
    }
    //protected ArrayList<String> mWordList;
    protected View.OnClickListener onClickListener;
    public RutaAdapter(List<Ruta> wordList) {
        this.mRutaData = wordList;
    }
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    @NonNull
    @Override
    public RutaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Cargar la vista desde el xml (con View)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutaAdapter.ViewHolder holder, int position) {
        // Recuperar los datos de esa posición.
        Ruta mCurrent = mRutaData.get(position);
        // Añadir el dato a la view.
        holder.wordItemView.setText(mCurrent.getNombreRuta());
        //holder.portada.setImageBitmap(mCurrent.getImage());
    }

    @Override
    public int getItemCount() {
        return mRutaData.size();
    }

    public List<Ruta> getmRutaData() {
        return mRutaData;
    }

    public void setBooksData(ArrayList<Ruta> data){
        mRutaData=data;
    }
    public void setData(List<Ruta> data){
        mRutaData=data;
        this.notifyDataSetChanged();
    }
}
