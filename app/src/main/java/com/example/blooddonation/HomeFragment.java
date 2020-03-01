package com.example.blooddonation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.blooddonation.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {


    public static final int SEND_SMS_PERMISSION_CODE=0;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);


        if(checkPermission(Manifest.permission.SEND_SMS))
        {


        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_CODE);
        }



        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();


        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(getContext(), "Back pressed", Toast.LENGTH_SHORT).show();
                // Handle the back button event
                new AlertDialog.Builder(getActivity())
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();

            //   System.exit(0);

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        RelativeLayout donor,requester;
        donor=view.findViewById(R.id.donor);
        requester=view.findViewById(R.id.requester);

        donor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getContext())
                        .setMessage("You will be added as a Donor")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                ///
                                //
                                //
                                DocumentReference documentReference=db.collection("Users")
                                        .document(mAuth.getCurrentUser().getUid());
                                documentReference.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document=task.getResult();
                                                if(document!=null){
                                                    User u=task.getResult().toObject(User.class);
                                                    if(u!=null) {


                                                        db.collection("Donors").document(mAuth.getCurrentUser().getUid())
                                                                .set(u)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(getContext(),"Added as Donor",Toast.LENGTH_SHORT).show();

                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getContext(),"Failed to add as Donor",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                    }

                                                }
                                            }
                                        });


                                /////



                            }
                        })
                        .setNegativeButton("No", null)

                        .show();
            }
        });




        requester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("You will be added as a Requester")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                ///
                                //
                                //
                                DocumentReference documentReference=db.collection("Users")
                                        .document(mAuth.getCurrentUser().getUid());
                                documentReference.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document=task.getResult();
                                                if(document!=null){
                                                    User u=task.getResult().toObject(User.class);
                                                    if(u!=null) {


                                                        db.collection("Requesters").document(mAuth.getCurrentUser().getUid())
                                                                .set(u)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(getContext(),"Added as Requester",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getContext(),"Failed to add as Requester",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                    }
                                                    else {
                                                        // startActivity(new Intent(GoogleLoginActivity.this,RegisterActivity.class));
                                                        //  progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                            }
                                        });


                                /////



                            }
                        })
                        .setNegativeButton("No", null)

                        .show();
            }
        });



        return view;
    }


    public boolean checkPermission(String permission)
    {
        int check= ContextCompat.checkSelfPermission(getContext(),permission);
        return (check== PackageManager.PERMISSION_GRANTED);
    }




}
