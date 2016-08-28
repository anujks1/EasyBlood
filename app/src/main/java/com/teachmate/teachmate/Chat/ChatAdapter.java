
package com.teachmate.teachmate.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teachmate.teachmate.R;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    private Context            mContext;
    private ArrayList<Message> mMessages;

    public ChatAdapter(Context context, ArrayList<Message> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = (Message) this.getItem(position);

        ViewHolder holder;
        if (message.isMine()) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_sent, parent,
                    false);
            holder.chatRow = (LinearLayout) convertView.findViewById(R.id.chat_row);
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            holder.time = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(holder);
            holder.message.setText(message.getMessage());
            holder.time.setText(message.getTime());
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_received, parent,
                    false);
            holder.chatRow = (LinearLayout) convertView.findViewById(R.id.chat_row);
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            holder.time = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(holder);
            holder.message.setText(message.getMessage());
            holder.time.setText(message.getTime());
        }

        return convertView;
    }

    private static class ViewHolder
    {
        LinearLayout chatRow;
        TextView     message;
        TextView     time;
    }

    @Override
    public long getItemId(int position) {
        // Unimplemented, because we aren't using Sqlite.
        return position;
    }
}
