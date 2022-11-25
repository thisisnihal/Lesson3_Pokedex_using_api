package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

public class PokemonActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView numberTextView;
    private String url;
    private RequestQueue requestQueue;
    private TextView type1TextView;
    private TextView type2TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        // important
        url = getIntent().getStringExtra("url");
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        load();


    }
    public void load() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    nameTextView.setText(response.getString("name"));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));
                  JSONArray typeEntries = response.getJSONArray("types");
                  for (int i = 0; i < typeEntries.length(); i++) {
                      JSONObject typeEntry = typeEntries.getJSONObject(i);
                      int slot = typeEntry.getInt("slot");
                      String type = typeEntry.getJSONObject("type").getString("name");

                      if (slot == 1) {
                          type1TextView.setText(type);
                      }
                      else if (slot == 2) {
                          type2TextView.setText(type);
                      }
                  }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon Value error");
            }
        });
        requestQueue.add(request);
    }
}