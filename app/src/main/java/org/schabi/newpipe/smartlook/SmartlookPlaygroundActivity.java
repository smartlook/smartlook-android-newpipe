package org.schabi.newpipe.smartlook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.schabi.newpipe.R;

public class SmartlookPlaygroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlook_playground);

        LinearLayout level1 = findViewById(R.id.lvl_1);
        FrameLayout level2 = findViewById(R.id.lvl_2);
        FrameLayout level3 = findViewById(R.id.lvl_3);
        ImageView level4 = findViewById(R.id.lvl_4);


        level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        level3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
