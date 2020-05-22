package com.example.chatappvistula;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View privateChats;
    private RecyclerView chatsList;

    private DatabaseReference chatPath,userPath;
    private FirebaseAuth mAuth;
    private String activeUserId;



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChats= inflater.inflate(R.layout.fragment_chat, container, false);

        //firebase
        mAuth=FirebaseAuth.getInstance();
        activeUserId=mAuth.getCurrentUser().getUid();
        chatPath= FirebaseDatabase.getInstance().getReference().child("Chats").child(activeUserId);
        userPath= FirebaseDatabase.getInstance().getReference().child("Users");




        chatsList=privateChats.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChats;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<People>()
                .setQuery(chatPath,People.class)
                .build();


        FirebaseRecyclerAdapter<People,chatsViewHolder> adapter = new FirebaseRecyclerAdapter<People, chatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final chatsViewHolder holder, int i, @NonNull People model) {

                //database

                final String userId = getRef(i).getKey();
                final String[] bringPic = {"Default Picture"};


                userPath.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if(dataSnapshot.exists())
                    {
                        if (dataSnapshot.hasChild("Picture"))
                        {
                            bringPic[0] = dataSnapshot.child("Picture").getValue().toString();

                            Picasso.get().load(bringPic[0]).into(holder.profilePic);

                        }

                        final String bringName = dataSnapshot.child("name").getValue().toString();
                        final String bringStatus = dataSnapshot.child("status").getValue().toString();

                        holder.userName.setText(bringName);
                        holder.userStatus.setText("Last Seen: "+"\n"+"Date"+"Time");

                        //open chat activity for every line
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent chatActivity = new Intent(getContext(),ChatActivity.class);
                                chatActivity.putExtra("user_id_visit",userId);
                                chatActivity.putExtra("user_name_visit",bringName);
                                chatActivity.putExtra("picture_visit", bringPic[0]);
                                startActivity(chatActivity);

                            }
                        });


                    }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public chatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_user_layout,parent,false);
                return new chatsViewHolder(view);

            }
        };

        chatsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    public static class chatsViewHolder extends RecyclerView.ViewHolder {

        //controls
        CircleImageView profilePic;
        TextView userName,userStatus;



        public chatsViewHolder(@NonNull View itemView) {
            super(itemView);

            //control desc
            profilePic = itemView.findViewById(R.id.users_profile_pic);
            userName = itemView.findViewById(R.id.profile_pic_user);
            userStatus = itemView.findViewById(R.id.user_status);



        }
    }


}
