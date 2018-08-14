package com.example.notepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notepad.Adapter.NoteAdapter;
import com.example.notepad.Bean.Note;
import com.example.notepad.Bean.User;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.notepad.Util.DAOTools.findNote;
import static com.example.notepad.Util.DAOTools.findUserByEmail;

public class NoteFragment extends Fragment {
    private View rootView;
    private Context context;
    private AppCompatActivity mActivity;
    private User user;

    private RecyclerView recyclerView;
    private FloatingActionButton addNote;

    private List<Note> notes;
    private NoteAdapter noteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_note, container, false);
        context = inflater.getContext();
        mActivity = (AppCompatActivity)getActivity();

        User oldUser = (User) mActivity.getIntent().getSerializableExtra("user");
        user = findUserByEmail(oldUser);

        initView();
        setRecyclerView();
        addButtonListener();
        return rootView;
    }

    public void initView(){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        addNote = (FloatingActionButton) rootView.findViewById(R.id.add_note);
    }

    public void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        notes = findNote(user, "false");
        if(notes.size()>0){
            noteAdapter = new NoteAdapter(notes);
            recyclerView.setAdapter(noteAdapter);
        }
    }

    public void addButtonListener(){
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddNoteActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    public static void refreshNotes(final List<Note>notes, final RecyclerView recyclerView){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(new NoteAdapter(notes));
            }
        }, 100);
    }

    @Override
    public void onResume() {
        SharedPreferences pref = context.getSharedPreferences("encrypt", MODE_PRIVATE);
        String encrypt = pref.getString("encrypt", "false");
        List<Note>notes = findNote(user, encrypt);
        if(notes.size() > 0){
            refreshNotes(notes, recyclerView);
        }
        super.onResume();
    }

}
