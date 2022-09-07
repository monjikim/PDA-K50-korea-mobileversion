package com.scandecode_example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectionPage extends Activity {


    Button Btn_export,Btn_eai_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_page);
        Btn_eai_data = (Button)findViewById(R.id.Btn_eai_data);
        Btn_export = (Button)findViewById(R.id.Btn_export);
        Btn_eai_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectionPage.this, MainActivity.class));
            }
        });

        Btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectionPage.this, Export_national.class));
            }
        });

    }
}
