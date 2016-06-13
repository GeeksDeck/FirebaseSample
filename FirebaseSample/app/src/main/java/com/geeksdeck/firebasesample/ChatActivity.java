package com.geeksdeck.firebasesample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

/**
 * Created by syron on 13.06.16.
 */
public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    static final int RC_PHOTO_PICKER = 1;

    private Button sendBtn;
    private EditText messageTxt;
    private RecyclerView messagesList;
    private ChatMessageAdapter adapter;
    private ImageButton imageBtn;


    private FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_chat);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        messageTxt = (EditText) findViewById(R.id.messageTxt);
        messagesList = (RecyclerView) findViewById(R.id.messagesList);
        imageBtn = (ImageButton) findViewById(R.id.imageBtn);
        username = PrefsHelper.getUserName();
        setTitle("Chatting as "+ username);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messagesList.setHasFixedSize(false);
        messagesList.setLayoutManager(layoutManager);

        // Show an image picker when the user wants to upload an imasge
        imageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        adapter = new ChatMessageAdapter(this);
        messagesList.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messagesList.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        // Get the Firebase app and all primitives we'll use
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance(app);
        storage = FirebaseStorage.getInstance(app);


        // Get a reference to our chat "room" in the database
        databaseRef = database.getReference("chat");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChatMessage chat = new ChatMessage(messageTxt.getText().toString(),username);
                // Push the chat message to the database
                databaseRef.push().setValue(chat);
                messageTxt.setText("");
            }
        });
        // Listen for when child nodes get added to the collection
        databaseRef.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot snapshot, String s) {
                // Get the chat message from the snapshot and add it to the UI
                ChatMessage chat = snapshot.getValue(ChatMessage.class);
                adapter.addMessage(chat);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            public void onCancelled(DatabaseError databaseError) { }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Get a reference to the location where we'll store our photos
            storageRef = storage.getReference("chat_photos");
            // Get a reference to store file at chat_photos/<FILENAME>
            final StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            // Set the download URL to the message box, so that the user can send it to the database
                            messageTxt.setText(downloadUrl.toString());
                        }
                    });
        }}



}
    /*private String username;
    private Firebase firebaseRef;
    private EditText inputText;
    private ListView chatList;
    private ValueEventListener connectedListener;
    private ChatMessageAdapter chatListAdapter;
    private static final String FIREBASE_URL = "https://geeksdeck-sample.firebaseio.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        username = PrefsHelper.getUserName();
        setTitle("Chatting as " + username);
        firebaseRef = new Firebase(FIREBASE_URL).child("chat");
        FirebaseApp app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);

        inputText = (EditText) findViewById(R.id.chat_inbox);
        chatList = (ListView) findViewById(R.id.chat_box);

        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.chat_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes

        // Tell our list adapter that we only want 50 messages at a time
        chatListAdapter = new ChatMessageAdapter(firebaseRef.limit(50), this, R.layout.item_message, username);
        chatList.setAdapter(chatListAdapter);
        chatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatList.setSelection(chatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        connectedListener = firebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(ChatActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        firebaseRef.getRoot().child(".info/connected").removeEventListener(connectedListener);
        chatListAdapter.cleanup();
    }

    private void sendMessage() {
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            ChatMessage chat = new ChatMessage(input, username);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            firebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }*/

