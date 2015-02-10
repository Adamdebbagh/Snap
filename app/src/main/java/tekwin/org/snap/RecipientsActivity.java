package tekwin.org.snap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;


public class RecipientsActivity extends ActionBarActivity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();
    protected ListView mListView;
    protected List<ParseUser> mFriends;
    protected ParseUser mCurrentUser ;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected TextView emptyLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //progress bar
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        emptyLabel = (TextView) findViewById(R.id.emptyrecipientlabel);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        //Add progress bar
       setSupportProgressBarIndeterminateVisibility(true);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstant.KEY_FRIENDS_RELATION);

        // add a recipients list
        recipientsList();
    }

    private void recipientsList() {
        //query friendsRelation field
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstant.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                // Remove progress bar
                setSupportProgressBarIndeterminateVisibility(false);
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
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(RecipientsActivity.this,
                            android.R.layout.simple_list_item_checked, usernames);
                    getListView().setAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    // display error message
                    erroMessageDialog(e);
                }
            }
        });
    }

    public ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }
    private void erroMessageDialog(ParseException e) {
        AlertDialog.Builder build = new AlertDialog.Builder(getListView().getContext())
                .setTitle(getString(R.string.error_title))
                .setMessage(e.getMessage())
                .setPositiveButton(android.R.string.ok, null);

        Dialog dialog = build.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
