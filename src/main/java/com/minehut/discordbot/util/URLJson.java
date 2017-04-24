package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Created by MatrixTunnel on 2/9/2017.
 */
public class URLJson {

    private StringBuilder json = new StringBuilder();

    public URLJson(String url) throws JSONException, IOException {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Core.log.error("Error connecting to url: \"" + url + "\"\n", e);
            return;
        }

        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream(), Charset.forName("UTF-8")));
        while ((line = in.readLine()) != null) {
            json.append(line).append("\n");
        }
        in.close();
    }

    public JSONObject getJsonObject() throws JSONException, IOException {
        return new JSONObject(json.toString());
    }

    public JSONArray getJsonArray() throws JSONException, IOException {
        return new JSONArray(json.toString());
    }

}
