package com.ducklings_corp.devandroid.tp6;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentStatistics extends Fragment  {
    View view;
    Bitmap loadedImageRef;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_data, viewGroup, false);

        loadedImageRef = ((MainActivity)getActivity()).getLastLoadedBitmap();
        ((ImageView)view.findViewById(R.id.loadedPicture)).setImageBitmap(loadedImageRef);
        return view;
    }
}
