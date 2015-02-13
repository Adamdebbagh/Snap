package tekwin.org.snap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

// Created by adamdebbagh on 2/4/15
public class InboxFragment extends ListFragment {
    public static final String TAG = InboxFragment.class.getSimpleName();
    protected List<ParseObject> mMessages;
    protected TextView emptyLabel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inbox, container,false);
        emptyLabel = (TextView) rootView.findViewById(R.id.emptyInboxLabel);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setProgressBarIndeterminateVisibility(true);

        //querry messages class
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstant.CLASSES_MESSAGES);
        query.whereEqualTo(ParseConstant.KEY_RECIPIENT_ID, ParseUser.getCurrentUser().getObjectId());
        query.addAscendingOrder(ParseConstant.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if(e == null) {
                    // hide the empty label textView
                    emptyLabel.setVisibility(View.GONE);
                    //we found messages
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstant.KEY_SENDER_NAME);
                        Log.d(TAG, "++  message inbox  ++ : " + message.getString(ParseConstant.KEY_FILE_TYPE));

                        i++;
                    }
                    // display usernames in a list view
                    //ArrayAdapter<String> adapter = new ArrayAdapter<>(getListView().getContext(),
                      //      android.R.layout.simple_list_item_1, usernames);
                   //getListView().setAdapter(adapter);
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        getListView().setAdapter(adapter);
                    }
                    else {
                        //refill the adapter
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);

                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstant.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstant.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals(ParseConstant.TYPE_IMAGE)){

            //view the image
            Intent intent = new Intent(getActivity(),ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        }
        else{
            // view the video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri,"video/*");
            startActivity(intent);
        }

        // Delete it
        List<String> ids = message.getList(ParseConstant.KEY_RECIPIENT_ID);

        if (ids.size() == 1){
            //last recipient, delete the whole thing!
            message.deleteInBackground();

        }
        else {
            // remove the recipient and save
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToRemove = new ArrayList<>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstant.KEY_RECIPIENT_ID,idsToRemove);
            message.saveInBackground();


        }
    }
}
