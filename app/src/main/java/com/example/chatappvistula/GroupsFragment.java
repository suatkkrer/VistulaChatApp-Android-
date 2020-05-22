package com.example.chatappvistula;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
    private View groupFrameView;
    private ListView list_view;
    private ArrayAdapter<String>arrayAdapter;
    private ArrayList<String>group_lists = new ArrayList<>();


    //Firebase
    private DatabaseReference groupPath;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFrameView =  inflater.inflate(R.layout.fragment_groups, container, false);

        //firebase
        groupPath= FirebaseDatabase.getInstance().getReference().child("Groups");


        list_view = groupFrameView.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,group_lists);
        list_view.setAdapter(arrayAdapter);

        bringGroupsAndShow();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String validGroupName = parent.getItemAtPosition(position).toString();

                Intent groupChatActivity = new Intent(getContext(),GroupChatActivity.class);
                groupChatActivity.putExtra("GroupName",validGroupName);
                startActivity(groupChatActivity);

            }
        });


        return  groupFrameView;
    }

    private void bringGroupsAndShow() {

        groupPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String>set=new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext())

                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                group_lists.clear();
                group_lists.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
