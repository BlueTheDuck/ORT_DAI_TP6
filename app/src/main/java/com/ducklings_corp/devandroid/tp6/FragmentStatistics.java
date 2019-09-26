package com.ducklings_corp.devandroid.tp6;

import android.app.Fragment;
import android.graphics.Bitmap;
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

import java.util.ArrayList;
import java.util.HashMap;


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
            int color = HelperFunctions.goldenColor(i);
            ageText += String.format( "<font color='#%X'>%d</font>",color&0xFFFFFF,(int)face.age);
            if(i!=lastImageFaces.size()-1) {
                ageText += ", ";
            }
        }
        Log.d("data",ageText);
        ((TextView)view.findViewById(R.id.ageValue)).setText(Html.fromHtml(ageText) ,TextView.BufferType.SPANNABLE );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addResults:
                allFacesAttributes.addAll(lastImageFaces);
                mainActivityRef.setAllFacesAttributes(allFacesAttributes);
                mainActivityRef.setLastImageFaces(null);
                calculateStatistics();
                view.findViewById(R.id.lastLoadedImageLayout).setVisibility(View.GONE);
                break;
            case R.id.discardResults:
                //allFacesAttributes.remove(allFacesAttributes.size() - 1);
                view.findViewById(R.id.lastLoadedImageLayout).setVisibility(View.GONE);
                calculateStatistics();
                break;
        }
    }

    private void calculateStatistics() {
        float age = 0, smile = 0;
        HashMap<Glasses, Float> glassesCount = new HashMap<>();
        HashMap<Hair.HairColor.HairColorType, Float> hairColors = new HashMap<>();
        for (FaceAttribute faceAttribute : this.allFacesAttributes) {

            age += faceAttribute.age;

            Float theirKindOfGlasses = glassesCount.get(faceAttribute.glasses);
            if (theirKindOfGlasses == null) {
                theirKindOfGlasses = 0f;
            }
            glassesCount.put(faceAttribute.glasses, theirKindOfGlasses + 1);

            Hair.HairColor.HairColorType theirHairColor = faceAttribute.hair.hairColor[0].color;// This is a list of _possible_ hair colors, the first is the most probable
            Float hairColorCount = hairColors.get(theirHairColor);
            if (hairColorCount == null) {
                hairColorCount = 0f;
            }
            hairColors.put(theirHairColor, hairColorCount + 1);

            smile += faceAttribute.smile;
        }

        displayGeneralStatistics(allFacesAttributes.size(), age, smile, glassesCount, hairColors);
    }

    private void displayGeneralStatistics(int amountAnalyzed, float age, float smile, HashMap<Glasses, Float> glassesCount, HashMap<Hair.HairColor.HairColorType, Float> hairColors) {
        String ageToDisplay = String.format("%.2f", age);
        String smileToDisplay = String.format("%.2f", smile);
        String glassesToDisplay = HelperFunctions.hashMapToPercentages(glassesCount);
        String hairsToDisplay = HelperFunctions.hashMapToPercentages(hairColors);

        ((TextView) view.findViewById(R.id.ageTotalValue)).setText(ageToDisplay);
        ((TextView) view.findViewById(R.id.glassesTotalValue)).setText(glassesToDisplay);
        ((TextView) view.findViewById(R.id.hairTotalValue)).setText(hairsToDisplay);
        ((TextView) view.findViewById(R.id.smileTotalValue)).setText(smileToDisplay);
    }

}
