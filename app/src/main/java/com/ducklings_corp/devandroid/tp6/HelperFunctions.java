package com.ducklings_corp.devandroid.tp6;

import android.graphics.Canvas;
import android.graphics.Paint;

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
            String statistics = String.format("%.1f", count / amount * 100);
            output += (name + " = " + statistics);
            i++;
            if (i < hashMap.size()) {
                output += "\n";
            }
        }
        return output;
    }

    public static void semisquareHighlight(Canvas image, Paint brush, int x, int y, int w, int h) {
        Float thirdOfW = (float) w / 3f;
        Float thirdOfH = (float) h / 3f;
        for (int dx = 0; dx < w; dx += thirdOfW * 2) {// Draw one third then another of the line (leaving the middle third empty)
            image.drawLine(x + dx, y, x + dx + thirdOfW, y, brush);// Top horizontal line
            image.drawLine(x + dx, y + h, x + dx + thirdOfW, y + h, brush);// Bottom horizontal line
        }
        for (int dy = 0; dy < h; dy += thirdOfH * 2) {// Idem /\ but with vertical lines
            image.drawLine(x, y + dy,x,y+dy+thirdOfH,brush);
            image.drawLine(x+w, y + dy,x+w,y+dy+thirdOfH,brush);
        }
        //image.drawLine(x,y,x+w,y+h,brush);
    }
}
