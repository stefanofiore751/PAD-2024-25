package viva_tu_pueblo;
import static org.maplibre.android.camera.CameraPosition.*;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.geometry.LatLngBounds;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;
import org.maplibre.android.style.layers.Layer;
import org.maplibre.android.style.layers.LineLayer;
import org.maplibre.android.style.layers.Property;
import org.maplibre.android.style.layers.PropertyFactory;
import org.maplibre.android.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import Adapters.RutaAdapter;
import Modelos.Ruta;
import Modelos.RutaLoader;
import es.ucm.fdi.viva_tu_pueblo.R;

public class RutaActivity extends AppCompatActivity {
    private MapView mapView;
    private MapLibreMap mapM;
    public static int RUTA_LOADER_ID=0;
    private RutaLoaderCallBacks rutaLoaderCallbacks = new RutaLoaderCallBacks();
    private RutaAdapter mAdapter;
    private List<Ruta> mWordList=new ArrayList<Ruta>();
    //private Style style;
    private boolean styleLoaded=false;
    private RutaActivity main=this;
    private int rutaActual=-1;
    public class RutaLoaderCallBacks implements LoaderManager.LoaderCallbacks<List<Ruta>>{
        public Loader<List<Ruta>> onCreateLoader(int id, @Nullable Bundle args) {
            return new RutaLoader(RutaActivity.this);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Ruta>> loader, List<Ruta> data) {
            while(!styleLoaded) {}
            Style style=mapM.getStyle();
            for(int i=0;i<data.size();i++){
                Ruta r=data.get(i);
                GeoJsonSource geoSource=new GeoJsonSource(r.getId(),r.getF());
                style.addSource(geoSource);
                LineLayer line=new LineLayer(r.getId(),r.getId()).withProperties(
                        PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                        PropertyFactory.lineOpacity(0.7f),
                        PropertyFactory.lineWidth(4f),
                        PropertyFactory.lineColor("#0094ff"),
                        PropertyFactory.visibility(Property.NONE)
                );
                style.addLayer(line);
            }
            LatLng latLng=new LatLng(48.90046,2.319887);
            double array[]= {1.0,1.0,1.0,1.0};
            mapM.setCameraPosition(new CameraPosition(latLng,11,0,0,array));
            mAdapter.setData(data);
            mWordList=data;
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Ruta>> loader) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(RUTA_LOADER_ID) != null){
            loaderManager.initLoader(RUTA_LOADER_ID,null,
                    rutaLoaderCallbacks);
        }


        MapLibre m=  MapLibre.getInstance(this.getApplicationContext());
        LayoutInflater inflater= LayoutInflater.from(this.getApplicationContext());
        View rootView=inflater.inflate(R.layout.activity_ruta, null);
        setContentView(rootView);
        mapView= rootView.findViewById(R.id.mapView);
        mAdapter = new RutaAdapter(mWordList); //se debe tener mWord List
        // Acceso a la view del layout con View binding:
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(mAdapter);
        // Controlar cuando se hace clic.
        mAdapter.setOnItemClickListener(v -> {
            int pos =  recyclerView.getChildAdapterPosition(v);
            //A continuación, invocar al método que responda al evento de haber
            // hecho clic sobre el elemento de la lista que está en la posición pos.
            if(pos!=rutaActual){
                Style s=mapM.getStyle();
                if(rutaActual!=-1){
                    int posAnterior = rutaActual;
                    Layer l=s.getLayer(mAdapter.getmRutaData().get(posAnterior).getId());
                    l.setProperties(
                            PropertyFactory.visibility(Property.NONE)
                    );
                }
                Layer l=s.getLayer(mAdapter.getmRutaData().get(pos).getId());
                l.setProperties(
                        PropertyFactory.visibility(Property.VISIBLE)
                );
                rutaActual=pos;
                Ruta r= mWordList.get(rutaActual);
                double[]min=r.getMin();
                double[]max=r.getMax();
                double a=(min[1]+max[1])/2;
                double b=(min[0]+max[0])/2;
                LatLng latLng=new LatLng(a,b);
                double array[]= {1.0,1.0,1.0,1.0};
                LatLngBounds bounds=new LatLngBounds(min[1],min[0],max[1],max[0]);
                mapM.setLatLngBoundsForCameraTarget(bounds);
                mapM.setCameraPosition(new CameraPosition(latLng,11,0,0,array));
                bounds=new LatLngBounds(min[1],min[0],max[1],max[0]);
                mapM.setLatLngBoundsForCameraTarget(bounds);
            }

            Log.d("asd","asd");
        });
        Bundle queryBundle = new Bundle();
        // LoaderManager.getInstance(this).restartLoader(RUTA_LOADER_ID, queryBundle, rutaLoaderCallbacks);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
                mapM=mapLibreMap;
                mapLibreMap.setStyle("https://raw.githubusercontent.com/go2garret/maps/main/src/assets/json/openStreetMap.json", (style)->{
                    LatLng latLng=new LatLng(48.90046,2.319887);
                    double array[]= {1.0,1.0,1.0,1.0};
                    mapLibreMap.setCameraPosition(new CameraPosition(latLng,6,0,0,array));
                    styleLoaded=true;
                    LoaderManager.getInstance(main).restartLoader(RUTA_LOADER_ID, queryBundle, rutaLoaderCallbacks);
                });

            }
        });
    }
    public void onStart() {

        super.onStart();
        mapView.onStart();
    }
    public void onResume() {

        super.onResume();
        mapView.onResume();
    }
    public void onPause() {

        super.onPause();
        mapView.onPause();
    }
    public void onStop() {

        super.onStop();
        mapView.onStop();
    }
    public void onLowMemory() {

        super.onLowMemory();
        mapView.onLowMemory();
    }
    public void onDestroy() {

        super.onDestroy();
        mapView.onDestroy();
    }
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    public void atras(View v){
        finish();
    }

}