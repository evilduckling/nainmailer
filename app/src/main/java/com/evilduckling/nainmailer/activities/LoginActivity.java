package com.evilduckling.nainmailer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evilduckling.nainmailer.R;
import com.evilduckling.nainmailer.interfaces.Const;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private EditText login;
    private EditText password;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        button = (Button) findViewById(R.id.button);

        loadLastCredentials();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams rp = new RequestParams();
                rp.put("login", login.getText().toString().trim());
                rp.put("password", password.getText().toString());

                saveNewCredentials();

                client.post(LoginActivity.this, Const.BASE_URL + "index.php", rp, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(Const.LOG_TAG, "" + responseString);
                        Toast.makeText(LoginActivity.this, "Sorry something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                        Log.d(Const.LOG_TAG, "" + responseString);

                        if (tryToExtractSessionId(responseString)) {
                            Intent intent = new Intent(LoginActivity.this, InboxActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Sorry something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }

    private boolean tryToExtractSessionId(String responseString) {

        Pattern extractorPattern = Pattern.compile("^.*logout\\.php\\?IDS=(.*)&auto.*$");

        String noReturnString = responseString
            .replaceAll("\n", "")
            .replaceAll("\r", "");

        Matcher m = extractorPattern.matcher(noReturnString);
        if (m.matches()) {

            String identifier = m.group(1);
            Log.d(Const.LOG_TAG, "Got session identifier key : " + identifier);
            getSharedPreferences(Const.STORAGE, MODE_PRIVATE)
                .edit()
                .putString("identifier", identifier)
                .apply();

            return true;
        }

        return false;

    }

    /**
     * Load default credentials in fields
     */
    private void loadLastCredentials() {

        String loginString = getSharedPreferences(Const.STORAGE, MODE_PRIVATE).getString("login", "");
        String passwordString = getSharedPreferences(Const.STORAGE, MODE_PRIVATE).getString("password", "");

        login.setText(loginString);
        password.setText(passwordString);

    }

    /**
     * Save registered credentials
     */
    private void saveNewCredentials() {

        String loginString = login.getText().toString().trim();
        String passwordString = password.getText().toString();

        getSharedPreferences(Const.STORAGE, MODE_PRIVATE)
            .edit()
            .putString("login", loginString)
            .putString("password", passwordString)
            .apply();

    }
}
