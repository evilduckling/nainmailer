package com.evilduckling.nainmailer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.evilduckling.nainmailer.R;
import com.evilduckling.nainmailer.adapters.MailAdapter;
import com.evilduckling.nainmailer.helper.Misc;
import com.evilduckling.nainmailer.interfaces.Callback;
import com.evilduckling.nainmailer.interfaces.Const;
import com.evilduckling.nainmailer.model.Mail;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class InboxActivity extends AppCompatActivity {

    private ListView list;
    private MailAdapter mailAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        list = (ListView) findViewById(R.id.inbox_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFullInbox();
            }
        });
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
        client.get(this, Const.BASE_URL + "jeu/chatbox.php?IDS=" + getIdentifier() + "&page=in", new TextHttpResponseHandler() {
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
        String decodedString = Misc.isoToUtf8(responseString);
        Pattern extractorPattern = Pattern.compile("^.*<td><a class=\"(.*)\" href=\"viewchat.php\\?IDS=.*&amp;id=(\\d*)&amp;page=in\"><b>.* : (.*)</b> : <i>(.*)</i></a></td>.*$");

        // Remove \r and split on \n
        String[] chunks = Misc.explode(decodedString.replaceAll("\r", ""), "\n");

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

        mailAdapter = new MailAdapter(this, mailList);
        list.setAdapter(mailAdapter);

        generateTitle();
        swipeRefreshLayout.setRefreshing(false);

    }

    public void generateTitle() {

        // Prepare title
        int nbUnread = 0;
        int nbTotal = mailAdapter.getCount();

        for (Mail mail : mailAdapter.getMails()) {
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

    public void getMailContent(final Mail mail, final Callback callback) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, Const.BASE_URL + "jeu/viewchat.php?IDS=" + getIdentifier() + "&page=in&id=" + mail.id, new TextHttpResponseHandler() {
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
                parseMail(responseString, mail);
                callback.afterRequest();
            }
        });

    }

    private void parseMail(String response, Mail mail) {
        String[] chunks = Misc.explode(response, "<hr>");
        if (chunks.length > 1) {
            mail.content = Misc.isoToUtf8(chunks[1]);
        }
    }

    public void actionMail(int mailId, String action) {

        String url;
        switch (action) {
            case "archive":
                url = Const.BASE_URL + "jeu/chataction.php?action=arch&sens=IN&IDS=" + getIdentifier() + "&page=in&mailsel=" + mailId;
                break;
            case "delete":
                url = Const.BASE_URL + "jeu/chataction.php?action=supp&IDS=" + getIdentifier() + "&page=in&mailsel=" + mailId;
                break;
            default:
                return;
        }

        mailAdapter.removeMail(mailId);
        generateTitle();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new TextHttpResponseHandler() {
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
            }
        });

    }

    public void closeAll() {
        for (Mail mail : mailAdapter.getMails()) {
            mail.opened = false;
        }
        mailAdapter.notifyDataSetChanged();
    }

}
