package com.karam.transport;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SaveNotas {
    DBConnection dbConnection;
    Context context;
    Activity activity;
    String numnota,numcar,dtfinal;
    ArrayList<HashMap<String,String>> notasList,pordsList;
    HashMap<String,String> map,imgs;
    public SaveNotas(Context context,Activity activity,String numcar) {
        this.context = context;
        this.activity = activity;
        this.numcar = numcar;
        dtfinal = Methods.getCurrentDate();
        dbConnection = new DBConnection(context);
    }

    public void enviar(){
        imgs = new HashMap<>();
        notasList = new ArrayList<>();
        pordsList = new ArrayList<>();
        Cursor c = dbConnection.select(false, "NF", null,
                " STENVI = 0 AND STENT>0 AND NUMCAR=?",
                new String[]{String.valueOf(numcar)}
                , null, null, "CLIENTE", null);
        if(c.getCount()>0){
            c.moveToFirst();
            HashMap<String,String> mapNF;
            while(c!=null && !c.isAfterLast()){
                mapNF = new HashMap<>();
                numnota = String.valueOf(c.getLong(c.getColumnIndex("NUMNOTA")));
                File mediaStorageDir = activity.getDir("records",MODE_PRIVATE);
                if (!mediaStorageDir.exists()) {
                    mediaStorageDir.mkdirs();
                }
                String outputFile =mediaStorageDir.getAbsolutePath() +File.separator+numcar+"-"+numnota+".3gp";
                String fileName = numcar + "-" + numnota + ".3gp";
                String audio = Methods.getBase64FromPath(outputFile);
                HashMap<String,String> mapImagesNota = Methods.getImagesHashMapBynota(context,numcar,numnota);
                if(mapImagesNota!=null){
                    imgs.putAll(mapImagesNota);
                }
                mapNF.put(":NUMNOTA",numnota);
                mapNF.put(":NUMCAR",numcar);
                mapNF.put(":LAT",String.valueOf(c.getFloat(c.getColumnIndex("LATENT"))));
                mapNF.put(":LONGT",String.valueOf(c.getFloat(c.getColumnIndex("LONGTENT"))));
                mapNF.put(":DTENTREGA",String.valueOf(c.getString(c.getColumnIndex("DTENT"))));
                mapNF.put(":OBSENT",String.valueOf(c.getString(c.getColumnIndex("OBSENTREGA"))));
                if(c.getString(c.getColumnIndex("EMAIL_CLIENTE2"))== null || c.getString(c.getColumnIndex("EMAIL_CLIENTE2")).isEmpty()){
                    mapNF.put(":EMAIL_CLIENTE", String.valueOf(c.getString(c.getColumnIndex("EMAIL_CLIENTE"))));
                }else{
                    mapNF.put(":EMAIL_CLIENTE", String.valueOf(c.getString(c.getColumnIndex("EMAIL_CLIENTE2"))));
                }
                mapNF.put(":STATUS",String.valueOf(c.getInt(c.getColumnIndex("STENT"))));
                mapNF.put(":STCRED",String.valueOf(c.getInt(c.getColumnIndex("STCRED"))));
                mapNF.put(":CODMOTIVO",String.valueOf(c.getInt(c.getColumnIndex("CODMOTIVO"))));
                mapNF.put(":NUMTRANSVENDA",String.valueOf(c.getInt(c.getColumnIndex("NUMTRANSVENDA"))));
                mapNF.put(":NUMPED",String.valueOf(c.getInt(c.getColumnIndex("NUMPED"))));
                mapNF.put(":AUDIO",audio);
                mapNF.put(":FILENAME",fileName);
                notasList.add(mapNF);
                if(Methods.integerParser(mapNF.get(":STATUS"))==2){
                    Cursor  c2 = dbConnection.select(false, "PROD", null,
                            "NUMCAR =? AND NUMNOTA=? AND STDEV>0", new String[]{numcar,numnota}
                            , null, null, "DESCRICAO", null);
                    if(c2.getCount()>0){
                        c2.moveToFirst();
                        HashMap<String,String> mapProd;
                        while(c2 != null && !c2.isAfterLast()){
                            mapProd = new HashMap<>();
                            mapProd.put(":CODPROD",String.valueOf(c2.getLong(c2.getColumnIndex("CODPROD"))));
                            mapProd.put(":QT",String.valueOf(c2.getLong(c2.getColumnIndex("QTFALTA"))));
                            mapProd.put(":CODMOTIVO",String.valueOf(c2.getInt(c2.getColumnIndex("CODMOTIVO"))));
                            mapProd.put(":STDEV",String.valueOf(c2.getInt(c2.getColumnIndex("STDEV"))));
                            mapProd.put(":NUMCAR",numcar);
                            mapProd.put(":NUMNOTA",numnota);
                            pordsList.add(mapProd);
                            c2.moveToNext();
                        }
                        c2.close();
                    }
                }
                c.moveToNext();
            }
            c.close();
            String jsonNotas = Methods.toJson(notasList);
            String jsonProds = Methods.toJson(pordsList);
            String imgsJson = Methods.toJsonHash(imgs);
            map = Methods.stringToHashMap("NOTAJSON%PRODJSON%DTFINAL%KM%NUMCAR%IMAGES",jsonNotas,jsonProds,dtfinal,"EXSISTS",numcar,imgsJson);
            String encodedParams = null;
            try {
                encodedParams = Methods.encode(map);
                SRVConnection connection = new SRVConnection(activity, null, "response");
                connection.execute(activity.getString(R.string.url_server_host) + activity.getString(R.string.url_server_save_auto), encodedParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            String imgsJson = Methods.toJsonHash(imgs);
            map = Methods.stringToHashMap("KM%NUMCAR%DTFINAL%IMAGES","EXSISTS",numcar,dtfinal,imgsJson);
            String encodedParams = null;
            try {
                encodedParams = Methods.encode(map);
                SRVConnection connection = new SRVConnection(activity, null, "response");
                connection.execute(activity.getString(R.string.url_server_host) + activity.getString(R.string.url_server_save_auto), encodedParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}