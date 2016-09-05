package com.coulcod.oauth2.view;

import android.content.Context;
import android.view.MotionEvent;
import android.webkit.WebView;

public class KeyboardWebView extends WebView {
    public KeyboardWebView(Context context) {
        super(context);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                if (!hasFocus()) {
                    requestFocus();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
}