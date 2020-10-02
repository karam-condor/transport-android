package com.karam.transport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

public class OlderNotasActivity extends AppCompatActivity {
    ExpandableListView notasExpandLV;
    ExpandableListAdapter nfPRODAdapter;
    ArrayList<NF> nfList;
    HashMap<String,ArrayList<Prod>> prodsMap;
    String numcar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_older_notas);


        notasExpandLV = findViewById(R.id.older_notas_lv);
        numcar = this.getIntent().getStringExtra("numcar");
        getSupportActionBar().setTitle(getString(R.string.carregamento_title)+" "+ numcar);
        LoadNotasBGOlder loadNotasBGOlder = new LoadNotasBGOlder();
        loadNotasBGOlder.execute();
    }


    class LoadNotasBGOlder extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            DBConnection dbConnection = new DBConnection(OlderNotasActivity.this);
            nfList = new ArrayList<>();
            prodsMap = new HashMap<>();
            Cursor c = dbConnection.select(false, "NF", new String[]{"NUMNOTA", "CODCLI", "CLIENTE", "EMAIL_CLIENTE", "STENT", "STPEND", "UF", "CIDADE", "ENDERECO", "CEP",
                            "BAIRRO", "OBS1", "OBS2", "OBS3", "DTENT", "PENDCODPROCESS", "PENDDTENT", "PENDOBS", "PENDLAT", "PENDLONGT"},
                    "NUMCAR=? AND STENT > 1", new String[]{numcar}
                    , null, null, "CLIENTE", null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                while (c != null && c.isAfterLast() == false) {
                    NF nf = new NF();
                    try {
                        nf.setNumnota(c.getLong(c.getColumnIndex("NUMNOTA")));
                        nf.setCodcli(c.getLong(c.getColumnIndex("CODCLI")));
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
                    //Select the products that has pendency
                    long numnota = 0;
                    if (nf.getNumnota() != null) {
                        numnota = nf.getNumnota();
                    }
                    Cursor c2 = dbConnection.select(false, "PROD", new String[]{
                                    "CODPROD", "QT", "QTFALTA", "STDEV", "CODBARRA1", "CODBARRA2", "DESCRICAO", "CODMOTIVO", "PENDQT"},
                            "NUMNOTA=? AND NUMCAR =? AND STDEV > 0", new String[]{String.valueOf(numnota), String.valueOf(numcar)}
                            , null, null, "DESCRICAO", null);
                    if (c2 != null) {
                        ArrayList<Prod> prodsArr = new ArrayList<>();
                        c2.moveToFirst();
                        while (c2 != null && c2.isAfterLast() == false) {
                            Prod prod = new Prod();
                            try {
                                prod.setCodprod(c2.getLong(c2.getColumnIndex("CODPROD")));
                                prod.setQt(c2.getLong(c2.getColumnIndex("QT")));
                                prod.setCodbarra1(c2.getLong(c2.getColumnIndex("CODBARRA1")));
                                prod.setCodbarra2(c2.getLong(c2.getColumnIndex("CODBARRA2")));
                                prod.setDescricao(c2.getString(c2.getColumnIndex("DESCRICAO")));
                                prod.setQtfalta(c2.getLong(c2.getColumnIndex("QTFALTA")));
                                prod.setStdev(c2.getInt(c2.getColumnIndex("STDEV")));
                                prod.setCodmotivodev(c2.getInt(c2.getColumnIndex("CODMOTIVO")));
                                prod.setPendqt(c2.getLong(c2.getColumnIndex("PENDQT")));
                            } catch (NumberFormatException ex) {

                            }
                            prodsArr.add(prod);
                            c2.moveToNext();
                        }
                        prodsMap.put(String.valueOf(numnota), prodsArr);
                        c2.close();
                        c.moveToNext();
                    }
                }
                c.close();
                return "ok";
            } else {
                return "no notas";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.trim().equals("ok")){
                nfPRODAdapter = new NFProdAdapter(OlderNotasActivity.this,nfList,prodsMap);
                notasExpandLV.setAdapter(nfPRODAdapter);
            }else{

            }
        }
    }
}
