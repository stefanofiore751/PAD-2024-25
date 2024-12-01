package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import es.ucm.fdi.viva_tu_pueblo.R;
import es.ucm.fdi.deffuncionalidadnoticias.PagerAdapter;

public class Notices_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_main, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        PagerAdapter pagerAdapter = new PagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Inicio");
                    break;
                case 1:
                    tab.setText("Deportes");
                    break;
                case 2:
                    tab.setText("Salud");
                    break;
                case 3:
                    tab.setText("Ciencia");
                    break;
                case 4:
                    tab.setText("Entretenimiento");
                    break;
                case 5:
                    tab.setText("Tecnología");
                    break;
            }
        }).attach();

        return view;
    }
}
