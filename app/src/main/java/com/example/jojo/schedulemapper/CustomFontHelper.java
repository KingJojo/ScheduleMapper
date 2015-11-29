package com.example.jojo.schedulemapper;

import android.widget.TextView;
import android.content.*;
import android.content.res.*;
import android.util.*;
import android.graphics.Typeface;

/**
 * Created by Guizus on 11/28/15.
 *
 * Created as a helper class to set a font on a textView; as button is a subclass
 * of textView.
 *
 * @methods: public static void setCustomFont(TextView textview, Context context, AttributeSet attrs);
 *           public static void setCustomFont(TextView textview, String font, Context context);
 */
public class CustomFontHelper {
    /**
     * Sets a font on a textview based on the custom com.my.package:font attribute
     * If the custom font attribute isn't found in the attributes nothing happens
     * @param textview
     * @param context
     * @param attrs
     */
    public static void setCustomFont(TextView textview, Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);
        String font = a.getString(R.styleable.CustomFont_font);
        setCustomFont(textview, font, context);
        a.recycle();
    }

    /**
     * Sets a font on a textview
     * @param textview
     * @param font
     * @param context
     */
    public static void setCustomFont(TextView textview, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            textview.setTypeface(tf);
        }
    }
}
