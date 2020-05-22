package com.example.chatappvistula;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View RequestsFragmentView;
    private RecyclerView requestList;

    //firebas
    private DatabaseReference chatRequestPath,usersPath,chatPath;
    private FirebaseAuth mAuth;
    private String activeUserId;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView =inflater.inflate(R.layout.fragment_requests, container, false);

        //firebase
        mAuth=FirebaseAuth.getInstance();
        activeUserId=mAuth.getCurrentUser().getUid();



        chatRequestPath= FirebaseDatabase.getInstance().getReference().child("Chat Request");
        usersPath= FirebaseDatabase.getInstance().getReference().child("Users");
        chatPath= FirebaseDatabase.getInstance().getReference().child("Chats");



        requestList=RequestsFragmentView.findViewById(R.id.chat_request_list);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));



        return RequestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<People> options = new FirebaseRecyclerOptions.Builder<People>()
                .setQuery(chatRequestPath.child(activeUserId),People.class)
                .build();


        FirebaseRecyclerAdapter<People,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<People, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int i, @NonNull People model) {

                //button visibility

                holder.itemView.findViewById(R.id.accept_request_button).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.cancel_request_button).setVisibility(View.VISIBLE);

                final String user_id_list = getRef(i).getKey();

                DatabaseReference requestType = getRef(i).child("request_type").getRef();

                requestType.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            String type = dataSnapshot.getValue().toString();
                            if(type.equals("received"))
                            {
                                usersPath.child(user_id_list).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("Picture"))
                                        {       //bring datas from database add to parameters

                                            final String requestProfilePic = dataSnapshot.child("Picture").getValue().toString();

                                            //send datas to controls

                                            Picasso.get().load(requestProfilePic).into(holder.profilePic);



                                        }


                                                final String requestUsername = dataSnapshot.child("name").getValue().toString();
                                                final String requestUserStatus = dataSnapshot.child("status").getValue().toString();


                                                //send datas to controls
                                                holder.userName.setText(requestUsername);
                                                holder.userStatus.setText("User wants to create chat");




                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "accept",
                                                                "cancel"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestUsername+" Chat Request");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if(which == 0)
                                                        {
                                                            chatPath.child(activeUserId).child(user_id_list).child("Chats")
                                                                    .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful())
                                                                    {
                                                                        chatPath.child(user_id_list).child(activeUserId)
                                                                                .child("Chats").setValue("Saved")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            chatRequestPath.child(activeUserId).child(user_id_list)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful())
                                                                                                            {
                                                                                                                chatRequestPath.child(user_id_list).child(activeUserId)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                Toast.makeText(getContext(), "Chat is Saved...", Toast.LENGTH_LONG).show();
                                                                                                                                
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
                                                        if(which == 1)
                                                        {

                                                            chatRequestPath.child(activeUserId).child(user_id_list)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                chatRequestPath.child(user_id_list).child(activeUserId)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                Toast.makeText(getContext(), "Chat is Deleted...", Toast.LENGTH_LONG).show();

                                                                                            }
                                                                                        });
                                                                            }



                                                                        }
                                                                    });

                                                        }


                                                    }
                                                });

                                                builder.show();


                                            }
                                        });



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_user_layout,parent,false);



                RequestViewHolder holder = new RequestViewHolder(view);


                return holder;


            }
        };

        requestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();


    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {

        TextView userName,userStatus;
        CircleImageView profilePic;
        Button acceptButton,cancelButton;

        public RequestViewHolder(@NonNull View itemView) {

            //control desc
            super(itemView);
            userName=itemView.findViewById(R.id.profile_pic_user);
            userStatus=itemView.findViewById(R.id.user_status);
            profilePic=itemView.findViewById(R.id.users_profile_pic);
            acceptButton=itemView.findViewById(R.id.accept_request_button);
            cancelButton=itemView.findViewById(R.id.cancel_request_button);



        }
    }


}
