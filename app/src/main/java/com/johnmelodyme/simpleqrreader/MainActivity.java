package com.johnmelodyme.simpleqrreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.core.CrashlyticsCore;

import java.io.IOException;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @Author: John Melody Melissa
 * @Project: QRREADER
 * @Inpired : By GF TAN SIN DEE <3
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "QR";
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private SurfaceView surfaceView;
    private TextView CodeValue;
    private Button Scan_btn;
    private CameraSource cameraSource;
    private String intentData = "";
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting application......");
        surfaceView = findViewById(R.id.surfaceView);
        CodeValue = findViewById(R.id.QrValue);
        Scan_btn = findViewById(R.id.Scan);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        init();
    }

    //TODO Init
    private void init() {
        BarcodeDetector barcodeDetector = new BarcodeDetector
                .Builder(MainActivity.this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource
                .Builder(MainActivity.this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        // TODO SURFACE_VIEW:
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                        Log.d(TAG, "surfaceCreated: getholder");
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        Log.d(TAG, "surfaceCreated: REQUEST_CAMERA_PERMISSION");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "surfaceCreated: " + e);
            }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                /////////////////////
                cameraSource.stop();
            }
        });


        // TODO BARCODE_DETECTOR:
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(),
                        "To prevent memory leaks barcode scanner has been stopped",
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes;
                barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    CodeValue.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            intentData = barcodes.valueAt(0).displayValue;
                            CodeValue.setText("Data: " +intentData);
                            Toast.makeText(getApplicationContext(),
                                    intentData,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            String result, url;
                            url = intentData;
                            result = CodeValue
                                    .getText()
                                    .toString()
                                    .trim();
                            Log.d(TAG, "CodeValue: " + result);
                            if (intentData.contains("www") || intentData.contains("http") || intentData.contains("https") || intentData.contains("html")){
                                Intent URL_DATA;
                                URL_DATA = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(URL_DATA);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
        Scan_btn.setText(R.string.Scan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.donate) {
            Intent donate;
            donate = new Intent(MainActivity.this, donateme.class);
            startActivity(donate);
            return true;
        }

        if (id == R.id.about){
            new SweetAlertDialog(MainActivity.this)
                    .setTitleText("Version 1.0.0")
                    .setContentText("Developed by John Melody Melissa")
                    .show();
            return true;
        }

        if (id == R.id.source){
            String URL;
            URL = getResources().getString(R.string.url);
            Intent SOURCE_CODE;
            SOURCE_CODE = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
            startActivity(SOURCE_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}