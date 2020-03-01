package com.example.blooddonation;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment{

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;

    TextView txtName,txtBloodGroup,txtGender,txtPhone,txtEmail,txtAddress;
    ImageView imgProfile;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_profile,container,false);

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        txtName=view.findViewById(R.id.txt_name_profile);
        txtBloodGroup=view.findViewById(R.id.txt_blood_group);
        txtGender=view.findViewById(R.id.txt_gender);
        txtEmail=view.findViewById(R.id.txt_email);
        txtAddress=view.findViewById(R.id.txt_address);
        txtPhone=view.findViewById(R.id.txt_phone);
        imgProfile=view.findViewById(R.id.img_profile);


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
                                //Toast.makeText(container, "Name" + u.getName(), Toast.LENGTH_SHORT).show();
                                txtName.setText("Name : "+u.getName());
                                txtBloodGroup.setText("Blood Group : "+u.getBloodGroup());
                                txtPhone.setText("Phone no : "+u.getPhone());
                                txtAddress.setText("Address : "+u.getAddress());
                                txtEmail.setText("Email : "+mAuth.getCurrentUser().getEmail());
                                txtGender.setText("Gender : "+u.getGender());

                             //   Toast.makeText(getContext(),"URL"+u.getProfileImageUrl(),Toast.LENGTH_SHORT).show();



                                storageReference.child("images/"+mAuth.getCurrentUser().getUid()).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(getContext())
                                                        .load(uri)
                                                        .into(imgProfile);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(),"ERROR "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });



                              //  startActivity(new Intent(GoogleLoginActivity.this, NavigationActivity.class));
                              //  progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    }
                });


        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
               startActivity(new Intent(getContext(),NavigationActivity.class));

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


        return view;
    }






}
