package com.hiddenkeyboard.app;

import android.inputmethodservice.InputMethodService;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import java.util.Locale;

public class HiddenKeyboardService extends InputMethodService {
    private LinearLayout root;
    private boolean hiddenMode = false;
    private boolean arabic = true;

    @Override public View onCreateInputView() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(6,6,6,6); root.setBackgroundColor(Color.rgb(235,235,235));
        addTopBar();
        addRows();
        return root;
    }

    private void addTopBar() {
        LinearLayout bar = new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL);
        TextView mode = label(hiddenMode ? "🔐 مخفي" : "⌨ عادي", 15); bar.addView(mode, new LinearLayout.LayoutParams(0, 48, 1));
        Button hide = small(hiddenMode ? "عادي" : "إخفاء"); hide.setOnClickListener(v -> { hiddenMode = !hiddenMode; buildKeyboard(); }); bar.addView(hide);
        Button decode = small("فك"); decode.setOnClickListener(v -> decodeCurrent()); bar.addView(decode);
        Button lang = small(arabic ? "EN" : "عربي"); lang.setOnClickListener(v -> { arabic = !arabic; buildKeyboard(); }); bar.addView(lang);
        root.addView(bar);
    }

    private void addRows() {
        String[][] ar = {{"ض","ص","ث","ق","ف","غ","ع","ه","خ","ح","ج","د"},{"ش","س","ي","ب","ل","ا","ت","ن","م","ك","ط"},{"ئ","ء","ؤ","ر","لا","ى","ة","و","ز","ظ"}};
        String[][] en = {{"q","w","e","r","t","y","u","i","o","p"},{"a","s","d","f","g","h","j","k","l"},{"z","x","c","v","b","n","m"}};
        String[][] rows = arabic ? ar : en;
        for (String[] row: rows) { LinearLayout line=new LinearLayout(this); for(String k:row) addKey(line,k,1f); root.addView(line,new LinearLayout.LayoutParams(-1,52)); }
        LinearLayout bottom=new LinearLayout(this);
        addKey(bottom,"⌫",1.2f).setOnClickListener(v -> backspace());
        Button space=key("مسافة"); space.setOnClickListener(v->type(" ")); bottom.addView(space,new LinearLayout.LayoutParams(0,52,3f));
        addKey(bottom,"↵",1.2f).setOnClickListener(v -> sendEnter()); root.addView(bottom,new LinearLayout.LayoutParams(-1,52));
    }

    private void buildKeyboard(){ root.removeAllViews(); addTopBar(); addRows(); }
    private Button addKey(LinearLayout line, String text, float w){ Button b=key(text); line.addView(b,new LinearLayout.LayoutParams(0,52,w)); b.setOnClickListener(v->type(text)); return b; }
    private Button key(String text){ Button b=new Button(this); b.setText(text); b.setTextSize(16); b.setAllCaps(false); GradientDrawable g=new GradientDrawable(); g.setColor(Color.WHITE); g.setCornerRadius(12); b.setBackground(g); b.setPadding(0,0,0,0); return b; }
    private Button small(String text){ Button b=key(text); b.setTextSize(13); b.setPadding(10,0,10,0); return b; }
    private TextView label(String t,int s){ TextView v=new TextView(this); v.setText(t); v.setTextSize(s); v.setGravity(Gravity.CENTER_VERTICAL); v.setTextColor(Color.DKGRAY); return v; }

    private void type(String s){
        InputConnection ic=getCurrentInputConnection(); if(ic==null)return;
        if(hiddenMode && !s.equals("مسافة") && !s.equals("⌫") && !s.equals("↵")) ic.commitText(HiddenCodec.encode(s),1); else ic.commitText(s.equals("مسافة")?" ":s,1);
    }
    private void backspace(){ InputConnection ic=getCurrentInputConnection(); if(ic!=null) ic.deleteSurroundingText(1,0); }
    private void sendEnter(){ InputConnection ic=getCurrentInputConnection(); if(ic!=null) ic.sendKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_ENTER)); }
    private void decodeCurrent(){
        InputConnection ic=getCurrentInputConnection(); if(ic==null)return;
        CharSequence selected=ic.getSelectedText(0); if(selected==null || selected.length()==0) { selected=ic.getTextBeforeCursor(5000,0); }
        if(selected==null)return; String d=HiddenCodec.decode(selected.toString()); if(d!=null) { ic.commitText(d,1); }
    }
}
