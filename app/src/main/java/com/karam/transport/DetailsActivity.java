package com.karam.transport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //set view compenant
        TextView codcli_details_txtvw = findViewById(R.id.details_codcli_txtvw);
        TextView cliente_details_txtvw = findViewById(R.id.details_cliente_txtvw);
        TextView nf_details_txtvw = findViewById(R.id.details_nf_txtvw);
        TextView status_details_txtvw = findViewById(R.id.details_status_txtvw);
        TextView status_data_txtvw = findViewById(R.id.details_dtent_txtvw);
        ExpandableListView details_list = findViewById(R.id.details_info_lv);
        //set the value of the items
        Intent intent = this.getIntent();
        Bundle args = intent.getExtras();
        codcli_details_txtvw.setText(args.getString("codcli"));
        cliente_details_txtvw.setText(args.getString("cliente"));
        nf_details_txtvw.setText(args.getString("nf"));
        status_details_txtvw.setText(args.getString("status"));
        status_data_txtvw.setText(args.getString("dtent"));
        try {
            ArrayList<String> titles =(ArrayList<String>) ObjectSerializer.deserialize(args.getString("titles"));
            HashMap<String,ArrayList<String>> map = (HashMap<String,ArrayList<String>>) ObjectSerializer.deserialize(args.getString("map"));
            DetailsAdapter adapter = new DetailsAdapter(this,titles,map);
            details_list.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
