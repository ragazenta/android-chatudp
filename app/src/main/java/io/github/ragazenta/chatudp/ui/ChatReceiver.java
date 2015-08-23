package io.github.ragazenta.chatudp.ui;

import io.github.ragazenta.chatudp.model.Chat;

/**
 * Created by renjaya on 8/23/15.
 */
public interface ChatReceiver {
    void receiveMessage(Chat chat);
}
