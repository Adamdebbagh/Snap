package tekwin.org.snap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends ActionBarActivity {

    protected EditText mUserName;
    protected EditText mPassword;

    protected Button mLoginButton;


    protected TextView mSignUpTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //progress bar
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView) findViewById(R.id.signup_label);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this , SignUp.class);
                startActivity(intent);
            }
        });


        //initialize Views
        mUserName     = (EditText) findViewById(R.id.login_username_field);
        mPassword     = (EditText) findViewById(R.id.login_password_field);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                // eliminate empty spaces
                username = username.trim();
                password = password.trim();


                // check text fields aren't empty. communicate with user through a dialog
                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder build = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(getString(R.string.signup_error_title))
                            .setMessage(getString(R.string.login_error_message))
                            .setPositiveButton(android.R.string.ok, null);

                    Dialog dialog = build.create();
                    dialog.show();


                } else {

                    //add progress bar
                    setSupportProgressBarIndeterminateVisibility(true);
                    //login user
                    ParseUser.logInInBackground(username,password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            //remove progress bar
                            setSupportProgressBarIndeterminateVisibility(false);

                            if (e==null){
                                // success
                                //take user to MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {

                                AlertDialog.Builder build = new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle(getString(R.string.login_error_title))
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
            }
