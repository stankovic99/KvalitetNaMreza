package com.example.kvalitetnamreza;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    private static final String BASE_URL_GET = "http://10.0.2.2:5000/getjobs/emulator";
    private static final String BASE_URL_POST = "http://10.0.2.2:5000/postresults";

    public static String getInfo(){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JSONString = null;

        try{
            Uri builtURI = Uri.parse(BASE_URL_GET).buildUpon().build();
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if (builder.length() == 0) {
                return null;
            }

            JSONString = builder.toString();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader!= null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return JSONString;
    }

    public static String putInfo(String json){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JSONString = null;
        try{
            Uri builtURI = Uri.parse(BASE_URL_POST).buildUpon().build();
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            String jsonInputString = "{\"result\":\""+json+"\"}";

            try(OutputStream os = urlConnection.getOutputStream()){
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = urlConnection.getResponseCode();

            System.out.println("Response code:"+code);//200

            try(BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"))){
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("RESPONE",response.toString());//"PING"
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }

        }
        return null;
    }
}
