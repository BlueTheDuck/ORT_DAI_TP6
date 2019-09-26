package com.ducklings_corp.devandroid.tp6;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class FragmentLoadImage extends Fragment implements View.OnClickListener {
    View view;
    Bitmap loadedImageRef;
    FaceServiceRestClient faceClient;
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_picture, viewGroup, false);

        loadedImageRef = ((MainActivity)getActivity()).getLastLoadedBitmap();
        ((ImageView)view.findViewById(R.id.loadedPicture)).setImageBitmap(loadedImageRef);

        view.findViewById(R.id.analyze).setOnClickListener(this);

        String apiEndpoint = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0";
        String subscriptionKey = "06b4c956a3b0439fb5356b239519f22a";
        faceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

        return view;
    }

    @Override
    public void onClick(View v) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        loadedImageRef.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        class processImage extends AsyncTask<ByteArrayInputStream,Void,Face[]> {

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
                faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Age);
                faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Glasses);
                faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Hair);
                faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Smile);
                faceAttributeTypes = new FaceServiceClient.FaceAttributeType[faceAttributeTypeArrayList.size()];
                for(int i=0; i < faceAttributeTypeArrayList.size(); i++) {
                    faceAttributeTypes[i] = faceAttributeTypeArrayList.get(i);
                }
            }

            @Override
            protected Face[] doInBackground(ByteArrayInputStream... inputStreams) {
                Face[] faces = null;
                try {
                    Log.d("img","Sending info to Microsoft");
                    faces = faceClient.detect(inputStreams[0],true,false,faceAttributeTypes);
                }catch (Exception e) {
                    Log.e("img",e.getMessage());
                }
                return faces;
            }

            @Override
            protected void onPostExecute(Face[] faces) {
                if(faces==null) {
                    Log.e("img","faces = null");
                    return;
                }
                super.onPostExecute(faces);
                Log.d("img","Finished face recognition");
                ((MainActivity)getActivity()).loadResults(faces);
            }
        }

        (new processImage()).execute(inputStream);

    }
}