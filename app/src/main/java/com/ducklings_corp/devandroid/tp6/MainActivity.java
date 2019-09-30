package com.ducklings_corp.devandroid.tp6;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceAttribute;

import java.util.ArrayList;

/**
 * ▗▄           ▐     ▝▜          ▐        ▗           ▝               ▗                        ▐      ▗  ▐    ▝                    ▐
 * ▗▘ ▘ ▄▖  ▄▖  ▄▟      ▐  ▗ ▗  ▄▖ ▐ ▗     ▗▟▄  ▖▄ ▗ ▗ ▗▄  ▗▗▖  ▄▄     ▗▟▄  ▄▖      ▖▄  ▄▖  ▄▖  ▄▟     ▗▟▄ ▐▗▖ ▗▄   ▄▖      ▄▖  ▄▖  ▄▟  ▄▖
 * ▐ ▗▖▐▘▜ ▐▘▜ ▐▘▜      ▐  ▐ ▐ ▐▘▝ ▐▗▘      ▐   ▛ ▘▝▖▞  ▐  ▐▘▐ ▐▘▜      ▐  ▐▘▜      ▛ ▘▐▘▐ ▝ ▐ ▐▘▜      ▐  ▐▘▐  ▐  ▐ ▝     ▐▘▝ ▐▘▜ ▐▘▜ ▐▘▐
 * ▐  ▌▐ ▐ ▐ ▐ ▐ ▐      ▐  ▐ ▐ ▐   ▐▜       ▐   ▌   ▙▌  ▐  ▐ ▐ ▐ ▐      ▐  ▐ ▐      ▌  ▐▀▀ ▗▀▜ ▐ ▐      ▐  ▐ ▐  ▐   ▀▚     ▐   ▐ ▐ ▐ ▐ ▐▀▀
 * ▚▄▘▝▙▛ ▝▙▛ ▝▙█      ▝▄ ▝▄▜ ▝▙▞ ▐ ▚      ▝▄  ▌   ▜  ▗▟▄ ▐ ▐ ▝▙▜      ▝▄ ▝▙▛      ▌  ▝▙▞ ▝▄▜ ▝▙█      ▝▄ ▐ ▐ ▗▟▄ ▝▄▞     ▝▙▞ ▝▙▛ ▝▙█ ▝▙▞
 * ▞           ▖▐
 */

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
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQ_CODES.ASK_PERMS.value);
        } else {
            findViewById(R.id.selectImg).setVisibility(View.VISIBLE);
        }
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
        switch (view.getId()) {
            case R.id.selectImg:
                activityIntent = new Intent(Intent.ACTION_GET_CONTENT);
                activityIntent.setType("image/*");
                reqCode = REQ_CODES.LOAD_IMAGE.value;
                break;
            case R.id.takePic:
                // The method described in the PDF is only required when the App uses an API level < 23
                // Since this app uses API23 as min (and 28 as target), we don't need (nor can) use ContextCompat
                // Instead, we can just use our own app as context
                if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CODES.ASK_PERMS.value);
                } else {

                }
                //activityIntent = new Intent(Intent.ACTION_IMAGE_CAPTURE);
                break;

        }
        if (activityIntent != null) {
            startActivityForResult(Intent.createChooser(activityIntent, "Elija una foto"), reqCode);
        }
    }

    public void emptyData(View view) {
        allFacesAttributes.clear();
        lastImageFaces.clear();
        ((FrameLayout) findViewById(R.id.fragmentLayout)).removeAllViews();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grantedAllPerms = true;
        if (requestCode == REQ_CODES.ASK_PERMS.value) {
            for (int i = 0; i < permissions.length; i++) {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    grantedAllPerms = false;
                }
            }
        }
        if(grantedAllPerms) {
            findViewById(R.id.selectImg).setVisibility(View.VISIBLE);
        }
    }

    public void loadResults(Face[] faces) {
        Log.d("res", "Analyzing results");
        for (Face face : faces) {
            lastImageFaces.add(face.faceAttributes);
        }
        createFragment(R.id.fragmentLayout, new FragmentStatistics(), "stats");
    }

    // Getters&Setters
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

    public Bitmap getLastLoadedBitmap() {
        return lastLoadedBitmap;
    }

    public void setLastLoadedBitmap(Bitmap lastLoadedBitmap) {
        this.lastLoadedBitmap = lastLoadedBitmap;
    }

    private enum REQ_CODES {
        LOAD_IMAGE(1),
        TAKE_PIC(2),
        ASK_PERMS(3);

        private final int value;

        REQ_CODES(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}
