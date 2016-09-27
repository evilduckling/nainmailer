package com.evilduckling.nainmailer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evilduckling.nainmailer.R;
import com.evilduckling.nainmailer.adapters.MailAdapter;
import com.evilduckling.nainmailer.interfaces.Const;
import com.evilduckling.nainmailer.model.Mail;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class MailActivity extends AppCompatActivity {

    private TextView allMail;
    private TextView unreadMail;
    private Button refresh;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

/*        allMail = (TextView) findViewById(R.id.mail_all);
        unreadMail = (TextView) findViewById(R.id.mail_unread);
        refresh = (Button) findViewById(R.id.mail_refresh_button);*/
        list = (ListView) findViewById(R.id.mail_list);

  /*      refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMailData();
            }
        });*/

        // getMailData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getFullInbox();
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

    private void getFullInbox() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, "http://nainwak.com/jeu/chatbox.php?IDS=" + getIdentifier() + "&page=in", new TextHttpResponseHandler() {
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
                tryToExtractInbox(responseString);
            }
        });
    }

    private void tryToExtractInbox(String responseString) {

        List<Mail> mailList = new ArrayList<>();

        Pattern extractorPattern = Pattern.compile("^.*<td><a class=\"(.*)\" href=\"viewchat.php\\?IDS=.*&amp;id=(\\d*)&amp;page=in\"><b>.* : (.*)</b> : <i>(.*)</i></a></td>.*$");

        String noReturnString = responseString.replaceAll("\r", "").replaceAll("\n", "@##@@##@");

        String[] exploded = explode(noReturnString, "@##@@##@");

        for (String chunk : exploded) {

            Log.d(Const.LOG_TAG, "Tested chunk = " + chunk);
            Matcher m = extractorPattern.matcher(chunk);
            if (m.matches()) {

                Mail mail = new Mail();

                mail.read = !m.group(1).trim().equals("messagenonlu");
                mail.id = Integer.parseInt(m.group(2).trim());
                mail.author = m.group(3).trim();
                mail.title = m.group(4).trim();

                mailList.add(mail);

            }
        }

        MailAdapter mailAdapter = new MailAdapter(this, mailList);
        list.setAdapter(mailAdapter);

        // Prepare title
        int nbUnread = 0;
        int nbTotal = mailList.size();

        for (Mail mail : mailList) {
            if (!mail.read) {
                nbUnread++;
            }
        }

        String unread = "";
        if (nbUnread > 0) {
            unread = "(" + nbUnread + ") ";
        }

        setTitle(unread + "Inbox " + nbTotal + " messages");

    }

    public static String[] explode(String values, String separator) {
        return values.split(separator, -1);
    }

}
