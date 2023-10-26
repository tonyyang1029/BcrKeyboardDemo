package com.oem.demo.bcrkeyboarddemo;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.DigitsKeyListener;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int BCR_VID = 0x0C2E;
    private final int BCR_PID = 0x10CF;
    private TextView mText1;
    private TextView mText2;
    private StringBuilder mStrKeyChars;
    private StringBuilder mStrKeyCharsWithMeta;
    private StringBuilder mStrBcrData;
    private ArrayList<Integer> mKeyDownCodeList;
    private ArrayList<KeyEvent> mKeyEventList;
    private ArrayList<KeyEvent> mKeyDownEventList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText1 = findViewById(R.id.LAYOUT_BCR_DATA1);
        mText2 = findViewById(R.id.LAYOUT_BCR_DATA2);
        //mText2.setKeyListener(DigitsKeyListener.getInstance());
        //mText2.setKeyListener(TextKeyListener.getInstance());

        mStrKeyChars = new StringBuilder();
        mStrKeyCharsWithMeta = new StringBuilder();
        mStrBcrData = new StringBuilder();
        mKeyDownCodeList = new ArrayList<>();
        mKeyEventList = new ArrayList<>();
        mKeyDownEventList = new ArrayList<>();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (false && event.getDevice().getVendorId() == BCR_VID && event.getDevice().getProductId() == BCR_PID) {
            mKeyEventList.add(event);
            mStrKeyChars.append(String.valueOf(event.getDisplayLabel()));
            int i = event.getUnicodeChar(event.getMetaState());
            mStrKeyCharsWithMeta.append(String.valueOf((char)i));
            if (mStrKeyChars.length() > 14 && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                mText1.append(mStrKeyChars);
                mText1.append("\n");
                mText1.append(mStrKeyCharsWithMeta.toString());
                //
                mKeyEventList.clear();
                mStrKeyChars = new StringBuilder();
                mStrKeyCharsWithMeta = new StringBuilder();
            }

            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (true && event.getDevice().getVendorId() == BCR_VID && event.getDevice().getProductId() == BCR_PID) {
            mKeyEventList.add(event);

            int i = event.getUnicodeChar(event.getMetaState());
            if (isValidCharacter(i)) {
                mStrKeyChars.append(String.valueOf(event.getDisplayLabel()));
                mStrKeyCharsWithMeta.append(String.valueOf((char) i));
            }

            if (mStrKeyChars.length() > 14 && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                int startOfMsgGet = mStrKeyCharsWithMeta.indexOf("MSGGET");
                String strOfBcrDataLength = mStrKeyCharsWithMeta.substring(startOfMsgGet + 6, startOfMsgGet + 10);
                int lengthOfBcrData = Integer.valueOf(strOfBcrDataLength).intValue();
                String strOfBcrData = mStrKeyCharsWithMeta.substring(mStrKeyCharsWithMeta.length() - lengthOfBcrData + 1);
                //
                mText1.append(mStrKeyChars.toString());
                mText1.append("\n");
                mText1.append(mStrKeyCharsWithMeta.toString());
                mText1.append("\n");
                mText1.append(strOfBcrData);
                mText1.append("\n\n");
                //
                mKeyEventList.clear();
                mStrKeyChars = new StringBuilder();
                mStrKeyCharsWithMeta = new StringBuilder();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean isValidKey(int code) {
        boolean ret = true;

        if (code >= KeyEvent.KEYCODE_F1 && code <= KeyEvent.KEYCODE_F12) {
            ret = false;
        } else if (code == KeyEvent.KEYCODE_ENTER && mKeyEventList.size() < 14) {
            ret = false;
        } else if (code >= KeyEvent.KEYCODE_SHIFT_LEFT && code <= KeyEvent.KEYCODE_SHIFT_RIGHT) {
            ret = false;
        }

        return ret;
    }

    private boolean isValidCharacter(int c) {
        boolean ret = false;

        if (c >= ' ' && c <= '~') {
            ret = true;
        }

        /*if (c >= 'a' && c <= 'z') {
            ret = true;
        } else if (c >= 'A' && c <= 'Z') {
            ret = true;
        } else if (c >= '0' && c <= '9') {
            ret = true;
        } */

        return ret;
    }
}