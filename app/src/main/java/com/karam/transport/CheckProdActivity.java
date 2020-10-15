package com.karam.transport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.MalformedInputException;

public class CheckProdActivity extends AppCompatActivity implements View.OnClickListener, RadioButton.OnCheckedChangeListener, View.OnTouchListener {
    long codprod,numcar,qt;
    String descricao;
    boolean motivo;
    Spinner motivoSpinner;
    Button registerBtn,resetBtn;
    RadioButton radDev,radPen;
    EditText qtpend_edittxt;
    TextView codProd_txtvw,des_txtvw,qt_txtvw;
    String[] motivoDes;
    int pos=0;
    DBConnection dbConnection;
    LinearLayout layoutContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_prod);
        //get numcar
        numcar =(long) Methods.getSharedPref(this,"long",getString(R.string.SHcarga));
        //set spinner
        setUpSpinner();
        //get layout element
        codProd_txtvw = findViewById(R.id.checkProd_cod_txtvw);
        des_txtvw = findViewById(R.id.checkProd_descri_txtvw);
        qt_txtvw = findViewById(R.id.checkProd_qt_txtvw);
        registerBtn = findViewById(R.id.checkProd_registrar_btn);
        resetBtn = findViewById(R.id.checkProd_reset_btn);
        qtpend_edittxt = findViewById(R.id.checkProd_qtpend_edittxt);
        radDev = findViewById(R.id.checkprod_dev_rad);
        radPen = findViewById(R.id.checkprod_pend_rad);
        layoutContainer = findViewById(R.id.checkprod_layout);
        //get variables the products activity
        Intent myIntent =this.getIntent();
        codprod = myIntent.getLongExtra("codprod",0);
        descricao = myIntent.getStringExtra("descricao");
        qt = myIntent.getLongExtra("qt",0);
        codProd_txtvw.setText(String.valueOf(codprod));
        des_txtvw.setText(descricao);
        qt_txtvw.setText(String.valueOf(qt));
        //set Listner
        registerBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        radPen.setOnCheckedChangeListener(this);
        radDev.setOnCheckedChangeListener(this);
        layoutContainer.setOnTouchListener(this);
    }

    private void setUpSpinner(){
        motivoSpinner = findViewById(R.id.checkProd_motivo_spinner);
        motivoDes = getResources().getStringArray(R.array.motivos_dev_des);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, motivoDes) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = null;
                v = super.getDropDownView(position, null, parent);
                v.setBackgroundResource(R.drawable.spinner_background);
                v.setElevation(5);
                return v;
            }
        };
        motivoSpinner.setAdapter(dataAdapter);
        motivoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkProd_registrar_btn:
                if((radDev.isChecked() || radPen.isChecked()) && !String.valueOf(qtpend_edittxt.getText()).matches("")
                        && Methods.longParser(String.valueOf(qtpend_edittxt.getText()))>0 && Methods.longParser(String.valueOf(qtpend_edittxt.getText())) <= qt){
                    dbConnection = new DBConnection(this);
                    Prod prod = new Prod();
                    long numnota =(long) Methods.getSharedPref(this,"long",getString(R.string.SHnota));
                    prod.setQtfalta(Methods.longParser(String.valueOf(qtpend_edittxt.getText())));
                    if(motivo){
                        prod.setStdev(1);
                    }else{
                        prod.setStdev(2);
                    }
                    String[] codArr = getResources().getStringArray(R.array.motivos_dev_cod);
                    int codMotDev;
                    try{
                        codMotDev = Integer.parseInt(codArr[pos]);
                    }catch (NumberFormatException ex){
                        codMotDev = 0;
                    }
                    prod.setCodmotivodev(codMotDev);
                    boolean checkInsertProd = dbConnection.updateProd(prod,"NUMCAR=? AND NUMNOTA=? AND CODPROD=?",new String[]{
                            String.valueOf(numcar),String.valueOf(numnota),String.valueOf(codprod)
                    });
                    if(checkInsertProd){
                        Intent intent = new Intent("GETPRODCHANGES");
                        intent.putExtra("qtpend",prod.getQtfalta());
                        intent.putExtra("codMotivoDev",prod.getCodmotivodev());
                        intent.putExtra("status",prod.getStdev());
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                        finish();
                        Intent intentProdutos = new Intent(this,ProdutosActivity.class);
                        startActivity(intentProdutos);
                    }else{
                        View view = Methods.setToastView(CheckProdActivity.this, "", false,"Aconteceu um erro em salvar",
                                true, "", false, "", false);
                        Toast toast = Toast.makeText(CheckProdActivity.this, "", Toast.LENGTH_SHORT);
                        toast.setView(view);
                        toast.show();
                    }
                }else{
                    View view = Methods.setToastView(CheckProdActivity.this, "", false, getString(R.string.check_prod_missing_info),
                            true, "", false, "", false);
                    Toast toast = Toast.makeText(CheckProdActivity.this, "", Toast.LENGTH_SHORT);
                    toast.setView(view);
                    toast.show();
                }
                break;
            case R.id.checkProd_reset_btn:
                dbConnection = new DBConnection(this);
                Prod prod = new Prod();
                long numnota =(long) Methods.getSharedPref(this,"long",getString(R.string.SHnota));
                prod.setQtfalta(null);
                prod.setCodmotivodev(null);
                prod.setStdev(null);
                SQLiteDatabase db = dbConnection.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("QTFALTA",0l);
                contentValues.put("CODMOTIVO",0);
                contentValues.put("STDEV",0);
                db.update("PROD",contentValues,"NUMNOTA=? and CODPROD=?",new String[]{
                        String.valueOf(numnota),String.valueOf(codprod)});
                Intent intent = new Intent("GETPRODCHANGES");
                intent.putExtra("qtpend",prod.getQtfalta());
                intent.putExtra("codMotivoDev",prod.getCodmotivodev());
                intent.putExtra("status",prod.getStdev());
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                finish();
                Intent intentProdutos = new Intent(this,ProdutosActivity.class);
                startActivity(intentProdutos);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.checkprod_pend_rad){
            motivo = true;
        }else if(buttonView.getId() == R.id.checkprod_dev_rad){
            motivo = false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId()==R.id.checkprod_layout){
            Methods.CloseSoftKeyboradOnTouch(this);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,ProdutosActivity.class);
        startActivity(intent);
    }
}
