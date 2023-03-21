package com.example.androidgilla42b;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

/**
 * Classe JSONParser contenant la méthode makeHttpRequest()
 * permettant de construire et envoyer la requête http
 * avec l'url, la méthode POST ou GET et les paramètres fournis
 * et retourne un objet JSON contenant la réponse du serveur.
 */

public class JSONParser {
    String charset = "UTF-8";
    HttpURLConnection conn;
    DataOutputStream wr;
    StringBuilder result;
    URL urlObj;
    JSONObject jObj = null;
    StringBuilder urlParams;
    StringBuilder bodyParams;
    String paramsString;

    protected static String convertBody(String str) {
        // A faire : les caractères accentués doivent être convertis en ISO_8859_1.
        // Exemple : é => Ã© (comme fait dans Postman).
        try {
            byte[] buffer = StandardCharsets.ISO_8859_1.encode(str).array();
            String strDecode = new String(buffer);
            return strDecode;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public JSONObject makeHttpRequest(String url, String apiKey, String method, String urlParam,
                                      HashMap<String, String> hmUrlParams, HashMap<String, String> hmBodyParams) {

        urlParams = new StringBuilder();
        int i = 0;
        for (String key : hmUrlParams.keySet()) {
            try {
                if (i != 0){
                    urlParams.append("&");
                }
                urlParams.append(key).append("=")
                        .append(URLEncoder.encode(hmUrlParams.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }

        bodyParams = new StringBuilder();
        int j = 0;
        for (String key : hmBodyParams.keySet()) {
            if (j == 0){
                bodyParams.append("{\"");
            } else {
                bodyParams.append(",\"");
            }
            bodyParams.append(key).append("\": \"").append(hmBodyParams.get(key)).append("\"");
            // bodyParams.append(key).append("\": \"").append(convertBody(hmBodyParams.get(key))).append("\"");
            j++;
        }
        if (bodyParams.length() != 0) {
            bodyParams.append("}");
            paramsString = bodyParams.toString();
        }

        if (method.equals("POST")) {
            // préparation et envoi de la requête http en POST :
            try {
                urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod(method);
                conn.addRequestProperty("Content-Type", "application/json");
                conn.addRequestProperty("X-Joomla-Token", apiKey);
                conn.addRequestProperty("Accept", "*/*");
                // conn.setRequestProperty("Accept-Charset", charset);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.connect();
                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(method.equals("GET")){
            // préparation et envoi de la requête http en GET :
            if (!Objects.isNull(urlParam)) {
                url += "/" + urlParam;
            } else if (urlParams.length() != 0) {
                url += "?" + urlParams.toString();
            }
            try {
                urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(false);
                conn.setRequestMethod(method);
                conn.addRequestProperty("X-Joomla-Token", apiKey);
                conn.addRequestProperty("Accept", "*/*");
                // conn.setRequestProperty("Accept-Charset", charset);
                conn.setConnectTimeout(15000);
                conn.connect();


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(method.equals("PATCH")){
            // préparation et envoi de la requête http en PATCH :
            if (!Objects.isNull(urlParam)) {
                url += "/" + urlParam;
            }
            try {
                urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod(method);
                conn.addRequestProperty("Content-Type", "application/json");
                conn.addRequestProperty("X-Joomla-Token", apiKey);
                conn.addRequestProperty("Accept", "*/*");
                // conn.setRequestProperty("Accept-Charset", charset);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.connect();
                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(method.equals("DELETE")){
            // préparation et envoi de la requête http en PATCH :
            if (!Objects.isNull(urlParam)) {
                url += "/" + urlParam;
            }
            try {
                urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(false);
                conn.setRequestMethod(method);
                conn.addRequestProperty("X-Joomla-Token", apiKey);
                conn.addRequestProperty("Accept", "*/*");
                // conn.setRequestProperty("Accept-Charset", charset);
                conn.setConnectTimeout(15000);
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // réception de la réponse du serveur :
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("JSON Parser", "result: " + result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.disconnect();

        // parse la chaîne de caractères reçue en un objet JSON :
        try {
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // retourne l'objet JSON :
        return jObj;
    }
}