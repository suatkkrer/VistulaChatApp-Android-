package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText UserMessageInput;
    private ScrollView mScrollView;
    private TextView showTextMessages;

    //firebase
    private FirebaseAuth mAuthorize;
    private DatabaseReference userPath,groupNamePath,groupMessageKeyPath;

    //Intent Parameter
    private String validGroupName,activeUsername,activeUserId,currentDate,currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //Bring Intent
        validGroupName=getIntent().getExtras().get("GroupName").toString();
        Toast.makeText(this, validGroupName, Toast.LENGTH_LONG).show();

        //Firebase description
        mAuthorize=FirebaseAuth.getInstance();
        activeUserId=mAuthorize.getCurrentUser().getUid();
        userPath= FirebaseDatabase.getInstance().getReference().child("Users");
        groupNamePath= FirebaseDatabase.getInstance().getReference().child("Groups").child(validGroupName);





        //Descriptions

        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(validGroupName);

        sendMessageButton=findViewById(R.id.send_message_button);
        UserMessageInput=findViewById(R.id.group_message);
        showTextMessages=findViewById(R.id.show_group_chat_text);
        mScrollView=findViewById(R.id.my_scroll_view);

        //User Input

        InputUserData();

        //
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveMessageToDatabase();

                UserMessageInput.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        groupNamePath.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists())
                {
                    showMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists())
                {
                    showMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessages(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatDate = (String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage= (String)((DataSnapshot)iterator.next()).getValue();
            String chatName= (String)((DataSnapshot)iterator.next()).getValue();
            String chatTime= (String)((DataSnapshot)iterator.next()).getValue();

            showTextMessages.append(chatName + "  :\n" + chatMessage +"\n" + chatTime + "    "+chatDate+"\n\n\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void saveMessageToDatabase() {

        String message=UserMessageInput.getText().toString();
        String messageKey = groupNamePath.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Message box can not be empty", Toast.LENGTH_LONG).show();
        }

        else{
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat activeDateFormat = new SimpleDateFormat("DD.MM.YYYY");
            currentDate = activeDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat activeTimeFormat = new SimpleDateFormat("HH.MM.SS");
            currentTime= activeTimeFormat.format(calForTime.getTime());

            HashMap<String,Object>groupMessageKey = new HashMap<>();
            groupNamePath.updateChildren(groupMessageKey);

            groupMessageKeyPath = groupNamePath.child(messageKey);

            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",activeUsername);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            groupMessageKeyPath.updateChildren(messageInfoMap);
        }
    }

    private void InputUserData() {

        userPath.child(activeUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    {
                        activeUsername=dataSnapshot.child("name").getValue().toString();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
