package Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.viewpager2.widget.ViewPager2;

import es.ucm.fdi.viva_tu_pueblo.R;

import deffuncionalidadnoticias.PagerAdapter;
import viva_tu_pueblo.MainActivity;
import viva_tu_pueblo.NetworkUtils;

public class Notices_Fragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Check for internet connection
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            // Show a message to the user
            Toast.makeText(requireContext(), R.string.no_internet_message, Toast.LENGTH_SHORT).show();

            // Redirect to the home fragment
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.putExtra("navigate_to", R.id.nav_home);
            startActivity(intent);
            requireActivity().finish();
            return null; // Return null since we're redirecting
        }
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        setupViewPager();

        return view;
    }

    private void setupViewPager() {
        PagerAdapter pagerAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.inicio);
                    break;
                case 1:
                    tab.setText(R.string.deportes);
                    break;
                case 2:
                    tab.setText(R.string.salud);
                    break;
                case 3:
                    tab.setText(R.string.ciencia);
                    break;
                case 4:
                    tab.setText(R.string.entretenimiento);
                    break;
                case 5:
                    tab.setText(R.string.tecnologia);
                    break;
            }
        }).attach();
    }
}
