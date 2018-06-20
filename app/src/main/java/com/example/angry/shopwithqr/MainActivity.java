package com.example.angry.shopwithqr;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;

import java.io.IOException;
import java.io.StringReader;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private static final int REQUEST_CAMERA= 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView= new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkPermission())
            {
                Toast.makeText(MainActivity.this,"Permission is Granted ", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermisson();
            }
        }


    }

    private boolean checkPermission(){
     return (ContextCompat.checkSelfPermission(MainActivity.this, CAMERA)== PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermisson()
     {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA},REQUEST_CAMERA);
      }
    public void onRequestPermissonsResult(int requestCode, String permisson[],int grantResult[])
     {
         switch (requestCode)
         {
             case REQUEST_CAMERA :
                 if(grantResult.length> 0 )
                 {
                     boolean cameraAccepted = grantResult[0]==PackageManager.PERMISSION_GRANTED;
                     if(cameraAccepted)
                     {
                         Toast.makeText(MainActivity.this,"Permisson granted",Toast.LENGTH_LONG).show();
                     }
                     else
                     {
                         Toast.makeText(MainActivity.this,"Permisson not granted",Toast.LENGTH_LONG).show();
                         if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                         {
                             if(shouldShowRequestPermissionRationale(CAMERA))
                             {
                               displayAlertMessage("You need to allow access for both Permission",
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                   requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                               }
                                           }
                                       });
                               return;

                             }
                         }
                     }

                 }
                 break;
         }
      }
      @Override
      public void onResume(){
        super.onResume();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
          if(checkPermission())
          {
              if(scannerView == null)
              {
                  scannerView=new ZXingScannerView(this);
                  setContentView(scannerView);
              }
              scannerView.setResultHandler(this);
              scannerView.startCamera();
          }
          else
          {
              requestPermisson();
          }
          }
      }

      @Override
      public void onDestroy(){
          super.onDestroy();
          scannerView.stopCamera();
      }

      public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok",listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
      }

@Override
    public void handleResult(final Result result) {
       final String scanResult= result.getText();
       AlertDialog.Builder builder= new AlertDialog.Builder(this);
       builder.setTitle("Scan Result");
       builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               scannerView.resumeCameraPreview(MainActivity.this);

           }
       });
       builder.setNeutralButton("Buy Now", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
               startActivity(intent);


           }
       });
       builder.setMessage(scanResult);
       AlertDialog alert = builder.create();
       alert.show();

    }
}

