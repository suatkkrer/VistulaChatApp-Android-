package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAdapter mytabAdapter;

    //Firebase
    private FirebaseUser presentuser;
    private FirebaseAuth mAuthorize;
    private DatabaseReference userReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("VistulaChatApp");

        myViewPager = findViewById(R.id.main_tabb_pager);
        mytabAdapter = new TabAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(mytabAdapter);

        myTabLayout = findViewById(R.id.main_tabb);
        myTabLayout.setupWithViewPager(myViewPager);

        //Firebase
        mAuthorize = FirebaseAuth.getInstance();
        presentuser = mAuthorize.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (presentuser == null)
        {
            SenduserToLoginActivity();
        }
        else
        {
            VerifyValidUser();
        }
    }

    private void VerifyValidUser() {

        String validUserId = mAuthorize.getCurrentUser().getUid();

        userReference.child("Users").child(validUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome..", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
                    settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(settings);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SenduserToLoginActivity() {

        Intent loginintent = new Intent(MainActivity.this, LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
             super.onCreateOptionsMenu(menu);

            getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_find_friend)
        {
            Intent findFriend = new Intent(MainActivity.this,FindFriendActivity.class);
            startActivity(findFriend);



        }
        if(item.getItemId()==R.id.main_options)
        {
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);

        }
        if(item.getItemId()==R.id.main_logout)
        {
        mAuthorize.signOut();
        Intent loginPage = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginPage);
        }

        if(item.getItemId()==R.id.main_create_group)
        {
            newGroupRequest();
        }
        return true;
    }

    private void newGroupRequest() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");

        final EditText groupName = new EditText(MainActivity.this);
        groupName.setHint("Example: Software and Development");
        builder.setView(groupName);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName1 = groupName.getText().toString();

                if (TextUtils.isEmpty(groupName1))
                {
                    Toast.makeText(MainActivity.this, "Group Name can not be empty", Toast.LENGTH_LONG).show();
                }
                else

                    {
                        createNewGroup(groupName1);

                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        builder.show();
    }

    private void createNewGroup(final String groupName1) {

        userReference.child("Groups").child(groupName1).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())

                        {
                            Toast.makeText(MainActivity.this, groupName1+" Group Created Successfully", Toast.LENGTH_LONG).show();
                        }

                    }
                });



    }
}

