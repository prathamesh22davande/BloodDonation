package com.example.blooddonation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blooddonation.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST=11;



    ImageView imgProfile;
    ProgressBar progressBar;
    EditText txtName,txtAddress,txtPhone;
    Spinner spinnerBloodGroup,spinnerGender;
    DatePicker datePickerBirthDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();


        imgProfile=findViewById(R.id.img_profile);
        spinnerBloodGroup = findViewById(R.id.spinner_blood_group);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("A+");
        arrayList.add("A-");
        arrayList.add("B+");
        arrayList.add("B-");
        arrayList.add("O+");
        arrayList.add("O-");
        arrayList.add("AB+");
        arrayList.add("AB-");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(arrayAdapter);

        spinnerGender=findViewById(R.id.spinner_gender);
        ArrayList<String> arrayList1=new ArrayList<>();
        arrayList1.add("Male");
        arrayList1.add("Female");
        ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arrayList1);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(arrayAdapter1);


        txtName=findViewById(R.id.txt_name);
        txtAddress=findViewById(R.id.txt_address);
        txtPhone=findViewById(R.id.txt_phone);
        datePickerBirthDate=findViewById(R.id.date_picker);
        progressBar=findViewById(R.id.progressbar_register);
        progressBar.setVisibility(View.INVISIBLE);



    }


    ////image
    public void imgProfileClicked(View v){
        chooseImage();
    }

    public void chooseImage(){

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }


    public void uploadImage(){
        if(filePath!=null){
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref=storageReference.child("images").child(mAuth.getCurrentUser().getUid() );
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Uploaded Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data!=null && data.getData()!=null){
            filePath=data.getData();
            try{
              //  Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imgProfile.setImageURI(filePath);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"ERROR UPLOADING IMG : "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs(String name, String phone, String address) {
        if(name.isEmpty()){
            txtName.setError("Name is required");
            txtName.requestFocus();
            return false;
        }
        if(phone.isEmpty() || phone.length()!=10){
            txtPhone.setError("Phone length should be 10");
            txtPhone.requestFocus();
            return  false;
        }
        if(address.isEmpty()){
            txtAddress.setError("Address is required");
            txtAddress.requestFocus();
            return false;
        }


        return true;
    }

    public void registerClick(View v){
        String uid=mAuth.getCurrentUser().getUid();
        String name=txtName.getText().toString().trim();
        String phone=txtPhone.getText().toString().trim();
        String address=txtAddress.getText().toString().trim();
        String dob=Integer.toString(datePickerBirthDate.getDayOfMonth())+"/"+
                Integer.toString(datePickerBirthDate.getMonth()+1)+"/"+
                Integer.toString(datePickerBirthDate.getYear());
        String bloodGroup=spinnerBloodGroup.getSelectedItem().toString();
        String gender=spinnerGender.getSelectedItem().toString();


        if(validateInputs(name,phone,address)){
            try {

                uploadImage();
                progressBar.setVisibility(View.VISIBLE);



                User user=new User(name,bloodGroup,address,phone,dob,gender,mAuth.getCurrentUser().getEmail());

                DocumentReference documentReference=db.collection("Users").document(uid);
                documentReference.set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(RegisterActivity.this,NavigationActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });


            }
            catch (Exception e){
                Toast.makeText(this, "ERROR : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        else
            return;



    }


}
