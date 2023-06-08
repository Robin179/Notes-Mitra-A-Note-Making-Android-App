package com.example.notesmitra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateNewNote extends AppCompatActivity {

    EditText mcreatetitleofnote,mcreatecontentofnote;
    FloatingActionButton msavenote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Button msetdateofnewnote;
    DatePickerDialog datePickerDialog;

    ProgressBar mprogressbarofcreatenote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_note);

        msavenote = findViewById(R.id.savenote);
        mprogressbarofcreatenote = findViewById(R.id.progressbarofcreatenote);
        mcreatecontentofnote = findViewById(R.id.createcontentofnote);
        mcreatetitleofnote = findViewById(R.id.createtitleofnote);
        msetdateofnewnote = findViewById(R.id.setdateofnewnote);

        Toolbar toolbar = findViewById(R.id.toolbarofcreatenewnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

        Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month  = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        msetdateofnewnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog =
                        new DatePickerDialog(CreateNewNote.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String fordate = dayOfMonth+"/"+month+"/"+year;
                        msetdateofnewnote.setText(fordate);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });



        msavenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mcreatetitleofnote.getText().toString();
                String content = mcreatecontentofnote.getText().toString();
                String date = msetdateofnewnote.getText().toString();
                if(title.isEmpty() || content.isEmpty()){

                    Toast.makeText(getApplicationContext()
                            ,"Both fields are Required",Toast.LENGTH_SHORT).show();
                }
                else{

                    mprogressbarofcreatenote.setVisibility(View.VISIBLE);

                    DocumentReference documentReference = firebaseFirestore.collection("notes2")
                            .document(firebaseUser.getUid())
                            .collection("mynotes2")
                            .document();
                    Map<String,Object> note = new HashMap<>();

                    note.put("title",title);
                    note.put("content",content);
                    note.put("date",date);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(getApplicationContext(),"Note created successfully ",Toast.LENGTH_SHORT).show();
                            mprogressbarofcreatenote.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(CreateNewNote.this,NotesActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mprogressbarofcreatenote.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"Failed to create note ",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}