package com.karam.transport;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class RecordFrag extends BottomSheetDialogFragment implements View.OnClickListener , View.OnTouchListener {
    Button btnFinalizar,btnCamera;
    TextView timerTxtvw,emailTxtVw,motivoTitle,imgCounterTxtVw;
    EditText obsEditTxt,emailEditTxt;
    Button recordBtn,deleteBtn,playBtn;
    LinearLayout recordLinLayout;
    View recordLayout;
    SeekBar recordBar;
    CountDownTimer timer,timer2;
    Spinner motivoSpinner;
    boolean checkRecordingTime = false;
    boolean checkTiming = true;
    boolean isClicked = false;
    boolean isFinalizar = false;
    String outputFile,email_cliente;
    HashMap<String,String> globalResponse;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    AlertDialog alertDialog;
    final int maxTime = 180000;
    private Handler mHandler;
    Runnable mRunnable;
    String[] motivoDes;

    int whitchActivity=0;//check activity 0 , produtos activity 1
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        timerTxtvw = view.findViewById(R.id.record_timer_txtvw);
        obsEditTxt= view.findViewById(R.id.record_obs_edittxt);
        emailEditTxt=view.findViewById(R.id.record_email_edittxt);
        imgCounterTxtVw = view.findViewById(R.id.record_imgs_txtvw);
        recordBtn= view.findViewById(R.id.recorder_record_btn);
        deleteBtn= view.findViewById(R.id.recorder_delete_btn);
        playBtn= view.findViewById(R.id.recorder_play_btn);
        btnCamera = view.findViewById(R.id.recorder_camera_btn);
        recordBar= view.findViewById(R.id.recorder_bar);
        recordLinLayout = view.findViewById(R.id.record_bar_layout);
        recordLayout = view.findViewById(R.id.record_layout);
        emailTxtVw = view.findViewById(R.id.recorder_email_txtvw);
        btnFinalizar = view.findViewById(R.id.check_finilizar_btn);
        //set the output file path str that sent from parent fragment
        Bundle args = this.getArguments();
        outputFile = args.getString("outputFile");
        emailTxtVw.setText(args.getString("email_cliente"));
        whitchActivity = args.getInt("whitchActivity");
        //set Spinner
        setUpSpinner(view);
        //add listeners
        recordBtn.setOnClickListener(this);
        recordBar.setOnTouchListener(this);
        btnFinalizar.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        recordLayout.setOnTouchListener(this);
        //set the number of the token images
        setTokenImagesNumber();
        emailEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(whitchActivity==0){
                    ((CheckActivity)getActivity()).setEmail_cliente2(String.valueOf(emailEditTxt.getText()));
                }else if(whitchActivity==1){
                    ((ProdutosActivity)getActivity()).setEmail_cliente2(String.valueOf(emailEditTxt.getText()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        obsEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(whitchActivity==0){
                    ((CheckActivity)getActivity()).setObs(String.valueOf(obsEditTxt.getText()));
                }else if(whitchActivity==1){
                    ((ProdutosActivity)getActivity()).setObs(String.valueOf(obsEditTxt.getText()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(isFinalizar==false){
            if(outputFile!=null){
                File file = new File(outputFile);
                if(file.exists()){
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recorder_camera_btn:
                if(whitchActivity==0){
                    Methods.takePicture(getContext(),getActivity(),((CheckActivity)getActivity()).getImageOrder());
                }else if(whitchActivity==1){
                    Methods.takePicture(getContext(),getActivity(),((ProdutosActivity)getActivity()).getImageOrder());
                }
                break;
            case R.id.toast_btn_confirm:
                goToSettings();
                break;
            case R.id.toast_btn_dismiss:
                alertDialog.cancel();
                break;
            case R.id.recorder_play_btn:
                play();
                break;
            case R.id.recorder_delete_btn:
                deleteAudio();
                break;
            case R.id.recorder_record_btn:
                //check the record permission
                Dexter.withContext(getContext()).
                        withPermission(Manifest.permission.RECORD_AUDIO)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                if (isClicked == false) {
                                    btnFinalizar.setEnabled(false);
                                    recordBtn.setBackgroundResource(R.drawable.record2);
                                    recordBtn.setEnabled(false);
                                    recordTiming();
                                    startRecording();
                                    timer = new CountDownTimer(1000, 100) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            checkRecordingTime = false;
                                        }

                                        @Override
                                        public void onFinish() {
                                            checkRecordingTime = true;
                                            recordBtn.setEnabled(true);
                                        }
                                    }.start();
                                    isClicked = !isClicked;
                                } else {
                                    if (checkRecordingTime == true) {
                                        recorded();
                                        isClicked = !isClicked;
                                        recordBtn.setBackgroundResource(R.drawable.record);
                                        btnFinalizar.setEnabled(true);
                                    }
                                }
                            }
                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                showMessageOnPermissionDenied();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
                break;
            case R.id.check_finilizar_btn:
                isFinalizar = true;
                if(whitchActivity==0){
                    btnFinalizar.setEnabled(false);
                    ((CheckActivity)getActivity()).finalizar();
                }else if(whitchActivity==1){
                    btnFinalizar.setEnabled(false);
                    ((ProdutosActivity)getActivity()).finalizar();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.recorder_bar){
            return false;
        }else if(v.getId()==R.id.record_layout){
            Methods.hideKeyboardFrom(getContext(),recordLayout);
        }
        return true;
    }



    private void deleteAudio() {
        File file = new File(outputFile);
        if(file.delete()){
            playBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            recordBtn.setEnabled(true);
            timerTxtvw.setText("00:00");
            checkTiming = true;
            recordLinLayout.setVisibility(View.INVISIBLE);
            releaseMediaPlayer();
            if(mHandler!= null){
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            recordBar.setProgress(0);
            playBtn.setBackgroundResource(R.drawable.play);
        }
    }

    private void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }



    private void recorded() {
        stopRecording();
        //show the recording layout
        recordLinLayout.setVisibility(View.VISIBLE);
        //stop the timer
        checkTiming=false;
        timer2.cancel();
        //activate and disactivate the layout
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordBtn.setEnabled(false);
        playBtn.setEnabled(true);
        deleteBtn.setEnabled(true);
        isFinalizar = true;
        btnFinalizar.setEnabled(true);
        //upload_audio("myRecord"+String.valueOf(id)+".3gp",outputFile,"http://10.0.2.2/pendencia_composer/classes/Uploader.php");
    }



    public  void startRecording(){
        if(mediaRecorder == null && outputFile!=null){
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
                globalResponse = setRespnse("401","erro",e.getMessage());
            }catch (IllegalStateException e2){
                e2.printStackTrace();
                globalResponse = setRespnse("401","erro", e2.getMessage());
            }
        }
    }

    public void stopRecording() {
        try{
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }catch (IllegalStateException e){
            e.printStackTrace();
            globalResponse = setRespnse("402","erro", e.getMessage());
        }
    }

    public void play(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playBtn.setBackgroundResource(R.drawable.play);

        }else{
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playBtn.setBackgroundResource(R.drawable.play);
                }
            });
            playBtn.setBackgroundResource(R.drawable.pause);
            recordBar.setMax(mediaPlayer.getDuration()/1000);
            mHandler = new Handler();
            mRunnable = setRunnable();
            //Make sure you update Seekbar on UI thread
            getActivity().runOnUiThread(mRunnable);
        }
    }
    private void releaseMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private Runnable setRunnable(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    recordBar.setProgress(mCurrentPosition);
                    if(mCurrentPosition == mediaPlayer.getDuration()){
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler=null;
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        return runnable;
    }


    private HashMap<String,String> setRespnse(String cod,String error,String message){
        HashMap<String,String> map = new HashMap<>();
        map.put("cod",cod);
        map.put("status",error);
        map.put("message",message);
        return map;
    }


    private void showMessageOnPermissionDenied(){
        View view = Methods.setToastView(getActivity(),getString(R.string.permission_title),true,
                getString(R.string.permission_audio), true,"Conceder",true,
                "Negar",true);
        Button btnConfirm = view.findViewById(R.id.toast_btn_confirm);
        btnConfirm.setOnClickListener(RecordFrag.this);
        Button btnCancel= view.findViewById(R.id.toast_btn_dismiss);
        btnCancel.setOnClickListener(RecordFrag.this);
        alertDialog = new AlertDialog.Builder(getContext())
                .setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
    private boolean recordTiming(){
        timer2 = new CountDownTimer(maxTime,1000) {
            int counter = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                if(checkTiming){
                    counter++;
                    int minutes = counter/60;
                    int seconds = counter%60;
                    timerTxtvw.setText(String.format("%d:%02d", minutes, seconds));
                }else{
                    timer2.cancel();
                }
            }
            @Override
            public void onFinish() {
                recorded();
            }
        }.start();
        return true;
    }

    private void setUpSpinner(View view) {
        motivoSpinner = view.findViewById(R.id.record_motivo_spinner);
        motivoTitle = view.findViewById(R.id.recorder_motivoTitle_txtvw);
        if (whitchActivity == 0) {
            motivoDes = getResources().getStringArray(R.array.motivos_dev_des);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, motivoDes) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = null;
                    v = super.getDropDownView(position, null, parent);
                    //v.setBackgroundColor(getResources().getColor(R.color.colorBege_app));
                    v.setBackgroundResource(R.drawable.spinner_background);
                    v.setElevation(5);
                    return v;
                }
            };
            motivoSpinner.setAdapter(dataAdapter);
            motivoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((CheckActivity) getActivity()).setPosSpinner(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else {
            motivoSpinner.setVisibility(View.INVISIBLE);
            motivoTitle.setVisibility(View.INVISIBLE);
        }
    }

    public void setTokenImagesNumber(){
        int num = 0;
        if(whitchActivity==0){
            num = (int) Methods.getSharedPref(getActivity(),"int",getString(R.string.SHimgOrderDevRenPer));
        }else if(whitchActivity==1){
            num = (int) Methods.getSharedPref(getActivity(),"int",getString(R.string.SHimgOrder));
        }
        imgCounterTxtVw.setText(String.valueOf(num));
    }
}
