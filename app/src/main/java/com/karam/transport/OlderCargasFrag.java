package com.karam.transport;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class OlderCargasFrag extends BottomSheetDialogFragment {
    ListView cargaLV;
    ArrayList<String> arr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_older_cargas, container, false);
        setUpListView(view);
        return view;
    }

    private void setUpListView(View view){
        cargaLV = view.findViewById(R.id.login_carga_lv);
        cargaLV.setEnabled(false);
        new AsyncTask<Void,Void, ArrayList<String>>(){
            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                DBConnection dbConnection = new DBConnection(getContext());
                Cursor c = dbConnection.select(false, "CARGA", new String[]{"NUMCAR", "DTSAIDA"},
                        null, null
                        , null, null, "DTSAIDA", null);
                arr = new ArrayList<>();
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    while ((c != null && !c.isAfterLast())) {
                        arr.add(c.getLong(c.getColumnIndex("NUMCAR")) + "  " + c.getString(c.getColumnIndex("DTSAIDA")).split(" ")[0]);
                        c.moveToNext();
                    }
                    c.close();
                }
                return arr;
            }

            @Override
            protected void onPostExecute(ArrayList<String> strings) {
                if (strings.size() > 0) {
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, strings){
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
                    cargaLV.setAdapter(dataAdapter);
                    cargaLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(),OlderNotasActivity.class);
                            intent.putExtra("numcar",arr.get(position).split(" ")[0].trim());
                            startActivity(intent);
                        }
                    });
                    cargaLV.setEnabled(true);
                }else{
                    cargaLV.setEnabled(false);
                }
            }
        }.execute();
    }

}
