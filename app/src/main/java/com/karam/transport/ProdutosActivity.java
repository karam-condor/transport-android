package com.karam.transport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProdutosActivity extends AppCompatActivity implements TaskListenerAct , View.OnClickListener {
    public static final int IMG_REQ_CODE = 535;
    RecordFrag recordFrag;
    TextView codcli_prod_txtvw, cliente_prod_txtvw, nf_prod_txtvw, prod_count_txtvw;
    ListView prodListView;
    ArrayList<Prod> prodList,prodListPend;
    boolean isDoubleBackClick = false;
    SearchView searchView;
    ProdAdapter prodAdapter;
    long numnota = 0, codcli = 0, numcar = 0,numtransvenda,numped;
    String cliente = "", outputFile = "", email_cliente = "", email_cliente2="", obs="",imgName;
    int lisPosition,notaPosition;
    DBConnection dbConnection;
    NF nf;
    int motivoPos,imgOrder = 0;
    AlertDialog alertDialog;
    Bitmap img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);
        numcar = (long) Methods.getSharedPref(this, "long", getString(R.string.SHcarga));
        numnota = (long) Methods.getSharedPref(this, "long", getString(R.string.SHnota));
        codcli = (long) Methods.getSharedPref(this, "long", getString(R.string.SHcodcli));
        email_cliente = (String) Methods.getSharedPref(this, "string", getString(R.string.SHemail_cliente));
        cliente = (String) Methods.getSharedPref(this, "string", getString(R.string.SHcliente));
        notaPosition = (int) Methods.getSharedPref(this,"int",getString(R.string.SHnotaPosition));
        numtransvenda = (long)Methods.getSharedPref(this,"long",getString(R.string.SHnumtransvenda));
        numped = (long) Methods.getSharedPref(this,"long",getString(R.string.SHnumped));
        prodListView = findViewById(R.id.prod_listvw);
        codcli_prod_txtvw = findViewById(R.id.prod_codcli_txtvw);
        cliente_prod_txtvw = findViewById(R.id.prod_cliente_txtvw);
        nf_prod_txtvw = findViewById(R.id.prod_nf_txtvw);
        prod_count_txtvw = findViewById(R.id.prod_count_txtvw);
        //set the value of the items
        if (codcli != 0) {
            codcli_prod_txtvw.setText(String.valueOf(codcli));
        }
        cliente_prod_txtvw.setText(cliente);
        if (numnota != 0) {
            nf_prod_txtvw.setText(String.valueOf(numnota));
            LoadProdsBG loadProdsBG = new LoadProdsBG();
            loadProdsBG.execute(numnota);
        }
        //get saved imgOrder from shared prefs
        imgOrder = (int)Methods.getSharedPref(this,"int",getString(R.string.SHimgOrder));
        //Add on click listener to the item of prodlist
        prodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProdutosActivity.this, CheckProdActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("codprod", prodList.get(position).getCodprod());
                bundle.putString("descricao", prodList.get(position).getDescricao());
                if(prodList.get(position).getPendqt()!=null && prodList.get(position).getPendqt()>0){
                    bundle.putLong("qt",(prodList.get(position).getPendqt()!=null)? prodList.get(position).getPendqt():0);
                }else{
                    bundle.putLong("qt", prodList.get(position).getQt());
                }
                lisPosition = position;
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(barcodeReciever, new IntentFilter("GETBARCODEPROD"));
        LocalBroadcastManager.getInstance(this).registerReceiver(prodsReciever, new IntentFilter("GETPRODCHANGES"));
    }


    private BroadcastReceiver barcodeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String barcode = intent.getStringExtra("barcode");
            for (Prod prod : prodList) {
                if (barcode.matches(String.valueOf(prod.codbarra1))
                        || barcode.matches(String.valueOf(prod.codbarra2))) {
                    Intent intent2 = new Intent(ProdutosActivity.this, CheckProdActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("codprod", prod.getCodprod());
                    bundle.putString("descricao", prod.getDescricao());
                    bundle.putLong("qt", prod.getQt());
                    intent2.putExtras(bundle);
                    startActivity(intent2);
                    return;
                }
            }
            View view = Methods.setToastView(ProdutosActivity.this, "", false, getString(R.string.invalid_barcode_nf),
                    true, "", false, "", false);
            Toast toast = Toast.makeText(ProdutosActivity.this, "", Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        }
    };


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prods, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem search_item_menu = menu.findItem(R.id.prod_menu_search);
        searchView = (SearchView) search_item_menu.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                prodAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    private BroadcastReceiver prodsReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            prodList.get(lisPosition).setQtfalta(intent.getLongExtra("qtpend", 0));
            prodList.get(lisPosition).setStdev(intent.getIntExtra("status", 0));
            prodList.get(lisPosition).setCodmotivodev(intent.getIntExtra("codMotivoDev", 0));
            prodAdapter.notifyDataSetChanged();
            if(String.valueOf(searchView.getQuery()).trim()!=""){
                searchView.setQuery("",true);
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.prod_menu_barcode:
                Intent intent3 = new Intent(this, BarcodeActivity.class);
                intent3.putExtra("chooser", "prod");
                startActivity(intent3);
                break;
            case R.id.prod_menu_cancelar:
                DBConnection dbConnection = new DBConnection(this);
                Prod prod = new Prod();
                numnota = (long) Methods.getSharedPref(this, "long", getString(R.string.SHnota));
                prod.setQtfalta(null);
                prod.setCodmotivodev(null);
                prod.setStdev(null);
                SQLiteDatabase db = dbConnection.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("QTFALTA", 0l);
                contentValues.put("CODMOTIVO", 0);
                contentValues.put("STDEV", 0);
                db.update("PROD", contentValues, "NUMNOTA=?", new String[]{
                        String.valueOf(numnota)});
                Methods.setSharedPref(this,"long",getString(R.string.SHnota),0l);
                Methods.setSharedPref(this,"long",getString(R.string.SHcodcli),0l);
                Methods.setSharedPref(this,"string",getString(R.string.SHcliente),"");
                Methods.setSharedPref(this,"string",getString(R.string.SHemail_cliente),"");
                Methods.setSharedPref(this,"int",getString(R.string.SHnotaPosition),-1);
                Methods.setSharedPref(this,"int",getString(R.string.SHimgOrder),0);
                //Apagar as imagens no caso foi tirada
                Methods.deleteImages(this,numcar,numnota);
                finish();
                Intent intentNF = new Intent(this, NotasActivity.class);
                startActivity(intentNF);
                break;
            case R.id.prod_menu_finalizar:
                outputFile = Methods.createGetDir(this,String.valueOf(numnota),numcar);
                recordFrag = new RecordFrag();
                Bundle args = new Bundle();
                args.putString("outputFile", outputFile);
                args.putString("email_cliente", email_cliente);
                args.putInt("whitchActivity", 1);
                recordFrag.setArguments(args);
                recordFrag.show(getSupportFragmentManager(), "TRANSPORTCHECK" + numnota);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isDoubleBackClick) {
            finish();
        } else {
            View view = Methods.setToastView(ProdutosActivity.this, "", false, getString(R.string.closing_alert),
                    true, "", false, "", false);
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
        }, 2000);
    }

    @Override
    public void onTaskFinish(String response) {
        Log.i("karam1313",response);
        if(response.trim().equals("ok")){
            nf.setStenvi(1);
        }
        //insert the nota into the local database
        saveSqliteBG saveSqliteBG = new saveSqliteBG();
        saveSqliteBG.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toast_btn_confirm:
                alertDialog.dismiss();
                nf.setStcred(1);
                executeAll();
                break;
            case R.id.toast_btn_dismiss:
                alertDialog.dismiss();
                nf.setStcred(0);
                executeAll();
                break;
        }
    }


    class LoadProdsBG extends AsyncTask<Long, Void, ArrayList<Prod>> {
        @Override
        protected ArrayList<Prod> doInBackground(Long... longs) {
            DBConnection dbConnection = new DBConnection(ProdutosActivity.this);
            Long numnota = longs[0];
            prodList = new ArrayList<>();
            Cursor c = dbConnection.select(false, "PROD", new String[]{
                            "CODPROD", "QT", "QTFALTA", "STDEV", "CODBARRA1", "CODBARRA2", "DESCRICAO", "CODMOTIVO","PENDQT"},
                    "NUMNOTA=? AND NUMCAR =?", new String[]{String.valueOf(numnota),String.valueOf(numcar)}
                    , null, null, "DESCRICAO", null);
            if (c != null) {
                c.moveToFirst();
                while (c != null && c.isAfterLast() == false) {
                    Prod prod = new Prod();
                    try {
                        prod.setCodprod(c.getLong(c.getColumnIndex("CODPROD")));
                        prod.setQt(c.getLong(c.getColumnIndex("QT")));
                        prod.setCodbarra1(c.getLong(c.getColumnIndex("CODBARRA1")));
                        prod.setCodbarra2(c.getLong(c.getColumnIndex("CODBARRA2")));
                        prod.setDescricao(c.getString(c.getColumnIndex("DESCRICAO")));
                        prod.setQtfalta(c.getLong(c.getColumnIndex("QTFALTA")));
                        prod.setStdev(c.getInt(c.getColumnIndex("STDEV")));
                        prod.setCodmotivodev(c.getInt(c.getColumnIndex("CODMOTIVO")));
                        prod.setPendqt(c.getLong(c.getColumnIndex("PENDQT")));
                    } catch (NumberFormatException ex) {

                    }
                    prodList.add(prod);
                    c.moveToNext();
                }
            } else {
                return null;
            }
            c.close();
            return prodList;
        }

        @Override
        protected void onPostExecute(ArrayList<Prod> prods) {
            try {
                prodAdapter = new ProdAdapter(ProdutosActivity.this, prods);
                prodListView.setAdapter(prodAdapter);
                prod_count_txtvw.setText(prodAdapter.getCount() + " Produtos");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setEmail_cliente2(String email_cliente2) {
        this.email_cliente2 = email_cliente2;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }


    public void finalizar() {
        if (email_cliente2 != null && !email_cliente2.trim().matches("") && !Methods.isValidEmail(email_cliente2)) {
            Methods.showEmailInvalidMsg(this);
        } else {
            if (Methods.checkGPSTurndOn(this, this)) {
                nf = new NF();
                if(checkIfDev()){
                    showGenerateCredit();
                }else{
                    nf.setStcred(0);
                    executeAll();
                }
            }
        }
    }

    private boolean checkIfDev(){
        for(Prod prod : prodList){
            if(prod.getStdev()==2){
                return true;
            }
        }
        return false;
    }

    private void executeAll(){
        GoogleLocation.requestSingleUpdate(this, new GoogleLocation.LocationCallback() {
            @Override
            public void onNewLocationAvailable(GoogleLocation.GPSCoordinates location) {
                Methods.showLoadingDialog(ProdutosActivity.this);
                String jsonArr="";
                //start new connetion to the database
                dbConnection = new DBConnection(ProdutosActivity.this);
                //set all the informatins to new nota fiscal
                nf.setNumnota(numnota);
                nf.setObsentrega((obs != null) ? obs : "");
                nf.setEmail_cliene2((email_cliente2!= null)?email_cliente2:"");
                nf.setDtent(Methods.getCurrentDate());
                nf.setLatent(Methods.roundFloat(location.latitude, 6));
                nf.setLongtent(Methods.roundFloat(location.longitude, 6));
                nf.setStenvi(0);
                //Add arraylist of hashmap
                ArrayList<HashMap<String,String>> mapArr = new ArrayList<>();
                //check the type of the nota
                Cursor c = dbConnection.select(false, "PROD", new String[]{
                                "CODPROD", "QT", "QTFALTA", "STDEV", "CODBARRA1", "CODBARRA2", "DESCRICAO", "CODMOTIVO"},
                        "NUMCAR=? AND NUMNOTA=? AND STDEV>?", new String[]{String.valueOf(numcar),String.valueOf(numnota)
                                , "0"}
                        , null, null, null, null);
                int rowsCount = c.getCount();
                if (rowsCount == 0) {
                    nf.setStent(1);
                    jsonArr = "";
                } else {
                    nf.setStent(2);
                    prodListPend = new ArrayList<>();
                    c.moveToFirst();
                    while(c!=null && !c.isAfterLast()){
                        HashMap<String,String> map = new HashMap<>();
                        Prod prod = new Prod();
                        map.put(":CODPROD",String.valueOf(c.getLong(c.getColumnIndex("CODPROD"))));
                        prod.setCodprod(c.getLong(c.getColumnIndex("CODPROD")));
                        map.put(":QT",String.valueOf(c.getLong(c.getColumnIndex("QTFALTA"))));
                        prod.setQtfalta(c.getLong(c.getColumnIndex("QTFALTA")));
                        map.put(":CODMOTIVO",String.valueOf(c.getInt(c.getColumnIndex("CODMOTIVO"))));
                        prod.setCodmotivodev(c.getInt(c.getColumnIndex("CODMOTIVO")));
                        map.put(":STDEV",String.valueOf(c.getInt(c.getColumnIndex("STDEV"))));
                        prod.setStdev(c.getInt(c.getColumnIndex("STDEV")));
                        prodListPend.add(prod);
                        mapArr.add(map);
                        c.moveToNext();
                    }
                    c.close();
                    jsonArr = Methods.toJson(mapArr);
                }
                //Try to save online into the server
                Methods.checkConnection(ProdutosActivity.this);
                String[] motivoCod = getResources().getStringArray(R.array.motivos_dev_cod);
                String fileName = numcar + "-" + nf.getNumnota();
                if (Methods.isNetworkConnected) {
                    String audio = Methods.getBase64FromPath(outputFile);
                    String email = "";
                    if(nf.getEmail_cliene2()== null || nf.getEmail_cliene2().isEmpty()){
                        email = email_cliente;
                    }else{
                        email = nf.getEmail_cliene2();
                    }
                    //get json of images
                    String images = Methods.getImagesJsonBynota(getApplicationContext(),String.valueOf(numcar),String.valueOf(nf.getNumnota()));
                    //_________________________
                    HashMap<String, String> map = Methods.stringToHashMap("NUMNOTA%NUMCAR%LAT%LONGT%DTENTREGA%OBSENT%EMAIL_CLIENTE%CODMOTIVO%STCRED%STATUS%AUDIO%FILENAME%PRODJSON%NUMTRANSVENDA%NUMPED%IMAGES",
                            String.valueOf(nf.getNumnota()), String.valueOf(numcar), String.valueOf(nf.getLatent()), String.valueOf(nf.getLongtent()), nf.getDtent(), nf.getObsentrega(),
                            email,motivoCod[motivoPos] , String.valueOf(nf.getStcred()), String.valueOf(nf.getStent()), audio, fileName,jsonArr,String.valueOf(numtransvenda),String.valueOf(numped),images);
                    try {
                        String encodedParams = Methods.encode(map);
                        SRVConnection connection = new SRVConnection(ProdutosActivity.this, null, "response");
                        connection.execute(getString(R.string.url_server_host) + getString(R.string.url_server_save_conferencia), encodedParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    nf.setStenvi(0);
                    saveSqliteBG saveSqliteBG = new saveSqliteBG();
                    saveSqliteBG.execute();
                }
            }
        });
    }


    class saveSqliteBG extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean checkInsertNF = dbConnection.updatetNF(nf,"NUMNOTA=? AND NUMCAR=?",new String[]{
                    String.valueOf(numnota),String.valueOf(numcar)
            });
            if(prodListPend!=null && prodListPend.size()>0){
                for(Prod prod:prodListPend){
                    dbConnection.updateProd(prod,"NUMCAR =? AND NUMNOTA=? AND CODPROD=?",new String[]{
                            String.valueOf(nf.getNumcar()),String.valueOf(nf.getNumnota()) , String.valueOf(prod.getCodprod())
                    });
                }
            }
            return checkInsertNF;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean == true){
                //send atualization to the notas app
                sendToBCRecNotas(nf);
                //close the loading dialog
                Methods.closeLoadingDialog();
                //Close the dialog and the activity
                finish();
                Intent intentNotas = new Intent(ProdutosActivity.this,NotasActivity.class);
                startActivity(intentNotas);
                Methods.setSharedPref(ProdutosActivity.this,"long",getString(R.string.SHnota),0l);
                Methods.setSharedPref(ProdutosActivity.this,"long",getString(R.string.SHcodcli),0l);
                Methods.setSharedPref(ProdutosActivity.this,"string",getString(R.string.SHcliente),"");
                Methods.setSharedPref(ProdutosActivity.this,"string",getString(R.string.SHemail_cliente),"");
                Methods.setSharedPref(ProdutosActivity.this,"int",getString(R.string.SHnotaPosition),-1);
                Methods.setSharedPref(ProdutosActivity.this,"int",getString(R.string.SHimgOrder),0);
            }else{
                View view = Methods.setToastView(ProdutosActivity.this,"",false,getString(R.string.save_nfdevren_error),
                        true,"",false,"",false);
                Toast toast = Toast.makeText(ProdutosActivity.this, getString(R.string.save_nfdevren_error), Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.show();
            }
        }
    }


    private void sendToBCRecNotas(NF nf){
        Intent intent = new Intent("GETNFCHANGES");
        intent.putExtra("nf",nf.getEmail_cliene2());
        intent.putExtra("obsEntrega",nf.getObsentrega());
        intent.putExtra("stEnvi",nf.getStenvi());
        intent.putExtra("stEnt",nf.getStent());
        intent.putExtra("stCred",nf.getStcred());
        intent.putExtra("position",notaPosition);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }

    private void showGenerateCredit(){
        View view = Methods.setToastView(this,"",false,
                getString(R.string.credito_check), true,"Sim",true,
                "Não",true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMG_REQ_CODE && resultCode == RESULT_OK && data != null){
            imgOrder++;
            Methods.setSharedPref(this,"int",getString(R.string.SHimgOrder),imgOrder);
            img =(Bitmap) data.getExtras().get("data");
            imgName = Methods.createGetDirImg(this,String.valueOf(numnota),numcar,imgOrder);
            if(imgName != null){
                Methods.saveImageToInternalMemoryAsJpg(this,imgName,img);
            }
            recordFrag.setTokenImagesNumber();
        }else{
            Methods.showCostumeToast(this,getString(R.string.check_img_validation));
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public int getImageOrder(){
        return this.imgOrder;
    }
}