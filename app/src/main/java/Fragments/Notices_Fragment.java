package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.viewpager2.widget.ViewPager2;

import es.ucm.fdi.viva_tu_pueblo.R;

import deffuncionalidadnoticias.PagerAdapter;
public class Notices_Fragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        setupViewPager();

        return view;
    }

    private void setupViewPager() {
        // Usa il PagerAdapter adattato
        PagerAdapter pagerAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Collega il TabLayout con il ViewPager2
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
    }
}
