package com.karam.transport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NFAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = "NFADAPTER";
    Context context;
    Activity activity;
    //Array de class NF tem a informação relacionada com a nota fiscal de cada entrega
    ArrayList<NF> items,temp_items;
    //Array de NF temporaria usada para guardar o resultado de filtro aplicado pelo Searchable (Search bar na em cima da Notas_activity)
    CustomFilter customFilter;



    public NFAdapter(Context context,Activity activity, ArrayList<NF> items) {
        this.context = context;
        this.activity = activity;
        this.items = items;
        this.temp_items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Defenir o costume view usado nesse adapter
        if(convertView == null){
            LayoutInflater inflater =(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notas_item_layout,parent,false);
        }
        //Defenir os objetos nesse view
        //Imagem de status da entrega
        final ImageView status_imgvw = convertView.findViewById(R.id.notas_status_ent_imgvw);
        //TextView para mostrar o codigo de cliente
        final TextView codcli_txtvw = convertView.findViewById(R.id.notas_codcli_txtvw);
        //TextView para mostrar a descrção de cliente
        final TextView cliente_txtvw = convertView.findViewById(R.id.notas_desc_txtvw);
        //TextView para mostrar o numero da nota fiscal
        final TextView nf_txtvw = convertView.findViewById(R.id.notas_nf_txtvw);
        //TextView para mostrar o status da entrega
        final TextView status_txtvw = convertView.findViewById(R.id.notas_status_txtvw);
        //Butão para mostrar a check_activity para mostrar as opções de entrega
        final ImageButton checkinBtn = convertView.findViewById(R.id.notas_checkin_imgvw);
        //Butão para abrir opção para gerar rotarização da localização de entrega da nota de pendencia
        final ImageButton locationBtn = convertView.findViewById(R.id.notas_location_imgvw);
        //Dependendo do caso da entrega vai mostrar Imagem diferente na lista e tbm a descrição do status
        // O buttão checking da entrega vai ser visivel só no caso da nota em transito, caso = 0
        switch (items.get(position).getStent()){
            case 0:
                status_imgvw.setImageResource(R.drawable.stent_emtransito);
                status_txtvw.setText(activity.getString(R.string.notas_status_emtransito));
                checkinBtn.setVisibility(View.VISIBLE);
                break;
            case 1:
                status_imgvw.setImageResource(R.drawable.stent_ok);
                status_txtvw.setText(activity.getString(R.string.notas_status_Entregue));
                checkinBtn.setVisibility(View.INVISIBLE);
                break;
            case 2:
                status_imgvw.setImageResource(R.drawable.stent_pend);
                status_txtvw.setText(activity.getString(R.string.notas_status_Entregue_pend));
                checkinBtn.setVisibility(View.INVISIBLE);
                break;
            case 3:
                status_imgvw.setImageResource(R.drawable.stent_dev);
                status_txtvw.setText(activity.getString(R.string.notas_status_Entregue_dev));
                checkinBtn.setVisibility(View.INVISIBLE);
                break;
            case 4:
                status_imgvw.setImageResource(R.drawable.stent_rentrega);
                status_txtvw.setText(activity.getString(R.string.notas_status_reentrega));
                checkinBtn.setVisibility(View.INVISIBLE);
                break;
        }
        //O butão mostrar localização vai ser visivel no caso da nota da pendencia que esta em transito, stpend = 1 e stent = 0
        if(items.get(position).getStpend()==1 && items.get(position).getStent()==0){
            locationBtn.setVisibility(View.VISIBLE);
        }else{
            locationBtn.setVisibility(View.INVISIBLE);
        }
        //textview para mostrar o codigo de cliente
        codcli_txtvw.setText(String.valueOf(items.get(position).getCodcli()));
        //textview para mostrar a descrição de cliente
        cliente_txtvw.setText(String.valueOf(items.get(position).getCliente()));
        //textview para mostrar o numero da nota físcal
        nf_txtvw.setText(String.valueOf(items.get(position).getNumnota()));

        //Butão para mostrar as informção relacionada da nota fiscal (endereço e cep do cadastro do cliente e
        //as observações de entrega
        ImageButton detailsBtn = convertView.findViewById(R.id.notas_details_imgvw);
        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //assign arrays para mandar array de titulos e hashmap das informaçoes de entrega para alimentar o class DetailsAdapter (ExpandableArratAdapter)
                ArrayList<String> titles = new ArrayList<>();
                titles.add(activity.getString(R.string.details_local));
                titles.add(activity.getString(R.string.details_obs));
                HashMap<String,ArrayList<String>> map = new HashMap<>();
                ArrayList<String> arr = new ArrayList<>();
                arr.add("UF: "+items.get(position).getUf());
                arr.add("Cidade: "+items.get(position).getCiddade());
                arr.add("Bairro: "+items.get(position).getBairro());
                arr.add("Endereço: "+items.get(position).getEndereco());
                arr.add("CEP: "+items.get(position).getCep());
                map.put("Local de entrega",arr);
                arr = new ArrayList<>();
                arr.add("OBS1: "+items.get(position).getObs1());
                arr.add("OBS2: "+items.get(position).getObs2());
                arr.add("OBS3: "+items.get(position).getObs3());
                if(items.get(position).getStpend()==1){
                    arr.add("OBS última entrega: "+items.get(position).getPendobs());
                    arr.add("DT última entrega: "+items.get(position).getPenddtent());
                    arr.add("Cod.Pend: "+items.get(position).getPendcodprocess());
                }
                map.put("OBS de entrega",arr);
                Bundle args = new Bundle();
                args.putString("codcli",String.valueOf(codcli_txtvw.getText()));
                args.putString("cliente",String.valueOf(cliente_txtvw.getText()));
                args.putString("nf",String.valueOf(nf_txtvw.getText()));
                args.putString("status",String.valueOf(status_txtvw.getText()));
                args.putString("dtent",items.get(position).getDtent());
                try {
                    args.putString("titles",ObjectSerializer.serialize(titles));
                    args.putString("map",ObjectSerializer.serialize(map));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity,DetailsActivity.class);
                intent.putExtras(args);
                activity.startActivity(intent);
            }
        });
        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("codcli",String.valueOf(codcli_txtvw.getText()));
                args.putString("cliente",String.valueOf(cliente_txtvw.getText()));
                args.putString("nf",String.valueOf(nf_txtvw.getText()));
                args.putString("status",String.valueOf(status_txtvw.getText()));
                args.putString("email_cliente",items.get(position).getEmail_cliente());
                args.putInt("position",position);
                args.putString("numtransvenda", String.valueOf(items.get(position).getNumtransvenda()));
                args.putString("numped", String.valueOf(items.get(position).getNumped()));
                //open the check activity
                Intent intent = new Intent(activity,CheckActivity.class);
                intent.putExtras(args);
                activity.startActivity(intent);
                NotasActivity.getInstance().finish();
            }
        });
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Methods.checkGPSTurndOn(context,activity)){
                    View view = Methods.setToastView(activity,"",false,
                            activity.getString(R.string.maps_app_dialog),true,
                            "Waze",true,"Google maps",true);
                    final AlertDialog dialogMap = new AlertDialog.Builder(context)
                            .create();
                    dialogMap.setView(view);
                    dialogMap.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogMap.show();
                    Button wazeButton = view.findViewById(R.id.toast_btn_confirm);
                    Button googleButton = view.findViewById(R.id.toast_btn_dismiss);
                    wazeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("https://waze.com/ul?q="+items.get(position).getPendlat()+
                                    ","+items.get(position).getPendlongt()+"&navigate=yes");
                            callMapIntent(position,gmmIntentUri,"com.waze");
                            dialogMap.dismiss();
                        }
                    });

                    googleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+items.get(position).getPendlat()+
                                    ","+items.get(position).getPendlongt());
                            callMapIntent(position,gmmIntentUri,"com.google.android.apps.maps");
                            dialogMap.dismiss();
                        }
                    });
                }
            }
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(customFilter==null){
            customFilter=new CustomFilter();
        }
        return customFilter;
    }



    class CustomFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint!= null && constraint.length()>0){
                constraint = String.valueOf(constraint).toUpperCase();
                ArrayList<NF> filters = new ArrayList<>();
                for (int i = 0 ; i<temp_items.size();i++){
                    if(String.valueOf(temp_items.get(i).getCodcli()).toUpperCase().contains(constraint) ||
                            String.valueOf(temp_items.get(i).getNumnota()).toUpperCase().contains(constraint) ||
                            temp_items.get(i).getCliente().toUpperCase().contains(constraint)){
                        filters.add(temp_items.get(i));
                    }
                }
                results.count = filters.size();
                results.values=filters;
            }else{
                results.count = temp_items.size();
                results.values = temp_items;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items = (ArrayList<NF>)results.values;
            notifyDataSetChanged();
        }
    }

    private void callMapIntent(int position,Uri uri,String packageName){
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage(packageName);
        if(mapIntent.resolveActivity(context.getPackageManager())!=null){
            activity.startActivity(mapIntent);
        }else{
            View view = Methods.setToastView(activity,"",false,context.getString(R.string.map_app_error),
                    true,"",false,"",false);
            Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        }
    }
}
