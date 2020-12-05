package com.example.inclass10;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class NewForumFragment extends Fragment {

    EditText forumTitleET, forumDesET;
    private FirebaseAuth mAuth;
    String username;

    public NewForumFragment(String username) {
        // Required empty public constructor
        this.username = username;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_forum, container, false);
        getActivity().setTitle("New Forum");

        forumTitleET = view.findViewById(R.id.forumTitleET);
        forumDesET = view.findViewById(R.id.forumDesET);


        view.findViewById(R.id.submitButtonNewForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String forumTitleValue = forumTitleET.getText().toString();
                final String forumDescVal = forumDesET.getText().toString();
                if(forumTitleValue.length() == 0){
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.email_hint));
                }else if(forumDescVal.length() == 0){
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.email_hint));
                }else {
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Date date = new Date();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("title", forumTitleValue);
                    map.put("description", forumDescVal);
                    map.put("likes", new HashMap<String,Boolean>());
                    map.put("createdAt", new Timestamp(date));
                    map.put("ownerId", mAuth.getCurrentUser().getUid());
                    map.put("username", username);

                    DocumentReference docRef = db.collection("forums").document();
                    map.put("forumId",docRef.getId());

                    docRef.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "new Forum created !!!", Toast.LENGTH_LONG).show();
                            mListener.goBackToForumsScreen();
                        }
                    });
                }


            }
        });

        view.findViewById(R.id.cancelNewForumButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goBackToForumsScreen();
            }
        });


        return view;
    }

    INewForumListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (INewForumListener) context;
    }

    public interface INewForumListener{
        void goBackToForumsScreen();
    }
}