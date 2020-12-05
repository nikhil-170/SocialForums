package com.example.inclass10;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    ArrayList<Comment> allComments;
    commentsAdapterInterface mListener;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private FirebaseAuth mAuth;

    public interface commentsAdapterInterface {
        void deleteCommentCall(Comment comment);
    }

    public CommentsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Comment> objects, commentsAdapterInterface mListener) {
        super(context, resource, objects);
        this.allComments = objects;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment_row_view, parent, false);
            holder = new CommentsAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.username.setText(allComments.get(position).username+"");
        holder.body.setText(allComments.get(position).body);

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = sfd.format(new Date(String.valueOf(allComments.get(position).createdAt.toDate())));
        holder.commentDateTV.setText(date);

        if (allComments.get(position).image!=null) {
                StorageReference imageRef = storageRef.child(allComments.get(position).image);
            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    holder.commentImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            holder.commentImageView.setVisibility(View.GONE);
        }

        mAuth = FirebaseAuth.getInstance();
        if(allComments.get(position).ownerId.equals(mAuth.getCurrentUser().getUid())){
            holder.delete.setVisibility(View.VISIBLE);
        }else{
            holder.delete.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getContext().getResources().getString(R.string.confirm));
                builder.setMessage(getContext().getResources().getString(R.string.delete_message));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.deleteCommentCall(allComments.get(position));
                    }
                });
                builder.show();

            }
        });


        return convertView;
    }

    @Override
    public int getCount() {
        return allComments.size();
    }

    public static class ViewHolder {
        TextView username, body, commentDateTV;
        ImageView commentImageView, delete;

        ViewHolder(View view) {
            username = view.findViewById(R.id.nameCommentTV);
            body = view.findViewById(R.id.descCommentTV);
            commentDateTV = view.findViewById(R.id.commentDateTV);
            commentImageView = view.findViewById(R.id.commentImageIV);
            delete = view.findViewById(R.id.deleteCommentIV);
        }
    }

}
