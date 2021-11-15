package com.ionexchange.Interface;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public interface RvOutputControl {

    void click(TextView outputName, TextView outputType, AutoCompleteTextView outputControl, View view, int outputNumber, int pos);
}
