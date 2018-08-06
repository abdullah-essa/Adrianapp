package com.hizyaz.adrianapp.adapters;

public class ListItem {
    public String id;
    public String Title;
    public String sent;
    public String deliver_date;
    public String link;

    //    public String file_name;
    public ListItem(String id, String sent, String title, String deliver_date, String link) {
        this.id = id;
        this.sent = sent;
        this.Title = title;
        this.deliver_date = deliver_date;
        this.link = link;
    }
}