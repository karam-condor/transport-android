package com.karam.transport;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.overlay.OverlayLayout;

import java.util.List;

public class BarcodeActivity extends AppCompatActivity implements View.OnClickListener{
    CameraView cameraView;
    boolean isFlash = false;
    boolean isDetected = false;
    int widthInPixels,heightInPixels;
    Button flashBtn,backbtn;
    Vibrator vib;
    TextView txtBarcodeOverlay;
    View rectBarcode;
    FirebaseVisionBarcodeDetector detector;
    FirebaseVisionBarcodeDetectorOptions options;
    AlertDialog alertDialog;
    String chooser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        Intent barcodeIntent = this.getIntent();
        Bundle barcodeBundle = barcodeIntent.getExtras();
        chooser = barcodeBundle.getString("chooser","");
        //set covers height
        setCoverFrames();
        flashBtn = findViewById(R.id.btn_barcode_flash);
        backbtn =findViewById(R.id.btn_barcode_back);
        backbtn.setOnClickListener(BarcodeActivity.this);
        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        try{
            vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        }catch(Exception ex){

        }
        Dexter.withContext(this).withPermissions(Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if(multiplePermissionsReport.areAllPermissionsGranted()){
                    setupCamera();
                    flashBtn.setOnClickListener(BarcodeActivity.this);
                }else{
                    View view = Methods.setToastView(BarcodeActivity.this,getString(R.string.permission_title),true,
                            getString(R.string.permission_camera), true,"Conceder",true,
                            "Negar",true);
                    Button btnConfirm = view.findViewById(R.id.toast_btn_confirm);
                    btnConfirm.setOnClickListener(BarcodeActivity.this);
                    Button btnCancel= view.findViewById(R.id.toast_btn_dismiss);
                    btnCancel.setOnClickListener(BarcodeActivity.this);
                    alertDialog = new AlertDialog.Builder(BarcodeActivity.this)
                            .setView(view).create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                }
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void setCoverFrames() {
        try
        {
            rectBarcode = findViewById(R.id.barcodeOverlayRect);
            rectBarcode.setVisibility(View.INVISIBLE);
            txtBarcodeOverlay = findViewById(R.id.barcodeOverlayText);
            txtBarcodeOverlay.setVisibility(View.INVISIBLE);
            int statusBarHeight = 0;
            int resource = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resource > 0) {
                statusBarHeight = getApplicationContext().getResources().getDimensionPixelSize(resource);
            }

            widthInPixels = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
            heightInPixels = getApplicationContext().getResources().getDisplayMetrics().heightPixels-statusBarHeight;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            ConstraintLayout.LayoutParams params1,params2,params3;
            View upperCover = findViewById(R.id.upperCover);
            View lowerCover = findViewById(R.id.lowerCover);
            final View middleCover = findViewById(R.id.middleCover);

            params1 =(ConstraintLayout.LayoutParams) upperCover.getLayoutParams();
            params2 =(ConstraintLayout.LayoutParams) lowerCover.getLayoutParams();
            params3 =(ConstraintLayout.LayoutParams) middleCover.getLayoutParams();

            params1.width = widthInPixels;params2.width = widthInPixels;params3.width = 11*widthInPixels/12;
            params1.height = heightInPixels/6;params2.height = heightInPixels/6;params3.height =7* heightInPixels/12;
            params3.leftMargin = params3.rightMargin = widthInPixels - params3.width/2;
            upperCover.setLayoutParams(params1);
            lowerCover.setLayoutParams(params2);
            middleCover.setLayoutParams(params3);

            //add animation
            Animation animation = AnimationUtils.loadAnimation(this,R.anim.fading_in_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    animation = AnimationUtils.loadAnimation(BarcodeActivity.this,R.anim.fading_in_out);
                    animation.setAnimationListener(this);
                    middleCover.startAnimation(animation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            middleCover.startAnimation(animation);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setupCamera() {
        cameraView =findViewById(R.id.cameraView);
        cameraView.setLifecycleOwner(this);
        //cameraView.setAutoFocusResetDelay(0);
        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getVisionImageFromFrame(frame));

            }
        });
    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21 )
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                .setRotation(frame.getRotationToUser())
                .build();
        return FirebaseVisionImage.fromByteArray(data,metadata);
    }

    private void processImage(FirebaseVisionImage image) {
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        if(isDetected == false){
                            processResult(firebaseVisionBarcodes);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        if(firebaseVisionBarcodes.size()>0) {
            isDetected = true;
            cameraView.close();
            final String barcode = firebaseVisionBarcodes.get(0).getRawValue();
            final String barcodeDisplay = firebaseVisionBarcodes.get(0).getDisplayValue();
            final Rect rec = firebaseVisionBarcodes.get(0).getBoundingBox();
            vib.vibrate(VibrationEffect.EFFECT_TICK);
            CountDownTimer timer = new CountDownTimer(100,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    OverlayLayout.LayoutParams params = new OverlayLayout.LayoutParams(rec.width(),rec.height());
                    params.setMargins(rec.left,rec.top/2,0,0);
                    txtBarcodeOverlay.setText(barcodeDisplay);
                    txtBarcodeOverlay.setTextSize(0,rec.width()/barcodeDisplay.length());
                    txtBarcodeOverlay.setLayoutParams(params);
                    txtBarcodeOverlay.setVisibility(View.VISIBLE);
                    rectBarcode.setLayoutParams(params);
                    rectBarcode.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFinish() {
                    Intent intent = new Intent();
                    if(chooser.matches("nf")){
                        intent = new Intent("GETBARCODENF");
                    }else if(chooser.matches("prod")){
                        intent = new Intent("GETBARCODEPROD");
                    }
                    intent.putExtra("barcode", barcode);
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                    finish();
                }
            }.start();
        }
        else
        {
            txtBarcodeOverlay.setVisibility(View.INVISIBLE);
            rectBarcode.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toast_btn_confirm:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.toast_btn_dismiss:
                alertDialog.cancel();
                break;
            case R.id.btn_barcode_flash:
                if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
                    isFlash = !isFlash;
                    if(isFlash==true){
                        cameraView.setFlash(Flash.TORCH);
                        flashBtn.setBackgroundResource(R.drawable.flash_on);
                    }else{
                        cameraView.setFlash(Flash.OFF);
                        flashBtn.setBackgroundResource(R.drawable.flash_off);
                    }
                }else{
                    View view = Methods.setToastView(BarcodeActivity.this,"",false,getString(R.string.no_flash),
                            true,"",false,"",false);
                    Toast toast = Toast.makeText(BarcodeActivity.this, getString(R.string.no_flash), Toast.LENGTH_SHORT);
                    toast.setView(view);
                    toast.show();
                }
                break;
            case R.id.btn_barcode_back:
                finish();
                break;
        }
    }
}