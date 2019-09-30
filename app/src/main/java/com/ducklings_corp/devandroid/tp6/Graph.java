package com.ducklings_corp.devandroid.tp6;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Graph<K, V extends Number & Comparable> {

    Bitmap graph;
    private int textSize = 30;
    private int plotXOffset;
    private int cols;
    private int width = 0;
    private int height = 0;
    private Canvas canvas;
    private Paint brush;
    private HashMap<K, ArrayList<V>> keyValues;
    private V lowest, highest;
    private String label = "KeyXValue";


    Graph(HashMap<K, ArrayList<V>> keyValues) {
        this.keyValues = keyValues;

        calculateMinMaxValues();

        height = 400;
        cols = keyValues.size();
        calculatePlotXOffset();

        brush = new Paint(Color.RED);
        brush.setTextSize(textSize);
    }

    private void calculateMinMaxValues() {
        for (K key : keyValues.keySet()) {// Find minmax of the values
            for (V value : keyValues.get(key)) {
                if (highest == null || value.compareTo(highest) > 0) {
                    highest = value;
                }
                if (lowest == null || value.compareTo(lowest) < 0) {
                    lowest = value;
                }
            }
        }
        if (highest == lowest) {
            lowest = (V) new Integer(0);
        }

    }

    private void calculatePlotXOffset() {// The plotting is offsetted by plotXOffset pixels so there is space for the left-hand-side labels
        plotXOffset = 0;
        for (String s : new String[]{lowest.toString(), highest.toString(), getLabel()}) {
            if (s.length() > plotXOffset) {
                plotXOffset = s.length();
            }
        }
        plotXOffset *= textSize;

        calculateWidth();
    }

    private void calculateWidth() {
        setWidth(cols * textSize + plotXOffset + 100);// width and height's setters trigger the recreation of the canvas
    }

    public void plot() {
        graph = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(graph);
        Log.d("graph", "plot: min/max: " + String.format("%f/%f", lowest.floatValue(), highest.floatValue()));
        int col = 0;
        for (K key : keyValues.keySet()) {
            Log.d("graph", "Key: " + key);
            ArrayList<V> values = keyValues.get(key);
            int x = col * (textSize * 2) + plotXOffset; // X pos is just (current col * space for labels) + offset
            canvas.drawText(key.toString(), x, getHeight() - textSize, brush);
            for (V value : values) {
                /* Y is calculated like this:
                                                     +- Y value goes top-to-down, so invert the percentage
                    Calc. high usable to graph      \|/               Calculate % of the current value
                   ( getHeight() - textSize * 2 ) * (1 -          (  value.floatValue() - lowest.floatValue() ) /
                                                 /|\              (highest.floatValue() - lowest.floatValue()) )
                                                  |
                                                  multiply % by height to get Y coord in the graph
                 */
                float y = (getHeight() - textSize * 2) * (1 - (value.floatValue() - lowest.floatValue()) / (highest.floatValue() - lowest.floatValue()));
                if (y < 1) {// Values outside the Canvas are not drawn
                    y = textSize;
                }
                canvas.drawText("x", x, y, brush);
                Log.d("display", ">value: " + value);
                Log.d("graph", "plot: " + x + ";" + y);

            }
            col++;
        }
        canvas.drawText(highest.toString(), 0, textSize, brush);
        canvas.drawText(lowest.toString(), 0, height - textSize * 2, brush);
        canvas.drawText(getLabel(), 0, height - textSize, brush);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        graph = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(graph);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        calculatePlotXOffset();
    }

}
