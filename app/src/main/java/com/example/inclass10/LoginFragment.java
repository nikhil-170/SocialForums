package com.example.inclass10;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    EditText emailEditText,passwordEditText;
    private FirebaseAuth mAuth;
    TextView createAccountTV;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login Screen");

        emailEditText = view.findViewById(R.id.emailLoginET);
        passwordEditText = view.findViewById(R.id.passLoginET);
        createAccountTV = view.findViewById(R.id.createAccountTV);

        view.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(email.isEmpty()){
                    AlertUtils.showOKDialog(getActivity(),getResources().getString(R.string.error),
                            getResources().getString(R.string.email_hint));
                }else if(password.isEmpty()){
                    AlertUtils.showOKDialog(getActivity(),getResources().getString(R.string.error),
                            getResources().getString(R.string.password_hint));
                }else {

                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),"Login Success", Toast.LENGTH_SHORT).show();
                                mListener.displayForumsListAfterLogin();

                            } else {
                                AlertUtils.showOKDialog(getActivity(),getResources().getString(R.string.error),
                                        task.getException().getMessage());
                            }


                        }
                    });
                }
            }
        });

        createAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCreateAccountFrag();
            }
        });


        return view;
    }

    ILoginListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ILoginListener) context;
    }

    public interface ILoginListener{
        void goToCreateAccountFrag();
        void displayForumsListAfterLogin();
    }
}