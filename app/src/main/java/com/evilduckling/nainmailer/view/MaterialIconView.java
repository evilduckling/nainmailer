package com.evilduckling.nainmailer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.evilduckling.nainmailer.helper.Font;

public class MaterialIconView extends TextView {

    public MaterialIconView(Context context) {
        super(context);
        init(context);
    }

    public MaterialIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Font.loadMaterialIcon(this, context);
    }

}
