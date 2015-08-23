package io.github.ragazenta.chatudp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.ragazenta.chatudp.R;
import io.github.ragazenta.chatudp.model.Chat;

/**
 * Created by renjaya on 8/23/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<Chat> data;
    private int size;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat c = data.get(position);
        holder.message.setText(c.getMessage());
        holder.sender.setText(c.getSender());
        holder.timestamp.setText(c.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : (size = data.size());
    }

    public void setData(List<Chat> data) {
        List<Chat> oldData = this.data;
        this.data = data;
        size = data.size();
        clear(oldData);
        notifyDataSetChanged();
    }

    public void addData(Chat chat) {
        if (data == null) {
            data = new ArrayList<Chat>();
        }
        data.add(chat);
        size++;
        notifyItemInserted(size - 1);
    }

    private void clear(List<Chat> data) {
        // data.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView sender;
        TextView timestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);
            sender = (TextView) itemView.findViewById(R.id.sender);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }
}
