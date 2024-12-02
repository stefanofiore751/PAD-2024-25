package Modelos;

import org.maplibre.android.style.sources.GeoJsonSource;
import org.maplibre.geojson.Feature;

import java.util.ArrayList;

public class Ruta {
    private String nombreRuta;
    private ArrayList<ArrayList<Double>> ruta;
    private GeoJsonSource source;
    private boolean estiloAñadido;
    private String id;
    private Feature f;
    private double[] min={10000,10000};
    private double[] max={-10000,-10000};
    public Ruta(String nombreRuta,String id,double[][]ruta1){
        this.nombreRuta=nombreRuta;
        this.id=id;
        ruta=new ArrayList<ArrayList<Double>>();
        for(int i=0;i<ruta1.length;i++){
            ArrayList<Double>a=new ArrayList<Double>();
            min[0]=getMinlatLong(ruta1[i][0],min[0]);
            min[1]=getMinlatLong(ruta1[i][1],min[1]);
            max[0]=getMaxlatLong(ruta1[i][0],max[0]);
            max[1]=getMaxlatLong(ruta1[i][1],max[1]);
            a.add(ruta1[i][0]);
            a.add(ruta1[i][1]);
            ruta.add(a);
        }
        int asd=0;
    }
    public double[] getMin(){
        return this.min;
    }
    public double[] getMax(){
        return this.max;
    }
    private double getMinlatLong(double a,double b){
        if(a<b)
            return a;
        return b;
    }
    private double getMaxlatLong(double a,double b){
        if(a>b)
            return a;
        return b;
    }
    public String getId(){
        return this.id;
    }
    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public String getSource() {
        String s=" {\n" +
                "                        \"type\": \"Feature\",\n" +
                "                        \"properties\": {},\n" +
                "                        \"geometry\": {\n" +
                "                            \"type\": \"MultiLineString\",\n" +
                "                            \"coordinates\": [\n" +
                this.ruta.toString()+"\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    }";
        return s;
    }


    public Feature getF() {
        return f;
    }

    public void setF(Feature f) {
        this.f = f;
    }
}
