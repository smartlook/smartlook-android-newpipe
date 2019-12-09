package org.schabi.newpipe.smartlook;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.smartlook.sdk.smartlook.Smartlook;

import org.json.JSONException;
import org.json.JSONObject;
import org.schabi.newpipe.R;

public class SmartlookIdentifyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etUserId;
    private EditText etName;
    private EditText etMail;
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
        etMail = findViewById(R.id.smartlook_user_email);
        etCompany = findViewById(R.id.smartlook_user_company);

        etUserId.setText(SmartlookPreferences.loadUserId(this));
        etName.setText(SmartlookPreferences.loadName(this));
        etMail.setText(SmartlookPreferences.loadMail(this));
        etCompany.setText(SmartlookPreferences.loadCompany(this));

        bIdentify = findViewById(R.id.smartlook_identify);
    }

    private void handleToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void handleIdentify() {
        bIdentify.setOnClickListener(v -> {

            SmartlookPreferences.storeUserId(this, etUserId.getText().toString());
            SmartlookPreferences.storeName(this, etName.getText().toString());
            SmartlookPreferences.storeMail(this, etMail.getText().toString());
            SmartlookPreferences.storeCompany(this, etCompany.getText().toString());

            Smartlook.setUserIdentifier(etUserId.getText().toString(), formatUserProperties());
        });
    }

    private JSONObject formatUserProperties() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", etName.getText().toString());
            jsonObject.put("email", etMail.getText().toString());
            jsonObject.put("company", etCompany.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
