package com.example.notesmitra;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class NotesActivity extends AppCompatActivity {

    FloatingActionButton mcreatenotefab;
    FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<FirebaseModel,NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        getSupportActionBar().setTitle("Your Notes");


        firebaseAuth = FirebaseAuth.getInstance();
        mcreatenotefab = findViewById(R.id.createnotefab);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Calendar calendar = Calendar.getInstance();
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        String formattedDate = day+"/"+month+"/"+year;

        mcreatenotefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this,CreateNewNote.class));
            }
        });

        List<String> condition = new ArrayList<>();
        condition.add(formattedDate);
        condition.add("For : DD/MM/YYYY");


        Query query = firebaseFirestore.collection("notes2")
                .document(firebaseUser.getUid())
                .collection("mynotes2")
                .whereIn("date",condition)
                .orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FirebaseModel> everyUsersNotes = new FirestoreRecyclerOptions.Builder<FirebaseModel>()
                .setQuery(query,FirebaseModel.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(everyUsersNotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull FirebaseModel model) {
                    ImageView popUpButton = holder.itemView.findViewById(R.id.menupopbutton);

                    int colorCode = getRandomColor();
                    holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colorCode,null));
                    holder.noteTitle.setText(model.getTitle());
                    holder.noteContent.setText(model.getContent());
                    holder.notedate.setText(model.getDate());



                    String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(),NoteDetailsActivity.class);
                                intent.putExtra("title",model.getTitle());
                                intent.putExtra("content",model.getContent());
                                intent.putExtra("date",model.getDate());
                                System.out.println("Date from firebase "+intent.putExtra("date",model.getDate()));
                                intent.putExtra("noteId",docId);

                                v.getContext().startActivity(intent);
                        }
                    });

                    popUpButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                            popupMenu.setGravity(Gravity.END);

                            popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(@NonNull MenuItem item) {
                                    Intent intent = new Intent(v.getContext(),EditNoteActivity.class);
                                    intent.putExtra("title",model.getTitle());
                                    intent.putExtra("content",model.getContent());
                                    intent.putExtra("date",model.getDate());
                                    intent.putExtra("noteId",docId);

                                    v.getContext().startActivity(intent);
                                    return true;
                                }
                            });

                            popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(@NonNull MenuItem item) {
                                    Toast.makeText(v.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();

                                    DocumentReference documentReference = firebaseFirestore
                                            .collection("notes2")
                                            .document(firebaseUser.getUid())
                                            .collection("mynotes2")
                                            .document(docId);
                                    documentReference.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(v.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(NotesActivity.this,NotesActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(v.getContext(),"Failed to delete",Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                    return false;
                                }
                            });

                            popupMenu.show();

                        }
                    });
            }



            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.notes_layout,parent,false);

                return new NoteViewHolder(view);
            }
        };

        mrecyclerview = findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView noteTitle;
        private TextView noteContent;
        private TextView notedate;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.notetitle);
            noteContent = itemView.findViewById(R.id.notecontent);
            notedate = itemView.findViewById(R.id.dateofnote);
            mnote = itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.notesactimenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(NotesActivity.this,MainActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null){
            noteAdapter.startListening();
        }
    }

    private int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.noteColor1);
        colorCode.add(R.color.noteColor2);
        colorCode.add(R.color.noteColor3);
        colorCode.add(R.color.noteColor4);
        colorCode.add(R.color.noteColor5);
        colorCode.add(R.color.noteColor6);
        colorCode.add(R.color.noteColor7);
        colorCode.add(R.color.noteColor8);

        Random random = new Random();
        int number = random.nextInt(colorCode.size());

        return colorCode.get(number);
    }
}