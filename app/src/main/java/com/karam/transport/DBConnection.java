package com.karam.transport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;


//how the class work:
//call new instance of the class
//then call any method
//then call the close method to close the connection
//when call the select method will return cursor (should not forget to close it after using it)
public class DBConnection extends SQLiteOpenHelper {
    private Context context;
    private static final String db_name =  "DB";
    private static final int db_version = 1;


    public DBConnection(@Nullable Context context) {
        super(context, db_name, null, db_version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String TAG = context.getString(R.string.db_connection_tag);
        db.execSQL(context.getString(R.string.db_sql_createTableCarga));
        db.execSQL(context.getString(R.string.db_sql_createTableNF));
        db.execSQL(context.getString(R.string.db_sql_createTableProd));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(context.getString(R.string.db_sql_dropTableProd));
        db.execSQL(context.getString(R.string.db_sql_dropTableNF));
        db.execSQL(context.getString(R.string.db_sql_dropTableCarga));
        onCreate(db);
    }

    public void close(){
        SQLiteDatabase dbr = this.getReadableDatabase();
        SQLiteDatabase dbw = this.getWritableDatabase();
        if(!dbr.isDbLockedByCurrentThread() && !dbw.isDbLockedByCurrentThread()){
            this.close();

        }
    }

    public boolean insertCarga(Carga carga,String column,int conflictAlgorithm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(carga.getNumcar()!=null){
            contentValues.put("NUMCAR",carga.getNumcar());
        }
        if(carga.getDtsaida()!=null){
            contentValues.put("DTSAIDA",carga.getDtsaida());
        }
        if(carga.getDtfinal()!=null){
            contentValues.put("DTFINAL",carga.getDtfinal());
        }
        long affectedRows = db.insertWithOnConflict("CARGA",column,contentValues,conflictAlgorithm);
        if(affectedRows>-1){
            return true;
        }else{
            return false;
        }
    }

    public boolean updateCarga(Carga carga,String whereClause,String[] whereValues){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(carga.getNumcar()!=null){
            contentValues.put("NUMCAR",carga.getNumcar());
        }
        if(carga.getDtsaida()!=null){
            contentValues.put("DTSAIDA",carga.getDtsaida());
        }
        if(carga.getDtfinal()!=null){
            contentValues.put("DTFINAL",carga.getDtfinal());
        }
        long affectedRows = db.update("CARGA",contentValues,whereClause,whereValues);
        if(affectedRows>-1){
            return true;
        }else{
            return false;
        }
    }


    public boolean insertNF(NF nf,String column,int conflictAlgorithm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(nf.getNumnota()!=null){
            contentValues.put("NUMNOTA",nf.getNumnota());
        }
        if(nf.getNumcar()!=null){
            contentValues.put("NUMCAR",nf.getNumcar());
        }

        if(nf.getCodcli()!=null){
            contentValues.put("CODCLI",nf.getCodcli());
        }

        if(nf.getCliente()!=null){
            contentValues.put("CLIENTE",nf.getCliente());
        }

        if(nf.getUf()!=null){
            contentValues.put("UF",nf.getUf());
        }

        if(nf.getCiddade()!=null){
            contentValues.put("CIDADE",nf.getCiddade());
        }

        if(nf.getBairro()!=null){
            contentValues.put("BAIRRO",nf.getBairro());
        }

        if(nf.getObs1()!=null){
            contentValues.put("OBS1",nf.getObs1());
        }

        if(nf.getObs2()!=null){
            contentValues.put("OBS2",nf.getObs2());
        }

        if(nf.getObs3()!=null){
            contentValues.put("OBS3",nf.getObs3());
        }

        if(nf.getCodusur()!=null){
            contentValues.put("CODUSUR",nf.getCodusur());
        }

        if(nf.getObsentrega()!=null){
            contentValues.put("OBSENTREGA",nf.getObsentrega());
        }
        if(nf.getDtent() != null){
            contentValues.put("DTENT",nf.getDtent());
        }

        if(nf.getStenvi()!=null){
            contentValues.put("STENVI",nf.getStenvi());
        }

        if(nf.getStent()!=null){
            contentValues.put("STENT",nf.getStent());
        }

        if(nf.getStpend()!=null){
            contentValues.put("STPEND",nf.getStpend());
        }

        if(nf.getStpend()!=null){
            contentValues.put("STCRED",nf.getStcred());
        }

        if(nf.getLatent()!=null){
            contentValues.put("LATENT",nf.getLatent());
        }

        if(nf.getLongtent()!=null){
            contentValues.put("LONGTENT",nf.getLongtent());
        }

        if(nf.getPendlat()!=null){
            contentValues.put("PENDLAT",nf.getPendlat());
        }

        if(nf.getPendlongt()!=null){
            contentValues.put("PENDLONGT",nf.getPendlongt());
        }

        if(nf.getEmail_cliente()!=null){
            contentValues.put("EMAIL_CLIENTE",nf.getEmail_cliente());
        }

        if(nf.getEmail_cliene2()!=null){
            contentValues.put("EMAIL_CLIENTE2",nf.getEmail_cliene2());
        }

        if(nf.getRca()!=null){
            contentValues.put("RCA",nf.getRca());
        }

        if(nf.getEmail_rca()!=null){
            contentValues.put("EMAIL_RCA",nf.getEmail_rca());
        }
        if(nf.getEndereco()!=null){
            contentValues.put("ENDERECO",nf.getEndereco());
        }
        if(nf.getCep()!=null){
            contentValues.put("CEP",nf.getCep());
        }
        if(nf.getPendcodprocess()!=null){
            contentValues.put("PENDCODPROCESS",nf.getPendcodprocess());
        }
        if(nf.getPenddtent()!=null){
            contentValues.put("PENDDTENT",nf.getPenddtent());
        }
        if(nf.getPendobs()!=null){
            contentValues.put("PENDOBS",nf.getPendobs());
        }

        if(nf.getCodmotivo()!=null){
            contentValues.put("CODMOTIVO",nf.getCodmotivo());
        }
        if(nf.getNumped()!=null){
            contentValues.put("NUMPED",nf.getNumped());
        }
        if(nf.getNumtransvenda()!=null){
            contentValues.put("NUMTRANSVENDA",nf.getNumtransvenda());
        }
        long affectedRows = db.insertWithOnConflict("NF",column,contentValues,conflictAlgorithm);
        if(affectedRows>-1){
            return true;
        }else{
            return false;
        }
    }


    public boolean updatetNF(NF nf,String whereClause,String[] whereValues){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(nf.getNumnota()!=null){
            contentValues.put("NUMNOTA",nf.getNumnota());
        }
        if(nf.getNumcar()!=null){
            contentValues.put("NUMCAR",nf.getNumcar());
        }

        if(nf.getCodcli()!=null){
            contentValues.put("CODCLI",nf.getCodcli());
        }

        if(nf.getCliente()!=null){
            contentValues.put("CLIENTE",nf.getCliente());
        }

        if(nf.getUf()!=null){
            contentValues.put("UF",nf.getUf());
        }

        if(nf.getCiddade()!=null){
            contentValues.put("CIDADE",nf.getCiddade());
        }

        if(nf.getBairro()!=null){
            contentValues.put("BAIRRO",nf.getBairro());
        }

        if(nf.getObs1()!=null){
            contentValues.put("OBS1",nf.getObs1());
        }

        if(nf.getObs2()!=null){
            contentValues.put("OBS2",nf.getObs2());
        }

        if(nf.getObs3()!=null){
            contentValues.put("OBS3",nf.getObs3());
        }

        if(nf.getCodusur()!=null){
            contentValues.put("CODUSUR",nf.getCodusur());
        }

        if(nf.getObsentrega()!=null){
            contentValues.put("OBSENTREGA",nf.getObsentrega());
        }

        if(nf.getStenvi()!=null){
            contentValues.put("STENVI",nf.getStenvi());
        }

        if(nf.getStent()!=null){
            contentValues.put("STENT",nf.getStent());
        }

        if(nf.getDtent() != null){
            contentValues.put("DTENT",nf.getDtent());
        }

        if(nf.getStpend()!=null){
            contentValues.put("STPEND",nf.getStpend());
        }

        if(nf.getStcred()!=null){
            contentValues.put("STCRED",nf.getStcred());
        }

        if(nf.getLatent()!=null){
            contentValues.put("LATENT",nf.getLatent());
        }

        if(nf.getLongtent()!=null){
            contentValues.put("LONGTENT",nf.getLongtent());
        }

        if(nf.getPendlat()!=null){
            contentValues.put("PENDLAT",nf.getPendlat());
        }

        if(nf.getPendlongt()!=null){
            contentValues.put("PENDLONGT",nf.getPendlongt());
        }

        if(nf.getEmail_cliente()!=null){
            contentValues.put("EMAIL_CLIENTE",nf.getEmail_cliente());
        }

        if(nf.getEmail_cliene2()!=null){
            contentValues.put("EMAIL_CLIENTE2",nf.getEmail_cliene2());
        }

        if(nf.getRca()!=null){
            contentValues.put("RCA",nf.getRca());
        }

        if(nf.getEmail_rca()!=null){
            contentValues.put("EMAIL_RCA",nf.getEmail_rca());
        }
        if(nf.getEndereco()!=null){
            contentValues.put("ENDERECO",nf.getEndereco());
        }
        if(nf.getCep()!=null){
            contentValues.put("CEP",nf.getCep());
        }
        if(nf.getPendcodprocess()!=null){
            contentValues.put("PENDCODPROCESS",nf.getPendcodprocess());
        }
        if(nf.getPenddtent()!=null){
            contentValues.put("PENDDTENT",nf.getPenddtent());
        }
        if(nf.getPendobs()!=null){
            contentValues.put("PENDOBS",nf.getPendobs());
        }
        if(nf.getNumped()!=null){
            contentValues.put("NUMPED",nf.getNumped());
        }
        if(nf.getNumtransvenda()!=null){
            contentValues.put("NUMTRANSVENDA",nf.getNumtransvenda());
        }
        if(nf.getCodmotivo()!=null){
            contentValues.put("CODMOTIVO",nf.getCodmotivo());
        }
        long affectedRows = db.update("NF",contentValues,whereClause,whereValues);
        if(affectedRows>-1){
            return true;
        }else{
            return false;
        }
    }


    public boolean insertProd(Prod prod,String column,int conflictAlgorithm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(prod.getCodprod()!=null){
            contentValues.put("CODPROD",prod.getCodprod());
        }

        if(prod.getNumnota()!=null){
            contentValues.put("NUMNOTA",prod.getNumnota());
        }
        if(prod.getNumcar()!=null){
            contentValues.put("NUMCAR",prod.getNumcar());
        }
        if(prod.getQt()!=null){
            contentValues.put("QT",prod.getQt());
        }
        if(prod.getQtfalta()!=null){
            contentValues.put("QTFALTA",prod.getQtfalta());
        }

        if(prod.getCodmotivodev()!=null){
            contentValues.put("CODMOTIVO",prod.getCodmotivodev());
        }

        if(prod.getStdev()!=null){
            contentValues.put("STDEV",prod.getStdev());
        }
        if(prod.getDescricao()!=null){
            contentValues.put("DESCRICAO",prod.getDescricao());
        }
        if(prod.getCodbarra1()!=null){
            contentValues.put("CODBARRA1",prod.getCodbarra1());
        }
        if(prod.getCodbarra2()!=null){
            contentValues.put("CODBARRA2",prod.getCodbarra2());
        }
        if(prod.getPendqt()!=null){
            contentValues.put("PENDQT",prod.getPendqt());
        }
        if(prod.getPendcodmotivo()!=null){
            contentValues.put("PENDCODMOTIVO",prod.getPendcodmotivo());
        }
        long affectedRows = db.insertWithOnConflict("PROD",column,contentValues,conflictAlgorithm);
        if(affectedRows>-1){
            return true;
        }else{
            return false;
        }
    }

    public boolean updateProd(Prod prod,String whereClause,String[] whereValues){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(prod.getCodprod()!=null){
            contentValues.put("CODPROD",prod.getCodprod());
        }

        if(prod.getNumnota()!=null){
            contentValues.put("NUMNOTA",prod.getNumnota());
        }
        if(prod.getNumcar()!=null){
            contentValues.put("NUMCAR",prod.getNumcar());
        }
        if(prod.getQt()!=null){
            contentValues.put("QT",prod.getQt());
        }
        if(prod.getQtfalta()!=null){
            contentValues.put("QTFALTA",prod.getQtfalta());
        }

        if(prod.getCodmotivodev()!=null){
            contentValues.put("CODMOTIVO",prod.getCodmotivodev());
        }

        if(prod.getStdev()!=null){
            contentValues.put("STDEV",prod.getStdev());
        }
        if(prod.getDescricao()!=null){
            contentValues.put("DESCRICAO",prod.getDescricao());
        }
        if(prod.getCodbarra1()!=null){
            contentValues.put("CODBARRA1",prod.getCodbarra1());
        }
        if(prod.getCodbarra2()!=null){
            contentValues.put("CODBARRA2",prod.getCodbarra2());
        }
        long affectedRows = db.update("PROD",contentValues,whereClause,whereValues);
        if(affectedRows>-1){
            return true;
        }else{
            return false;
        }
    }

    public Cursor select(boolean distinct, String table, String[] columns,
                         String selection, String[] selectionArgs, String groupBy,
                         String having, String orderBy, String limit){
        Carga carga = new Carga();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(distinct,table,columns,selection,selectionArgs,groupBy,having,orderBy,limit);
        return c;
    }

    public void deleteCarga(long numcar){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CARGA WHERE NUMCAR="+String.valueOf(numcar));
    }

    public void deleteNF(long numcar){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM NFSAID WHERE NUMCAR="+String.valueOf(numcar));
    }

    public void deleteProd(long numnota){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM PROD WHERE NUMNOTA="+String.valueOf(numnota));
    }

    public long deleteAbove5(){
        long numcar =0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = select(false,"CARGA",new String[]{"NUMCAR"},null,null,null,null,"DTSAIDA DESC",null);
        if(c!= null && c.getCount()>5){
            c.moveToLast();
            numcar = c.getLong(c.getColumnIndex("NUMCAR"));
            db.execSQL(context.getString(R.string.db_sql_delete_above_5_prod));
            db.execSQL(context.getString(R.string.db_sql_delete_above_5_NF));
            db.execSQL(context.getString(R.string.db_sql_delete_above_5_carga));
            c.close();
        }
        return numcar;
    }
}
