package com.example.yt80.cs591e1_geekin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.MD5;
import com.example.yt80.cs591e1_geekin.Views.CircleImageView;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;


public class LogIn extends AppCompatActivity implements Serializable {
    // Used for objects transfer between activities
    public  final static String SER_KEY = "com.example.yt80.cs591e1_geekin";
    // Views on layout
    private EditText edtusername;
    private EditText edtpassword;
    private Button btnlogin;
    private Button customizeFB;
    private LoginButton fbLogin;
    private CallbackManager cbManager;
    public String username;
    public String user_Email;
    // Functions needed
    private NetworkUtil utilRequest;
    private MD5 md5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Init Facebooke sdk
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Elements on layout
        edtusername=(EditText)findViewById(R.id.edtusername);
        edtpassword=(EditText)findViewById(R.id.edtpassword);
        btnlogin=(Button)findViewById(R.id.login_sign_in_button);
        fbLogin = (LoginButton) findViewById(R.id.fb_login_btn);
        customizeFB = (Button) findViewById(R.id.customizeFB);
        fbLogin.setReadPermissions(Arrays.asList("email"));

        // Instantiate md5 class. MD5 hash function needed for password;
        md5 = new MD5();

        // Callback manager to wait facebbook server response.
        cbManager = CallbackManager.Factory.create();

        setFbLogin();


        // Use a customize facebook login button
        customizeFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLogin.performClick();
            }
        });

        utilRequest = new NetworkUtil(getApplicationContext());
        // set login Button
        setLoginBtn();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cbManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Transfer to sign up activity.
     * @param view
     */
    public void signup(View view){
        Intent intent=new Intent(this,SignUpInfo.class);
        startActivity(intent);
    }


    // ---------------------------------------------------
    // Layout Elements Set Up Functions
    // ---------------------------------------------------

    /**
     * Register facebook callback in the callback manager.
     */
    public void setFbLogin() {
        fbLogin.registerCallback(cbManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken()
                        .getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {@Override
                        public void onCompleted(JSONObject object,
                                                GraphResponse response) {
                            try {
                                username = object.getString("name");
                                user_Email = object.getString("email");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                signIn(true,user_Email,username);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields",
                        "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    /**
     * Set up a progress dialog after user click login
     * @return ProgressDialog
     */
    public ProgressDialog setProgressBar() {
        final ProgressDialog progressDialog = new ProgressDialog(LogIn.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        return progressDialog;
    }

    public void setLoginBtn() {
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signIn(false,"","");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    public void signIn(final boolean facebook, final String facebookEmail, final String facebookUserName) throws JSONException, InterruptedException {
        // If it is not facebook login, check whether validate.
        if (!facebook) {
            if (!validate()) {
                return;
            }
        }

        // Set progress bar, get input from Text box.
        final ProgressDialog progress = setProgressBar();
        final String email = edtusername.getText().toString();
        final String passwordToHash = edtpassword.getText().toString();
        // Hash the pwd by MD5.
        final String generatedPassword = md5.generateMD5(passwordToHash);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        try{

                            JSONObject user = new JSONObject();
                            user.put("self", true);

                            if (!facebook) {
                                // Normal login information
                                user.put("email", email);
                                user.put("password", generatedPassword);
                            } else {
                                // Facebook login information.
                                user.put("facebook", facebook);
                                user.put("email",facebookEmail);
                                user.put("username", facebookUserName);
                            }

                            // Send login HttpRequest to server.
                            utilRequest.Login(user, new NetworkUtil.VolleyCallback() {
                                @Override
                                public void onSuccess(Boolean result) {}
                                @Override
                                public void onLoginSuccess(Boolean result, User user) {
                                    logInResultHandle(result, user);
                                }
                                @Override
                                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts) {}
                                @Override
                                public void onGetPDetailSuccess(Boolean result, PostDetail pDetail) {}
                                @Override
                                public void onGetCommentsSuccess(Boolean result, ArrayList<Comment> commentList) {}
                                @Override
                                public void onGetNotifySuccess(Boolean result, ArrayList<Notification> notificationList) {}
                                @Override
                                public void onGetUserListSuccess(Boolean result, ArrayList<User> userList) {}

                            });
                        }catch(Exception e){
                            try {
                                throw e;
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        progress.dismiss();
                    }
                }, 3000);

    }

    /**
     * After receiving response from server, check the result.
     * If result is true, login success else failed
     * @param result
     * @param user
     */
    private void logInResultHandle(boolean result, User user) {
        if (result) {
            btnlogin.setEnabled(true);
            Intent intent=new Intent(this,Main.class);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(SER_KEY,user);
            intent.putExtras(mBundle);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            btnlogin.setEnabled(true);
        }
    }


    // ---------------------------------------------------
    // Helper Functions
    // ---------------------------------------------------

    /**
     * Check whether the input is valid
     * @return true if valid, false if not valid.
     */
    public boolean validate() {
        boolean valid = true;

        String email = edtusername.getText().toString();
        String password = edtpassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtusername.setError("enter a valid email address");
            valid = false;
        } else {
            edtusername.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            edtpassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtpassword.setError(null);
        }

        return valid;
    }

    // ---------------------------------------------------
    // OS Function Override
    // ---------------------------------------------------

    @Override
    public void onBackPressed() {
        // disable going back to the Main
        moveTaskToBack(true);
    }


}
