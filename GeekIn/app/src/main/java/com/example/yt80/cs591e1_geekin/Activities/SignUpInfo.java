package com.example.yt80.cs591e1_geekin.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.MD5;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Views.CircleImageView;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SignUpInfo extends AppCompatActivity {
    private static final String TAG = "Signup1Activity";
    private CircleImageView imageview;
    private Button btn_nextStep;
    private LinearLayout.LayoutParams layoutParams;
    private ImageButton btn_img;

    private EditText _emailText;
    private EditText _passwordText;
    private EditText _cpasswordText;
    private EditText _nameText;
    private NetworkUtil utilRequest;
    private MD5 md5;

    private Bitmap userPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);
        utilRequest = new NetworkUtil(getApplicationContext());

        md5 = new MD5();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        layoutParams=new LinearLayout.LayoutParams(dm.widthPixels/2,dm.widthPixels/2);
        layoutParams.gravity= Gravity.CENTER;

        imageview = (CircleImageView)findViewById(R.id.signup_image);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        imageview.setLayoutParams(layoutParams);
        imageview.requestLayout();
        btn_nextStep = (Button) findViewById(R.id.nextStep);


        _emailText=(EditText)findViewById(R.id.signup_edt_email);
        _passwordText=(EditText)findViewById(R.id.signup_edt_password);
        _cpasswordText=(EditText)findViewById(R.id.signup_edt_cpassword);
        _nameText=(EditText)findViewById(R.id.signup_edt_username);

        btn_nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    signup();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ---------------------------------------------------
    // User Picture Functions
    // ---------------------------------------------------

    File mphotoFile;

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                    File photoFile=null;
                    try {
                        photoFile = File.createTempFile(
                                "temp",  // prefix
                                ".jpg",         // suffix
                                storageDir      // directory
                        );
                        mphotoFile=photoFile;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(photoFile!=null){
                        Uri photoURI = FileProvider.getUriForFile(SignUpInfo.this,
                                "com.example.android.fileprovider2",
                                photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }

                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_IMAGE_GALLERY);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY =2;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageview.setLayoutParams(layoutParams);
            imageview.requestLayout();
            imageview.setImageURI(Uri.fromFile(mphotoFile));
            try {
                userPic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(mphotoFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mphotoFile.delete();
        }
        else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                userPic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageview.setLayoutParams(layoutParams);
                imageview.requestLayout();
                imageview.setImageBitmap(userPic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Get variables from the EditText and put into the json object.
     Send the sign up request to the server and handle the result received from server.
     * @throws JSONException
     */
    public void signup() throws JSONException {
        if (!validate()) {
            return;
        }

        final ProgressDialog progress = setProgressBar();
        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String passwordToHash = _passwordText.getText().toString();

        final String generatedPWD = md5.generateMD5(passwordToHash);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        try{
                            JSONObject user = new JSONObject();
                            if (userPic == null) {
                                userPic = BitmapFactory.decodeResource(getResources(),R.drawable.default_user_pic);
                            }
                            user.put("image", ImageUtil.imageToString(userPic));
                            user.put("username", name);
                            user.put("email", email);
                            user.put("password", generatedPWD);
                            utilRequest.SignUp(user, new NetworkUtil.VolleyCallback() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    onSignupResultHandler(result, email);
                                }

                                @Override
                                public void onLoginSuccess(Boolean result, User user) {
                                }

                                @Override
                                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts) {

                                }

                                @Override
                                public void onGetPDetailSuccess(Boolean result, PostDetail pDetail) {

                                }

                                @Override
                                public void onGetCommentsSuccess(Boolean result, ArrayList<Comment> commentList) {

                                }

                                @Override
                                public void onGetNotifySuccess(Boolean result, ArrayList<Notification> notificationList) {
                                }

                                @Override
                                public void onGetUserListSuccess(Boolean result, ArrayList<User> userList) {

                                }

                            });
                        }catch(Exception e){
                            try {
                                throw e;
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        progress.dismiss();
                    }
                }, 2000);
    }

    /**
     * Handle the result of creating account.
     result is got from server.
     * @param result
     * @param email
     */
    public void onSignupResultHandler(boolean result, String email) {
        if (result) {
            Log.i("BBBBBBB", "wocao");
            Intent intent=new Intent(this,SignUpTag.class);
            intent.putExtra("User_Email",email);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Sign up failed", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Set the progress bar.
     * @return
     */
    public ProgressDialog setProgressBar() {
        final ProgressDialog progressDialog = new ProgressDialog(SignUpInfo.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account");
        progressDialog.show();
        return progressDialog;
    }


    // ---------------------------------------------------
    // Helper Functions
    // ---------------------------------------------------

    /**
     * Check whether the input of username, email, password.etc is valid.
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String cpassword= _cpasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if(!cpassword.equals(password)){
            _cpasswordText.setError("inconsistent password input");
            valid = false;
        }
        else{
            _cpasswordText.setError(null);
        }

        return valid;
    }

}
