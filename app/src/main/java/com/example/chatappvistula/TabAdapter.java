package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {
    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatFragment chatsfragment = new ChatFragment();
                return chatsfragment;

            case 1:
                GroupsFragment groupsfragement = new GroupsFragment();
                return groupsfragement;

            case 2:
                FriendsFragment friendsfragement = new FriendsFragment();
                return friendsfragement;

            case 3:
                RequestsFragment requestFragment  = new RequestsFragment();
                return requestFragment;


            default:
                return null;


        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Chats";

                case 1:
                    return "Groups";

                case 2:
                    return "Friends";

                case 3:
                    return "Requests";

                default:
                    return null;


            }
        }
    }

