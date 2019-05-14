package com.example.android.sunshineinterview.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

import com.google.gson.*;

public class NetworkUtils {
    private final static String TAG = "NetworkUtils";
    private final static String BASE_URL = "http://192.168.43.226";
    private final static int UP_TIMEOUT = 10*1000;
    private final static String UP_CHARSET = "utf-8"; //设置编码
    private final static String UP_PREFIX = "--" , LINE_END = "\r\n";
    private final static String UP_CONTENT_TYPE = "multipart/form-data";   //内容类型

    public static URL buildUrl(String SearchQuery) {
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

    public static boolean uploadFile(File file, String name, URL url){
        if (file == null) {
            Log.v(TAG, "uploadImg(): file does not exist");
            return false;
        }
        String BOUNDARY =  UUID.randomUUID().toString();  //边界标识，随机生成
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(UP_TIMEOUT);
            urlConnection.setConnectTimeout(UP_TIMEOUT);
            urlConnection.setDoInput(true); //允许输入流
            urlConnection.setDoOutput(true); //允许输出流
            urlConnection.setUseCaches(false); //不允许使用缓存
            urlConnection.setRequestMethod("POST"); //请求方式
            urlConnection.setRequestProperty("Charset", UP_CHARSET);
            urlConnection.setRequestProperty("connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", UP_CONTENT_TYPE + ";boundary=" + BOUNDARY);
            urlConnection.connect();

            DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
            StringBuffer sb = new StringBuffer();
            sb.append(UP_PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);

            /**
             * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+file.getName()+"\""+LINE_END);
            sb.append("Content-Type: application/octet-stream; charset="+UP_CHARSET+LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());
            InputStream is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            while((len=is.read(bytes))!=-1) {
                dos.write(bytes, 0, len);
            }
            is.close();
            dos.write(LINE_END.getBytes());
            byte[] end_data = (UP_PREFIX + BOUNDARY + UP_PREFIX+LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            // 获取响应码 200=成功, 当响应成功，获取响应的流
            int res = urlConnection.getResponseCode();
            if(res == 200) {
                InputStream input =  urlConnection.getInputStream();
                StringBuffer sb1= new StringBuffer();
                int ss ;
                while((ss=input.read())!=-1){
                    sb1.append((char)ss);
                }
                String result = sb1.toString();
                Log.i(TAG, "response result: " + result);
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean uploadHashCode(String interviewID, int videoID, String hashCode){
        // TBD
        URL url = buildUrl("/addhash?interviewid=" + interviewID + "&index=" + videoID + "&hash=" + hashCode);
        try {
            getJsonReponse(url);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return true;
    }
}
