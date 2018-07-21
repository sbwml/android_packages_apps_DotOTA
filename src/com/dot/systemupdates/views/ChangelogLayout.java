package com.dot.systemupdates.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dot.systemupdates.R;

public class ChangelogLayout extends LinearLayout {

    View view;
    TextView c_system, d_system,
            c_settings, d_settings,
            c_launcher, d_launcher,
            c_sec_patch , d_sec_patch,
            c_misc, d_misc;

    public ChangelogLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ChangelogLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        view = inflate(context, R.layout.changelog_template, this);
        c_system = findViewById(R.id.c_system);
        d_system = findViewById(R.id.d_system);
        c_settings = findViewById(R.id.c_settings);
        d_settings = findViewById(R.id.d_settings);
        c_launcher = findViewById(R.id.c_launcher);
        d_launcher = findViewById(R.id.d_launcher);
        c_sec_patch = findViewById(R.id.c_sec_patch);
        d_sec_patch = findViewById(R.id.d_sec_patch);
        c_misc = findViewById(R.id.c_misc);
        d_misc = findViewById(R.id.d_misc);
    }

    public void setSystem(String str) {
        if (c_system.getVisibility() != VISIBLE && d_system.getVisibility() != VISIBLE && str != null) {
            c_system.setVisibility(VISIBLE);
            d_system.setVisibility(VISIBLE);
        }
        d_system.setText(str);
    }

    public void setSettings(String str) {
        if (c_settings.getVisibility() != VISIBLE && d_settings.getVisibility() != VISIBLE && str != null) {
            c_settings.setVisibility(VISIBLE);
            d_settings.setVisibility(VISIBLE);
        }
        d_settings.setText(str);
    }

    public void setLauncher(String str) {
        if (c_launcher.getVisibility() != VISIBLE && d_launcher.getVisibility() != VISIBLE && str != null) {
            c_launcher.setVisibility(VISIBLE);
            d_launcher.setVisibility(VISIBLE);
        }
        d_launcher.setText(str);
    }

    public void setSecurityPatch(String str) {
        if (c_sec_patch.getVisibility() != VISIBLE && d_sec_patch.getVisibility() != VISIBLE && str != null) {
            c_sec_patch.setVisibility(VISIBLE);
            d_sec_patch.setVisibility(VISIBLE);
        }
        d_sec_patch.setText(String.format(" Security Patch upstream to %s", str));
    }

    public void setMisc(String str) {
        if (c_misc.getVisibility() != VISIBLE && d_misc.getVisibility() != VISIBLE && str != null) {
            c_misc.setVisibility(VISIBLE);
            d_misc.setVisibility(VISIBLE);
        }
        d_misc.setText(str);
    }
}
