package com.ducklings_corp.devandroid.tp6;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.FaceServiceClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class FragmentLoadImage extends Fragment implements View.OnClickListener {
    View view;
    Bitmap loadedImageRef;
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_picture, viewGroup, false);

        loadedImageRef = ((MainActivity)getActivity()).getLastLoadedBitmap();
        ((ImageView)view.findViewById(R.id.loadedPicture)).setImageBitmap(loadedImageRef);

        return view;
    }

    @Override
    public void onClick(View v) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        loadedImageRef.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        class processImage extends AsyncTask<ByteArrayInputStream,Void,Void> {

            FaceServiceClient.FaceAttributeType[] faceAttributeTypes;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ArrayList<FaceServiceClient.FaceAttributeType> faceAttributeTypeArrayList = new ArrayList<>();
                if(((CheckBox)view.findViewById(R.id.facialHairCheck)).isChecked()) {
                    faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.FacialHair);
                }
                if(((CheckBox)view.findViewById(R.id.makeupCheck)).isChecked()) {
                    faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Makeup);
                }
                faceAttributeTypeArrayList.toArray(faceAttributeTypes);
            }

            @Override
            protected Void doInBackground(ByteArrayInputStream... inputStreams) {

                return null;
            }
        }



    }
}