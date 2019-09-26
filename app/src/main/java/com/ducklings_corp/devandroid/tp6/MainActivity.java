package com.ducklings_corp.devandroid.tp6;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceAttribute;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private final int LOAD_IMAGE_REQ_CODE = 1;
    private final int TAKE_PICTURE_REQ_CODE = 2;
    private ArrayList<FaceAttribute> allFacesAttributes = new ArrayList<>();
    private ArrayList<FaceAttribute> lastImageFaces = new ArrayList<>();
    private Bitmap lastLoadedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void createFragment(int id, Fragment fragment, String tag) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(id, fragment, tag);
        transaction.commit();
    }

    public void requestImg(View view) {
        Intent activityIntent = null;
        int reqCode = -1;
        switch (view.getTag().toString()) {
            case "selectImg":
                activityIntent = new Intent(Intent.ACTION_GET_CONTENT);
                activityIntent.setType("image/*");
                reqCode = LOAD_IMAGE_REQ_CODE;
                break;
            case "takePic":
                //activityIntent = new Intent(Intent.ACTION_IMAGE_CAPTURE);
                break;
        }
        if (activityIntent != null) {
            startActivityForResult(Intent.createChooser(activityIntent, "Elija una foto"), reqCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.d("img", "Error received as a result");
            return;
        }
        if (data == null) {
            Log.d("img", "Data received is null");
            return;
        }
        switch (requestCode) {
            case LOAD_IMAGE_REQ_CODE:
                Uri location = data.getData();
                Log.d("img", "Loading image from " + location);
                try {
                    lastLoadedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), location);
                } catch (Exception e) {
                    Log.d("img", "Error loading image: " + e.getMessage());
                }
                createFragment(R.id.fragmentLayout, new FragmentLoadImage(), "fli");
                break;
            case TAKE_PICTURE_REQ_CODE:

                break;
        }
    }

    public Bitmap getLastLoadedBitmap() {
        return lastLoadedBitmap;
    }

    public void loadResults(Face[] faces) {
        Log.d("res", "Analyzing results");
        highlightFaces(faces);
        for (Face face : faces) {
            lastImageFaces.add(face.faceAttributes);
        }
        createFragment(R.id.fragmentLayout, new FragmentStatistics(), "stats");
    }

    private void highlightFaces(Face[] faces) {
        Bitmap copy = lastLoadedBitmap.copy(Bitmap.Config.ARGB_8888, true);
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
        }
        lastLoadedBitmap = copy;// Replace original with copy
    }

    public ArrayList<FaceAttribute> getAllFacesAttributes() {
        return allFacesAttributes;
    }

    public void setAllFacesAttributes(ArrayList<FaceAttribute> allFacesAttributes) {
        this.allFacesAttributes = allFacesAttributes;
    }

    public ArrayList<FaceAttribute> getLastImageFaces() {
        return lastImageFaces;
    }

    public void setLastImageFaces(ArrayList<FaceAttribute> lastImageFaces) {
        this.lastImageFaces = lastImageFaces;
    }
}
