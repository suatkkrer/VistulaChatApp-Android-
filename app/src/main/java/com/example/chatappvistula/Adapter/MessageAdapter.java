package com.example.chatappvistula.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappvistula.Messages;
import com.example.chatappvistula.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessagesViewHolder> {

    private List <Messages> userMessageList;


    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userPath;

    //adapter
    public MessageAdapter (List<Messages> userMessageList )
    {
        this.userMessageList=userMessageList;
    }

    //ViewHolder
    public class MessagesViewHolder extends RecyclerView.ViewHolder
    {

        //private messages layout contrlos
        public TextView senderMessageText,receiverMessageText;
        public CircleImageView receiverProfilePic;



        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMessageText=itemView.findViewById(R.id.receiver_message_text);
            senderMessageText=itemView.findViewById(R.id.sender_message_text);
            receiverProfilePic=itemView.findViewById(R.id.message_profile_picc);



        }
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.private_messages_layout,parent,false);

        //firebase
        mAuth=FirebaseAuth.getInstance();

        return new MessagesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder messagesViewHolder, int position)
    {
        String messageSenderId=mAuth.getCurrentUser().getUid();

        //model desc

        Messages messages = userMessageList.get(position);

        String from_whomUserId = messages.getFrom_whom();
        String messageType = messages.getType();

        //database Path
       userPath= FirebaseDatabase.getInstance().getReference().child("Users").child(from_whomUserId);

        userPath.addValueEventListener(new ValueEventListener() {

            //bring data from firebase
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Picture"))
                {
                    String PicReceiver = dataSnapshot.child("Picture").getValue().toString();

                    Picasso.get().load(PicReceiver).placeholder(R.drawable.ic_person_black_24dp).into(messagesViewHolder.receiverProfilePic);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (messageType.equals("text"))
        {
            messagesViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
            messagesViewHolder.receiverProfilePic.setVisibility(View.INVISIBLE);
            messagesViewHolder.senderMessageText.setVisibility(View.INVISIBLE);

            if (from_whomUserId.equals(messageSenderId))


            {

                messagesViewHolder.senderMessageText.setVisibility(View.VISIBLE);

                messagesViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
            messagesViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messagesViewHolder.senderMessageText.setText(messages.getMessage());
            }

            else

            {



                messagesViewHolder.receiverProfilePic.setVisibility(View.VISIBLE);
                messagesViewHolder.receiverMessageText.setVisibility(View.VISIBLE);

                messagesViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                messagesViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messagesViewHolder.receiverMessageText.setText(messages.getMessage());



            }
        }

    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }
}
