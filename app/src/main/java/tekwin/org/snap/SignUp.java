package tekwin.org.snap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUp extends ActionBarActivity {

    protected EditText mUserName;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

        //initialize Views
        mUserName     = (EditText) findViewById(R.id.signup_username_field);
        mPassword     = (EditText) findViewById(R.id.signup_password_field);
        mEmail        = (EditText) findViewById(R.id.signup_email_field);
        mSignupButton = (Button) findViewById(R.id.signup_button);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();
                // eliminate empty spaces
                username =username.trim();
                password = password.trim();
                email = email.trim();

                // check text fields aren't empy. communicate with user through a dialog
                if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
                    AlertDialog.Builder build = new AlertDialog.Builder(SignUp.this)
                               .setTitle(getString(R.string.signup_error_title))
                               .setMessage(getString(R.string.signup_error_message))
                               .setPositiveButton(android.R.string.ok, null);

                   Dialog dialog = build.create();
                    dialog.show();


                }
                else{
                    //add pregress bar
                    setProgressBarIndeterminateVisibility(true);
                    // sign up user
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            //remove progress bar
                            setProgressBarIndeterminateVisibility(false);

                            if (e == null) {
                                //success!
                                //take user to MainActivity
                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {

                                AlertDialog.Builder build = new AlertDialog.Builder(SignUp.this)
                                        .setTitle(getString(R.string.signup_error_title))
                                        .setMessage(e.getMessage())
                                        .setPositiveButton(android.R.string.ok, null);

                                Dialog dialog = build.create();
                                dialog.show();
                            }
                        }
                    });

                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
