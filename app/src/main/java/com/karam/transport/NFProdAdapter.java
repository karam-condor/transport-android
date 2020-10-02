package com.karam.transport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class NFProdAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<NF> titles;
    HashMap<String, ArrayList<Prod>> items;


    public NFProdAdapter(Context context, ArrayList<NF> titles, HashMap<String, ArrayList<Prod>> items) {
        this.context = context;
        this.titles = titles;
        this.items = items;
    }

    @Override
    public int getGroupCount() {
        return titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(String.valueOf(titles.get(groupPosition).getNumnota())).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(titles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notas_item_layout,parent,false);
        }
        final ImageView status_imgvw = convertView.findViewById(R.id.notas_status_ent_imgvw);
        final TextView codcli_txtvw = convertView.findViewById(R.id.notas_codcli_txtvw);
        final TextView cliente_txtvw = convertView.findViewById(R.id.notas_desc_txtvw);
        final TextView nf_txtvw = convertView.findViewById(R.id.notas_nf_txtvw);
        final TextView status_txtvw = convertView.findViewById(R.id.notas_status_txtvw);
        final ImageButton checkinBtn = convertView.findViewById(R.id.notas_checkin_imgvw);
        final ImageButton locationBtn = convertView.findViewById(R.id.notas_location_imgvw);
        final ImageButton detailsBtn = convertView.findViewById(R.id.notas_details_imgvw);
        checkinBtn.setVisibility(View.INVISIBLE);
        detailsBtn.setVisibility(View.INVISIBLE);
        locationBtn.setVisibility(View.INVISIBLE);
        switch (titles.get(groupPosition).getStent()){
            case 2:
                status_imgvw.setImageResource(R.drawable.stent_pend);
                status_txtvw.setText(context.getString(R.string.notas_status_Entregue_pend));
                break;
            case 3:
                status_imgvw.setImageResource(R.drawable.stent_dev);
                status_txtvw.setText(context.getString(R.string.notas_status_Entregue_dev));
                break;
            case 4:
                status_imgvw.setImageResource(R.drawable.stent_rentrega);
                status_txtvw.setText(context.getString(R.string.notas_status_reentrega));
                break;
        }
        codcli_txtvw.setText(String.valueOf(titles.get(groupPosition).getCodcli()));
        cliente_txtvw.setText(String.valueOf(titles.get(groupPosition).getCliente()));
        nf_txtvw.setText(String.valueOf(titles.get(groupPosition).getNumnota()));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.produto_item,parent,false);
        }
        //set the view componens
        TextView codprod_txtvw = convertView.findViewById(R.id.prod_cod_txtvw);
        TextView desprod_txtvw = convertView.findViewById(R.id.prod_txtvw);
        TextView qt_txtvw = convertView.findViewById(R.id.prod_qt_txtvw);
        TextView qt_pend_txtvw = convertView.findViewById(R.id.txt_list_prod_qtd_falta);
        TextView motivo_txtvw = convertView.findViewById(R.id.prod_motivo_txtvw);
        TextView status_txtvw = convertView.findViewById(R.id.prod_status);
        //set the values of the component

        codprod_txtvw.setText(String.valueOf(items.get(String.valueOf(titles.get(groupPosition).getNumnota())).get(childPosition).getCodprod()));
        desprod_txtvw.setText(String.valueOf(items.get(String.valueOf(titles.get(groupPosition).getNumnota())).get(childPosition).getDescricao()));
        qt_txtvw.setText(String.valueOf(items.get(String.valueOf(titles.get(groupPosition).getNumnota())).get(childPosition).getQt()));
        qt_pend_txtvw.setText(String.valueOf(items.get(String.valueOf(titles.get(groupPosition).getNumnota())).get(childPosition).getQtfalta()));
        String[] arrCod = context.getResources().getStringArray(R.array.motivos_dev_cod);
        String[] arrMot = context.getResources().getStringArray(R.array.motivos_dev_des);

        int x =0 ;
        for (int i=0;i<arrCod.length;i++){
            if(arrCod[i].matches(String.valueOf(items.get(String.valueOf(titles.get(groupPosition).getNumnota())).get(childPosition).getCodmotivodev()))){
                x=i;
                break;
            }
        }
        try{
            motivo_txtvw.setText(arrMot[x]);
        }catch (Exception ex){

        }

        if(items.get(String.valueOf(titles.get(groupPosition).getNumnota())).get(childPosition).getStdev()!=null){
            switch (items.get(String.valueOf(titles.get(groupPosition).getNumnota())).get(childPosition).getStdev()){
                case 1:
                    status_txtvw.setVisibility(View.VISIBLE);
                    status_txtvw.setText("Pendencia");
                    break;
                case 2:
                    status_txtvw.setVisibility(View.VISIBLE);
                    status_txtvw.setText("Devolução");
                    break;
            }
        }else{
            status_txtvw.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
