package viva_tu_pueblo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

import es.ucm.fdi.viva_tu_pueblo.R;

public class Camara extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private boolean cameraPermision=false;
    private boolean storagePermision=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camara);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Request camera permissions
        if (PermissionsGranted(android.Manifest.permission.CAMERA)&&PermissionsGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startCamera();
        }
        else {
            //requestPermissions( new String[] { android.Manifest.permission.CAMERA ,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            String[] PERMISSIONS = {android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions( PERMISSIONS, CAMERA_PERMISSION_CODE);
            //requestPermissions( new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, STORAGE_PERMISSION_CODE);

        }

        // Set up the listeners for take photo and video capture buttons
        // viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        // viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        // cameraExecutor = Executors.newSingleThreadExecutor()
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==CAMERA_PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (PermissionsGranted(Manifest.permission.CAMERA)) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                startCamera();
                cameraPermision=true;

            }
        }
        else if(requestCode==STORAGE_PERMISSION_CODE){
            if (PermissionsGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                storagePermision=true;
            }
        }

        else {
            Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        if(storagePermision&&cameraPermision)
            startCamera();


    }
    public void takePhoto(View v) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        // insert your code here.
                        Log.d("Camara","Foto guardada");
                        Intent replyIntent = new Intent();
                        //replyIntent.putExtra("urlImage", outputFileResults.getSavedUri());
                        replyIntent.putExtra("urlImage", outputFileResults.getSavedUri().toString());
                        Log.d("Camara", String.valueOf(outputFileResults.getSavedUri()));
                        setResult(RESULT_OK, replyIntent);
                        finish();

                    }
                    @Override
                    public void onError(ImageCaptureException error) {
                        // insert your code here
                        Log.d("Camara","Error de camara");
                        finish();
                    }
                }
        );
    }
    private void captureVideo() {}
    void bindPreview( ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();
        PreviewView previewView = findViewById(R.id.previewView);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        //imageCapture = ImageCapture.Builder().build();
        imageCapture= new ImageCapture.Builder().build();
        //cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview,imageCapture);
    }
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean PermissionsGranted(String permissionRequired)  {
        return(ContextCompat.checkSelfPermission(this.getApplicationContext(), permissionRequired) == PackageManager.PERMISSION_GRANTED);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}