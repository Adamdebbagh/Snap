package tekwin.org.snap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by adamdebbagh on 2/4/15.
 */
public class FriendsFragment extends ListFragment {
    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseUser mCurrentUser ;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected TextView emptyLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        emptyLabel = (TextView) rootView.findViewById(R.id.emptyfriendslabel);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Add progress bar
        getActivity().setProgressBarIndeterminateVisibility(true);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstant.KEY_FRIENDS_RELATION);
        
        // add a friends list
        friendsList();
    }

    private void friendsList() {
        //query friendsRelation field
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstant.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                // Remove progress bar
                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    // hide the empty label textView
                    emptyLabel.setVisibility(View.GONE);
                   // get friends list
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser friend : mFriends) {
                        usernames[i] = friend.getUsername();
                        i++;
                    }
                    // display usernames in a list view
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getListView().getContext(),
                            android.R.layout.simple_list_item_1, usernames);
                    getListView().setAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    // display error message
                    erroMessageDialog(e);
                }
            }
        });
    }
    private void erroMessageDialog(ParseException e) {
        AlertDialog.Builder build = new AlertDialog.Builder(getListView().getContext())
                .setTitle(getString(R.string.error_title))
                .setMessage(e.getMessage())
                .setPositiveButton(android.R.string.ok, null);

        Dialog dialog = build.create();
        dialog.show();
    }
}
