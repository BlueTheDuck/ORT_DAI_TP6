package com.ducklings_corp.devandroid.tp6;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.contract.FaceAttribute;
import com.microsoft.projectoxford.face.contract.Glasses;
import com.microsoft.projectoxford.face.contract.Hair;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class StatisticsData {
    public float age = 0, smile = 0;
    public HashMap<Glasses, Float> glassesCount = new HashMap<>();
    public HashMap<Hair.HairColor.HairColorType, Float> hairColors = new HashMap<>();// How many people with an specific hair color
    public HashMap<Double, ArrayList<Double>> ageHairRelation = new HashMap<>();// For a certain age, how much hair does a person have?
    public Double smallestHair = 1.00d;
    public Double highestHair = 0d;
    public int amountAnalyzed = 0;
}

public class FragmentStatistics extends Fragment implements View.OnClickListener {
    View view;
    MainActivity mainActivityRef;
    Bitmap loadedImageRef;
    private ArrayList<FaceAttribute> allFacesAttributes;
    private ArrayList<FaceAttribute> lastImageFaces;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_data, viewGroup, false);

        mainActivityRef = ((MainActivity) getActivity());

        loadedImageRef = mainActivityRef.getLastLoadedBitmap();
        ((ImageView) view.findViewById(R.id.loadedPicture)).setImageBitmap(loadedImageRef);

        allFacesAttributes = mainActivityRef.getAllFacesAttributes();
        lastImageFaces = mainActivityRef.getLastImageFaces();

        view.findViewById(R.id.discardResults).setOnClickListener(this);
        view.findViewById(R.id.addResults).setOnClickListener(this);

        displayImageData();

        return view;
    }

    private void displayImageData() {
        String ageText = "", glasesText = "", hairText = "", smileText = "";
        for (int i = 0; i < lastImageFaces.size(); i++) {
            FaceAttribute face = lastImageFaces.get(i);
            int color = HelperFunctions.goldenColor(i)&0xFFFFFF;

            ageText += String.format( "<font color='#%X'>%d</font>",color,(int)face.age);
            glasesText += String.format("<font color='#%X'>%s</font>",color,face.glasses.toString());
            hairText += String.format("<font color='#%X'>%.2f</font>",color,face.hair.bald);
            smileText += String.format("<font color='#%X'>%.2f</font>",color,face.smile);

            if(i!=lastImageFaces.size()-1) {
                ageText += ", ";
                glasesText += ", ";
                hairText += ", ";
                smileText += ", ";
            }
        }
        ((TextView)view.findViewById(R.id.ageValue)).setText(Html.fromHtml(ageText) ,TextView.BufferType.SPANNABLE );
        ((TextView)view.findViewById(R.id.glassesValue)).setText(Html.fromHtml(glasesText) ,TextView.BufferType.SPANNABLE );
        ((TextView)view.findViewById(R.id.hairValue)).setText(Html.fromHtml(hairText) ,TextView.BufferType.SPANNABLE );
        ((TextView)view.findViewById(R.id.smileValue)).setText(Html.fromHtml(smileText) ,TextView.BufferType.SPANNABLE );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addResults:
                allFacesAttributes.addAll(lastImageFaces);// Include the last analysis in the list
                mainActivityRef.setAllFacesAttributes(allFacesAttributes);// Update it on the activity
                mainActivityRef.setLastImageFaces(new ArrayList<FaceAttribute>());// Now that it was included in the main array, replace it with an empty one
                view.findViewById(R.id.lastLoadedImageLayout).setVisibility(View.GONE);
                view.findViewById(R.id.generalDataLayout).setVisibility(View.VISIBLE);
                calculateStatistics();
                break;
            case R.id.discardResults:
                view.findViewById(R.id.lastLoadedImageLayout).setVisibility(View.GONE);
                view.findViewById(R.id.generalDataLayout).setVisibility(View.VISIBLE);
                mainActivityRef.setLastImageFaces(new ArrayList<FaceAttribute>());// Discard the last analysis, and replace it with an empty one
                calculateStatistics();
                break;
        }
    }

    private void calculateStatistics() {
        StatisticsData data = new StatisticsData();
        for (FaceAttribute faceAttribute : this.allFacesAttributes) {

            data.age += faceAttribute.age;

            Float theirKindOfGlasses = data.glassesCount.get(faceAttribute.glasses);
            if (theirKindOfGlasses == null) {// If this kind of classes hasn't been registered yet...
                theirKindOfGlasses = 0f;// set the count to 0
            }
            data.glassesCount.put(faceAttribute.glasses, theirKindOfGlasses + 1);// Increment counter for this kind of glasses

            Hair.HairColor.HairColorType theirHairColor = faceAttribute.hair.hairColor[0].color;// This is a list of _possible_ hair colors, the first is the most probable
            Float hairColorCount = data.hairColors.get(theirHairColor);
            if (hairColorCount == null) {
                hairColorCount = 0f;
            }
            data.hairColors.put(theirHairColor, hairColorCount + 1);

            ArrayList<Double> hair = data.ageHairRelation.get(faceAttribute.age);// Get the list of amount of hair for this age
            if(hair==null) {
                hair = new ArrayList<>();
            }
            hair.add(faceAttribute.hair.bald);// Add this entry
            data.ageHairRelation.put(faceAttribute.age,hair);
            if(data.smallestHair>faceAttribute.hair.bald) {
                data.smallestHair = faceAttribute.hair.bald;
            }
            if(data.highestHair<faceAttribute.hair.bald) {
                data.highestHair = faceAttribute.hair.bald;
            }

            data.smile += faceAttribute.smile;
        }
        data.amountAnalyzed = allFacesAttributes.size();

        displayGeneralStatistics(data);
    }

    private void displayGeneralStatistics(StatisticsData data) {
        String ageToDisplay = String.format("%.2f", data.age/data.amountAnalyzed);
        String smileToDisplay = String.format("%.2f", data.smile/data.amountAnalyzed);
        String glassesToDisplay = HelperFunctions.hashMapToPercentages(data.glassesCount);
        String hairsToDisplay = HelperFunctions.hashMapToPercentages(data.hairColors);

        ((TextView) view.findViewById(R.id.ageTotalValue)).setText(ageToDisplay);
        ((TextView) view.findViewById(R.id.glassesTotalValue)).setText(glassesToDisplay);
        ((TextView) view.findViewById(R.id.hairTotalValue)).setText(hairsToDisplay);
        ((TextView) view.findViewById(R.id.smileTotalValue)).setText(smileToDisplay);



        ArrayList<Double> keys = new ArrayList(data.ageHairRelation.keySet());
        Collections.sort(keys);
        int w = 450,h = 450, textSize = 30;
        Bitmap image = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        Paint brush = new Paint(Color.RED);
        brush.setTextSize(textSize);
        int col = 0, cols = data.ageHairRelation.size();
        for(Double age: keys) {
            Log.d("display","Age: "+age);
            ArrayList<Double> hairs = data.ageHairRelation.get(age);
            int x = col*(textSize*2);
            Double smallest = hairs.get(0), biggest = hairs.get(hairs.size()-1);
            canvas.drawText(age.intValue()+"",x,h-14,brush);
            for(Double hairAmount: hairs) {
                Double vertical_pos = h*((hairAmount-data.smallestHair)/data.highestHair)+textSize;
                canvas.drawText("x",x,vertical_pos.floatValue(),brush);
                Log.d("display", ">Hair: "+hairAmount);

            }
            col++;
            //hairProm/=hairs.size();
            //Log.d("display","Hair: "+hairProm);
        }

        ((ImageView)view.findViewById(R.id.ageHairMap)).setImageBitmap(image);
    }

}
