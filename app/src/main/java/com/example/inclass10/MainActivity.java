package com.example.inclass10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class MainActivity extends AppCompatActivity implements LoginFragment.ILoginListener, CreateNewAccFragment.IRegisterListener,
        ForumsFragment.IForumsFragmentListener, NewForumFragment.INewForumListener, ForumDetailFragment.IForumDetailListener {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth= FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentView, new LoginFragment())
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentView, new ForumsFragment())
                    .commit();
        }


    }

    @Override
    public void goToCreateAccountFrag() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView, new CreateNewAccFragment())
                .commit();
    }

    @Override
    public void displayForumsListAfterLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView, new ForumsFragment())
                .commit();
    }

    @Override
    public void goBackToLoginFrag() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView, new LoginFragment())
                .commit();
    }

    @Override
    public void goToForumsFromCreateAcc() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView, new ForumsFragment())
                .commit();
    }


    @Override
    public void callNewForumScreen(String username) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView, new NewForumFragment(username))
                .commit();
    }

    @Override
    public void logOutSession() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView, new LoginFragment())
                .commit();
    }

    @Override
    public void callForumDetails(ForumsClass forum, String username) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView,new ForumDetailFragment(forum, username))
                .commit();
    }

    @Override
    public void goBackToForumsScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView, new ForumsFragment())
                .commit();
    }

    @Override
    public void forumDetailsCancelled() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentView, new ForumsFragment())
                .commit();
    }
}