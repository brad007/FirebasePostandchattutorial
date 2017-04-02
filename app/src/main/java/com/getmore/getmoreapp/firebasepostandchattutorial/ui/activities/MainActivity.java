package com.getmore.getmoreapp.firebasepostandchattutorial.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getmore.getmoreapp.firebasepostandchattutorial.R;
import com.getmore.getmoreapp.firebasepostandchattutorial.models.Post;
import com.getmore.getmoreapp.firebasepostandchattutorial.models.User;
import com.getmore.getmoreapp.firebasepostandchattutorial.ui.fragments.HomeFragment;
import com.getmore.getmoreapp.firebasepostandchattutorial.utils.BaseActivity;
import com.getmore.getmoreapp.firebasepostandchattutorial.utils.Constants;
import com.getmore.getmoreapp.firebasepostandchattutorial.utils.FirebaseUtils;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
/*
Up until now, those of you who are watching everything you have done and seen so far is enough
for youu to build notifications in your app. for the next part I'll be working on an app that
I've built in a previous tutorial. if you'd like to watch further it's perfectly okay. but if
you'd like to code along, you can download the code below in the description, I'll also leave a link
to the pervious tutorials below
 */

/*
So the notifications in this app will either be triggered when a user likes a post, or comments
on a post, so let's do that, but we also need to be alerted when to any activity that we make on
a post
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ImageView mDisplayImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;
    private ValueEventListener mUserValueListener;
    private DatabaseReference mUserRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    //User is not signed in
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                }
            }
        };

        init();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment())
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        setupNavHeader(navHeaderView);


        //When a link is clicked, the MainAcitivity will open up and this will handle what needs
        // to be done
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, false)
                .setResultCallback(new ResultCallback<AppInviteInvitationResult>() {
                    @Override
                    public void onResult(@NonNull AppInviteInvitationResult appInviteInvitationResult) {
                        if(appInviteInvitationResult.getStatus().isSuccess()){
                            Intent intent = appInviteInvitationResult.getInvitationIntent();
                            String deepLink = AppInviteReferral.getDeepLink(intent);
                            String uid = deepLink.substring(deepLink.lastIndexOf("/") + 1);

                            FirebaseUtils.getPostRef().child(uid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getValue() != null){
                                                Post post = dataSnapshot.getValue(Post.class);
                                                Intent intent1 = new Intent(MainActivity.this,
                                                        PostActivity.class);
                                                intent1.putExtra(Constants.EXTRA_POST, post);
                                                startActivity(intent1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                });


    }

    //todo:new

    private void init() {
        if (mFirebaseUser != null)
            mUserRef = FirebaseUtils.getUserRef(mFirebaseUser.getEmail().replace(".", ","));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        if (mUserRef != null)
            mUserRef.addValueEventListener(mUserValueListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
        if (mUserRef != null)
            mUserRef.removeEventListener(mUserValueListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setupNavHeader(View view) {
        mDisplayImageView = (ImageView) view.findViewById(R.id.imageView_display);
        mNameTextView = (TextView) view.findViewById(R.id.textView_name);
        mEmailTextView = (TextView) view.findViewById(R.id.textView_email);

        mUserValueListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User users = dataSnapshot.getValue(User.class);
                    Glide.with(MainActivity.this)
                            .load(users.getPhotUrl())
                            .into(mDisplayImageView);

                    mNameTextView.setText(users.getUser());
                    mEmailTextView.setText(users.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }


}
