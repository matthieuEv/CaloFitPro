package com.example.calofitpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    EditText editTextText = null;
    Button homeCalculate = null;
    Button graphBtn = null;
    ImageView homeImage = null;
    TextView oldCalorieValue = null;
    SharedPreferences preferences = null;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();

        homeCalculate = findViewById(R.id.homeCalculate);
        editTextText = findViewById(R.id.editTextText);
        homeImage = findViewById(R.id.homeImage);
        oldCalorieValue = findViewById(R.id.oldCalorieValue);
        graphBtn = findViewById(R.id.graphBtn);

        Animation animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.home);
        animation.setDuration(2000);
        homeImage.startAnimation(animation);

        homeCalculate.setOnClickListener(compute(editTextText));
        graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, thirdActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if(intent.getStringExtra("calories") != null){
            editTextText.setText(intent.getStringExtra("calories"));
        }

        preferences = getSharedPreferences("calories",MODE_PRIVATE);

        if(preferences.getString("calories", null) != null && preferences.getString("name", null) != null){
            oldCalorieValue.setText(preferences.getString("name", "")+", votre ancien calcul est de "+preferences.getString("calories", "")+" Kcal/j.");
        } else {
            oldCalorieValue.setText("");
        }

        if(preferences.getString("name", null) != null){
            editTextText.setText(preferences.getString("name", ""));
        }

    }

    private View.OnClickListener compute(final EditText editTextText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextText.getText().toString();
                if (name.length() > 15) {
                    Toast.makeText(MainActivity.this, "Le nom ne peut pas dépasser 15 caractères", Toast.LENGTH_SHORT).show();
                } else if (name.length() < 3) {
                    Toast.makeText(MainActivity.this, "Le nom doit contenir au moins 3 caractères", Toast.LENGTH_SHORT).show();
                } else if (!name.matches("[a-zA-ZàâäéèêëïîôöùûüçÀÂÄÉÈÊËÏÎÔÖÙÛÜÇ0-9]*")){
                    Toast.makeText(MainActivity.this, "Le nom ne peut pas contenir de caractères spéciaux", Toast.LENGTH_SHORT).show();
                } else {
                    preferences.edit().putString("name", name).apply();
                    Intent intent = new Intent(MainActivity.this, second_activity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        };
    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.beige));
        setSupportActionBar(toolbar);
    }
}