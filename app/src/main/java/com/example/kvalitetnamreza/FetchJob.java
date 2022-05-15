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
        return NetworkUtils.getInfo();
    }

    @Override
    protected void onPostExecute(String jsonItem) {
        super.onPostExecute(jsonItem);
        try{
            Log.i("onPostExecute",jsonItem);
            int i = 0;
            JSONArray jsonArray = new JSONArray(jsonItem);
            String job = null;
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String name = jsonObject.getString("jobType");
            if(name != null){
                mJobText.get().setText(name);
            }else{
                mJobText.get().setText(R.string.no_results);
            }
            Log.i("onPostExecuteJSON",name);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
