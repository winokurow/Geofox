package org.games.geofox;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StartFragment extends DialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String text = this.getArguments().getString("text");
        getDialog().setTitle("Waiting");

        View v = inflater.inflate(R.layout.fragment_start, null);
        TextView tv = (TextView) v.findViewById(R.id.textDialog);
        tv.setText(text);
        return v;
    }
}