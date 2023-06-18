package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Objects;

/**
 * scan the the digits in the barcode from the image and check if them in repository
 */
public class ScannerProduct extends AppCompatActivity {
    //properties
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;
    private int whichScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_product);
        //sound when scan a product
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //get id
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);

        Intent intent = getIntent();                            //get intent
        Bundle data = intent.getExtras();
        if (data != null) {
            whichScreen = data.getInt("screen");       //get message of product name
        }
        initialiseDetectorsAndSources();
    }

    //scan the image for a barcode.
    private void initialiseDetectorsAndSources() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)         //object of barcode detector - detect barcodes in an image
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)         //access the device's camera and obtain video frames for processing.
                .setRequestedPreviewSize(1920, 1080)                            //set to use an auto-focus enabled camera with a requested preview size of 1920x1080
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        //part in memory that show in screen
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            /**
             * called when the SurfaceView is created
             * @param holder - SurfaceHolder
             */
            @Override
            public void surfaceCreated(SurfaceHolder holder) {          //holder give permission only to one
                try {
                    if (ActivityCompat.checkSelfPermission(ScannerProduct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannerProduct.this, new              //request permissions
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {               //catch io exception
                    e.printStackTrace();
                }
            }

            /**
             * called when the SurfaceView is changed
             * @param holder - SurfaceHolder
             * @param format - format
             * @param width - width of SurfaceView
             * @param height - height of SurfaceView
             */
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            /**
             * called when the SurfaceView is destroyed
             * @param holder - SurfaceHolder
             */
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            /**
             * used to release the resources associated with a BarcodeDetector object.
             */
            @Override
            public void release() {
            }

            /**
             * This method is called by the BarcodeDetector when it has detected one or more barcodes in the camera's preview frame
             * @param detections - Detector.Detections<Barcode>, detected barcode like coordinates and values
             */
            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeText.post(() -> {                        //lambda to Runnable, executed on the main thread
                        barcodeData = barcodes.valueAt(0).displayValue;
                        com.example.project.Barcode barcode = new com.example.project.Barcode();
                        if(barcode.barcodeExists(barcodeData)) {                  //check if the barcode exist in repository
                            barcodeText.setText(barcodeData);                     //set text the value of barcode number
                        }
                        else
                            Toast.makeText(ScannerProduct.this, "not found the barcode in repository", Toast.LENGTH_SHORT).show();

                        Intent i;
                        if(whichScreen == 1)
                            i = new Intent(ScannerProduct.this, AddProductActivity.class);       //go back to add product activity
                        else
                            i = new Intent(ScannerProduct.this, SearchProductActivity.class);       //go back to search product activity
                        i.putExtra("barcode", barcodeText.getText().toString());                            //the barcode number
                        i.putExtra("product_name", MySql.getProductInBarcode(barcodeText.getText().toString()));        //get product name
                        startActivity(i);

                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                    });
                }
            }
        });
    }


    /**
     * onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
        Objects.requireNonNull(getSupportActionBar()).hide();
        cameraSource.release();
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        Objects.requireNonNull(getSupportActionBar()).hide();
        initialiseDetectorsAndSources();
    }
}