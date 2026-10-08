package com.hiddenkeyboard.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends android.app.Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 40);

        TextView title = new TextView(this); title.setText("Hidden Keyboard"); title.setTextSize(28); title.setTextColor(0xFF111111);
        TextView info = new TextView(this); info.setText("كيبورد أندرويد مع وضع عادي ووضع إخفاء النص محليًا على الجهاز.\n\nفعّل الكيبورد من الإعدادات ثم اختبره داخل واتساب."); info.setTextSize(18); info.setPadding(0,30,0,30);
        Button enable = new Button(this); enable.setText("تفعيل الكيبورد");
        enable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        Button choose = new Button(this); choose.setText("اختيار الكيبورد");
        choose.setOnClickListener(v -> {
            android.view.inputmethod.InputMethodManager imm=(android.view.inputmethod.InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showInputMethodPicker();
        });
        root.addView(title); root.addView(info); root.addView(enable); root.addView(choose);
        setContentView(root);
    }
}
