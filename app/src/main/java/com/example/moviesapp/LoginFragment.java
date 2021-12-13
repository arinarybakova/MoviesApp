package com.example.moviesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private Button buttonLogin;
    private Context context;
    EditText inputEmailL;
    EditText inputPasswordL;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    NavigationView navigationView;
    Button btnGoogle;
    Button btnFacebook;

    public LoginFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_login, container, false);


        TextView btn = view.findViewById(R.id.textViewSignUp);
        buttonLogin = (Button) view.findViewById(R.id.btnlogin);
        inputEmailL = view.findViewById(R.id.inputEmailL);
        inputPasswordL = view.findViewById(R.id.inputPasswordL);
        btnGoogle = view.findViewById(R.id.btnGoogle);
        btnFacebook = view.findViewById(R.id.btnFacebook);
        progressDialog = new ProgressDialog(view.getContext());
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new RegisterFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                performLogin();
                /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new HomeFragment(""));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new GoogleSignInFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        /*button.setOnClickListener(startMainActivity);
        button.setOnLongClickListener(startMainActivityLong);*/
        return view;
    }

    private void performLogin() {
        String email = inputEmailL.getText().toString();
        String password = inputPasswordL.getText().toString();

        //validation for an email
        if (!email.matches(emailPattern)) {
            inputEmailL.setError("Enter correct email");
        } else if (password.isEmpty() || password.length() < 6) {
            inputPasswordL.setError("Enter valid password");
        } else {
            progressDialog.setMessage("Login is in process...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            sendUserToNextFragment();
                            // there might be issues with usage of getActivity cause it's fragment
                            // instead of Activity.this
                            Toast.makeText( getActivity(),
                                    "Login is successful",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText( getActivity(),
                                    ""+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void sendUserToNextFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new HomeFragment(""));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void runMainActivity(boolean flag){
        Intent intent = new Intent(context, HomeFragment.class);
        intent.putExtra("flag", flag);
        context.startActivity(intent);
    }

    View.OnClickListener startMainActivity = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            runMainActivity(true);
        }
    };
    /*to display list items*/
    View.OnLongClickListener startMainActivityLong = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View view) {
            runMainActivity(false);
            return true;
        }
    };
}
