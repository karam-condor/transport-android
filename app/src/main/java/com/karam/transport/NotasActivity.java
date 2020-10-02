package com.karam.transport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.WorkManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.HashMap;

public class NotasActivity extends AppCompatActivity implements View.OnClickListener,TaskListenerAct {
    private String TAG;
    ArrayList<NF> nfList,nfListFiltered ;
    TabLayout notasHeaderLayout;
    ListView notasListView;
    Button btnFinalizar;
    TextView notasCountTxtvw;
    SearchView searchView;
    NFAdapter notasAdapter;
    int selecter=0;
    long numcar;
    Boolean  isDoubleBackClick = false;
    private static NotasActivity nActivity;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        Log.i(TAG, "onCreate: ");
        //assign a static instance of this actitvity
        nActivity = this;
        TAG = getString(R.string.notas_activity);
        //reset the number of images has token
        Methods.setSharedPref(this,"int",getString(R.string.SHimgOrder),0);
        Methods.setSharedPref(this,"int",getString(R.string.SHimgOrderDevRenPer),0);
        //_________________________________________________________
        notasHeaderLayout = findViewById(R.id.notas_tab_layout);
        notasListView = findViewById(R.id.notas_listvw);
        notasCountTxtvw = findViewById(R.id.notas_count_txtvw);
        //add the tabs
        addtabs(this,notasHeaderLayout);
        //get the numcar to filter the results in the database
        numcar =(long) Methods.getSharedPref(this,"long",getString(R.string.SHcarga));
        //load notas from the database
        if(numcar!=0){
            LoadNotasBG loadNotasBG = new LoadNotasBG();
            loadNotasBG.execute(numcar);
        }
        //Apagar as fotos de lixo
        Methods.deleteTrashImages(this,numcar);
        //add onChangetabs
        notasHeaderLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        notasAdapter = new NFAdapter(NotasActivity.this,NotasActivity.this,nfList);
                        notasListView.setAdapter(notasAdapter);
                        notasCountTxtvw.setText(notasAdapter.getCount()+" Notas");
                        break;
                    case 1:
                        nfListFiltered = new ArrayList<NF>();
                        for(NF nf:nfList){
                            if(nf.getStent()==0){
                                nfListFiltered.add(nf);
                            }
                        }
                        notasAdapter = new NFAdapter(NotasActivity.this,NotasActivity.this,nfListFiltered);
                        notasListView.setAdapter(notasAdapter);
                        notasCountTxtvw.setText(notasAdapter.getCount()+" Notas");
                        break;
                    case 2:
                        nfListFiltered = new ArrayList<NF>();
                        for(NF nf:nfList){
                            if(nf.getStent()>0){
                                nfListFiltered.add(nf);
                            }
                        }
                        notasAdapter = new NFAdapter(NotasActivity.this,NotasActivity.this,nfListFiltered);
                        notasListView.setAdapter(notasAdapter);
                        notasCountTxtvw.setText(notasAdapter.getCount()+" Notas");
                        break;
                    case 3:
                        nfListFiltered = new ArrayList<NF>();
                        for(NF nf:nfList){
                            if(nf.getStpend()==1){
                                nfListFiltered.add(nf);
                            }
                        }
                        notasAdapter = new NFAdapter(NotasActivity.this,NotasActivity.this,nfListFiltered);
                        notasListView.setAdapter(notasAdapter);
                        notasCountTxtvw.setText(notasAdapter.getCount()+" Notas");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(barcodeReceiver,
                new IntentFilter("GETBARCODENF"));//the name of the intenet filter to know exactly what event will recive and excute
        LocalBroadcastManager.getInstance(this).registerReceiver(notasReciever,
                new IntentFilter("GETNFCHANGES"));
    }


    private BroadcastReceiver notasReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pos = intent.getIntExtra("position",-1);
            if(pos > -1){
                if(notasHeaderLayout.getSelectedTabPosition()==0) {
                    setListNotasItemRefresh(pos,intent);
                }else if(notasHeaderLayout.getSelectedTabPosition()==1){
                    nfListFiltered.get(pos).setEmail_cliene2(intent.getStringExtra("nf"));
                    nfListFiltered.get(pos).setObsentrega(intent.getStringExtra("obsEntrega"));
                    nfListFiltered.get(pos).setStenvi(intent.getIntExtra("stEnvi",0));
                    nfListFiltered.get(pos).setStent(intent.getIntExtra("stEnt",0));
                    nfListFiltered.get(pos).setStcred(intent.getIntExtra("stCred",0));
                    nfListFiltered.remove(pos);
                    setListNotasItemRefresh(pos,intent);
                }else if(notasHeaderLayout.getSelectedTabPosition()==3){
                    nfListFiltered.get(pos).setEmail_cliene2(intent.getStringExtra("nf"));
                    nfListFiltered.get(pos).setObsentrega(intent.getStringExtra("obsEntrega"));
                    nfListFiltered.get(pos).setStenvi(intent.getIntExtra("stEnvi",0));
                    nfListFiltered.get(pos).setStent(intent.getIntExtra("stEnt",0));
                    nfListFiltered.get(pos).setStcred(intent.getIntExtra("stCred",0));
                    setListNotasItemRefresh(pos,intent);
                }
                searchView.setQuery("",true);
                notasAdapter.notifyDataSetChanged();

            }
        }
    };


    private BroadcastReceiver barcodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String barcode = intent.getStringExtra("barcode");
            for(NF nf:nfList){
                if(barcode.matches(String.valueOf(nf.getNumnota()))){
                    if(nf.getStent() == 0){
                        Bundle args = new Bundle();
                        args.putString("codcli", String.valueOf(nf.getCodcli()));
                        args.putString("cliente", String.valueOf(nf.getCliente()));
                        args.putString("nf", String.valueOf(nf.getNumnota()));
                        args.putString("status", Methods.getStatus(nf, NotasActivity.this));
                        args.putString("email_cliente", nf.getEmail_cliente());
                        // call the check in activity
                        Intent myIntent = new Intent(NotasActivity.this,CheckActivity.class);
                        myIntent.putExtras(args);
                        startActivity(myIntent);
                        return;
                    }else {
                        View view = Methods.setToastView(NotasActivity.this, "", false, getString(R.string.invalid_barcode_nf2),
                                true, "", false, "", false);
                        return;
                    }
                }
            }
            View view = Methods.setToastView(NotasActivity.this, "", false, getString(R.string.invalid_barcode_nf),
                    true, "", false, "", false);
            Toast toast = Toast.makeText(NotasActivity.this, "", Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        }
    };


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(barcodeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notasReciever);
        super.onDestroy();
    }


    public static void addtabs(Activity activity, TabLayout notasTabLayout) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View item1_view = inflater.inflate(R.layout.notas_tab_layout,null);
        ImageView item1_image_view = item1_view.findViewById(R.id.tab_imgvw);
        TextView item1_text_view = item1_view.findViewById(R.id.tab_txtvw);
        View item2_view = inflater.inflate(R.layout.notas_tab_layout,null);
        ImageView item2_image_view = item2_view.findViewById(R.id.tab_imgvw);
        TextView item2_text_view = item2_view.findViewById(R.id.tab_txtvw);
        View item3_view = inflater.inflate(R.layout.notas_tab_layout,null);
        ImageView item3_image_view = item3_view.findViewById(R.id.tab_imgvw);
        TextView item3_text_view = item3_view.findViewById(R.id.tab_txtvw);
        View item4_view = inflater.inflate(R.layout.notas_tab_layout,null);
        ImageView item4_image_view = item4_view.findViewById(R.id.tab_imgvw);
        TextView item4_text_view = item4_view.findViewById(R.id.tab_txtvw);
        item1_image_view.setImageResource(R.drawable.item1);
        item1_text_view.setText("Todos");
        item2_image_view.setImageResource(R.drawable.item2);
        item2_text_view.setText("Em trânsito");
        item3_image_view.setImageResource(R.drawable.item3);
        item3_text_view.setText("Finalizados");
        item4_image_view.setImageResource(R.drawable.item4);
        item4_text_view.setText("Pendencia");
        notasTabLayout.addTab(notasTabLayout.newTab().setCustomView(item1_view));
        notasTabLayout.addTab(notasTabLayout.newTab().setCustomView(item2_view));
        notasTabLayout.addTab(notasTabLayout.newTab().setCustomView(item3_view));
        notasTabLayout.addTab(notasTabLayout.newTab().setCustomView(item4_view));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toast_btn_confirm:
                alertDialog.dismiss();
                Methods.showLoadingDialog(NotasActivity.this);
                SaveNotas saveNotas = new SaveNotas(NotasActivity.this,NotasActivity.this,String.valueOf(numcar));
                saveNotas.enviar();
                break;
            case R.id.toast_btn_dismiss:
                alertDialog.dismiss();
                break;
        }
    }

    @Override
    public void onTaskFinish(String response) {
        if(response.trim().equals("ok")){
            AfterFinish afterFinish = new AfterFinish();
            afterFinish.execute();
        }else if(response.trim().equals("501")){
            Methods.setSharedPref(NotasActivity.this,"string",getString(R.string.SHtoken),"");
            AfterFinish afterFinish = new AfterFinish();
            afterFinish.execute();
        }
        else{
            Methods.showCostumeToast(this,response);
        }
    }


    class AfterFinish extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DBConnection dbConnection = new DBConnection(NotasActivity.this);
            NF nf = new NF();
            nf.setStenvi(1);
            dbConnection.updatetNF(nf,"NUMCAR=?",new String[]{String.valueOf(numcar).trim()});
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Methods.setSharedPref(NotasActivity.this,"long",getString(R.string.SHcarga),0l);
            dropDownDataWorker();
            try{
                Methods.closeLoadingDialog();
            }catch (NullPointerException ex){
            }
            Intent intent = new Intent(NotasActivity.this,LoginActivity.class);
            finish();
            startActivity(intent);
        }
    }


    class LoadNotasBG extends AsyncTask<Long, Void, ArrayList<NF>> {
        @Override
        protected ArrayList<NF> doInBackground(Long... longs) {
            DBConnection dbConnection = new DBConnection(NotasActivity.this);
            Long numcarga = longs[0];
            nfList = new ArrayList<>();
            Cursor c = dbConnection.select(false, "NF", new String[]{"NUMNOTA", "CODCLI", "CLIENTE", "EMAIL_CLIENTE", "STENT", "STPEND", "UF", "CIDADE", "ENDERECO", "CEP",
                            "BAIRRO", "OBS1", "OBS2", "OBS3","DTENT","PENDCODPROCESS","PENDDTENT","PENDOBS","PENDLAT","PENDLONGT","NUMTRANSVENDA","NUMPED"},
                    "NUMCAR=? OR (NUMCAR=? AND STPEND = 1)", new String[]{String.valueOf(numcarga)}
                    , null, null, "CLIENTE", null);
            if (c != null) {
                c.moveToFirst();
                while (c != null && c.isAfterLast() == false) {
                    NF nf = new NF();
                    try {
                        nf.setNumnota(c.getLong(c.getColumnIndex("NUMNOTA")));
                        nf.setCodcli(c.getLong(c.getColumnIndex("CODCLI")));
                        nf.setNumtransvenda(c.getLong(c.getColumnIndex("NUMTRANSVENDA")));
                        nf.setNumped(c.getLong(c.getColumnIndex("NUMPED")));
                        nf.setCliente(c.getString(c.getColumnIndex("CLIENTE")));
                        nf.setStent(c.getInt(c.getColumnIndex("STENT")));
                        nf.setStpend(c.getInt(c.getColumnIndex("STPEND")));
                        nf.setPendcodprocess(c.getLong(c.getColumnIndex("PENDCODPROCESS")));
                        nf.setPendlat(c.getFloat(c.getColumnIndex("PENDLAT")));
                        nf.setPendlongt(c.getFloat(c.getColumnIndex("PENDLONGT")));
                    } catch (NumberFormatException ex) {
                    }
                    nf.setUf(c.getString(c.getColumnIndex("UF")));
                    nf.setCiddade(c.getString(c.getColumnIndex("CIDADE")));
                    nf.setBairro(c.getString(c.getColumnIndex("BAIRRO")));
                    nf.setObs1(c.getString(c.getColumnIndex("OBS1")));
                    nf.setObs2(c.getString(c.getColumnIndex("OBS2")));
                    nf.setObs3(c.getString(c.getColumnIndex("OBS3")));
                    nf.setCep(c.getString(c.getColumnIndex("CEP")));
                    nf.setEndereco(c.getString(c.getColumnIndex("ENDERECO")));
                    nf.setEmail_cliente(c.getString(c.getColumnIndex("EMAIL_CLIENTE")));
                    nf.setDtent(c.getString(c.getColumnIndex("DTENT")));
                    nf.setPendobs(c.getString(c.getColumnIndex("PENDOBS")));
                    nf.setPenddtent(c.getString(c.getColumnIndex("PENDDTENT")));
                    nfList.add(nf);
                    c.moveToNext();
                }
            } else {
                return null;
            }
            c.close();
            return nfList;
        }
        @Override
        protected void onPostExecute(ArrayList<NF> nfs) {
            try{
                notasAdapter = new NFAdapter(NotasActivity.this,NotasActivity.this, nfs);
                notasListView.setAdapter(notasAdapter);
                notasCountTxtvw.setText(notasAdapter.getCount() + " Notas");
                Methods.setSharedPref(NotasActivity.this,"int",getString(R.string.SHqtdNotasTotal),nfs.size());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notas,menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        MenuItem search_item_menu = menu.findItem(R.id.notas_menu_search);
        searchView =(SearchView)search_item_menu.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notasAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if(isDoubleBackClick){
            finish();
        }else{
            View view = Methods.setToastView(NotasActivity.this,"",false,getString(R.string.closing_alert),
                    true,"",false,"",false);
            Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        }
        isDoubleBackClick = true;

        final Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isDoubleBackClick = false;
                myHandler.removeCallbacksAndMessages(null);
            }
        },2000);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.notas_menu_barcode){
            Intent intent = new Intent(this,BarcodeActivity.class);
            intent.putExtra("chooser","nf");
            startActivity(intent);
        }else if(item.getItemId()==R.id.notas_menu_finalizar){
            Methods.checkConnection(this);
            if(Methods.isNetworkConnected){
                new AsyncTask<Void,Void,String>(){
                    @Override
                    protected String doInBackground(Void... voids) {
                        Looper.prepare();
                        DBConnection dbConnection = new DBConnection(NotasActivity.this);
                        Cursor c =dbConnection.select(false, "NF", new String[]{"NUMNOTA"},
                                "STENT = 0 AND NUMCAR = ?", new String[]{String.valueOf(numcar)},null,null,null,null);//condition all nota checked if equal to 0
                        if(c.getCount()==0){
                            c.close();
                            return "ok";
                        }else {
                            c.close();
                            return "not ok";
                        }
                    }
                    @Override
                    protected void onPostExecute(String s) {
                        if(s.trim().equals("ok")){
                            Methods.showLoadingDialog(NotasActivity.this);
                            SaveNotas saveNotas = new SaveNotas(NotasActivity.this,NotasActivity.this,String.valueOf(numcar));
                            saveNotas.enviar();
                        }else{
                            showDialog();
                        }
                    }
                }.execute();
            }else{
                Methods.showCostumeToast(this,getString(R.string.finalizar_no_connection));
            }
        }
        return true;
    }

    public static NotasActivity getInstance(){
        return nActivity;
    }

    public void showDialog(){
        View view = Methods.setToastView(this,"",false,
                this.getString(R.string.finalizar_warning), true,"Confirmar",true,
                "Cancelar",true);
        Button btnConfirm = view.findViewById(R.id.toast_btn_confirm);
        btnConfirm.setOnClickListener(this);
        Button btnCancel= view.findViewById(R.id.toast_btn_dismiss);
        btnCancel.setOnClickListener(this);
        alertDialog = new AlertDialog.Builder(this)
                .setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
    private void setListNotasItemRefresh(int pos,Intent intent){
        nfList.get(pos).setEmail_cliene2(intent.getStringExtra("nf"));
        nfList.get(pos).setObsentrega(intent.getStringExtra("obsEntrega"));
        nfList.get(pos).setStenvi(intent.getIntExtra("stEnvi",0));
        nfList.get(pos).setStent(intent.getIntExtra("stEnt",0));
        nfList.get(pos).setStcred(intent.getIntExtra("stCred",0));
    }

    private void dropDownDataWorker(){
        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("com.karam.transport");
    }

    private void saveKM(){
        selecter = 1;
        HashMap <String,String> map = Methods.stringToHashMap("KM%NUMCAR","EXSISTS",String.valueOf(numcar));
        String encodedParams = null;
        try {
            encodedParams = Methods.encode(map);
            SRVConnection connection = new SRVConnection(NotasActivity.this, null, "response");
            connection.execute(NotasActivity.this.getString(R.string.url_server_host) + NotasActivity.this.getString(R.string.url_server_save_auto), encodedParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}