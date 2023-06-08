package com.example.notesmitra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteDetailsActivity extends AppCompatActivity {

    private TextView mtitleofnotedetails,mcontentofnotedetails,mdateofnotedetails;
    FloatingActionButton mgotoeditnote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        mtitleofnotedetails = findViewById(R.id.titleofnotedetails);
        mcontentofnotedetails = findViewById(R.id.contentofnotedetails);
        mdateofnotedetails = findViewById(R.id.dateofnotedetails);
        mgotoeditnote = findViewById(R.id.gotoeditnote);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();


        mcontentofnotedetails.setText(data.getStringExtra("content"));
        mtitleofnotedetails.setText(data.getStringExtra("title"));
        mdateofnotedetails.setText(data.getStringExtra("date"));

        mgotoeditnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),EditNoteActivity.class);
                intent.putExtra("title",data.getStringExtra("title"));
                intent.putExtra("content",data.getStringExtra("content"));
                intent.putExtra("date",data.getStringExtra("date"));
                intent.putExtra("noteId",data.getStringExtra("noteId"));

                v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}