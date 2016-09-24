package com.evilduckling.nainmailer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.evilduckling.nainmailer.R;
import com.evilduckling.nainmailer.interfaces.Const;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class MailActivity extends AppCompatActivity {

    private TextView allMail;
    private TextView unreadMail;
    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        allMail = (TextView) findViewById(R.id.mail_all);
        unreadMail = (TextView) findViewById(R.id.mail_unread);
        refresh = (Button) findViewById(R.id.mail_refresh_button);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMailData();
            }
        });

        getMailData();

    }

    private void getMailData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, "http://nainwak.com/jeu/chat.php?IDS=" + getIdentifier(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(Const.LOG_TAG, "" + responseString);
                Toast.makeText(MailActivity.this, "Sorry something went wrong", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(Const.LOG_TAG, "" + responseString);
                if (!tryToExtractData(responseString)) {
                    Toast.makeText(MailActivity.this, "Session must have expired", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean tryToExtractData(String responseString) {

        Pattern extractorPattern = Pattern.compile("^.*<span class=\"event-temps\">(.*)messages, <span class=\"chatpager.*lu\">(.*)non lus</span>.*$");

        String noReturnString = responseString
            .replaceAll("\n", "")
            .replaceAll("\r", "");

        Matcher m = extractorPattern.matcher(noReturnString);
        if (m.matches()) {

            String all = m.group(1).trim();
            String unread = m.group(2).trim();

            allMail.setText(all + " messages au total");
            unreadMail.setText(unread + " messages non lus");

            return true;
        }

        return false;
    }

    private String getIdentifier() {
        return getSharedPreferences(Const.STORAGE, MODE_PRIVATE).getString("identifier", "");
    }

}
