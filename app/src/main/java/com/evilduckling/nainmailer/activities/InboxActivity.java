package com.evilduckling.nainmailer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.evilduckling.nainmailer.R;
import com.evilduckling.nainmailer.adapters.MailAdapter;
import com.evilduckling.nainmailer.interfaces.Const;
import com.evilduckling.nainmailer.model.Mail;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class InboxActivity extends AppCompatActivity {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        list = (ListView) findViewById(R.id.inbox_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFullInbox();
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
                Toast.makeText(InboxActivity.this, "Sorry something went wrong", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(InboxActivity.this, LoginActivity.class);
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
        String decodedString;

        try {
            decodedString = new String(responseString.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            decodedString = responseString;
            Log.e(Const.LOG_TAG, "Cannot convert to utf-8");
        }

        Pattern extractorPattern = Pattern.compile("^.*<td><a class=\"(.*)\" href=\"viewchat.php\\?IDS=.*&amp;id=(\\d*)&amp;page=in\"><b>.* : (.*)</b> : <i>(.*)</i></a></td>.*$");

        // Remove \r and split on \n
        String[] chunks = explode(decodedString.replaceAll("\r", ""), "\n");

        for (String line : chunks) {

            Log.d(Const.LOG_TAG, "Tested line = " + line);
            Matcher m = extractorPattern.matcher(line);
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
