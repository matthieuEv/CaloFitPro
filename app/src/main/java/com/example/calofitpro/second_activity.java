package com.example.calofitpro;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import org.json.JSONException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class second_activity extends AppCompatActivity {

    Spinner spinner_gender = null;
    EditText edittext_date_of_birth = null;
    EditText edittext_weight = null;
    EditText edittext_height = null;
    RadioButton radiobutton_meter = null;
    RadioButton radiobutton_centimeter = null;
    Spinner spinner_physical_activity_level = null;
    CheckBox checkbox_display_raw_result = null;
    TextView textview_calorie_result = null;
    Button button_calculate_calories = null;
    Button button_reset_all_fields = null;
    String name = null;
    Boolean isCalculated = false;
    double calories;
    SharedPreferences preference = null;
    Intent intent = null;
    boolean isUserChange = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        setupToolbar();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Log.d("debug","the name is: "+name);

        spinner_gender = findViewById(R.id.spinner_gender);
        addItemOnSpinnerGender(spinner_gender);

        spinner_physical_activity_level = findViewById(R.id.spinner_physical_activity_level);
        addItemOnSpinnerActivityLevel(spinner_physical_activity_level);

        edittext_date_of_birth = findViewById(R.id.edittext_date_of_birth);
        edittext_date_of_birth.addTextChangedListener(verifyDate(edittext_date_of_birth));

        edittext_weight = findViewById(R.id.edittext_weight);

        edittext_height = findViewById(R.id.edittext_height);

        textview_calorie_result = findViewById(R.id.textview_calorie_result);
        textview_calorie_result.setText(Html.fromHtml(getString(R.string.calorie_result_idle), Html.FROM_HTML_MODE_LEGACY));

        radiobutton_meter = findViewById(R.id.radiobutton_meter);
        radiobutton_centimeter = findViewById(R.id.radiobutton_centimeter);
        radiobutton_meter.setChecked(true);
        radiobutton_centimeter.setChecked(false);

        checkbox_display_raw_result = findViewById(R.id.checkbox_display_raw_result);

        button_reset_all_fields = findViewById(R.id.button_reset_all_fields);
        button_reset_all_fields.setOnClickListener(raz(button_reset_all_fields));

        button_calculate_calories = findViewById(R.id.button_calculate_calories);
        button_calculate_calories.setOnClickListener(calculateCalories(button_calculate_calories));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.mail) {
            if(isCalculated){
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // Seuls les clients de messagerie devraient gérer ceci
                intent.putExtra(Intent.EXTRA_SUBJECT, "Calories");
                intent.putExtra(Intent.EXTRA_TEXT, "Mon calcul de calories est de "+ calories + " Kcal/j.");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            } else {
                Toast.makeText(second_activity.this, "Veuillez calculer vos calories avant d'envoyer un mail", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else if(item.getItemId()==R.id.calendar) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
            return true;
        }
        else if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("calories", calories);
            startActivity(intent);
            return true;
        }

        else {
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void setupToolbar(){
        Toolbar myToolbar = findViewById(R.id.second_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myToolbar.setNavigationIcon(R.drawable.back);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.beige));
    }

    private void addItemOnSpinnerGender(Spinner spinner){
        ArrayList<String> items_genre = new ArrayList<>();
        items_genre.add("-");
        items_genre.add("Homme");
        items_genre.add("Femme");
        items_genre.add("Autre");

        ArrayAdapter<String> adapter_genre = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items_genre) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter_genre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_genre);
        adapter_genre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter_genre);
    }

    private void addItemOnSpinnerActivityLevel(Spinner spinner){
        ArrayList<String> items_activity = new ArrayList<>();
        items_activity.add("-");
        items_activity.add("Sédentaire");
        items_activity.add("Actif");
        items_activity.add("Sprotif");

        ArrayAdapter<String> adapter_activity = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items_activity) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter_activity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_activity);
        adapter_activity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter_activity);
    }

    private TextWatcher verifyDate(final EditText edittext_date_of_birth){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((s.length() == 2 || s.length() == 5) && count > before) {
                    edittext_date_of_birth.append("/");
                }
                if (s.length() > 10) {
                    edittext_date_of_birth.setText(s.subSequence(0, 10));
                    edittext_date_of_birth.setSelection(10);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Faites quelque chose avec la date choisie
        }
    }
    
    public View.OnClickListener raz(View view) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(second_activity.this)
                    .setTitle("RAZ")
                    .setMessage("Voulez-vous vraiment remettre tous les champs à vide ?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            spinner_gender.setSelection(0);
                            edittext_date_of_birth.setText("");
                            edittext_weight.setText("");
                            edittext_height.setText("");
                            radiobutton_meter.setChecked(true);
                            radiobutton_centimeter.setChecked(false);
                            spinner_physical_activity_level.setSelection(0);
                            checkbox_display_raw_result.setChecked(false);
                            textview_calorie_result.setText(Html.fromHtml(getString(R.string.calorie_result_idle), Html.FROM_HTML_MODE_LEGACY));
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
                isCalculated = false;
            }
        };
    }

    private boolean checkValues(){
        if(spinner_gender.getSelectedItem().toString().equals("-")){
            Toast.makeText(second_activity.this, "Veuillez sélectionner un genre", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(edittext_date_of_birth.getText().toString().isEmpty()){
            Toast.makeText(second_activity.this, "Veuillez sélectionner une date de naissance", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidDate(edittext_date_of_birth.getText().toString())) {
            Toast.makeText(second_activity.this, "Veuillez sélectionner une date de naissance valide", Toast.LENGTH_SHORT).show();
            return false;
        } else if(edittext_weight.getText().toString().isEmpty()){
            Toast.makeText(second_activity.this, "Veuillez sélectionner un poids", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(edittext_height.getText().toString().isEmpty()){
            Toast.makeText(second_activity.this, "Veuillez sélectionner une taille", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(spinner_physical_activity_level.getSelectedItem().toString().equals("-")){
            Toast.makeText(second_activity.this, "Veuillez sélectionner un niveau d'activité physique", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate parsedDate = LocalDate.parse(date, formatter);
            LocalDate now = LocalDate.now();
            if (parsedDate.isAfter(now) || parsedDate.isBefore(now.minusYears(70))) {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private String calculateCalories() throws JSONException {
        String gender = spinner_gender.getSelectedItem().toString();
        String dob = edittext_date_of_birth.getText().toString();
        double weight = Double.parseDouble(edittext_weight.getText().toString());
        double height = Double.parseDouble(edittext_height.getText().toString());
        String activityLevel = spinner_physical_activity_level.getSelectedItem().toString();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int birthYear = Integer.parseInt(dob.split("/")[2]);
        int age = currentYear - birthYear;

        double calories = (weight * 10) + (height * 6.25) - (age * 5);

        switch (gender) {
            case "Homme":
                calories += 5;
                break;
            case "Femme":
                calories -= 161;
                break;
            case "Autre":
                calories += (double) (-161 + 5) / 2;
                break;
        }

        switch (activityLevel) {
            case "Sédentaire":
                calories *= 1.37;
                break;
            case "Actif":
                calories *= 1.55;
                break;
            case "Sportif":
                calories *= 1.8;
                break;
        }

        calories = Math.round(calories * 100.0) / 100.0;

        isCalculated = true;

        preference = getSharedPreferences("calories",MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("calories", String.valueOf(calories));
        editor.commit();

        saveCalories(calories);

        return String.valueOf(calories);
    }


    private View.OnClickListener calculateCalories(Button button_calculate_calories){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkValues()){
                    String calculatedCalories = null;
                    try {
                        calculatedCalories = calculateCalories();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if(checkbox_display_raw_result.isChecked()){
                        if(!spinner_gender.getSelectedItem().toString().equals("Autre")){
                            String genre = spinner_gender.getSelectedItem().toString().equals("Homme") ? "Monsieur" : "Madame";
                            textview_calorie_result.setText(
                                    Html.fromHtml("<b>"+ genre + " " + name + "</b>, vous êtes <b>" + spinner_physical_activity_level.getSelectedItem().toString() + "</b>."+getString(R.string.calorie_result)+" <b>" + calculatedCalories + " Kcal/j</b>.", Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            textview_calorie_result.setText(
                                    Html.fromHtml("Vous êtes <b>" + spinner_physical_activity_level.getSelectedItem().toString() + "</b>."+getString(R.string.calorie_result)+" <b>" + calculatedCalories + " Kcal/j</b>.", Html.FROM_HTML_MODE_LEGACY));
                        }

                    } else {
                        textview_calorie_result.setText(
                                Html.fromHtml(getString(R.string.calorie_result) + " <b>" + calculatedCalories + " Kcal/j</b>.", Html.FROM_HTML_MODE_LEGACY));
                    }

                }
            }
        };
    }

    public void saveCalories(double calories) {
        String fileName = "calories.txt";
        FileOutputStream output = null;
        try {
            output = openFileOutput(fileName, MODE_APPEND);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            String record = currentDate + " - " + calories + "\n";
            output.write(record.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}