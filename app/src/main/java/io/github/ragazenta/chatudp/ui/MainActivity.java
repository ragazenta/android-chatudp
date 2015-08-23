package io.github.ragazenta.chatudp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import io.github.ragazenta.chatudp.R;
import io.github.ragazenta.chatudp.model.Chat;
import io.github.ragazenta.chatudp.service.ChatService;
import io.github.ragazenta.chatudp.service.ChatService.ChatBinder;


public class MainActivity extends BaseActivity implements ChatSender {

    private View mProgressView;
    private ChatService mChatService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mChatService = ((ChatBinder) service).getService();
            mBound = true;
            hideProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private IntentFilter mChatFilter = new IntentFilter(ChatService.ACTION_RECEIVE_CHAT);
    private BroadcastReceiver mChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
            Chat c = intent.getParcelableExtra(ChatService.EXTRA_PARCEL_CHAT);
            if (f != null && f instanceof ChatReceiver) {
                ((ChatReceiver) f).receiveMessage(c);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarShadowVisible(false);
        setTitle(null);
        mProgressView = findViewById(R.id.progress_container);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ChatFragment(), "Chat")
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(getApplicationContext(), ChatService.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mChatReceiver, mChatFilter);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mChatReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        super.onStop();
    }

    @Override
    public void sendMessage(String message) {
        if (mBound) {
            mChatService.sendMessage(message);
        }
    }

    private void hideProgress() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressView.animate()
                        .translationY(-mProgressView.getHeight())
                        .setDuration(500l)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                setToolbarShadowVisible(true);
                                setTitle(getString(R.string.app_name));
                            }
                        });
            }
        }, 2000l);
    }
}
