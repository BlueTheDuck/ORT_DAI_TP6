package com.ducklings_corp.devandroid.tp6;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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
import com.microsoft.projectoxford.face.contract.FaceLandmarks;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.FeatureCoordinate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class FragmentLoadImage extends Fragment implements View.OnClickListener {
    View view;
    Bitmap loadedImageRef;
    FaceServiceRestClient faceClient;
    MainActivity mainActivity;
    CheckBox facialHairAnalyze, makeupAnalyze, eyesHighlight, mouthHighlight;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_picture, viewGroup, false);

        mainActivity = ((MainActivity) getActivity());
        loadedImageRef = mainActivity.getLastLoadedBitmap();
        ((ImageView) view.findViewById(R.id.loadedPicture)).setImageBitmap(loadedImageRef);

        view.findViewById(R.id.analyze).setOnClickListener(this);

        facialHairAnalyze = view.findViewById(R.id.facialHairAnalyze);
        makeupAnalyze = view.findViewById(R.id.makeupAnalyze);
        eyesHighlight = view.findViewById(R.id.eyesHighlight);
        mouthHighlight = view.findViewById(R.id.mouthHighlight);

        String apiEndpoint = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0";
        String subscriptionKey = "06b4c956a3b0439fb5356b239519f22a";
        faceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

        return view;
    }

    @Override
    public void onClick(View v) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        loadedImageRef.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        class processImage extends AsyncTask<ByteArrayInputStream, Void, Face[]> {

            FaceServiceClient.FaceAttributeType[] faceAttributeTypes;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ArrayList<FaceServiceClient.FaceAttributeType> faceAttributeTypeArrayList = new ArrayList<>();
                if (facialHairAnalyze.isChecked()) {
                    faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.FacialHair);
                }
                if (makeupAnalyze.isChecked()) {
                    faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Makeup);
                }
                faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Age);
                faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Glasses);
                faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Hair);
                faceAttributeTypeArrayList.add(FaceServiceClient.FaceAttributeType.Smile);
                faceAttributeTypes = new FaceServiceClient.FaceAttributeType[faceAttributeTypeArrayList.size()];
                for (int i = 0; i < faceAttributeTypeArrayList.size(); i++) {
                    faceAttributeTypes[i] = faceAttributeTypeArrayList.get(i);
                }

            }

            @Override
            protected Face[] doInBackground(ByteArrayInputStream... inputStreams) {
                Face[] faces = null;
                try {
                    Log.d("img", "Sending info to Microsoft");
                    faces = faceClient.detect(inputStreams[0], true, true, faceAttributeTypes);
                } catch (Exception e) {
                    Log.e("img", e.getMessage());
                }
                return faces;
            }

            @Override
            protected void onPostExecute(Face[] faces) {
                if (faces == null) {
                    Log.e("img", "faces = null");
                    return;
                }
                super.onPostExecute(faces);
                Log.d("img", "Finished face recognition");
                highlightFaces(faces);
                mainActivity.setLastLoadedBitmap(loadedImageRef);
                mainActivity.loadResults(faces);
            }
        }

        (new processImage()).execute(inputStream);

    }


    private void highlightFaces(Face[] faces) {
        Bitmap copy = loadedImageRef.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);
        Paint brush = new Paint();

        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeWidth(5);

        for (int i = 0; i < faces.length; i++) {
            Face face = faces[i];
            FaceRectangle rect = face.faceRectangle;
            brush.setColor(HelperFunctions.goldenColor(i));
            HelperFunctions.semisquareHighlight(canvas, brush, rect.left, rect.top, rect.width, rect.height);
            if(face.faceLandmarks!=null) {
                highlightLandmarks(canvas,brush,face.faceLandmarks);
            }
            Log.d("highlightFaces", (face.faceLandmarks==null)+"");
        }
        loadedImageRef = copy;// Replace original with copy
    }
    // Highlight different parts of the face
    private void highlightLandmarks(Canvas canvas, Paint brush, FaceLandmarks landmarks) {
        Log.d("highlightLandmarks", " ------------------------- ");
        boolean mouth;
        mouth = mouthHighlight.isChecked();
        if(eyesHighlight.isChecked()) {
            Log.d("highlightLandmarks", "Eyes");
            // Declare arrays of coords. eyeCoords[0] and [1] contain the coords of the left and right eyes, respectively
            FeatureCoordinate[][] eyeCoords = {{
                    landmarks.eyeLeftOuter,
                    landmarks.eyeLeftTop,
                    landmarks.eyeLeftInner,
                    landmarks.eyeLeftBottom
            }, {
                    landmarks.eyeRightOuter,
                    landmarks.eyeRightTop,
                    landmarks.eyeRightInner,
                    landmarks.eyeRightBottom
            }
            };

            // "s" means "s"ide, 0 is left, 1 is right
            for (int s = 0; s < 2; s++) {
                HelperFunctions.featuresHighlight(canvas,brush,eyeCoords[s]);
            }
        }
        if(mouthHighlight.isChecked()) {
            Log.d("highlightLandmarks", "Mouth");
            FeatureCoordinate[] mouthCoords = {
                    landmarks.mouthLeft,
                    landmarks.upperLipTop,
                    landmarks.mouthRight,
                    landmarks.underLipBottom
            };
            HelperFunctions.featuresHighlight(canvas,brush,mouthCoords);
        }
        Log.d("highlightLandmarks", " ------------------------- ");
    }
}