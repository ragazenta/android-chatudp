package io.github.ragazenta.chatudp.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import io.github.ragazenta.chatudp.R;

/**
 * Created by renjaya on 8/23/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbarActionBar;
    private TextView mToolbarTitleView;
    private TextView mToolbarSubtitleView;
    private View mToolbarShadow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
        setTitle(getTitle());
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mToolbarTitleView != null) {
            mToolbarTitleView.setText(title);
        }
        setSubtitle(null);
        super.setTitle(null);
    }

    public void setSubtitle(CharSequence subtitle) {
        if (mToolbarSubtitleView != null) {
            mToolbarSubtitleView.setText(subtitle);
            mToolbarSubtitleView.setVisibility(subtitle == null ? View.GONE : View.VISIBLE);
        }
    }

    protected Toolbar getActionBarToolbar() {
        if (mToolbarActionBar == null) {
            mToolbarActionBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mToolbarActionBar != null) {
                setSupportActionBar(mToolbarActionBar);
                mToolbarTitleView = (TextView) mToolbarActionBar.findViewById(R.id.toolbar_title);
                mToolbarSubtitleView = (TextView) mToolbarActionBar.findViewById(R.id.toolbar_subtitle);
            }
            mToolbarShadow = findViewById(R.id.toolbar_shadow);
        }
        return mToolbarActionBar;
    }

    protected final void setToolbarShadowVisible(boolean visible) {
        if (mToolbarShadow != null) {
            mToolbarShadow.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
