package deffuncionalidadnoticias;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import es.ucm.fdi.viva_tu_pueblo.R;

public class MainActivity extends AppCompatActivity {

    String api = "b5c1b7fd34d0448292156a799e4c0055";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        PagerAdapter pagerAdapter = new PagerAdapter(this);
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
    }
}