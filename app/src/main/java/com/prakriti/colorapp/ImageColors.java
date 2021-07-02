package com.prakriti.colorapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ImageColors {

    private Bitmap bitmap;
    private float redColorValue, greenColorValue, blueColorValue;

    public ImageColors(Bitmap bitmap, float redColorValue, float greenColorValue, float blueColorValue) {
        this.bitmap = bitmap;
        setRedColorValue(redColorValue);
        setGreenColorValue(greenColorValue);
        setBlueColorValue(blueColorValue);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setRedColorValue(float redColorValue) {
        if(redColorValue >= 0 && redColorValue <= 1)
            this.redColorValue = redColorValue;
    }

    public void setGreenColorValue(float greenColorValue) {
        if(greenColorValue >= 0 && greenColorValue <= 1)
            this.greenColorValue = greenColorValue;
    }

    public void setBlueColorValue(float blueColorValue) {
        if(blueColorValue >= 0 && blueColorValue <= 1)
            this.blueColorValue = blueColorValue;
    }

    public float getRedColorValue() {
        return redColorValue;
    }

    public float getGreenColorValue() {
        return greenColorValue;
    }

    public float getBlueColorValue() {
        return blueColorValue;
    }

    // method to colorise image taken
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap returnColorizedBitmap() {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Bitmap.Config bitmapConfig = bitmap.getConfig(); // get bitmap format
            // create a bitmap with above info
        Bitmap localBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, bitmapConfig);
        // get pixels of image via loop
        for(int row = 0; row < bitmapWidth; row++) {
            for (int column = 0; column < bitmapHeight; column++) {
                // iterate over each pixel
                int pixelColor = bitmap.getPixel(row, column); // from instance var, get color as int
                // change color of each pixel, so pass pixelColor var -> cast to int if needed
                // Color.argb -> min req API is 26 for use
                pixelColor = Color.argb(Color.alpha(pixelColor), (redColorValue * Color.red(pixelColor)), (greenColorValue * Color.green(pixelColor)),
                        (blueColorValue * Color.blue(pixelColor)));
                // set the current pixel (row & column) with new color
                localBitmap.setPixel(row, column, pixelColor);
            }
        }
        return localBitmap;
    }

}
