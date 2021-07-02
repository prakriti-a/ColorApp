package com.prakriti.colorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
// save image works better on physical device

    private static final int CAMERA_REQUEST = 99;
    private static final int CAMERA_INTENT = 100;
    private static final int STORAGE_REQUEST = 50;

    private SeekBar seekRed, seekGreen, seekBlue;
    private TextView txtRedValue, txtGreenValue, txtBlueValue;
    private ImageView imgPicture;
    private Bitmap bitmap;
    private ImageColors colorsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPicture = findViewById(R.id.imgPicture);
        seekRed = findViewById(R.id.seekRed);
        seekGreen = findViewById(R.id.seekGreen);
        seekBlue = findViewById(R.id.seekBlue);

        txtRedValue = findViewById(R.id.txtRedValue);
        txtGreenValue = findViewById(R.id.txtGreenValue);
        txtBlueValue = findViewById(R.id.txtBlueValue);

        findViewById(R.id.btnTakePicture).setOnClickListener(this);
        findViewById(R.id.btnSavePicture).setOnClickListener(this);
        findViewById(R.id.btnSharePicture).setOnClickListener(this);

        ColorizationHandler colorizationHandler = new ColorizationHandler();
        seekRed.setOnSeekBarChangeListener(colorizationHandler);
        seekGreen.setOnSeekBarChangeListener(colorizationHandler);
        seekBlue.setOnSeekBarChangeListener(colorizationHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTakePicture: // camera permission
                checkCameraPermission();
                break;
            case R.id.btnSavePicture: // external storage permission
                checkExternalStoragePermission();
                break;
            case R.id.btnSharePicture:
                shareImageViaOtherApps();
                break;
        }
    }

    private void checkCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraPermissionGranted();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_REQUEST);
        }
    }

    private void checkExternalStoragePermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            externalStoragePermissionGranted();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, STORAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionGranted();
                } else {
                    checkCameraPermission();
                }
                break;
            case STORAGE_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    externalStoragePermissionGranted();
                } else {
                    checkExternalStoragePermission();
                }
                break;
        }
    }

    private void externalStoragePermissionGranted() {
        // call saveFile() from SaveImage class -> throws exception
        try {
            if(bitmap == null) {
                Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show();
                return;
            }
            SaveImage.saveFile(this, bitmap);
            Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cameraPermissionGranted() {
        PackageManager packageManager = getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) { // camera exists & we can access it
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_INTENT); // camera app opens
        }
        else { // no camera
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_INTENT && resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.image_captured, Toast.LENGTH_SHORT).show();
            Bundle bundle = data.getExtras(); // access extended data of intent as map -> image captured
            bitmap = (Bitmap) bundle.get("data"); // pass data as key and cast Object to Bitmap
            // init ImageColors object
            colorsObject = new ImageColors(bitmap, 0.0f, 0.0f, 0.0f);
            imgPicture.setImageBitmap(bitmap);
        }
    }

    private void shareImageViaOtherApps() {
        try { // null check
            if(bitmap == null) {
                Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show();
                return;
            }
            File imageFile = SaveImage.saveFile(this, bitmap);
            Uri myUri = Uri.fromFile(imageFile); // has ref to image
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Image sent from ColorApp");
            shareIntent.putExtra(Intent.EXTRA_STREAM, myUri);

            startActivity(Intent.createChooser(shareIntent, "Share your image via...")); // URI exposed Exception --?
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    // private inner class
    private class ColorizationHandler implements SeekBar.OnSeekBarChangeListener {

        @RequiresApi(api = Build.VERSION_CODES.O) // for accessing Colors.argb() from ImageColors {}
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                if (colorsObject != null) {
                    switch (seekBar.getId()) {
                        case R.id.seekRed:
                            colorsObject.setRedColorValue((float) progress / 100); // decimal val between 0 and 1
                            txtRedValue.setText(progress + "% R");
                            break;

                        case R.id.seekGreen:
                            colorsObject.setGreenColorValue((float) progress / 100);
                            txtGreenValue.setText(progress + "% G");
                            break;

                        case R.id.seekBlue:
                            colorsObject.setBlueColorValue((float) progress / 100);
                            txtBlueValue.setText(progress + "% B");
                            break;
                    }
                    // after changing seekbar values, colorize the image accordingly and set it to image view
                    bitmap = colorsObject.returnColorizedBitmap();
                    imgPicture.setImageBitmap(bitmap);
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.select_image, Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

}