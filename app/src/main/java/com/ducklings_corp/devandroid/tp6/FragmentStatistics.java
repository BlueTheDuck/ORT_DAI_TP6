package com.ducklings_corp.devandroid.tp6;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.FaceAttribute;
import com.microsoft.projectoxford.face.contract.Glasses;
import com.microsoft.projectoxford.face.contract.Hair;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentStatistics extends Fragment implements View.OnClickListener {
    View view;
    Bitmap loadedImageRef;
    private ArrayList<FaceAttribute> allFacesAttributes;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_data, viewGroup, false);

        MainActivity mainActivity = ((MainActivity)getActivity());

        loadedImageRef = mainActivity.getLastLoadedBitmap();
        ((ImageView)view.findViewById(R.id.loadedPicture)).setImageBitmap(loadedImageRef);

        allFacesAttributes = mainActivity.getAllFacesAttributes();

        view.findViewById(R.id.discardResults).setOnClickListener(this);
        view.findViewById(R.id.addResults).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addResults:
                calculateStatics();
                view.findViewById(R.id.lastLoadedImageLayout).setVisibility(View.GONE);
                break;
            case R.id.discardResults:
                allFacesAttributes.remove(allFacesAttributes.size()-1);
                view.findViewById(R.id.lastLoadedImageLayout).setVisibility(View.GONE);
                calculateStatics();
                break;
        }
    }

    private void calculateStatics() {
        float age = 0, smile = 0;
        HashMap<Glasses,Float> glassesCount = new HashMap<>();
        HashMap<Hair.HairColor.HairColorType,Float> hairColors = new HashMap<>();
        for(FaceAttribute faceAttribute: this.allFacesAttributes) {

            age += faceAttribute.age;

            Float theirKindOfGlasses = glassesCount.get(faceAttribute.glasses);
            if(theirKindOfGlasses==null) {
                theirKindOfGlasses = 0f;
            }
            glassesCount.put(faceAttribute.glasses,theirKindOfGlasses + 1);

            Hair.HairColor.HairColorType theirHairColor = faceAttribute.hair.hairColor[0].color;// This is a list of _possible_ hair colors, the first is the most probable
            Float hairColorCount = hairColors.get(theirHairColor);
            if(hairColorCount==null) {
                hairColorCount = 0f;
            }
            hairColors.put(theirHairColor,hairColorCount+1);

            smile += faceAttribute.smile;
        }

        displayStatics(allFacesAttributes.size(),age,smile,glassesCount,hairColors);
    }

    private void displayStatics(int amountAnalyzed,float age, float smile, HashMap<Glasses, Float> glassesCount, HashMap<Hair.HairColor.HairColorType, Float> hairColors) {
        String ageToDisplay = String.format("%.2f",age);
        String smileToDisplay = String.format("%.2f",smile);
        String glassesToDisplay = HelperFunctions.hashMapToPercentages(glassesCount);
        String hairsToDisplay = HelperFunctions.hashMapToPercentages(hairColors);

        ((TextView)view.findViewById(R.id.ageTotalValue)).setText(ageToDisplay);
        ((TextView)view.findViewById(R.id.glassesTotalValue)).setText(glassesToDisplay);
        ((TextView)view.findViewById(R.id.hairTotalValue)).setText(hairsToDisplay);
        ((TextView)view.findViewById(R.id.smileTotalValue)).setText(smileToDisplay);
    }

}
