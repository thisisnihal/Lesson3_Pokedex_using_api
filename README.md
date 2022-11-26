### This is Lesson 2 of CS50 Android Track
you can take this course for free - https://cs50.harvard.edu/x/2020/tracks/mobile/android/  

In this lesson We learnt about APIs and how to use [Volley library]()  
In previous lesson we hardcoded the Pokemon's *Name* and *Number* values, but in this lesson we will fetch the data from internet using APIs.  
Basically We will make a request to a **URL(in this case https://pokeapi.co/)** using Volley Library and it will return a JSON format.  
* [x] Step 1: Add Volley libaray in `build.gradle(:app)` in dependencies  
```Groovy
implementation 'com.android.volley:volley:1.1.1'
```  
And make some changes in `Pokemon.java` data class
```Java
public class Pokemon {
    private String name;
    private String url; // -> use url

    Pokemon (String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }
    public String getUrl() {
        return url;
    }
}
```

* [x] Step 2: This is how `PokedexAdapter.java` should look like    
```Java
public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> {

    public static class PokedexViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout containerView;
        private TextView textView;

        PokedexViewHolder(View view) {
            super(view);

            containerView = view.findViewById(R.id.pokedex_row);
            textView = view.findViewById(R.id.pokedex_row_text_view);

            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pokemon current = (Pokemon) containerView.getTag();
                    Intent intent = new Intent(v.getContext(), PokemonActivity.class);
                    intent.putExtra("name", current.getName());    
                    intent.putExtra("url", current.getUrl());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
    // TO EXPLAIN : )
    private List<Pokemon> pokemon = new ArrayList<>();
    private RequestQueue requestQueue;

    PokedexAdapter (Context context) {
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
    }
    // Load Json Data from api
    public void loadPokemon() {
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        pokemon.add(new Pokemon(
                                name.substring(0, 1).toUpperCase() + name.substring(1),
                                result.getString("url")
                        ));
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon list error");
            }
        });
        requestQueue.add(request);
    }

    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokedex_row, parent, false);
        return new PokedexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
        Pokemon current = pokemon.get(position);
        holder.textView.setText(current.getName());
        holder.containerView.setTag(current);
    }

    @Override
    public int getItemCount() {
        return pokemon.size();
    }
}
```  
* [x] Step 3: Add Internet permission in `AndroidManifest.xml` file  
```xml
<manifest ...
    ...
    ...
<uses-permission android:name="android.permission.INTERNET"/>
</manifest>
```  
* [x] Step 4: Add few more TextViews in `activity_pokemon.xml` layout  
```Xml
<LinearLayout
   xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".PokemonActivity">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/pokemon_name"
        android:textAlignment="center"
        android:textSize="20sp"
        android:paddingTop="10dp" />
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/pokemon_number"
        android:textAlignment="center"
        android:textSize="20sp"
        android:paddingTop="5dp"/>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/pokemon_type1"
        android:textAlignment="center"
        android:textSize="20sp"
        android:paddingTop="5dp"/>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/pokemon_type2"
        android:textAlignment="center"
        android:textSize="20sp"
        android:paddingTop="5dp"/>
</LinearLayout>
``` 
* [x] Step 5: Make request for different attributes like Name, Number, Types `PokemonActivty.java`  
```java
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
```


***Note: I am still Learning so what I explained above take it as a pinch of salt. There is gaurantee/warranty of information given above : )***

* [ ] 
* [ ] Add apk file 
* [ ] Add screenshot of apk 
