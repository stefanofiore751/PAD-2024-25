package deffuncionalidadnoticias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.ucm.fdi.viva_tu_pueblo.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TecnologiaFragment extends Fragment {

    String api = "b5c1b7fd34d0448292156a799e4c0055";
    ArrayList<ModelClass> modelClassArrayList;
    Adapter adapter;
    String country = "us";
    private RecyclerView recyclerViewTecnologia;
    private String category = "technology";

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.tecnologiafragment,null);

        recyclerViewTecnologia = v.findViewById(R.id.recyclerViewTecnologia);
        modelClassArrayList = new ArrayList<>();
        recyclerViewTecnologia.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter(getContext(), modelClassArrayList);
        recyclerViewTecnologia.setAdapter(adapter);

        findNews();

        return v;
    }

    private void findNews() {

        ApiUtilities.getApiInterface().getCategoryNews(country, category).enqueue(new Callback<mainNews>() {
            @Override
            public void onResponse(Call<mainNews> call, Response<mainNews> response) {

                if(response.isSuccessful()){
                    modelClassArrayList.addAll(response.body().getArticles());
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<mainNews> call, Throwable t) {

            }
        });

    }

}

