package com.ducklings_corp.devandroid.tp6;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.util.BitSet;

public class MainActivity extends Activity {
    private final int LOAD_IMAGE_REQ_CODE = 1;
    private final int TAKE_PICTURE_REQ_CODE = 2;
    private Bitmap lastLoadedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void createFragment(int id, Fragment fragment, String tag) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(id,fragment,tag);
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
        if(activityIntent!=null) {
            startActivityForResult(Intent.createChooser(activityIntent,"Elija una foto"),reqCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) {
            Log.d("img","Error received as a result");
            return;
        }
        if(data==null) {
            Log.d("img","Data received is null");
            return;
        }
        switch (requestCode) {
            case LOAD_IMAGE_REQ_CODE:
                Uri location = data.getData();
                Log.d("img","Loading image from "+location);
                try {
                    lastLoadedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),location);
                } catch (Exception e) {
                    Log.d("img","Error loading image: "+e.getMessage());
                }
                createFragment(R.id.fragmentLayout,new FragmentLoadImage(),"fli");
                break;
            case TAKE_PICTURE_REQ_CODE:

                break;
        }
    }

    public Bitmap getLastLoadedBitmap() {
        return lastLoadedBitmap;
    }
}
