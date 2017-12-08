package com.braimahabdullah.ghreport;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by Ibrahim-Abdullah on 12/7/2017.
 */

public abstract class TextValidator implements TextWatcher {

    private final TextView textView;

    public TextValidator(TextView textView){
        this.textView = textView;
    }

    public abstract void validate(TextView textView);

    @Override
    final public void afterTextChanged(Editable s){
        String text = textView.getText().toString();
        validate(textView);
    }

}
