package project.se3354.sms_messenger_group8;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
//import android.widget.ImageView;
import android.widget.TextView;

public class ContactsAdapter extends ArrayAdapter<MyMessage> {
    private final Context context;
    private final ArrayList<MyMessage> data;
    private final int layoutResourceId;

    public ContactsAdapter(Context context, int layoutResourceId, ArrayList<MyMessage> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View message = convertView;
        ViewHolder holder = null;

        if(message == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            message = inflater.inflate(R.layout.message_layout, parent, false);

            holder = new ViewHolder();
            //holder.icon = (ImageView)message.findViewById(R.id.icon);
            holder.contactName = (TextView)message.findViewById(R.id.contactName);
            holder.messageDate = (TextView)message.findViewById(R.id.messageDate);
            holder.messageBody = (TextView)message.findViewById(R.id.messageBody);
            holder.isDraft = (TextView)message.findViewById(R.id.isDraft);

            message.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)message.getTag();
        }

        MyMessage contact = data.get(position);

        //holder.icon.setImageBitmap(contact.getIcon());
        holder.contactName.setText(contact.getContactName());
        holder.messageDate.setText(contact.getMessageDate());
        holder.messageBody.setText(contact.getMessageBody());
        holder.isDraft.setText(contact.isDraft());

        return message;
    }

    static class ViewHolder
    {
    	//ImageView icon;
        TextView contactName;
        TextView messageDate;
        TextView messageBody;
        TextView isDraft;
    }

}
