package com.example.inclass10;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class ForumsAdapter extends ArrayAdapter<ForumsClass> {

    ArrayList<ForumsClass> allForums;
    adapterListener listener;
    private FirebaseAuth mAuth;


    public ForumsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ForumsClass> objects, adapterListener mListener4) {
        super(context, resource, objects);
        this.allForums = objects;
        this.listener = mListener4;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.forum_row_view, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mAuth = FirebaseAuth.getInstance();
        final ForumsClass currentForum = getItem(position);

        holder.title.setText(currentForum.title);
        holder.username.setText(currentForum.username);
        holder.description.setMaxLines(2);
        holder.description.setEllipsize(TextUtils.TruncateAt.END);
        holder.description.setText(currentForum.description);
        holder.likes.setText(currentForum.likeCount + " likes");


        if(mAuth.getCurrentUser().getUid().matches(currentForum.ownerId)){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    allForums.remove(position);
                    listener.deleteForumCall(currentForum);
                }
            });
        }else{
            holder.delete.setVisibility(View.GONE);
        }

        HashMap<String,Boolean> map = currentForum.likes;
        if(map.containsKey(mAuth.getCurrentUser().getUid())){
            holder.like.setImageResource(R.drawable.like_favorite);
        }else{
            holder.like.setImageResource(R.drawable.like_not_favorite);
        }


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Boolean> map = currentForum.likes;
                if (!map.containsKey(mAuth.getCurrentUser().getUid())) {
                    map.put(mAuth.getCurrentUser().getUid(), true);
                    holder.like.setImageResource(R.drawable.like_favorite);
                } else {
                    map.remove(mAuth.getCurrentUser().getUid());
                    holder.like.setImageResource(R.drawable.like_not_favorite);
                }
                listener.likeForumCall(currentForum, map);

            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.callForumDetailsFragment(currentForum);
            }
        });

        return convertView;
    }

    public interface adapterListener {
        void deleteForumCall(ForumsClass forum);

        void likeForumCall(ForumsClass forum, HashMap<String, Boolean> map);

        void callForumDetailsFragment(ForumsClass forum);
    }

    @Override
    public int getCount() {
        return allForums.size();
    }

    public static class ViewHolder {
        TextView title, username, description, likes;
        ImageView delete, like;
        ConstraintLayout cardView;


        ViewHolder(View view) {
            title = view.findViewById(R.id.title);
            username = view.findViewById(R.id.username);
            description = view.findViewById(R.id.description);
            likes = view.findViewById(R.id.likes);
            delete = view.findViewById(R.id.delete);
            like = view.findViewById(R.id.like);
            cardView = view.findViewById(R.id.forumView);
        }
    }
}
