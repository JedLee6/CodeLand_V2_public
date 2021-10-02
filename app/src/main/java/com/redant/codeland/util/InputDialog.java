package com.redant.codeland.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.redant.codeland.R;

public class InputDialog extends AlertDialog implements View.OnClickListener {
    private EditText etName;
    private Button buttonConfrim;
    private Button buttonCancel;
    private OnEditInputFinishedListener mListener;

    public interface OnEditInputFinishedListener{
        void editInputFinished(String name);
    }

    public InputDialog(Context context, OnEditInputFinishedListener mListener){
        super(context);
        this.mListener=mListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_dialog);

        etName=(EditText)findViewById(R.id.record_input_name);
        buttonCancel=(Button)findViewById(R.id.button_cancel);
        buttonConfrim=(Button)findViewById(R.id.button_confirm);
        buttonCancel.setOnClickListener(this);
        buttonConfrim.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.button_confirm){
            if(mListener!=null){
                String name=etName.getText().toString();
                mListener.editInputFinished(name);
            }
            dismiss();
        }else{
            dismiss();
        }
    }
}
