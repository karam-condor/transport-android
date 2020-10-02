package com.karam.transport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginCarregFrag extends Fragment implements TaskListener, View.OnTouchListener , View.OnClickListener {
    String TAG,token,insertedToken;
    EditText carregEditText;
    Button carregButton,cargasButton,logoutButton;
    TextView responseTxtVw;
    ProgressBar carregProgress;
    LinearLayout carregLayoutFrag;
    String numCarga;
    boolean isPaused=false;
    AlertDialog alertDialog;
    int selector = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TAG = getString(R.string.login_carreg_frag);
        View view = inflater.inflate(R.layout.fragment_login_carreg, container, false);
        carregEditText = view.findViewById(R.id.carreg_etxt);
        carregButton = view.findViewById(R.id.carreg_btn);
        cargasButton = view.findViewById(R.id.carreg_cargas_btn);
        responseTxtVw = view.findViewById(R.id.carreg_response_txtv);
        carregProgress = view.findViewById(R.id.carreg_progress);
        carregLayoutFrag = view.findViewById(R.id.login_carreg_frag);
        logoutButton = view.findViewById(R.id.carreg_logout_btn);
        carregLayoutFrag.setOnTouchListener(this);
        carregButton.setOnClickListener(this);
        cargasButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.login_carreg_frag){
            Methods.CloseSoftKeyboradOnTouch(getActivity());
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Methods.checkConnection(getContext());
        if (v.getId() == R.id.carreg_btn) {
            if(Methods.isNetworkConnected) {
                responseTxtVw.setVisibility(View.VISIBLE);
                numCarga = String.valueOf(carregEditText.getText());
                if (!numCarga.matches("")) {
                    //configure the UI elementes
                    carregProgress.setVisibility(View.VISIBLE);
                    carregButton.setEnabled(false);
                    cargasButton.setEnabled(false);
                    logoutButton.setEnabled(false);
                    carregEditText.setEnabled(false);
                    sendCarga();

                } else {
                    responseTxtVw.setText(getString(R.string.carreg_missing_info));
                }
            }else {
                responseTxtVw.setText(getString(R.string.internet_connectivity_error));
            }
        }else if(v.getId()==R.id.carreg_cargas_btn){
            OlderCargasFrag frag = new OlderCargasFrag();
            frag.show(getActivity().getSupportFragmentManager(), "com.karam.transport_OlderFrag");
        }else if(v.getId() == R.id.carreg_logout_btn){
            showConfirmeLogout();
        }else if(v.getId() == R.id.toast_btn_confirm){
            alertDialog.dismiss();
            Methods.setSharedPref(getActivity(),"boolean",getString(R.string.SHisLogin),false);
            Methods.setSharedPref(getActivity(),"long",getString(R.string.SHcodmotorista),0l);
            Intent intent= new Intent(getContext(),MainActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }else if(v.getId() == R.id.toast_btn_dismiss){
            alertDialog.dismiss();
        }
    }


    @Override
    public void onTaskFinish(String response) {
        if(Methods.checkValidJson(response)){//check if the response is json or jarray or just a string
            responseTxtVw.setText("Carregando");
            HashMap<String,String> responseMap = Methods.toHashMap(response);
            if(String.valueOf(responseMap.get("cod")).matches("310")){
                showTokenDialog(getActivity(),getString(R.string.token_check),"Ok","Cancelar");
                carregButton.setEnabled(true);
                cargasButton.setEnabled(true);
                logoutButton.setEnabled(true);
                carregEditText.setEnabled(true);
                responseTxtVw.setText("");
                carregProgress.setVisibility(View.INVISIBLE);
            }else{
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        String res =(Methods.loadNewCarga(getActivity(),strings[0]));
                        return res;
                    }

                    @Override
                    protected void onPostExecute(String res) {
                        if(res=="ok"){
                            try{
                                Methods.setSharedPref(getContext(),"long",getString(R.string.SHcarga),Methods.longParser(numCarga));
                                Intent intent = new Intent(getActivity(),NotasActivity.class);
                                if(isPaused==false){
                                    startActivity(intent);
                                }
                                getActivity().finish();
                                //add work to check notas sending and location tracking
                                try{
                                    final Constraints constraints = new Constraints.Builder().setRequiredNetworkType(
                                            NetworkType.CONNECTED).build();
                                    final PeriodicWorkRequest workRequest = new  PeriodicWorkRequest.Builder(
                                            DataWorker.class,15, TimeUnit.MINUTES)
                                            .setConstraints(constraints)
                                            .build();
                                    WorkManager.getInstance(getContext()).enqueueUniquePeriodicWork(getString(R.string.tag_unique_work), ExistingPeriodicWorkPolicy.REPLACE,workRequest);
                                }catch (Exception ex){
                                }
                            }catch (Exception ex){
                            }
                        }else{
                            responseTxtVw.setText(res);
                        }
                        //configure the UI elementes
                        carregButton.setEnabled(true);
                        cargasButton.setEnabled(true);
                        logoutButton.setEnabled(true);
                        carregEditText.setEnabled(true);
                        carregProgress.setVisibility(View.INVISIBLE);
                    }
                }.execute(response);
            }
        }else{
            responseTxtVw.setText((response!=null) ? response : getString(R.string.login_invalid_json_error));
            //configure the UI elementes
            carregButton.setEnabled(true);
            carregEditText.setEnabled(true);
            carregProgress.setVisibility(View.INVISIBLE);
        }

    }




    private void showConfirmeLogout(){
        View view = Methods.setToastView(getActivity(),"",false,
                getString(R.string.confirme_logout_msg), true,"Sim",true,
                "Não",true);
        Button btnConfirm = view.findViewById(R.id.toast_btn_confirm);
        btnConfirm.setOnClickListener(this);
        Button btnCancel= view.findViewById(R.id.toast_btn_dismiss);
        btnCancel.setOnClickListener(this);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    public  void showTokenDialog(Activity activity, String msg, String msgConfirm, String msgDismiss){
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.token_dialog_layout,null);
        TextView message = view.findViewById(R.id.token_message);
        Button btnConfirm = view.findViewById(R.id.token_btn_confirm);
        Button btnDismiss = view.findViewById(R.id.token_btn_dismiss);
        final EditText tokenText = view.findViewById(R.id.token_editTxt);
        message.setText(msg);
        btnConfirm.setText(msgConfirm);
        btnDismiss.setText(msgDismiss);
        final AlertDialog tokenDialog = new AlertDialog.Builder(activity).setView(view).create();
        tokenDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tokenDialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertedToken = String.valueOf(tokenText.getText());
                if(!String.valueOf(tokenText.getText()).equals("")){
                    Methods.setSharedPref(getActivity(),"string",getString(R.string.SHtoken),insertedToken);
                    tokenDialog.dismiss();
                }else{
                    tokenText.setHint("Insere o codigo");
                }
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenDialog.dismiss();
            }
        });
    }

    private void sendCarga(){
        try {
            long codmotorista = (long) Methods.getSharedPref(getActivity(), "long", getString(R.string.SHcodmotorista));
            token = (String) Methods.getSharedPref(getActivity(), "string", getString(R.string.SHtoken));
            HashMap<String, String> map = Methods.stringToHashMap("NUMCAR%CODMOTORISTA%TOKEN", numCarga, String.valueOf(codmotorista),token);
            String encodedParams = Methods.encode(map);
            SRVConnection connection = new SRVConnection(null, this, "response");
            connection.execute(getString(R.string.url_server_host) + getString(R.string.url_server_load_carreg), encodedParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}