package tekwin.org.snap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends ActionBarActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    protected ListView mListView;
    protected List<ParseUser> mUsers;
    protected ParseUser mCurrentUser ;
    protected ParseRelation<ParseUser> mFriendsRelation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //progress bar
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        // make users list checkable
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // if user clicked on a list item, add or remove friend
                addRemoveFriend(position);
                //save click action in background
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstant.KEY_FRIENDS_RELATION);

         //Add progress bar
        setSupportProgressBarIndeterminateVisibility(true);
        // Set up a query for users
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstant.KEY_USERNAME);
        query.setLimit(ParseConstant.QUERY_LIMIT);
        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> users, ParseException e) {
                //remove Progress bar
                setSupportProgressBarIndeterminateVisibility(false);
                if(e == null) {
                    // success. get users usernames
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers){
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    // display usernames in a list view
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked,usernames);
                    getListView().setAdapter(adapter);
                    // add Friend CheckMarks
                    addFriendCheckMarks();
                }
                else{
                Log.e(TAG,e.getMessage());
                    // display error message
                    erroMessageDialog(e);
                }
            }
        });
    }

    private void erroMessageDialog(ParseException e) {
        AlertDialog.Builder build = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(e.getMessage())
                .setPositiveButton(android.R.string.ok, null);

        Dialog dialog = build.create();
        dialog.show();
    }
    private void addRemoveFriend(int position) {

        if (mListView.isItemChecked(position)){
            //add Friends
            mFriendsRelation.add(mUsers.get(position));
        }
        else {
            //remove friend
            mFriendsRelation.remove(mUsers.get(position));
        }
    }
    public ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }
    private void addFriendCheckMarks() {
        //query friendsRelation field
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if (e == null) {
                    //list returned . look for a match
                    for (int i= 0; i<mUsers.size();i++){
                        ParseUser user = mUsers.get(i);
                        for(ParseUser friend : friends){
                            if (friend.getObjectId().equals(user.getObjectId())){
                                //check item
                                getListView().setItemChecked(i, true);
                            }
                        }
                    }
                }
                else {
                    Log.e(TAG,e.getMessage());
                }
            }
        });
    }
}
