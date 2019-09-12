package org.schabi.newpipe.smartlook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.smartlook.sdk.smartlook.Smartlook;

import org.schabi.newpipe.R;

public class SmartlookPlaygroundActivity extends AppCompatActivity {

    private static final String HTML = "<!doctype html>\n" +
            "\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "  <meta charset=\"utf-8\">\n" +
            "\n" +
            "  <title>Smartlook Test</title>\n" +
            "  <meta name=\"description\" content=\"Smartlook Test\">\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "    <div class='random'>\n" +
            "        <h1>Smartlook testing</h1>\n" +
            "    </div>\n" +
            "    <div class='smartlook-hide'>\n" +
            "        <h1>Smartlook HIDDEN</h1>\n" +
            "    </div>\n" +
            "    <div class='random'>\n" +
            "        <h1>Smartlook testing</h1>\n" +
            "    </div>\n" +
            "    <div class='smartlook-hide'>\n" +
            "        <h1>Smartlook HIDDEN</h1>\n" +
            "    </div>\n" +
            "    <div class='random'>\n" +
            "        <h1>Smartlook testing</h1>\n" +
            "    </div>\n" +
            "    <div>\n" +
            "        <input type='text' />\n" +
            "    </div>\n" +
            "    <div>\n" +
            "        <input type='button' value='Button' />\n" +
            "    </div>\n" +
            "    <div>\n" +
            "        <input type='password' />\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlook_playground);

        LinearLayout level1 = findViewById(R.id.lvl_1);
        FrameLayout level2 = findViewById(R.id.lvl_2);
        FrameLayout level3 = findViewById(R.id.lvl_3);
        ImageView level4 = findViewById(R.id.lvl_4);
        WebView webView = findViewById(R.id.webview);

        Smartlook.registerWhitelistedView(findViewById(R.id.et_whitelisted1));
        Smartlook.registerWhitelistedView(findViewById(R.id.et_whitelisted2));
        Smartlook.registerBlacklistedView(findViewById(R.id.clip_fl));
        Smartlook.registerBlacklistedView(findViewById(R.id.uncliped_view));
        Smartlook.registerBlacklistedView(findViewById(R.id.clip_fl_v2));
        Smartlook.registerBlacklistedView(findViewById(R.id.uncliped_view_v2));
        Smartlook.registerBlacklistedClass(ProgressBar.class);

        webView.loadDataWithBaseURL("", HTML, "text/html", "UTF-8", "");
        webView.getSettings().setJavaScriptEnabled(true);


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
