package com.karam.transport;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class ProdAdapter extends BaseAdapter implements Filterable {
    Context context;
    ArrayList<Prod> items;
    ArrayList<Prod> temp_items;
    CustomFilter customFilter;
    public ProdAdapter(Context context,ArrayList<Prod> items){
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
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

        codprod_txtvw.setText(String.valueOf(items.get(position).getCodprod()));
        desprod_txtvw.setText(items.get(position).getDescricao());
        if(items.get(position).getPendqt()!=null && items.get(position).getPendqt()>0){
            qt_txtvw.setText(String.valueOf(items.get(position).getPendqt()));
        }else{
            qt_txtvw.setText(String.valueOf(items.get(position).getQt()));
        }

        qt_pend_txtvw.setText((items.get(position).getQtfalta()!=null)?String.valueOf(items.get(position).getQtfalta()):"");
        String[] arrCod = context.getResources().getStringArray(R.array.motivos_dev_cod);
        String[] arrMot = context.getResources().getStringArray(R.array.motivos_dev_des);

        int x =0 ;
        for (int i=0;i<arrCod.length;i++){
            if(arrCod[i].matches(String.valueOf(items.get(position).getCodmotivodev()))){
                x=i;
                break;
            }
        }
        motivo_txtvw.setText(items.get(position).getCodmotivodev()!=0 ?arrMot[x]:"");
        if(items.get(position).getStdev()!=null){
            switch (items.get(position).getStdev()){
                case 0:
                    status_txtvw.setVisibility(View.INVISIBLE);
                    break;
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
    public Filter getFilter() {
        if(customFilter==null){
            customFilter=new ProdAdapter.CustomFilter();
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
                ArrayList<Prod> filters = new ArrayList<>();
                for (int i = 0 ; i<temp_items.size();i++){
                    if(String.valueOf(temp_items.get(i).getCodprod()).toUpperCase().contains(constraint) ||
                            String.valueOf(temp_items.get(i).getCodbarra1()).toUpperCase().contains(constraint) ||
                            String.valueOf(temp_items.get(i).getCodbarra2()).toUpperCase().contains(constraint) ||
                            temp_items.get(i).getDescricao().toUpperCase().contains(constraint)){
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
            items = (ArrayList<Prod>) results.values;
            notifyDataSetChanged();
        }
    }
}
