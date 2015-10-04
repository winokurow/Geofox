package org.games.geofox;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StartFragment extends DialogFragment {

    final String LOG_TAG = "myLogs";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Please wait!");
        View v = inflater.inflate(R.layout.fragment_start, null);
        return v;
    }
}