package com.mix.domain;
import com.mix.constant.DataType;

import java.io.Serializable;
import java.util.Date;


public class Feed implements Serializable, Comparable<Feed> {
    private int id;
    private String title;
    private String link;
    private String imageThumbnail;
    private String description;
    private DataType dataType;
    private Date date;

    public Feed(String title, String imageThumbnail, String link,
                String description, DataType dataType, Date date) {
        this.title = title;
        this.imageThumbnail = imageThumbnail;
        this.link = link;
        this.description = description;
        this.dataType = dataType;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Date getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(Feed o) {

        if (o == null) {
            return 0;
        }
         if (date.getTime() > o.date.getTime()){
            return -1;
        }

        if (date.getTime() < o.date.getTime()){
            return 1;
        } else {
            return 0;
        }
    }
}
