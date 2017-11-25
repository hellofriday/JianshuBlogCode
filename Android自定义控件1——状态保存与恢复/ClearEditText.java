package com.futureproteam.base.view;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.futureproteam.base.R;

import java.util.concurrent.atomic.AtomicInteger;

public class ClearEditText extends FrameLayout {
    private EditText editText;
    private ImageView imageView;

    private boolean hasFocus = false;

    public ClearEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        editText = new EditText(context, attrs);
        editText.setId(generateViewId());
        layoutParams.bottomMargin = 0;
        layoutParams.topMargin = 0;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        editText.setPadding(0, 0, 0, 0);
        addView(editText, layoutParams);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageView = new ImageView(context);
        editText.setId(generateViewId());
        layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        layoutParams.rightMargin = 20;
        imageView.setImageResource(R.drawable.ic_edit_delete);
        imageView.setVisibility(GONE);
        addView(imageView, layoutParams);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ClearEditText.this.hasFocus = hasFocus;
                if (hasFocus) {
                    if (isEmpty()) {
                        imageView.setVisibility(View.INVISIBLE);
                    } else {
                        imageView.setVisibility(View.VISIBLE);
                    }
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (hasFocus) {
                    if (s == null || s.length() == 0) {
                        imageView.setVisibility(View.INVISIBLE);
                    } else {
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        editText.setHintTextColor(ContextCompat.getColor(context, R.color.text_gray));
    }

    private boolean isEmpty() {
        return editText.getText() == null || editText.getText().length() == 0;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setText(int ids) {
        editText.setText(ids);
    }

    public final void setText(CharSequence text) {
        editText.setText(text);
    }

    /**
     * An {@code int} value that may be updated atomically.
     */
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    /**
     * 动态生成View ID
     * API LEVEL 17 以上View.generateViewId()生成
     * API LEVEL 17 以下需要手动生成
     */
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (ss.text != null) {
            editText.setText(ss.text);

            if (ss.selEnd >= 0) {
                editText.setSelection(ss.selEnd);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        String mText = editText.getText() != null ? editText.getText().toString() : null;
        if (mText == null){
            return superState;
        }

        int end = editText.getSelectionEnd();
        SavedState ss = new SavedState(superState);
        ss.text = mText;
        ss.selEnd = end;
        return ss;
    }

    /**
     * User interface state that is stored by TextView for implementing
     * {@link View#onSaveInstanceState}.
     */
    public static class SavedState extends BaseSavedState {
        int selEnd = -1;
        String text;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(selEnd);
            out.writeString(text);
        }

        @Override
        public String toString() {
            String str = "ClearEditText.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " end=" + selEnd;
            if (text != null) {
                str += " text=" + text;
            }
            return str + "}";
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
            selEnd = in.readInt();
            text = in.readString();

        }
    }
}
