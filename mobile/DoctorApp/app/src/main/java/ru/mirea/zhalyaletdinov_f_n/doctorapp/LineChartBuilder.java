package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineChartBuilder {
    private List<GetMeasurment> weekMeasList;
    private LineChart lineChart;

    private Context context;

    public LineChartBuilder(Context context, LineChart lineChart, List<GetMeasurment> weekMeasList) {
        this.context = context;
        this.lineChart = lineChart;
        this.weekMeasList = weekMeasList;
    }

    public void buildChart() {
        Collections.reverse(weekMeasList);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        DateTimeFormatter chartFormatter = DateTimeFormatter.ofPattern("dd.MM");

        List<Entry> topEntries = new ArrayList<>();
        List<Entry> bottomEntries = new ArrayList<>();
        List<Entry> pulseEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < weekMeasList.size(); i++) {
            GetMeasurment measurement = weekMeasList.get(i);
            LocalDateTime dateTime = LocalDateTime.parse(measurement.getCreated_at(), inputFormatter);

            topEntries.add(new Entry(i, measurement.getTop()));
            bottomEntries.add(new Entry(i, measurement.getBottom()));
            pulseEntries.add(new Entry(i, measurement.getPulse()));
            labels.add(dateTime.format(chartFormatter));
        }

        Typeface interTypeface = ResourcesCompat.getFont(context, R.font.inter);
        lineChart.setPinchZoom(false);

        LineDataSet topDataSet = new LineDataSet(topEntries, "Верхнее");
        topDataSet.setLineWidth(2f);
        topDataSet.setDrawValues(false);
        topDataSet.setColor(Color.RED);

        LineDataSet bottomDataSet = new LineDataSet(bottomEntries, "Нижнее");
        bottomDataSet.setLineWidth(2f);
        bottomDataSet.setDrawValues(false);
        bottomDataSet.setColor(Color.GREEN);

        LineDataSet pulseDataSet = new LineDataSet(pulseEntries, "Пульс");
        pulseDataSet.setLineWidth(2f);
        pulseDataSet.setColor(Color.BLUE);
        pulseDataSet.setDrawValues(false);

        LineData lineData = new LineData(topDataSet, bottomDataSet, pulseDataSet);
        int gridLineColor = Color.parseColor("#646978");
        int gridLineColorWithAlpha = Color.argb(100, Color.red(gridLineColor), Color.green(gridLineColor), Color.blue(gridLineColor));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(interTypeface);
        xAxis.setTextSize(8f);
        xAxis.setGridColor(gridLineColorWithAlpha);
        xAxis.setTextColor(gridLineColor);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTypeface(interTypeface);
        leftAxis.setTextSize(8f);
        leftAxis.setGridColor(gridLineColorWithAlpha);
        leftAxis.setTextColor(gridLineColor);
        leftAxis.setGranularity(1f);

        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }
}