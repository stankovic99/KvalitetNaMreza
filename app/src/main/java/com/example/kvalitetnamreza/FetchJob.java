package com.example.kvalitetnamreza;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FetchJob extends AsyncTask<String, Void, String> {

    @Override
    public String doInBackground(String... strings) {
        String jsonItem = NetworkUtils.getInfo();
        String job = null;
        try{
            int i = 0;
            JSONArray jsonArray = new JSONArray(jsonItem);
            while (i<jsonArray.length() && job == null){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                try {
                    job = jsonObject.getString("jobType");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                i++;
            }
            if(strings[0]=="Post"){
                NetworkUtils.putInfo(job);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return job;
    }
}
