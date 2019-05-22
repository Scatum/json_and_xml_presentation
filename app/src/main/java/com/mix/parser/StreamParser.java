package com.mix.parser;

import android.util.Log;
import android.util.Xml;

import com.mix.constant.ConstantValues;
import com.mix.constant.DataType;
import com.mix.domain.Feed;
import com.mix.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StreamParser {
    private static volatile StreamParser instance = null;

    private StreamParser() {
    }

    public static StreamParser getInstance() {
        if (instance == null) {
            synchronized (StreamParser.class) {
                if (instance == null) {
                    instance = new StreamParser();
                }
            }
        }
        return instance;
    }


    public List<Feed> parseRssFeed(InputStream inputStream) {
        String title = null;
        String link = null;
        String thumbnail = null;
        String description = null;
        String pubDate = null;
        boolean isItem = false;
        List<Feed> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }
                if (name.equalsIgnoreCase("media:thumbnail")) {
                    result = xmlPullParser.getAttributeValue(null, "url");
                    thumbnail = result;
                }

                //Log.e("pub_date", name + " :: " + result);

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("pubDate")) {

                    pubDate = result;
                    Log.e("pub_date", name + " ___ " + pubDate);

                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null && description != null) {
                    if (isItem) {
                        Feed item = new Feed(title, thumbnail, link, description,
                                DataType.RSS, DateUtil.convertToDate(pubDate, ConstantValues.RSS_DATE_PATTERN));
                        items.add(item);
                    } else {
                        // just ignore
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return items;
    }


    public List<Feed> parseJsonFeed(InputStream inputStream) {
        String title = null;
        String link = null;
        String thumbnail = null;
        String description = null;
        String publishedAt = null;

        List<Feed> items = new ArrayList<>();

        JSONObject jsonObject = getJsonObject(inputStream);
        if (jsonObject != null) {
            JSONArray arr = null;
            try {
                arr = jsonObject.getJSONArray("articles");
            } catch (JSONException e) {
               throw new RuntimeException(e);
            }
            for (int i = 0; i < arr.length(); i++) {
                try {
                    title = arr.getJSONObject(i).getString("title");
                    link = arr.getJSONObject(i).getString("url");
                    thumbnail = arr.getJSONObject(i).getString("urlToImage");
                    description = arr.getJSONObject(i).getString("description");
                    publishedAt = arr.getJSONObject(i).getString("publishedAt");
                    Feed item = new Feed( title, thumbnail, link, description,
                            DataType.JSON, DateUtil.convertToDate(publishedAt, ConstantValues.JSON_DATE_PATTERN));
                    items.add(item);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return items;

    }


    private JSONObject getJsonObject(InputStream inputStream) {

        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

            //returns the json object
            return jsonObject;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //if something went wrong, return null
        return null;
    }
}
