package com.minehut.discordbot.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Created by MatrixTunnel on 2/9/2017.
 */
public class URLJson {

    private StringBuilder json = new StringBuilder();

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public URLJson(String url) throws JSONException, IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        try (InputStream is = connection.getInputStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json.append(jsonText);
            is.close();
            rd.close();
        }
    }

    public JSONObject getJsonObject() throws JSONException, IOException {
        return new JSONObject(json.toString());
    }

    public JSONArray getJsonArray() throws JSONException, IOException {
        return new JSONArray(json.toString());
    }

}
