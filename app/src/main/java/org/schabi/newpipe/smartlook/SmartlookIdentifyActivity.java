package org.schabi.newpipe.smartlook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.smartlook.sdk.smartlook.Smartlook;

import org.json.JSONException;
import org.json.JSONObject;
import org.schabi.newpipe.R;

public class SmartlookIdentifyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etUserId;
    private EditText etName;
    private EditText etAge;
    private EditText etCompany;
    private Button bIdentify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlook_identify);

        initViews();
        handleToolbar();
        handleIdentify();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);

        etUserId = findViewById(R.id.smartlook_user_id);
        etName = findViewById(R.id.smartlook_user_name);
        etAge = findViewById(R.id.smartlook_user_age);
        etCompany = findViewById(R.id.smartlook_user_company);

        bIdentify = findViewById(R.id.smartlook_identify);
    }

    private void handleToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void handleIdentify() {
        bIdentify.setOnClickListener(v -> {
            Smartlook.identify(etUserId.getText().toString(),
                    formatUserProperties());
        });
    }

    private JSONObject formatUserProperties() {
        JSONObject jsonObject = new JSONObject();

        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        String company = etCompany.getText().toString();

        try {
            if (!name.isEmpty()) {
                jsonObject.put("name", name);
            }
            if (!age.isEmpty()) {
                jsonObject.put("age", age);
            }
            if (!company.isEmpty()) {
                jsonObject.put("company", company);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
