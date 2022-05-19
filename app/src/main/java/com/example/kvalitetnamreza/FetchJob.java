package com.example.kvalitetnamreza;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchJob extends AsyncTask<String, Void, String> {

    private WeakReference<TextView> mJobText;

    FetchJob(TextView jobText){
        this.mJobText = new WeakReference<>(jobText);
    }

    @Override
    protected String doInBackground(String... strings) {
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
            NetworkUtils.putInfo(job);
        }catch (Exception e){
            e.printStackTrace();
        }
        return job;
    }

    @Override
    protected void onPostExecute(String job) {
        super.onPostExecute(job);
        try{
            if(job != null){
                mJobText.get().setText(job);
            }else{
                mJobText.get().setText(R.string.no_results);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
