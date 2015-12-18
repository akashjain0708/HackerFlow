package com.example.akash.hackerflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.github.dkharrat.nexusdialog.controllers.LabeledFieldController;

/**
 * Created by Akash on 11/21/2015.
 */
public class ButtonElement extends LabeledFieldController {

        public ButtonElement(Context ctx, String name) {
            super(ctx, name, " ", false);
        }

        @Override
        protected View createFieldView() {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return inflater.inflate(R.layout.activity_hacker_application, null);
        }

    @Override
    public void refresh() {

    }
}
