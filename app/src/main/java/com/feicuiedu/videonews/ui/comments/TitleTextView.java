package com.feicuiedu.videonews.ui.comments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 用于{@link CommentsActivity}标题的TextView。
 * <p/>
 * 重写{@link #isFocused()}方法，让走马灯效果(<em>android:ellipsize="marquee"</em>)始终播放。
 */
public class TitleTextView extends TextView {
    public TitleTextView(Context context) {
        super(context);
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public boolean isFocused() {
        return true;
    }
}
