package io.github.ragazenta.chatudp.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.ragazenta.chatudp.model.Chat;

public class ChatService extends Service {

    public static final String ACTION_RECEIVE_CHAT = "action_receive_chat";
    public static final String EXTRA_PARCEL_CHAT = "extra_parcel_chat";

    static final String TAG = "ChatService";
    static final String MULTICAST_LOCK_TAG = "io.github.ragazenta.chatudp.service.ChatService.MulticastLock";
    static final String MULTICAST_GROUP = "224.0.0.1";
    static final int PORT = 8123;
    static final int BUFFER = 1024;

    private ChatBinder mBinder;
    private ReceiverThread mReceiverThread;
    private SenderThread mSenderThread;
    private SenderHandler mSenderHandler;

    public ChatService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        mBinder = new ChatBinder(this);
        ChatHandler chatHandler = new ChatHandler(this);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mReceiverThread = new ReceiverThread("ReceiverThread", Process.THREAD_PRIORITY_BACKGROUND, chatHandler, wifiManager);
        mSenderThread = new SenderThread("SenderThread", Process.THREAD_PRIORITY_BACKGROUND);
        mReceiverThread.start();
        mSenderThread.start();
        mSenderHandler = new SenderHandler(mSenderThread.getLooper());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mReceiverThread.setRunning(false);
        mSenderHandler.sendEmptyMessage(SenderHandler.QUIT);
        mSenderThread.quit();
        Log.d(TAG, "onDestroy");
    }

    public void sendMessage(String message) {
        mSenderHandler.obtainMessage(SenderHandler.SEND, message).sendToTarget();
    }

    void onReceiveChat(Chat chat) {
        Intent broadcast = new Intent(ACTION_RECEIVE_CHAT);
        broadcast.putExtra(EXTRA_PARCEL_CHAT, chat);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
    }

    public static class ChatBinder extends Binder {

        WeakReference<ChatService> reference;

        public ChatBinder(ChatService service) {
            this.reference = new WeakReference<ChatService>(service);
        }

        public ChatService getService() {
            return reference.get();
        }
    }

    @WorkerThread
    private static class ReceiverThread extends Thread {

        ChatHandler handler;
        WifiManager wifiManager;
        MulticastSocket socket;
        volatile boolean isRunning;
        int priority;
        SimpleDateFormat dateFormat;

        public ReceiverThread(String name, int priority, ChatHandler handler, WifiManager wifiManager) {
            super(name);
            this.priority = priority;
            this.handler = handler;
            this.wifiManager = wifiManager;
            this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                this.socket = new MulticastSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.myTid(), priority);
            if (socket == null) {
                return;
            }
            WifiManager.MulticastLock lock = wifiManager.createMulticastLock(MULTICAST_LOCK_TAG);
            lock.acquire();
            isRunning = true;
            try {
                InetAddress multicastGroup = InetAddress.getByName(MULTICAST_GROUP);
                socket.joinGroup(multicastGroup);
                while (isRunning) {
                    byte[] data = new byte[BUFFER];
                    DatagramPacket packet = new DatagramPacket(data, BUFFER);
                    socket.receive(packet);
                    Chat chat = new Chat();
                    chat.setTimestamp(dateFormat.format(new Date()));
                    chat.setMessage(new String(data, "US-ASCII"));
                    chat.setSender(packet.getAddress().getHostAddress());
                    handler.obtainMessage(0, chat).sendToTarget();
                }
                socket.leaveGroup(multicastGroup);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
            lock.release();
        }

        public void setRunning(boolean running) {
            isRunning = running;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    @WorkerThread
    private static class SenderThread extends HandlerThread {

        public SenderThread(String name, int priority) {
            super(name, priority);
        }
    }

    @WorkerThread
    private static class SenderHandler extends Handler {

        static final int SEND = 1;
        static final int QUIT = 2;
        DatagramSocket socket;

        public SenderHandler(Looper looper) {
            super(looper);
            try {
                this.socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND:
                    if (socket != null) {
                        String message = (String) msg.obj;
                        try {
                            DatagramPacket packet = new DatagramPacket(message.getBytes("US-ASCII"),
                                    message.length(), InetAddress.getByName(MULTICAST_GROUP), PORT);
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case QUIT:
                    if (socket != null) socket.close();
                    break;
            }
        }
    }

    @UiThread
    private static class ChatHandler extends Handler {

        WeakReference<ChatService> reference;

        public ChatHandler(ChatService service) {
            this.reference = new WeakReference<ChatService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatService service = reference.get();
            if (service != null) service.onReceiveChat((Chat) msg.obj);
        }
    }
}
