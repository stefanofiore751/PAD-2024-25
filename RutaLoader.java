package Modelos;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.maplibre.geojson.Feature;

import java.util.ArrayList;
import java.util.List;

public class RutaLoader extends AsyncTaskLoader<List<Ruta>> {
    public RutaLoader(Context c){
        super(c);
    }
    @Nullable
    @Override
    public List<Ruta> loadInBackground() {
        List<Ruta> asd=new ArrayList<Ruta>();
        int id=0;
        Rutas d=new Rutas();
        Ruta r=new Ruta("ruta",String.valueOf(id),d.getRuta1());
        String s=" {\n" +
                "                        \"type\": \"Feature\",\n" +
                "                        \"properties\": {},\n" +
                "                        \"geometry\": {\n" +
                "                            \"type\": \"MultiLineString\",\n" +
                "                            \"coordinates\": [\n" +
                d.getRuta1().toString()+"\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    }";

        id++;
        Feature f=Feature.fromJson(r.getSource());
        r.setF(f);
        asd.add(r);
        Ruta r1=new Ruta("ruta1",String.valueOf(id),d.getRuta2());
        id++;
        String s1=" {\n" +
                "                        \"type\": \"Feature\",\n" +
                "                        \"properties\": {},\n" +
                "                        \"geometry\": {\n" +
                "                            \"type\": \"MultiLineString\",\n" +
                "                            \"coordinates\": [\n" +
                d.getRuta2().toString()+"\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    }";


        Feature f1=Feature.fromJson(r1.getSource());
        r1.setF(f1);
        asd.add(r1);
        return asd;
    }
    public void onStartLoading(){
        forceLoad();
    }
}
