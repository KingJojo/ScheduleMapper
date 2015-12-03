package com.example.jojo.schedulemapper;

import android.content.*;
import android.graphics.Typeface;
import java.util.*;

/**
 * Created by Guizus on 11/28/15.
 *
 * Created to reduce memory usage on older devices while using custom fonted buttons
 *
 * @variable: Hashtable<String, Typeface> fontCache
 * @methods: public static Typeface get(String name, Context context);
 */

public class FontCache {
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    /**
     * Puts typeface into fontCache if not already in the fontCache
     * @param name
     * @param context
     */
    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}
