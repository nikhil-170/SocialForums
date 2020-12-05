package com.example.inclass10;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class ForumsFragment extends Fragment {

    ListView listView;
    private FirebaseAuth mAuth;
    static String username;
    ArrayList<ForumsClass> allForums = new ArrayList<>();
    ForumsAdapter forumsAdapter;


    public ForumsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forums, container, false);
        getActivity().setTitle("Forums List");

        getUserName();
        listView = view.findViewById(R.id.listView);
        forumsAdapter = new ForumsAdapter(getContext(), R.layout.forum_row_view, allForums, new ForumsAdapter.adapterListener(){

            @Override
            public void deleteForumCall(final ForumsClass forum) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.confirm));
                builder.setMessage(getResources().getString(R.string.delete_message));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("forums").document(forum.forumId)
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                forumsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                builder.show();
            }

            @Override
            public void likeForumCall(ForumsClass forum, HashMap<String, Boolean> map) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("forums").document(forum.forumId).update("likes",map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        forumsAdapter.notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void callForumDetailsFragment(ForumsClass forum) {
                mListener.callForumDetails(forum, username);
            }
        });


        listView.setAdapter(forumsAdapter);
        getData();

        return view;
    }


    public void getData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        db.collection("forums").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                allForums.clear();
                for(QueryDocumentSnapshot ds: value){
                    ForumsClass forum = new ForumsClass();
                    forum.username = (String) ds.getData().get("username");
                    forum.forumId = (String) ds.getData().get("forumId");
                    forum.ownerId = (String) ds.getData().get("ownerId");
                    forum.title = (String) ds.getData().get("title");
                    forum.description = (String) ds.getData().get("description");
                    forum.createdAt = (Timestamp) ds.getData().get("createdAt");
                    forum.likes = (HashMap<String,Boolean>) ds.getData().get("likes");
                    forum.likeCount = forum.likes!=null?forum.likes.size():0;
                    allForums.add(forum);

                }
                forumsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getUserName(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    username = (String) task.getResult().get("username");
                }
            }
        });

    }

    IForumsFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (IForumsFragmentListener) context;
    }

    public interface IForumsFragmentListener {
        void callNewForumScreen(String username);
        void logOutSession();
        void callForumDetails(ForumsClass forum, String username);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoutid){
            mAuth.signOut();
            mListener.logOutSession();
        }else if(id == R.id.addIcon){
            mListener.callNewForumScreen(username);
        }
        return super.onOptionsItemSelected(item);
    }
}