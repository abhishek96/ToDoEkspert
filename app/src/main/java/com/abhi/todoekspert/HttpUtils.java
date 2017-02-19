package com.abhi.todoekspert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.text.TextUtils;

/**
 * Pomocnicza klasa zawierająca metody odpowiedzialne za komunikację po HTTP.
 */
public class HttpUtils {

    public static String getTodos(String token) throws IOException {
        return callHttp("https://api.parse.com/1/classes/Todo", "GET",
                token, null);
    }

    /**
     * Metoda tworząca nowy obiekt po HTTP POST.
     *
     * @param taskJson
     * @param token
     * @return
     * @throws IOException
     */
    public static String postTodo(String taskJson, String token) throws IOException {
        return callHttp("https://api.parse.com/1/classes/Todo", "POST", token, taskJson);
    }


    /**
     * Metoda logowania po HTTP GET.
     *
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static String getLogin(String username, String password)
            throws IOException {
        String loginUrl = getLoginUrl(username, password);

        return callHttp(loginUrl, "GET", null, null);
    }

    /**
     * Metoda budująca url do logowania po HTTP GET.
     *
     * @param username
     * @param password
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getLoginUrl(String username, String password) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        result.append(URLEncoder.encode("username", "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(username, "UTF-8"));
        result.append("&");
        result.append(URLEncoder.encode("password", "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(password, "UTF-8"));
        return String.format("https://api.parse.com/1/login?%s", result.toString());
    }

    /**
     * Generyczna metoda zapytania HTTP.
     *
     * @param urlString url pod który należy wysłać zapytanie
     * @param method    metoda HTTP
     * @param token     session token otrzymany podczas logowania
     * @param body      cialo dla metoda POST i PUT
     * @return
     * @throws IOException
     */
    private static String callHttp(String urlString, String method, String token, String body) throws IOException {
        BufferedReader reader = null;
        try {


            URL url = new URL(urlString);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod(method);
            c.setReadTimeout(15000);
            c.setRequestProperty("X-Parse-Application-Id",
                    "EhZ7ps1nVRbYCz4d1IkLlOLUAMkuzaA6NGS89hDX");
            c.setRequestProperty("X-Parse-REST-API-Key",
                    "0cc1iqhHHEg3j8do8b6DNkjC0nmnNNMKVJ11blov");


            if (!TextUtils.isEmpty(token)) {
                c.setRequestProperty("X-Parse-Session-Token", token);
            }

            if (!"GET".equals(method)) {
                c.setRequestProperty("Content-Type", "application/json");

                c.setDoInput(true);
                c.setDoOutput(true);


                OutputStream os = c.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(body);
                writer.flush();
                writer.close();
                os.close();
            }

            c.connect();

            int code = c.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(
                        c.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(
                        c.getErrorStream()));
            }

            StringBuilder buf = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line + "\n");
            }
            return (buf.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
