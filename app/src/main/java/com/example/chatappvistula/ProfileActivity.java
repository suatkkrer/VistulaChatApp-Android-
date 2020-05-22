package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receivedUserId,active_status,activeUserId;

    private CircleImageView userProfilePic;
    private TextView userProfileName,userProfileStatus;
    private Button sendRequestButton,cancelMessageRequestButton;

    //firebase
    private DatabaseReference userPath,chatRequestPath,chatPath;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receivedUserId=getIntent().getExtras().get("showClickedId").toString();


         userProfileName=findViewById(R.id.username_visit);
         userProfilePic=findViewById(R.id.profile_pic_visit);
         userProfileStatus=findViewById(R.id.profile_status_visit);
         sendRequestButton=findViewById(R.id.request_message_button);
         cancelMessageRequestButton=findViewById(R.id.cancel_message_request);
         mAuth=FirebaseAuth.getInstance();
         activeUserId=mAuth.getCurrentUser().getUid();


         active_status="new";
         userPath= FirebaseDatabase.getInstance().getReference().child("Users");
         chatRequestPath= FirebaseDatabase.getInstance().getReference().child("Chat Request");
         chatPath= FirebaseDatabase.getInstance().getReference().child("Chats");



         bringUserData();



    }

    private void bringUserData() {

        userPath.child(receivedUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("Picture")))
                {
                    String userPic = dataSnapshot.child("Picture").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userPic).placeholder(R.drawable.ic_person_black_24dp).into(userProfilePic);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    //chat request
                    controlChatRequest();
                }

                else
                {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    //chat request
                    controlChatRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {



            }
        });


    }

    private void controlChatRequest() {


            //if there is request button shows cancellation
        chatRequestPath.child(activeUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(receivedUserId))
                {
                    String request_type = dataSnapshot.child(receivedUserId).child("request_type").getValue().toString();

                    if(request_type.equals("sent"))
                    {
                        active_status = "request_is_sent";
                        sendRequestButton.setText("Cancel Message Request");


                    }

                    else
                    {
                        active_status = "request_received";
                        sendRequestButton.setText("Accept Message Request");
                        cancelMessageRequestButton.setVisibility(View.VISIBLE);
                        cancelMessageRequestButton.setEnabled(true);

                        cancelMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                cancelMessageRequest();


                            }
                        });

                    }
                }

                else
                {
                    chatPath.child(activeUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receivedUserId))
                                    {
                                        active_status="Friends";
                                        sendRequestButton.setText("Delete this chat");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(activeUserId.equals(receivedUserId))
        {   //hide button
            sendRequestButton.setVisibility(View.INVISIBLE);
        }

        else
            {
                sendRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendRequestButton.setEnabled(false);

                        if(active_status.equals("new"))

                        {
                            sendChatRequest();
                        }
                        if(active_status.equals("request_is_sent"))
                        {
                            cancelMessageRequest();
                        }

                        if(active_status.equals("request_is_received"))
                        {
                            
                            acceptMessageRequest();
                        }

                        if(active_status.equals("Friends"))
                        {

                            deletePrivateChat();
                        }
                    }
                });

        }
    }

    private void deletePrivateChat() {

        chatPath.child(activeUserId).child(receivedUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    chatRequestPath.child(receivedUserId).child(activeUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                sendRequestButton.setEnabled(true);
                                active_status = "new";
                                sendRequestButton.setText("Send Message Request");

                                cancelMessageRequestButton.setVisibility(View.INVISIBLE);
                                cancelMessageRequestButton.setEnabled(false);
                            }


                        }
                    });

                }



            }
        });

    }

    private void acceptMessageRequest() {

        chatPath.child(activeUserId).child(receivedUserId).child("Chats").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful())
                      {
                          chatPath.child(receivedUserId).child(activeUserId).child("Chats").setValue("Saved")
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            chatPath.child(activeUserId).child(receivedUserId)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful())
                                                            {
                                                                chatPath.child(receivedUserId).child(activeUserId)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                sendRequestButton.setEnabled(true);
                                                                                active_status="Friends";
                                                                                sendRequestButton.setText("Delete this chat");
                                                                                cancelMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                cancelMessageRequestButton.setEnabled(false);

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                      }
                                  });
                      }
                    }
                });



    }

    private void cancelMessageRequest() {

        //delete request from sender
        chatRequestPath.child(activeUserId).child(receivedUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    chatPath.child(receivedUserId).child(activeUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                sendRequestButton.setEnabled(true);
                                active_status = "new";
                                sendRequestButton.setText("Send Message Request");

                                cancelMessageRequestButton.setVisibility(View.INVISIBLE);
                                cancelMessageRequestButton.setEnabled(false);
                            }


                        }
                    });

                }



            }
        });


    }

    private void sendChatRequest() {

        chatRequestPath.child(activeUserId).child(receivedUserId).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            chatRequestPath.child(receivedUserId).child(activeUserId).child("request_type")
                                    .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        sendRequestButton.setEnabled(true);
                                        active_status="request_is_sent";
                                        sendRequestButton.setText("Cancel Message Request");

                                    }

                                }
                            });
                        }

                    }
                });
    }
}
