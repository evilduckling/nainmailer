package com.evilduckling.nainmailer.model;

public class Mail {

    public int id;
    public String title;
    public String author;
    public boolean read;
    public String content;

    // internal data
    public boolean opened = false;

}