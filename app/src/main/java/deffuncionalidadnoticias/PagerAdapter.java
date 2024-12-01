package deffuncionalidadnoticias;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.Notices_Fragment;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull Notices_Fragment fragmentActivity) {
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
