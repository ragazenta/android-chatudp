package io.github.ragazenta.chatudp.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.github.ragazenta.chatudp.R;
import io.github.ragazenta.chatudp.model.Chat;
import io.github.ragazenta.chatudp.ui.adapters.ChatAdapter;

/**
 * A {@link BaseFragment} subclass.
 */
public class ChatFragment extends BaseFragment implements ChatReceiver {

    private EditText mInputChat;
    private RecyclerView mRecyclerView;
    private Button mButtonSend;

    private ChatSender mChatSender;
    private ChatAdapter mAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mChatSender = (ChatSender) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ChatSender");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ChatAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mInputChat = (EditText) view.findViewById(R.id.input_chat);
        mButtonSend = (Button) view.findViewById(R.id.button_send);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mInputChat.getText().toString();
                mInputChat.setText(null);
                mChatSender.sendMessage(message);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        mRecyclerView = null;
        mButtonSend = null;
        mInputChat = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mChatSender = null;
        super.onDetach();
    }

    @Override
    public void receiveMessage(Chat chat) {
        mAdapter.addData(chat);
    }
}
