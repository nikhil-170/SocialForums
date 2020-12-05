package com.example.inclass10;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class ForumDetailFragment extends Fragment {

    ForumsClass forum;
    private FirebaseAuth mAuth;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    CommentsAdapter commentsAdapter;
    ArrayList<Comment> commentArrayList = new ArrayList<>();
    private static final int CAM_REQUEST_CODE =1;
    Bitmap mImageUri;
    String commentImagePath = null;

    TextView titleDetails, userNameDetails, descriptionDetails, likesDetails;
    EditText commentEditText;
    ImageView deleteImageDetails, likeImage, imagepreview;
    Button addPhotoButton, postButton;
    ListView commentsListView;
    String currentUser;


    public ForumDetailFragment(ForumsClass forum, String currentUser){
        this.forum = forum;
        this.currentUser = currentUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum_detail, container, false);

        getComments();

        titleDetails = view.findViewById(R.id.titleDetails);
        userNameDetails = view.findViewById(R.id.userNameDetails);
        descriptionDetails = view.findViewById(R.id.descriptionDetails);
        likesDetails = view.findViewById(R.id.likesDetails);

        commentEditText =view.findViewById(R.id.commentEditText);

        deleteImageDetails = view.findViewById(R.id.deleteImageDetails);
        likeImage = view.findViewById(R.id.likeImage);

        imagepreview = view.findViewById(R.id.imagepreview);
        addPhotoButton = view.findViewById(R.id.addphotobutton);
        postButton = view.findViewById(R.id.postbutton);
        commentsListView = view.findViewById(R.id.commentsListView);


        titleDetails.setText(forum.title);
        userNameDetails.setText(forum.username);
        descriptionDetails.setText(forum.description);
        likesDetails.setText(forum.likeCount+" likes");

        commentsAdapter = new CommentsAdapter(getContext(),R.layout.comment_row_view, commentArrayList, new CommentsAdapter.commentsAdapterInterface(){

            @Override
            public void deleteCommentCall(final Comment comment) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.confirm));
                builder.setMessage(getResources().getString(R.string.delete_message));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("forums").document(forum.forumId).collection("comments").document(comment.commentId)
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                commentsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                builder.show();

            }
        });
        commentsListView.setAdapter(commentsAdapter);

        if(mAuth.getCurrentUser().getUid().matches(forum.ownerId)) {
            deleteImageDetails.setVisibility(View.VISIBLE);
            deleteImageDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getResources().getString(R.string.error));
                    builder.setMessage(getResources().getString(R.string.delete_message));
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("forums").document(forum.forumId)
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mListener.forumDetailsCancelled();
                                }
                            });
                        }
                    });
                    builder.show();
                }
            });
        }else{
            deleteImageDetails.setVisibility(View.GONE);
        }


        HashMap<String,Boolean> map = forum.likes;
        if(map.containsKey(mAuth.getCurrentUser().getUid())){
            likeImage.setImageResource(R.drawable.like_favorite);
        }else{
            likeImage.setImageResource(R.drawable.like_not_favorite);
        }

        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Boolean> map = forum.likes;
                if(map.containsKey(mAuth.getCurrentUser().getUid())){
                    map.remove(mAuth.getCurrentUser().getUid());
                    likeImage.setImageResource(R.drawable.like_not_favorite);
                    forum.likeCount = map.size();
                    likesDetails.setText(forum.likeCount+" likes");
                }else{
                    map.put(mAuth.getCurrentUser().getUid(),true);
                    likeImage.setImageResource(R.drawable.like_favorite);
                    forum.likeCount = map.size();
                    likesDetails.setText(forum.likeCount+" likes");
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("forums").document(forum.forumId).update("likes",map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(intent, CAM_REQUEST_CODE);
                }
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentEditText.getText().toString().matches("")){
                    AlertUtils.showOKDialog(getActivity(),getResources().getString(R.string.error),
                            getResources().getString(R.string.enter_comment));
                }else{
                    postComment();
                }
            }
        });



        return view;
    }


    public void postComment(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        HashMap<String, Object> map = new HashMap<>();
        map.put("ownerId",mAuth.getCurrentUser().getUid());
        map.put("body",commentEditText.getText().toString());
        map.put("username",currentUser);
        map.put("createdAt",new Timestamp(new Date()));

        DocumentReference docRef = db.collection("forums")
                .document(forum.forumId)
                .collection("comments").document();
        map.put("commentId",docRef.getId());

        if(commentImagePath!=null){
            map.put("image",commentImagePath);
            commentImagePath = null;
        }

        docRef.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

        imagepreview.setImageResource(R.drawable.image_icon);
        commentEditText.setText("");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAM_REQUEST_CODE && resultCode == RESULT_OK){
            mImageUri  = (Bitmap) data.getExtras().get("data");
            imagepreview.setImageBitmap(mImageUri);

            if(imagepreview.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.image_icon).getConstantState()){
                StorageReference storageRef = storage.getReference();
                final String path = "comment_image"+ UUID.randomUUID().toString() + ".jpg";
                StorageReference imageRef = storageRef.child(path);
                Bitmap bitmap = ((BitmapDrawable) imagepreview.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();
                UploadTask uploadTask = imageRef.putBytes(data1);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       // Log.d(TAG, "onSuccess: ");
                        commentImagePath = path;
                    }
                });

            }

        }
    }


    public void getComments(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        db.collection("forums").document(forum.forumId).collection("comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        commentArrayList.clear();
                        for(QueryDocumentSnapshot ds: value){
                            Comment comment = new Comment();
                            comment.username = (String) ds.getData().get("username");
                            comment.commentId = (String) ds.getData().get("commentId");
                            comment.ownerId = (String) ds.getData().get("ownerId");
                            comment.createdAt = (Timestamp) ds.getData().get("createdAt");
                            comment.body = (String) ds.getData().get("body");
                            comment.image = (String) ds.getData().get("image");
                            commentArrayList.add(comment);
                        }
                        Collections.sort(commentArrayList);
                        commentsAdapter.notifyDataSetChanged();
                    }
                });
    }

    IForumDetailListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (IForumDetailListener) context;
    }

    public interface IForumDetailListener{
        void forumDetailsCancelled();
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cancel_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cancelId) {
            mListener.forumDetailsCancelled();
        }
        return super.onOptionsItemSelected(item);
    }
}