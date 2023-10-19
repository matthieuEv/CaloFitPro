package com.example.calofitpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class thirdActivity extends AppCompatActivity {

    BarChart barChart = null;
    BarDataSet dataSet = null;
    BarData barData = null;
    XAxis xAxis = null;
    FileInputStream input = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        setupToolbar();

        barChart = findViewById(R.id.chart);

        setupBarChart();

        List<BarEntry> entries = new ArrayList<>();
        List<String> labelsList = new ArrayList<>();

        String fileName = "calories.txt";
        try {
            input = openFileInput(fileName);
            int c;
            StringBuilder temp = new StringBuilder();
            int index = 0;
            while ((c = input.read()) != -1) {
                temp.append(Character.toString((char) c));
            }
            String[] lines = temp.toString().split("\n");
            for (String line : lines) {
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    String date = parts[0];
                    float value = Float.parseFloat(parts[1]);
                    entries.add(new BarEntry((float) index, value));
                    labelsList.add(date);
                    index++;
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Définir les étiquettes pour l'axe X
        String[] labels = labelsList.toArray(new String[0]);

        computeBarChart(entries, labels);

        Button deleteLastBtn = findViewById(R.id.deleteLastBtn);
        deleteLastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "calories.txt";
                try {
                    FileInputStream fis = openFileInput(fileName);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    List<String> allLines = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        allLines.add(line);
                    }
                    reader.close();
                    fis.close();

                    // Remove the last line
                    if (!allLines.isEmpty()) {
                        allLines.remove(allLines.size() - 1);
                    }

                    // Write the remaining lines back to the file
                    FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                    OutputStreamWriter writer = new OutputStreamWriter(fos);
                    for (String outputLine : allLines) {
                        writer.write(outputLine + "\n");
                    }
                    writer.close();
                    fos.close();

                } catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }

                refreshGraph();
            }
        });

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBarChart(){
        barChart.setDrawGridBackground(false);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        barChart.getAxisRight().setDrawLabels(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
    }

    private void computeBarChart(List<BarEntry> entries, String[] labels){
        dataSet = new BarDataSet(entries, "");
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(15f);
        dataSet.setHighlightEnabled(false);
        dataSet.setColor(getColor(R.color.dark_blue));

        barData = new BarData(dataSet);
        barChart.setData(barData);

        barChart.setVisibleXRangeMaximum(4.5f);
        barChart.moveViewToX(barData.getEntryCount());

        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        barChart.setScaleEnabled(false);
        barChart.invalidate();
    }

    private void refreshGraph() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labelsList = new ArrayList<>();

        String fileName = "calories.txt";
        try {
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    String date = parts[0];
                    float value = Float.parseFloat(parts[1]);
                    entries.add(new BarEntry((float) index, value));
                    labelsList.add(date);
                    index++;
                }
            }
            reader.close();
            fis.close();

            // Define labels for the X axis
            String[] labels = labelsList.toArray(new String[0]);

            computeBarChart(entries, labels);

        } catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
        }
    }

}
