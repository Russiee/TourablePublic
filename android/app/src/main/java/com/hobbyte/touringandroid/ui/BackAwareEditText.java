package com.hobbyte.touringandroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.hobbyte.touringandroid.ui.activity.StartActivity;

/**
 * @author Jonathan
 * An EditText that knows when the user had pressed back to hide the keyboard
 */
public class BackAwareEditText extends EditText {

    private StartActivity activity;

    /**
     * Creates an edittext that knows when the back button has been pressed
     * @param context context the editText is in
     * @param attrs attributes
     */
    public BackAwareEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Gives reference to the startActivity so it can call the hideInput() method
     * @param activity the startActivity to reference
     */
    public void setCallBackClass(StartActivity activity) {
        this.activity = activity;
    }

    /**
     * Calls the activity's hideInput() method when back button is pressed
     * @param keyCode code of the pressed key
     * @param event event to perform
     * @return success of event
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activity.hideInput();
        }

        //so default behaviour still occurs
        return super.dispatchKeyEvent(event);
    }
}
