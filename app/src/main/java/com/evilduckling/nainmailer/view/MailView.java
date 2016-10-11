package com.evilduckling.nainmailer.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evilduckling.nainmailer.R;
import com.evilduckling.nainmailer.activities.InboxActivity;
import com.evilduckling.nainmailer.interfaces.Callback;
import com.evilduckling.nainmailer.model.Mail;

public class MailView extends LinearLayout {

    /**
     * Calling activity.
     */
    private InboxActivity inboxActivity;

    /**
     * Layout components.
     */
    // Header
    private View icoUnread;
    private View icoRead;
    private TextView author;
    private TextView title;

    // Action bar
    private View actionBar;
    private View buttonTrash;
    private View buttonArchive;

    // mail content
    private WebView content;

    /**
     * Internal data.
     */
    private Mail mail;

    public MailView(Context context) {
        super(context);
        initializeViews(context, null);
    }

    public MailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    public MailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context, attrs);
    }

    private void initializeViews(Context context, AttributeSet attrs) {
        inflate(context, R.layout.view_mail, this);

        icoRead = findViewById(R.id.ico_read);
        icoUnread = findViewById(R.id.ico_unread);
        author = (TextView) findViewById(R.id.author);
        title = (TextView) findViewById(R.id.title);

        actionBar = findViewById(R.id.action_bar);
        buttonTrash = findViewById(R.id.button_trash);
        buttonArchive = findViewById(R.id.button_archive);

        content = (WebView) findViewById(R.id.mail_content);

        inboxActivity = (InboxActivity) context;
    }

    private void setRead(boolean read) {
        if (read) {
            icoRead.setVisibility(View.VISIBLE);
            icoUnread.setVisibility(View.GONE);
            title.setTextColor(ContextCompat.getColor(getContext(), R.color.read));
            author.setTextColor(ContextCompat.getColor(getContext(), R.color.read));
            title.setTypeface(null, Typeface.NORMAL);
            author.setTypeface(null, Typeface.NORMAL);
        } else {
            icoRead.setVisibility(View.GONE);
            icoUnread.setVisibility(View.VISIBLE);
            title.setTextColor(ContextCompat.getColor(getContext(), R.color.unread));
            author.setTextColor(ContextCompat.getColor(getContext(), R.color.unread));
            title.setTypeface(null, Typeface.BOLD);
            author.setTypeface(null, Typeface.BOLD);
        }
    }

    private void setTitle(String t) {
        title.setText(t);
    }

    private void setAuthor(String a) {
        author.setText(a);
    }

    private void setContent(String c) {
        if (c == null) {
            actionBar.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
        } else {
            setRead(true);
            actionBar.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
            // content.loadData(c, "text/html; charset=ISO-8859-1", "ISO-8859-1");
            content.loadData(htmlWrapper(c), "text/html; charset=utf-8", "utf-8");
        }

    }

    private static String htmlWrapper(String s) {
        return "<html>" +
            "<body>" +
            "<style>" +
            "   body { margin:0; background-color:#F5F5F5;} " +
            "   .paslignereply {color:#000000;} " +
            "   .lignereply {color:#808080;}" +
            "</style>" +
            s +
            "</body>" +
            "</html>";
    }

    public void setMail(Mail m) {

        mail = m;

        setTitle(mail.title);
        setAuthor(mail.author);
        setRead(mail.read);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mail.content == null) {
                    inboxActivity.getMailContent(mail, new Callback() {
                        @Override
                        public void afterRequest() {
                            setContent(mail.content);
                        }
                    });
                } else {
                    if (content.getVisibility() == View.VISIBLE) {
                        content.setVisibility(View.GONE);
                        actionBar.setVisibility(View.GONE);
                    } else {
                        content.setVisibility(View.VISIBLE);
                        actionBar.setVisibility(View.VISIBLE);
                    }
                }
            }

        });

        buttonTrash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                inboxActivity.actionMail(mail.id, "delete");
            }
        });

        buttonArchive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                inboxActivity.actionMail(mail.id, "archive");
            }
        });

    }

}