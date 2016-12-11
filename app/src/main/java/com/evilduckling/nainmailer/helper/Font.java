package com.evilduckling.nainmailer.helper;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class Font {

    public static final String MATERIAL_ICONS = "MaterialIcons-Regular.ttf";

    private static Map<String, Typeface> fontCache = new HashMap<>();

    /**
     * Apply material icon font to a TextView.
     */
    public static void loadMaterialIcon(TextView view, Context context) {
        loadFont(view, MATERIAL_ICONS, context);
    }

    /**
     * Apply a font to the text view.
     */
    private static void loadFont(TextView view, String fontName, Context context) {
        view.setTypeface(getFont(fontName, context));
    }

    /**
     * Underline all text of a textView.
     */
    public static void underline(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * Get Typeface from internal cache.
     */
    private static Typeface getFont(String fontName, Context context) {

        if (fontCache.containsKey(fontName)) {

            // Get from cache
            return fontCache.get(fontName);

        } else {

            // Create and store in cache
            Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);
            fontCache.put(fontName, font);
            return font;

        }

    }

}
