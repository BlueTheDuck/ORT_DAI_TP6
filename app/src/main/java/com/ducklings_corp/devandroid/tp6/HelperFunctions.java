package com.ducklings_corp.devandroid.tp6;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.HashMap;

public class HelperFunctions {
    public static double goldenRatio(int i) {
        double φ = (Math.sqrt(5) + 1) / 2;
        return (i * φ) % 1;
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

    public static void semiSquareHighlight(Canvas image, Paint brush, int x, int y, int w, int h) {
        Float thirdOfW = (float)w / 3f;
        Float thirdOfH = (float)h / 3f;
        /*  |1/3||1/3||1/3|
         *   A---a     B---b  \    1
         *   |             |  | >  -
         *   ∀             |  /    3
         *
         *
         *
         *   c             |
         *   |             |
         *   C----     ----+
         *
         *   Aa = (_x,_y) -> (_x+thirdOfW,_y)
         *   A∀ = (_x,_y) -> (_x,_y+thirdOfW)
         *   Bb = (_x + thirdOfW * 2,_y) -> (_x+_w,_y)
         *   cC = (_x,_y+thirdOfH*2) -> (_x,_y+_h)
         * */

        image.drawLine(x,y,x+w,y,brush);
        image.drawLine(x,y,x,y+h,brush);
        image.drawLine(x+w,y,x+w,y+h,brush);
        image.drawLine(x,y+h,x+w,y+h,brush);
    }
}
