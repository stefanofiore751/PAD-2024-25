package viva_tu_pueblo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import Modelos.Reporte;
import es.ucm.fdi.viva_tu_pueblo.R;

public class IncidenciaActivity extends AppCompatActivity {
    private String image;
    private ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            switch (result.getResultCode()) {
                                case Activity.RESULT_OK:
// Recuperar intent.
                                    Intent intent =  result.getData();
                                    image=intent.getStringExtra("urlImage");

                                    break;
                                case Activity.RESULT_CANCELED:
// Procesar
                                    break;
                            }
                        }
                    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incidencia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void foto(View v){
        Intent intent = new Intent(this, Camara.class);
        intent.putExtra("level", "Basic");
        mStartForResult.launch(intent);
        //startActivity(intent);
    }
    public void enviar(View v){
        String asunto="";
        String descripcion="";
        TextView asun=findViewById(R.id.asuntoText);
        TextView desc=findViewById(R.id.descripcionText);
        asunto= String.valueOf(asun.getEditableText());
        descripcion= String.valueOf(desc.getEditableText());
        Reporte r=new Reporte(asunto,descripcion,image);
        finish();
    }
}