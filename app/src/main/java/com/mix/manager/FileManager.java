package com.mix.manager;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mix.constant.DataType;
import com.mix.domain.Feed;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


public class FileManager {
    private static volatile FileManager instance = null;

    private FileManager() {
    }

    public static FileManager getInstance() {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }


    public void saveToFile(Context context, Feed feed, DataType dataType) {

        if (dataType.equals(DataType.RSS)) {
            String json = new Gson().toJson(feed);
            writeXmlToSDFile(json, feed.getTitle() + ".xml");
        } else if (dataType.equals(DataType.JSON)) {
            writeToSDFile(new Gson().toJson(feed), feed.getTitle() + ".json");
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
        }
        return file;
    }

    private void writeToSDFile(String contentData, String fileName) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/MIX");
        dir.mkdirs();
        File file = new File(dir, fileName);

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(contentData);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void writeXmlToSDFile(String contentData, String fileName) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/MIX");
        dir.mkdirs();
        File file = new File(dir, fileName);

        FileOutputStream f = null;
        try {
            f = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XmlSerializer serializer = Xml.newSerializer();

        try {
            serializer.setOutput(f, "UTF-8");

            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "root");
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int j = 0; j < 3; j++) {
            try {
                serializer.startTag(null, "record");
                serializer.text(contentData);
                serializer.endTag(null, "record");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            serializer.endDocument();
            serializer.flush();

            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
