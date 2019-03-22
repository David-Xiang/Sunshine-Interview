package com.example.android.sunshineinterview.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.*;

public class NetworkUtils {
    final static private String TAG = "NetworkUtils";

    final static private String BASE_URL = "http://10.3.104.218";

    //final static private String PARAM_QUERY = "query";

    public static URL buildUrl(String SearchQuery) {
        /*Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("", SearchQuery)
                .build();*/

        String urlString = BASE_URL + SearchQuery;
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.i(TAG, "Trying to connect to URL: " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static JsonElement getJsonReponse(URL url) throws IOException {
        String jsonString = getResponseFromHttpUrl(url);
        Log.i(TAG, jsonString);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(jsonString);
    }
}
