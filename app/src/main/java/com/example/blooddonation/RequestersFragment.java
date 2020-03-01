package com.example.blooddonation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonation.adapters.RequesterRecyclerViewAdapter;
import com.example.blooddonation.adapters.UserRecyclerViewAdapter;
import com.example.blooddonation.model.BloodBank;
import com.example.blooddonation.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RequestersFragment extends Fragment {


    private List<User> lstUsers;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText txtSearch;

    RequesterRecyclerViewAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_requester,container,false);


        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();


        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        txtSearch=view.findViewById(R.id.txt_search);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


        lstUsers = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerviewid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RequesterRecyclerViewAdapter(view.getContext(), lstUsers);

        recyclerView.setAdapter(adapter);

        try{
            db.collection("Requesters").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {


                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                for (DocumentSnapshot d : list) {
                                    User user = d.toObject(User.class);
                                    lstUsers.add(user);
                                }
                                adapter.notifyDataSetChanged();

                                //startAnim();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                progressBar.setVisibility(View.INVISIBLE);
//                            stopAnim();
                                Toast.makeText(getContext(),"No Data Found",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
        catch (Exception e){
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    //// filter for search
    private void filter(String text){
        ArrayList<User> filteredList=new ArrayList<>();
        for(User user:lstUsers){
            if(user.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(user);
            }
        }

        adapter.filterList(filteredList);
    }
}
