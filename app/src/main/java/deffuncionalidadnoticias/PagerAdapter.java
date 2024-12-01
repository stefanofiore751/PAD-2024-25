package deffuncionalidadnoticias;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new InicioFragment();
            case 1:
                return new DeportesFragment();
            case 2:
                return new SaludFragment();
            case 3:
                return new CienciaFragment();
            case 4:
                return new EntretenimientoFragment();
            case 5:
                return new TecnologiaFragment();
            default:
                throw new IllegalArgumentException("Posición inválida: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
