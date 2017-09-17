package com.zahergoesapp.travelappilka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class SLTrafikJsonAcivity extends AppCompatActivity {
    TextView SLinformation;
    String JsonUrl ="http://api.sl.se/api2/trafficsituation.JSON?key=Enter SL key";
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sltrafik_json_acivity);

        SLinformation = (TextView) findViewById(R.id.SLjsonRequest);
        requestQueue = Volley.newRequestQueue(this);


        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, JsonUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject ResponseData = response.getJSONObject("ResponseData");
                    JSONArray TrafficTypes = ResponseData.getJSONArray("TrafficTypes");

                    for (int i =0 ; i< TrafficTypes.length(); i++){
                        JSONArray Events =  TrafficTypes.getJSONObject(i).getJSONArray("Events");
                        String Name = TrafficTypes.getJSONObject(i).getString("Name");

                        for(int j=0; j< Events.length(); j++) {
                            String Message = Events.getJSONObject(j).getString("Message");

                            SLinformation.append("\n"+Name+"\n"+" "+ "\n"+ Message+ "\n");
                            System.out.println(Message);
                        }
                    }



                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }


        });
        requestQueue.add(jsonObjectRequest);
    }
}










