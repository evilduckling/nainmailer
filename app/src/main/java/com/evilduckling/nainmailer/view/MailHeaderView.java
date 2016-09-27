package com.evilduckling.nainmailer.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evilduckling.nainmailer.R;

public class MailHeaderView extends LinearLayout {

    private View icoUnread;
    private View icoRead;
    private TextView author;
    private TextView title;

    public MailHeaderView(Context context) {
        super(context);
        initializeViews(context, null);
    }

    public MailHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    public MailHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context, attrs);
    }

    private void initializeViews(Context context, AttributeSet attrs) {
        inflate(context, R.layout.view_mail_header, this);

        icoRead = findViewById(R.id.ico_read);
        icoUnread = findViewById(R.id.ico_unread);
        author = (TextView) findViewById(R.id.author);
        title = (TextView) findViewById(R.id.title);

    }

    public void setRead(boolean read) {
        if (read) {
            icoRead.setVisibility(View.VISIBLE);
            icoUnread.setVisibility(View.GONE);
            title.setTextColor(ContextCompat.getColor(getContext(), R.color.read));
            author.setTextColor(ContextCompat.getColor(getContext(), R.color.read));
        } else {
            icoRead.setVisibility(View.GONE);
            icoUnread.setVisibility(View.VISIBLE);
            title.setTextColor(ContextCompat.getColor(getContext(), R.color.unread));
            author.setTextColor(ContextCompat.getColor(getContext(), R.color.unread));
        }
    }

    public void setTitle(String t) {
        title.setText(t);
    }

    public void setAuthor(String a) {
        author.setText(a);
    }

}