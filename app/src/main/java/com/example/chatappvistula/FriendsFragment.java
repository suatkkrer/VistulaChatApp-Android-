package com.example.chatappvistula;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class FriendsFragment extends Fragment {

    private View friendsView;

    private RecyclerView friendsList;

    //direbase
    private DatabaseReference chatPath,userPath;
    private FirebaseAuth mAuth;

    private String activeUserId;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        friendsView = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsList=friendsView.findViewById(R.id.friends_list);
        friendsList.setLayoutManager((new LinearLayoutManager(getContext())));

        //firebase
        mAuth=FirebaseAuth.getInstance();

        activeUserId=mAuth.getCurrentUser().getUid();
        chatPath = FirebaseDatabase.getInstance().getReference().child("Chats").child(activeUserId);
        userPath = FirebaseDatabase.getInstance().getReference().child("Users");




        return friendsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<People>()
                .setQuery(chatPath,People.class)
                .build();

        //Adapter
        FirebaseRecyclerAdapter<People,friendsViewHolder>adapter= new FirebaseRecyclerAdapter<People, friendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final friendsViewHolder holder, int i, @NonNull People model) {

                String clickedUserId = getRef(i).getKey();
                userPath.child(clickedUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("Picture"))
                        {
                            String userPic =  dataSnapshot.child("Picture").getValue().toString();
                            String userName =  dataSnapshot.child("name").getValue().toString();
                            String userStatus =  dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(userName);
                            holder.userStatus.setText(userStatus);
                            Picasso.get().load(userPic).placeholder(R.drawable.ic_person_black_24dp).into(holder.userPic);



                        }
                        else
                        {
                            String userName =  dataSnapshot.child("name").getValue().toString();
                            String userStatus =  dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(userName);
                            holder.userStatus.setText(userStatus);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public friendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_user_layout,parent,false);

                friendsViewHolder viewHolder = new friendsViewHolder(view);
                return viewHolder;



            }
        };

        friendsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();




    }


    public static class friendsViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircleImageView userPic;




        public friendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.profile_pic_user);
            userStatus=itemView.findViewById(R.id.user_status);
            userPic=itemView.findViewById(R.id.users_profile_pic);


        }
    }



}
