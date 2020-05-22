package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappvistula.Adapter.MessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private  String idMessageReceiver,nameMessageReceiver,receiverPicMessage,idMessageSender;

    private TextView userName,userLastSeen;
    private CircleImageView userPic;

    private ImageView mainPageArrow;

    private ImageButton sendMessageButton;

    private EditText enteredMessage;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessageList;


    //firebase

    private FirebaseAuth mAuth;
    private DatabaseReference messagePath;

    //toolbar

    private Toolbar chatToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //bring intent from chat fragment



        idMessageReceiver=getIntent().getExtras().get("user_id_visit").toString();
        nameMessageReceiver=getIntent().getExtras().get("user_name_visit").toString();
        receiverPicMessage=getIntent().getExtras().get("picture_visit").toString();





        userName=findViewById(R.id.show_username_chat_activity);
        userLastSeen=findViewById(R.id.show_user_last_seen);
        userPic=findViewById(R.id.show_user_pic_chat_activity);
        mainPageArrow=findViewById(R.id.send_main_page);
        sendMessageButton=findViewById(R.id.send_message_btn);
        enteredMessage=findViewById(R.id.entered_message);

        messageAdapter=new MessageAdapter(messagesList);
        userMessageList=findViewById(R.id.private_message_list);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);


        //firebase

        mAuth = FirebaseAuth.getInstance();
        messagePath= FirebaseDatabase.getInstance().getReference();
        idMessageSender=mAuth.getCurrentUser().getUid();




        mainPageArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chats = new Intent(ChatActivity.this,MainActivity.class);
                startActivity(chats);

            }
        });


        //transfer info to control with intent
        userName.setText(nameMessageReceiver);
        Picasso.get().load(receiverPicMessage).placeholder(R.drawable.ic_person_black_24dp).into(userPic);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                
                sendMessage();


            }
        });





    }


    @Override
    protected void onStart() {
        super.onStart();

                //bring datas from database
        messagePath.child("Messages").child(idMessageSender).child(idMessageReceiver)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                        Messages messages = dataSnapshot.getValue(Messages.class);



                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        //scroll Vew
                        userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());

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

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void sendMessage() {
        
        //bring message from control
        String messageText = enteredMessage.getText().toString();
        
        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "Please Write Your Message", Toast.LENGTH_SHORT).show();
        }
        
        else
        {
            String senderMessagePath = "Messages/"+idMessageSender+"/"+idMessageReceiver;
            String receiverMessagePath = "Messages/"+idMessageReceiver+"/"+idMessageSender;

            DatabaseReference userMessageKeyPath = messagePath.child("Messages").child(idMessageSender).child(idMessageReceiver).push();

            String addMessageId = userMessageKeyPath.getKey();

            Map messageTextBody = new HashMap();

            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from_whom",idMessageSender);


            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(senderMessagePath+"/"+addMessageId,messageTextBody);
            messageBodyDetails.put(receiverMessagePath+"/"+addMessageId,messageTextBody);

            messagePath.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message is Sent", Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Message is not Sent", Toast.LENGTH_SHORT).show();
                    }

                    enteredMessage.setText("");

                }
            });


        }
        
        
    }
}
