package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView FindFriendRecyclerList;
    private DatabaseReference userPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        FindFriendRecyclerList=findViewById(R.id.find_friend_recycler_list);
        FindFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        mToolbar=findViewById(R.id.find_friend_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friend");

        //firebase
        userPath = FirebaseDatabase.getInstance().getReference().child("Users");




    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<People> options =
                new FirebaseRecyclerOptions.Builder<People>()
                .setQuery(userPath,People.class)
                .build();



        FirebaseRecyclerAdapter<People,findFriendViewHolder> adapter = new FirebaseRecyclerAdapter<People, findFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull findFriendViewHolder holder, final int i, @NonNull People model) {

                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getPicture()).into(holder.profilePic);

                //clicked

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    String showClickedId = getRef(i).getKey();
                        Intent profileActivity = new Intent(FindFriendActivity.this,ProfileActivity.class);
                        profileActivity.putExtra("showClickedId",showClickedId);
                        startActivity(profileActivity);


                    }
                });

            }

            @NonNull
            @Override
            public findFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_user_layout,parent,false);
                findFriendViewHolder viewHolder = new findFriendViewHolder(view);
                return viewHolder;
            }
        };


        FindFriendRecyclerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();




    }

    public static class findFriendViewHolder extends RecyclerView.ViewHolder

    {
        TextView userName,userStatus;
        CircleImageView profilePic;


        public findFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            //desc
            userName=itemView.findViewById(R.id.profile_pic_user);
            userStatus=itemView.findViewById(R.id.user_status);
            profilePic=itemView.findViewById(R.id.users_profile_pic);



        }
    }
}
