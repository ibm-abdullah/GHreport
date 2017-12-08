package com.braimahabdullah.ghreport;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Ibrahim-Abdullah on 12/7/2017.
 */

public class TextViewValidator extends TextValidator implements View.OnFocusChangeListener {

    public TextViewValidator(TextView textView) {
        super(textView);
    }
    @Override
    public void validate(TextView textView) {
        if(TextUtils.isEmpty(textView.getText().toString())){
//            android.support.design.widget.TextInputLayout parent = (android.support.design.widget.TextInputLayout) textView.getParent();
//            textView.setError(parent.getHint().toString()+ "is required");
            textView.setError("Required");
        }else {
            textView.setError(null);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus){
            validate((TextView)v);
        }
    }
    public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */}
}
