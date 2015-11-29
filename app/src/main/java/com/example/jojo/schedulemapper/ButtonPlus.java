package com.example.jojo.schedulemapper;

import android.content.*;
import android.util.*;
import android.widget.Button;

/**
 * Created by Guizus on 11/28/15.
 */
public class ButtonPlus extends Button {
    public ButtonPlus(Context context) {
        super(context);
    }

    public ButtonPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public ButtonPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }
}
