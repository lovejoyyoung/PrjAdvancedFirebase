 package com.example.prjadvfirebase7260_oct29;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import model.Car;
import model.Person;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, ValueEventListener, ChildEventListener{

    EditText editTextId;
    Button btnAdd,btnBrowse,btnUpload,btnFind;
    ImageView imageViewPhoto;

    DatabaseReference personDatabase;

    final int IMAGE_REQUEST = 71;

    Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {

        editTextId = findViewById(R.id.editTextId);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);

        btnAdd = findViewById(R.id.btnAdd);
        btnUpload = findViewById(R.id.btnUpload);
        btnBrowse = findViewById(R.id.btnBrowse);
        btnFind = findViewById(R.id.btnFind);

        btnFind.setOnClickListener(this);
        btnBrowse.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        personDatabase = FirebaseDatabase.getInstance().getReference("person");

        Log.d("FIREBASE","Widgets are initialized!");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


    }

    @Override
    public void onClick(View view) {

        int id  = view.getId();
        switch (id){

            case R.id.btnAdd:addPerson();break;
            case R.id.btnBrowse:browse();break;
            case R.id.btnUpload:upload();break;
            case R.id.btnFind:findPerson();break;
        }
    }

    private void upload() {

        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);

            progressDialog.setTitle("Upload the image in progress ...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/myphoto.jpg");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("FIREBASE","The photo is uploaded successfully");
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("FIREBASE",e.getMessage());
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            Log.d("FIREBASE","Upload in progress ...");
                        }
                    });

        }

    }

    private void browse() {

        Intent intent = new Intent();
        intent.setType("images/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select photo"),IMAGE_REQUEST);
    }


    private void addPerson() {

        try{
            ArrayList<String> hobbies = new ArrayList<String>();
            hobbies.add("Soccer"); hobbies.add("Handball");
            hobbies.add("Music"); hobbies.add("Read");
            hobbies.add("Draw");

            Car car = new Car("M400","Toyota","Corolla");

            Person person = new Person(400,"Catherine","=====",car,hobbies);

            personDatabase.child(String.valueOf("400")).setValue(person);

            Log.d("FIREBASE","The person" + person.toString()+ "is added successfully !");

        }catch (Exception e){
            Log.d("FIREBASE",e.getMessage());
        }


    }

    private void findPerson() {

        String id = editTextId.getText().toString();
        DatabaseReference personChild = FirebaseDatabase.getInstance().getReference("person").child(id);
        personChild.addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        try{

            if (dataSnapshot.exists()) {

                String name = dataSnapshot.child("name").getValue().toString();

                Log.d("FIREBASE", "The name is " + name);


                //--Find Hobbies
                ArrayList<String> hobbies = (ArrayList) dataSnapshot.child("hobbies").getValue();

                //Log.d("FIREBASE", hobbies.toString());

                int counter = 0;
                for(String oneHobby:hobbies)
                    Log.d("FIREBASE",counter++ +":"+oneHobby);

                //Find Car
                HashMap car = (HashMap) dataSnapshot.child("car").getValue();

                Log.d("FIREBASE", car.toString());
                //Log.d("FIREBASE", car.get("brand").toString());



                //-=================================================================

                // !!!!! Load photos on firebase
                // add the library :Picasso
                // implementation 'com.squreup.picasso:picasso:2.5.2'
                // find and display the photo

                String urlPhoto = dataSnapshot.child("photo").getValue().toString();
                Log.d("FIREBASE", urlPhoto);

                //displyay the photo
//                Picasso
//                        .with(this)
//                        .load(urlPhoto)
//                        .placeholder(R.drawable.noimage)
//                        .into(imageViewPhoto);
//                Log.d("FIREBASE", "The photo is displayed successfully");
                Picasso.with(this).load(urlPhoto).into(imageViewPhoto);
                }


            }catch (Exception e) {
                Log.d("FIREBASE",e.getMessage());
            }
        }


    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            filePath = data.getData();

            try{
                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageViewPhoto.setImageBitmap(bitmap);

            }catch (Exception e){
                Log.d("FIREBASE",e.getMessage());
            }
        }
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }



}