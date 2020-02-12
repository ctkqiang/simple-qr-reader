package com.johnmelodyme.simpleqrreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.barcode.BarcodeDetector.Builder;

import java.io.IOException;
import java.security.CodeSource;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "QR";
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private SurfaceView surfaceView;
    private TextView CodeValue;
    private Button Scan_btn;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private String intentData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting application......");
        surfaceView = findViewById(R.id.surfaceView);
        CodeValue = findViewById(R.id.QrValue);
        Scan_btn = findViewById(R.id.Scan);

        init();

        Scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0 ){
                    //
                }
            }
        });
    }

    private void init() {
        barcodeDetector = new BarcodeDetector
                .Builder(MainActivity.this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource
                .Builder(MainActivity.this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

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
                cameraSource.stop();
            }
        });


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
                        @Override
                        public void run() {
                            intentData = barcodes.valueAt(0).displayValue;
                            CodeValue.setText(intentData);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
