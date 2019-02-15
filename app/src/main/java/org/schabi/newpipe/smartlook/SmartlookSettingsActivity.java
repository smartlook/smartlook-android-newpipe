package org.schabi.newpipe.smartlook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.smartlook.consentsdk.ConsentApi;
import com.smartlook.consentsdk.data.Consent;
import com.smartlook.consentsdk.data.ConsentItem;
import com.smartlook.sdk.smartlook.api.client.Server;

import org.schabi.newpipe.R;

public class SmartlookSettingsActivity extends AppCompatActivity {

    public static final int RESTART_INTENT_ID = 223344;
    public static final int RESTART_DELAY = 100;
    public static final int APP_SETTLE_DELAY = 200;

    private Toolbar toolbar;
    private Spinner spinner;
    private Button confirmButton;
    private Button reloadDefaultApiKeyButton;
    private EditText apiKeyInput;
    private Switch debugSelectors;
    private Button goToPlayground;
    private Switch runInExperimentalMode;

    private int lastSpinnerPosition;

    private String alfaApiKey;
    private String betaApiKey;
    private String productionApiKey;

    AlertDialog exitAlertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlook_settings);

        alfaApiKey = SmartlookPreferences.loadApiKey(this, Server.ALFA);
        betaApiKey = SmartlookPreferences.loadApiKey(this, Server.BETA);
        productionApiKey = SmartlookPreferences.loadApiKey(this, Server.PRODUCTION);

        initViews();
        handleToolbar();

        setInitialServerSelection();
        setInitialDebugSelectorsSwitchSelection();
        setInitialRunInExperimentalModeSelection();
        handleSpinner();
        handleReloadDefault();
        handleConfirm();
        handleGoToPlayground();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    super.onBackPressed();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    exitAlertDialog.dismiss();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkDialogTheme);
        exitAlertDialog = builder.setMessage(R.string.exit_dialog_message)
                .setPositiveButton(android.R.string.ok, dialogClickListener)
                .setNegativeButton(android.R.string.cancel, dialogClickListener)
                .show();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.smartlook_server_spinner);
        confirmButton = findViewById(R.id.smartlook_server_confirm);
        reloadDefaultApiKeyButton = findViewById(R.id.smartlook_reload_default);
        apiKeyInput = findViewById(R.id.smartlook_input_api_key);
        debugSelectors = findViewById(R.id.smartlook_debug_selectors_switch);
        goToPlayground = findViewById(R.id.smartlook_go_to_playground);
        runInExperimentalMode = findViewById(R.id.smartlook_run_in_experimental_mode);
    }

    private void handleToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setInitialServerSelection() {
        int server = SmartlookPreferences.loadServerSelection(this);
        spinner.setSelection(server);
        lastSpinnerPosition = server;

        displayActualServerApiKey(server);
    }

    private void setInitialDebugSelectorsSwitchSelection() {
        debugSelectors.setChecked(SmartlookPreferences.loadDebugSelectors(this));
    }

    private void setInitialRunInExperimentalModeSelection() {
        runInExperimentalMode.setChecked(SmartlookPreferences.loadRunInExperimentalMode(this));
    }

    private void handleSpinner() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                rememberApiKey(position);
                displayActualServerApiKey(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void handleReloadDefault() {
        reloadDefaultApiKeyButton.setOnClickListener(v -> {
            int selectedServer = spinner.getSelectedItemPosition();

            apiKeyInput.setText(SmartlookPreferences.getDefaultApiKey(selectedServer));
            rememberApiKey(selectedServer);
        });
    }

    private void handleConfirm() {
        confirmButton.setOnClickListener(v -> {
            int server = spinner.getSelectedItemPosition();

            SmartlookPreferences.storeServerSelection(this, server);
            storeApiKeys(server);
            SmartlookPreferences.storeDebugSelectors(this, debugSelectors.isChecked());
            SmartlookPreferences.storeRunInExperimentalMode(this, runInExperimentalMode.isChecked());

            // let app settle before restart
            (new Handler()).postDelayed(this::doRestart, APP_SETTLE_DELAY);
        });
    }

    private void handleGoToPlayground() {
        goToPlayground.setOnClickListener(v -> {

            ConsentItem[] consentItems = {
                    new ConsentItem("AGE_CONSENT", true, "I certify that I'm over the age of fifteen, have read, understood adn accepted Privacy Policy.", null),
                    new ConsentItem("SDK_CONSENT", false, "I agree to play for free and that my personal data is collected via the SDK tools build into the application.", "https://www.nplix.com/kotlin-parcelable-array-objects/")
            };

            new ConsentApi(SmartlookSettingsActivity.this).showConsentDialogFragment(
                    SmartlookSettingsActivity.this,
                    new Consent(
                            "Consent api dialog",
                            "Curabitur sagittis hendrerit ante. Aenean fermentum risus id tortor. Integer in sapien. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Aliquam ornare wisi eu metus. Etiam dui sem, fermentum vitae, sagittis id, malesuada in, quam.",
                            "This is my will",
                            consentItems));
        });
    }

    private void displayActualServerApiKey(int server) {
        switch (server) {
            case Server.ALFA:
                apiKeyInput.setText(alfaApiKey);
                break;
            case Server.BETA:
                apiKeyInput.setText(betaApiKey);
                break;
            case Server.PRODUCTION:
                apiKeyInput.setText(productionApiKey);
                break;
        }
    }

    private void rememberApiKey(int position) {
        switch (lastSpinnerPosition) {
            case Server.ALFA:
                alfaApiKey = apiKeyInput.getText().toString();
                break;
            case Server.BETA:
                betaApiKey = apiKeyInput.getText().toString();
                break;
            case Server.PRODUCTION:
                productionApiKey = apiKeyInput.getText().toString();
                break;
        }

        lastSpinnerPosition = position;
    }

    private void storeApiKeys(int server) {
        rememberApiKey(server);

        SmartlookPreferences.storeApiKey(this, alfaApiKey, Server.ALFA);
        SmartlookPreferences.storeApiKey(this, betaApiKey, Server.BETA);
        SmartlookPreferences.storeApiKey(this, productionApiKey, Server.PRODUCTION);
    }

    public void doRestart() {

        // Prepare start intent
        Intent startActivity = getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Prepare pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, RESTART_INTENT_ID, startActivity, PendingIntent.FLAG_CANCEL_CURRENT);

        // Setup alarm manager to start up application again
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + RESTART_DELAY, pendingIntent);

        // Kill app
        System.exit(0);
    }
}
