package com.example.yt80.cs591e1_geekin.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Activities.LogIn;
import com.example.yt80.cs591e1_geekin.Activities.Main;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Adapters.NewpostTagAdapter;
import com.example.yt80.cs591e1_geekin.Fragments.NewPostFragMain;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.yt80.cs591e1_geekin.R.id.next;

/**
 * Created by yt80 on 2017/4/10.
 */

public class NewPostFragContent extends Fragment{
    private EditText edt_title;
    private EditText edt_content;
    private GridView gridview;
    private NewpostTagAdapter tagAdapter;

    /**
     * Fragments communication functions
     */
    public interface main_fragment_newpost_2_channel{
        public void sendContent(String title, String content, ArrayList<Integer> chosenTag) throws JSONException;
    }
    main_fragment_newpost_2_channel BFL;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        BFL=(main_fragment_newpost_2_channel) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_newpost_2, null);
        edt_title=(EditText)view.findViewById(R.id.newpost_edt_title);
        edt_content=(EditText)view.findViewById(R.id.newpost2_edt_content);
        gridview=(GridView)view.findViewById(R.id.main_newpost_gridview_tab);
        tagAdapter = new NewpostTagAdapter(getContext());
        gridview.setAdapter(tagAdapter);

        return view;
    }

    /**
     * Set up a progress dialog after user click login
     * @return ProgressDialog
     */
    public ProgressDialog setProgressBar() {
        final ProgressDialog progressDialog = new ProgressDialog((Main)getActivity(),
                R.style.AppTheme_white_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
        return progressDialog;
    }

    /**
     * Send content to main activity to send request to server.
     */
    public void post(){

        final ProgressDialog progress = setProgressBar();

        if(edt_title.getText().toString().length()==0){
            Toast.makeText(getContext(),"title cannot be empty",Toast.LENGTH_LONG).show();
            return;
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BFL.sendContent(edt_title.getText().toString(),edt_content.getText().toString(),tagAdapter.getChosenTag());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress.dismiss();
                    }
                },1000);

    }

    /**
     * Check whether input is empty.
     * @return
     */
    public boolean isEmpty() {
        if(edt_title.getText().toString().length()==0 || !tagAdapter.tagIsChosen()){
            Toast.makeText(getContext(),"Please finish your post",Toast.LENGTH_LONG).show();
            return true;
        } else {
            return false;
        }
    }
}
