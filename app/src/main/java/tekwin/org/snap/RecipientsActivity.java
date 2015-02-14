package tekwin.org.snap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ActionBarActivity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();
    protected ListView mListView;
    protected List<ParseUser> mFriends;
    protected ParseUser mCurrentUser ;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected TextView emptyLabel;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected  String mFileType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //progress bar
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        emptyLabel = (TextView) findViewById(R.id.emptyrecipientlabel);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // show send menu item when a recipient is checked
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView < ? > parent, View view,final int position, long id){
                if (mListView.isItemChecked(position)){
                mSendMenuItem.setVisible(true);
                }
                else {
                    mSendMenuItem.setVisible(false);
                }
            }
        });

        mMediaUri = getIntent().getData();
       mFileType = getIntent().getExtras().getString(ParseConstant.KEY_FILE_TYPE);
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
                    errorMessageDialog(e);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            //upload messages to backend
            ParseObject message = createMessage();

            if (message == null){
                fileSelectedError();
            }
            else {
                send(message);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private ParseObject createMessage() {

     ParseObject message = new ParseObject(ParseConstant.CLASSES_MESSAGES);

        message.put(ParseConstant.KEY_SENDER_ID,mCurrentUser.getObjectId());
        message.put(ParseConstant.KEY_SENDER_NAME,mCurrentUser.getUsername());
        message.put(ParseConstant.KEY_RECIPIENT_ID,getRecipientIds());
        message.put(ParseConstant.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this,mMediaUri);

        if (fileBytes == null){
            return null;
        }
        else {
            if(mFileType.equals(ParseConstant.TYPE_IMAGE)){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this,mMediaUri,mFileType);
            ParseFile file =new ParseFile(fileName,fileBytes);
            message.put(ParseConstant.KEY_FILE,file);

            return message;
        }
    }

    private ArrayList<String> getRecipientIds() {

        ArrayList<String> recipientIds = new ArrayList<>();
        for (int i =0; i< getListView().getCount();i++){
            if (getListView().isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    private void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(RecipientsActivity.this,R.string.sucees_message,Toast.LENGTH_LONG).show();
                }
                else{
                    fileSentError();
                }
            }
        });
    }

    private void fileSentError() {
        AlertDialog.Builder build = new AlertDialog.Builder(getListView().getContext())
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.file_sending_error))
                .setPositiveButton(android.R.string.ok, null);

        Dialog dialog = build.create();
        dialog.show();
    }
    private void fileSelectedError() {
        AlertDialog.Builder build = new AlertDialog.Builder(getListView().getContext())
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.file_error))
                .setPositiveButton(android.R.string.ok, null);

        Dialog dialog = build.create();
        dialog.show();
    }
    private void errorMessageDialog(ParseException e) {
        AlertDialog.Builder build = new AlertDialog.Builder(getListView().getContext())
                .setTitle(getString(R.string.error_title))
                .setMessage(e.getMessage())
                .setPositiveButton(android.R.string.ok, null);

        Dialog dialog = build.create();
        dialog.show();
    }
}
