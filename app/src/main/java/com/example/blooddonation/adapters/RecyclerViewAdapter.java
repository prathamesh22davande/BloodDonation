package com.example.blooddonation.adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blooddonation.NavigationActivity;
import com.example.blooddonation.R;
import com.example.blooddonation.model.BloodBank;
import com.example.blooddonation.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<BloodBank> mData;
    RequestOptions option;

    public RecyclerViewAdapter(Context mContext, List<BloodBank> mData) {
        this.mContext = mContext;
        this.mData = mData;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater= LayoutInflater.from(mContext);
        view=inflater.inflate(R.layout.blood_bank_row,parent,false);
        final MyViewHolder viewHolder=new MyViewHolder(view);

        /// open call activity
        viewHolder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mData.get(viewHolder.getAdapterPosition()).getMobile()));

                mContext.startActivity(intent);
            }
        });

        //send sms
        viewHolder.imgMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(mContext)
                        .setMessage("You are about to send SMS to Blood Bank")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                FirebaseAuth mAuth=FirebaseAuth.getInstance();
                                FirebaseFirestore db=FirebaseFirestore.getInstance();
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

                                                        String msg="Urgent Blood Donation Request:\n"+u.getName()+" needs "+u.getBloodGroup()+" blood very urgently. Any blood within the '"+u.getBloodGroup()+" group' would be accepted.";
                                                        sendMessage(msg,mData.get(viewHolder.getAdapterPosition()).getMobile());

                                                    }

                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", null)

                        .show();

            }
        });








        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.txtName.setText(mData.get(position).getName());
        holder.txtMobile.setText(mData.get(position).getMobile());
        holder.txtAddress.setText(mData.get(position).getAddress());


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


       TextView txtName,txtAddress,txtMobile;
       ImageView imgCall,imgMsg;
       LinearLayout view_container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            view_container=itemView.findViewById(R.id.view_container);
            txtName=itemView.findViewById(R.id.txt_name);
            txtMobile=itemView.findViewById(R.id.txt_mobile);
            txtAddress=itemView.findViewById(R.id.txt_address);
            imgCall=itemView.findViewById(R.id.img_call);
            imgMsg=itemView.findViewById(R.id.img_msg);

        }
    }

    public void filterList(ArrayList<BloodBank> filteredList){
        mData=filteredList;
        notifyDataSetChanged();
    }



/////Send sms
    public boolean checkPermission(String permission)
    {
        int check= ContextCompat.checkSelfPermission(mContext,permission);
        return (check== PackageManager.PERMISSION_GRANTED);
    }

    public void sendMessage(String msg,String phoneNo)
    {
       // String phoneNo="8806010270";
        if(checkPermission(Manifest.permission.SEND_SMS))
        {
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null,msg,null,null);
          //  smsManager.sendTextMessage("9075773706",null,msg,null,null);
         //   smsManager.sendTextMessage("7083038342",null,msg,null,null);
            Toast.makeText(mContext,"SMS SENT",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(mContext,"Permission Denied",Toast.LENGTH_LONG).show();
        }
    }
    ////
}
