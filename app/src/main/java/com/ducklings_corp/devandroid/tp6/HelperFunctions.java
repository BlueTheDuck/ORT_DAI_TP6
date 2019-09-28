package com.ducklings_corp.devandroid.tp6;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.microsoft.projectoxford.face.contract.FaceLandmarks;
import com.microsoft.projectoxford.face.contract.FeatureCoordinate;

import java.util.HashMap;

public class HelperFunctions {
    public static double goldenRatio(int i) {// Magic function that returns nicely separated values per i
        double φ = (Math.sqrt(5) + 1) / 2;
        return (i * φ) % 1;
    }
    public static int goldenColor(int i) {
        // Multiply WHITE with this function to get nicely separated colors per face (also, add a 100% opacity)
        return (int) (HelperFunctions.goldenRatio(i) * 0xFFFFFF + 0xFF000000);
    }

    public static <K, V extends Number> String hashMapToPercentages(HashMap<K, V> hashMap) {
        String output = "";
        int amount = 0;
        int i = 0;
        for (V v : hashMap.values()) {
            amount += (Float) v;
        }
        for (K k : hashMap.keySet()) {
            String name = k.toString();
            Float count = (Float) hashMap.get(k);
            String statistics = String.format("%.1f", count / amount * 100)+"%";
            output += (name + " = " + statistics);
            i++;
            if (i < hashMap.size()) {
                output += "\n";
            }
        }
        return output;
    }

    public static void semisquareHighlight(Canvas canvas, Paint brush, int x, int y, int w, int h) {
        Float thirdOfW = (float) w / 3f;
        Float thirdOfH = (float) h / 3f;
        for (int dx = 0; dx < w; dx += thirdOfW * 2) {// Draw one third then another of the line (leaving the middle third empty)
            canvas.drawLine(x + dx, y, x + dx + thirdOfW, y, brush);// Top horizontal line
            canvas.drawLine(x + dx, y + h, x + dx + thirdOfW, y + h, brush);// Bottom horizontal line
        }
        for (int dy = 0; dy < h; dy += thirdOfH * 2) {// Idem /\ but with vertical lines
            canvas.drawLine(x, y + dy,x,y+dy+thirdOfH,brush);
            canvas.drawLine(x+w, y + dy,x+w,y+dy+thirdOfH,brush);
        }
        //image.drawLine(x,y,x+w,y+h,brush);
    }
    public static void featuresHighlight(Canvas canvas, Paint brush, FeatureCoordinate[] coordinates) {
        Path path = new Path();
        path.moveTo((float) coordinates[0].x, (float) coordinates[0].y);
        for (int i = 1; i < coordinates.length; i++) {
            path.lineTo((float) coordinates[i].x, (float) coordinates[i].y);
            path.moveTo((float) coordinates[i].x, (float) coordinates[i].y);
        }
        path.lineTo((float) coordinates[0].x, (float) coordinates[0].y);
        canvas.drawPath(path, brush);
    }
}
