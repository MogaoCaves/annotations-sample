package com.nelson.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nelson.annotation.Bind;
import com.nelson.api.ViewInjector;

/**
 * Created by Nelson on 17/4/27.
 */

public class SimpleFragment extends Fragment {

    @Bind(R.id.id_textview)
    TextView mIdTextView;
    @Bind(R.id.id_btn)
    Button mIdBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        ViewInjector.injectView(this, view);

        mIdTextView.setText("ViewInject");
        mIdBtn.setText("ViewInject ~");

        return view;
    }
}
