package com.karam.transport;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.HashMap;



public class LoginMainFrag extends Fragment implements TaskListener , View.OnClickListener , View.OnTouchListener {
    String TAG;
    String cod;
    EditText codEditText,passEditText;
    TextView responseTxtVw;
    ProgressBar loginProgBar;
    Button loginButton;
    boolean isPause = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getString(R.string.login_main_frag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login_main, container, false);
        codEditText = view.findViewById(R.id.login_edtext_username);
        passEditText = view.findViewById(R.id.login_edtext_password);
        responseTxtVw = view.findViewById(R.id.login_response_txtv);
        loginButton = view.findViewById(R.id.login_button);
        loginProgBar = view.findViewById(R.id.login_progress);
        loginButton.setOnClickListener(this);
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    public void onClick(View v) {
        Methods.checkConnection(getContext());
        if (v.getId() == R.id.login_button) {
            if (Methods.isNetworkConnected) {
                cod = String.valueOf(codEditText.getText());
                String password = String.valueOf(passEditText.getText());
                if (!cod.matches("") && !password.matches("")) {
                    //configure the UI elementes
                    loginProgBar.setVisibility(View.VISIBLE);
                    loginButton.setEnabled(false);
                    codEditText.setEnabled(false);
                    passEditText.setEnabled(false);
                    try {
                        HashMap<String, String> map = Methods.stringToHashMap("USUARIO%SENHA", cod, password);
                        String encodedParams = Methods.encode(map);
                        SRVConnection connection = new SRVConnection(null, this, "response");
                        connection.execute(getString(R.string.url_server_host) + getString(R.string.url_server_login), encodedParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    responseTxtVw.setText(getString(R.string.login_missing_info));
                }
            } else {
                responseTxtVw.setText(getString(R.string.internet_connectivity_error));
            }
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.login_main_frag){
            Methods.CloseSoftKeyboradOnTouch(getActivity());
        }
        return false;
    }

    @Override
    public void onTaskFinish(String response) {
        //Confugure the UI elements
        loginProgBar.setVisibility(View.INVISIBLE);//hide the progress bar
        loginButton.setEnabled(true);
        codEditText.setEnabled(true);
        passEditText.setEnabled(true);
        if(isPause == false){
            if(Methods.checkValidJson(response)){//check if the response is json or jarray or just a string
                HashMap<String,String> responseMap = Methods.toHashMap(response);//Convert the json to list of hashmap
                if(String.valueOf(responseMap.get("cod")).matches("200")){
                    Methods.setSharedPref(getContext(),"boolean",getString(R.string.SHisLogin),true);
                    Methods.setSharedPref(getContext(),"long",getString(R.string.SHcodmotorista),Long.parseLong(cod));//should reset it when finish the process
                    Fragment fragment = new LoginCarregFrag();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.login_container_layout,fragment).addToBackStack(null).commit();
                }else{
                    responseTxtVw.setText(responseMap.get("status") + " : "+ responseMap.get("cod")+"\n"+responseMap.get("message"));
                }
            }else{
                responseTxtVw.setText((response!=null) ? response : getString(R.string.login_invalid_json_error));
            }
        }
    }
}