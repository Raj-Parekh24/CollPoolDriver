package com.example.collpooldriver;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DriveInfo extends AppCompatActivity {
    private static int intentCheckForLicense=123;
    private static int intentCheckForRC=312;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference,storageroot;
    private DatabaseReference databaseReference;
    private EditText numberPlate,carCompany,carModel;
    private Spinner type;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private Bitmap imageOfLicense,imageOfRc;
    private Uri imagePath;
    private UploadTask uploadTask;
    //private DriverData driverData;
    private Map<String,String> map;
    private Intent e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_info);
        Intent intent=getIntent();
        String phoneNumber=intent.getStringExtra("phoneno.");
        e=new Intent(DriveInfo.this,OneTimePass.class);
        e.putExtra("phoneno.",phoneNumber);
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance("https://coll-pool-driver.firebaseio.com/");//+firebaseAuth.getCurrentUser().getUid()+"/User");
        databaseReference=firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
        storageReference=firebaseStorage.getReference();
        storageroot=storageReference.child(firebaseAuth.getCurrentUser().getUid());
        map=new HashMap<String, String>();
        numberPlate=(EditText)findViewById(R.id.numberplate);
        carCompany=(EditText)findViewById(R.id.model1);
        carModel=(EditText)findViewById(R.id.model2);
        type=(Spinner)findViewById(R.id.Type);
        progressDialog=new ProgressDialog(this);
    }
    //for checking fields are empty or not
    private boolean validate()
    {
        if(numberPlate.getText().toString().isEmpty()||carCompany.getText().toString().isEmpty()||carModel.getText().toString().isEmpty())
        {
            Toast.makeText(DriveInfo.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(imageOfLicense==null)
        {
            Toast.makeText(DriveInfo.this,"Please select image of Driving License",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(imageOfRc==null)
        {
            Toast.makeText(DriveInfo.this,"Please select image of RC Book",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //for assigning values
    private boolean collectDriverData()
    {
        if(validate())
        {
            //driverData=new DriverData();
            map.put("Vehicale Number",numberPlate.getText().toString());
            map.put("Car Company",carCompany.getText().toString());
            map.put("Car Model",carModel.getText().toString());
            map.put("Car type",type.getSelectedItem().toString());
            //driverData.setNumberplate(numberPlate.getText().toString().trim());
            //driverData.setCarCompany(carCompany.getText().toString());
            //driverData.setCarModel(carModel.getText().toString());
            //driverData.setType(type.getSelectedItem().toString());
            return true;
        }
        return false;
    }

    //for transferring to cloud
    public void continueToNext(View view)
    {
        if(collectDriverData())
        {
            progressDialog.setMessage("Uploading");
            progressDialog.show();
           // databaseReference.child("User").push().setValue("Vehicle details");
           // final DatabaseReference user=databaseReference.child("User").child("Vehicle Details");
           databaseReference.child("User").child("Vehicle Details").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(DriveInfo.this,"Successfull",Toast.LENGTH_SHORT).show();
                        startActivity(e);
                    }
                }
            });
        }
        else{
            Toast.makeText(this,"Sorry",Toast.LENGTH_SHORT).show();
        }
    }
    public void onDrivingLicense(View view)
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image of Driving License"),intentCheckForLicense);
    }

    public void onRcBook(View view)
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image of RC Book"),intentCheckForRC);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==intentCheckForLicense && resultCode==RESULT_OK && data!=null)
        {
            imagePath=data.getData();
            final ProgressDialog x=new ProgressDialog(DriveInfo.this);
            x.setMessage("License Going");
            x.show();
            try {
                imageOfLicense= MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                uploadTask=storageroot.child("Driving License").putFile(imagePath);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        x.dismiss();
                        Toast.makeText(DriveInfo.this,"Some failure occur while uploading",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        x.dismiss();
                        Toast.makeText(DriveInfo.this,"Image upload successfully",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==intentCheckForRC && resultCode==RESULT_OK && data!=null)
        {
            imagePath=data.getData();
            try {
                imageOfRc= MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                uploadTask=storageroot.child("Rc Book").putFile(imagePath);
                final ProgressDialog x=new ProgressDialog(DriveInfo.this);
                x.setMessage("Rc Book Going");
                x.show();
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        x.dismiss();
                        Toast.makeText(DriveInfo.this,"Some failure occur while uploading",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        x.dismiss();
                        Toast.makeText(DriveInfo.this,"Image upload successfully",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
