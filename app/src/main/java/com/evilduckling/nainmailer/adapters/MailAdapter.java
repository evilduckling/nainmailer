package com.evilduckling.nainmailer.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.evilduckling.nainmailer.model.Mail;
import com.evilduckling.nainmailer.view.MailHeaderView;

import java.util.List;

public class MailAdapter extends BaseAdapter {

    private List<Mail> mails;
    private Activity activity;

    public MailAdapter(Activity activity, List<Mail> mails) {
        this.activity = activity;
        this.mails = mails;
    }

    @Override
    public int getCount() {
        return mails.size();
    }

    @Override
    public Object getItem(int i) {
        return mails.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mails.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Mail mail = (Mail) getItem(i);
        MailHeaderView mailHeaderView = new MailHeaderView(activity);
        mailHeaderView.setAuthor(mail.author);
        mailHeaderView.setRead(mail.read);
        mailHeaderView.setTitle(mail.title);

        return mailHeaderView;

    }
}
