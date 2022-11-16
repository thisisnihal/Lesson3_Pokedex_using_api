### This is Lesson 2 of CS50 Android Track
you can take this course for free - https://cs50.harvard.edu/x/2020/tracks/mobile/android/  

In this lesson we learnt about recycler views  

* [x] Step 1: Add dependency for recycler view in app level gradle file  `implementation "androidx.recyclerview:recyclerview:1.2.1"` see below in line 40.
```Groovy
android {
    compileSdk 31

    defaultConfig {
        applicationId "com.example.pokedex"
        minSdk 21
        targetSdk 31
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_7
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'com.google.android.material:material:1.6.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    implementation "androidx.recyclerview:recyclerview:1.2.1"

    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
```
* [x] Step 2: Add a recycler **view** in `activity_main.xml`  
```xml
    <androidx.recyclerview.widget.RecyclerView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/recycler_view" />
```  
* [x] Step 3: Let's create a layout file named `pokedex_row.xml`  
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:id="@+id/pokedex_row"
    android:padding="8dp"
    android:clickable="true"
    android:focusable="true"
    android:foreground="?android:attr/selectableItemBackground">
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/pokedex_row_text_view"
        android:textSize="20sp"/>
</LinearLayout>
```  
* [x] Step 4: Create a data class `Pokemon.java`  
```java
public class Pokemon {
    private String name;
    private int number;

    Pokemon (String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }
    public int getNumber() {
        return number;
    }
}
```  
* [ ] Step 5: To explain
We only used getters here cause we need just a read-only object  
Recycler View has a class attached to it called an [`Adapter`](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter).  
Adapter defines what data is going to be displayed in a recycler view. 

Now lets create a `PokedexAdapter.java` class, this class is going to represent all of the data inside of our recycler view.  
```java
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
                    intent.putExtra("name", current.getName());     // it just a key value pair
                    intent.putExtra("number", current.getNumber());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private List<Pokemon> pokemon = Arrays.asList(
            new Pokemon("Bulbasaur", 1),
            new Pokemon("Pikachu", 2),
            new Pokemon("Lavnasaur", 3)
            );

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
Now add this code. in `MainActivity.java` 
```java
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new PokedexAdapter();
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }
```
Now Try to run the app.
Lets create another activity called `PokemonActivity.java` and its corresponding layout file `activity_pokemon.xml`  
This is how our `` should look like:  
```java
public class PokemonActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView numberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        // important
        String name  = getIntent().getStringExtra("name");
        int number = getIntent().getIntExtra("number", 0);

        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);

        nameTextView.setText(name);
//      numberTextView.setText(Integer.toString(number));
        numberTextView.setText(String.format("#%03d", number));
    }
}
``` 
and our `` file  
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".PokemonActivity">
<!-- above [tools:context=".PokemonActivity"]  basically tells us that this layout is associated with PokemonActivity.java  -->
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
</LinearLayout>
```  
Make sure to add this line in `AndroidManifest.xml`:  
```xml
<activity
        android:name=".PokemonActivity"
        android:exported="false" />
```

***Note: I am still Learning so what I explained above take it as a pinch of salt. There is gaurantee/warranty of information given above : )***

* [ ] To explain from Step 5
* [ ] Add apk file 
* [ ] Add screenshot of apk 
