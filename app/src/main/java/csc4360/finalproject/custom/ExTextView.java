package csc4360.finalproject.custom;

/**
 * Created by stephen on 4/10/2017.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;
 import csc4360.finalproject.R;

public class ExTextView extends TextView {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public ExTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public ExTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public ExTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExView);

            String fontName = a.getString(R.styleable.ExView_font);

            try {
                if (fontName != null) {
                    Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                    setTypeface(myTypeface);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            a.recycle();
        }
    }
}

