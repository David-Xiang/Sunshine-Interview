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

    final static private String BASE_URL = "https://192.168.1.5";

    final static private String PARAM_QUERY = "query";

    public static URL buildUrl(String SearchQuery) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, SearchQuery)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
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

    public static JsonElement getJsonReponse(URL url) {
        String jsonString;
        try {
            jsonString = getResponseFromHttpUrl(url);
        } catch (IOException e){
            Log.e(TAG, "IOException encountered!");
            return null;
        }
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(jsonString);
    }
}
