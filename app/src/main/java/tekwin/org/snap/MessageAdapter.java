package tekwin.org.snap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by adamdebbagh on 2/13/15.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject>{
    public static final String TAG = MessageAdapter.class.getSimpleName();

    protected Context  mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context,R.layout.message_item, messages);

        mContext = context;
        mMessages = messages;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderlabel);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        ParseObject message = mMessages.get(position);
        Log.d(TAG,"++  message  ++ : "+ message.getString(ParseConstant.KEY_FILE_TYPE));

        if(message.getString(ParseConstant.KEY_FILE_TYPE).equals(ParseConstant.TYPE_IMAGE)){

            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
        }
        else {
            holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
        }

        holder.nameLabel.setText(message.getString(ParseConstant.KEY_SENDER_NAME));
        return convertView;
    }

    private static class ViewHolder{
        ImageView iconImageView;
        TextView nameLabel;

    }
    public void refill(List<ParseObject> messages){
            mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();


    }
}
