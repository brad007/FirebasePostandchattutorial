package com.getmore.getmoreapp.firebasepostandchattutorial.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by brad on 2017/02/01.
 */

public class FirebaseUtils {
    public static DatabaseReference getUserRef(String email) {
        return FirebaseDatabase.getInstance()
                .getReference(Constants.USERS_KEYS)
                .child(email);
    }

    public static DatabaseReference getPostRef() {
        return FirebaseDatabase.getInstance()
                .getReference(Constants.POSTS_KEY);
    }

    public static Query getPostQuery() {
        return getPostRef().orderByChild(Constants.TIME_CREATED_KEY);
    }

    public static DatabaseReference getPostLikedRef() {
        return FirebaseDatabase.getInstance()
                .getReference(Constants.POSTS_LIKED_KEY);
    }

    public static DatabaseReference getPostLikedRef(String postId) {
        return getPostLikedRef().child(getCurrentUser().getEmail()
                .replace(".", ","))
                .child(postId);
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUid() {
        String path = FirebaseDatabase.getInstance().getReference().push().toString();
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static StorageReference getImagesSRef() {
        return FirebaseStorage.getInstance().getReference(Constants.POST_IMAGES);
    }

    public static DatabaseReference getMyPostRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.MY_POSTS)
                .child(getCurrentUser().getEmail().replace(".", ","));
    }

    public static DatabaseReference getCommentRef(String postId) {
        return FirebaseDatabase.getInstance().getReference(Constants.COMMENTS_KEY)
                .child(postId);
    }

    public static DatabaseReference getMyRecordRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.USER_RECORD)
                .child(getCurrentUser().getEmail().replace(".", ","));
    }

    public static void addToMyRecord(String node,final String id) {
        FirebaseUtils.getMyRecordRef().child(node).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ArrayList<String> myPostCollection;
                if (mutableData.getValue() == null) {
                    myPostCollection = new ArrayList<>(1);
                    myPostCollection.add(id);
                    mutableData.setValue(myPostCollection);
                } else {
                    myPostCollection = (ArrayList<String>) mutableData.getValue();
                    myPostCollection.add(id);
                    mutableData.setValue(myPostCollection);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    //I'll be creating the dynamic link now. Since all of our post has a unique Id in them we
    // want that in our url. so let's start

    public static String generateDeepLink(String uid){
        //We first need to get our firebase deep link prefix
        //The https://firebasetutorial.com/<uid> is what will be recieved in our app when this
        // linked is clicked. We need to add our package name so that android knows which app to
        // open

        return "https://t53y3.app.goo.gl/?link=https://firebasetutorial.com/" + uid +
                "&apn=com.getmore.getmoreapp.firebasepostandchattutorial";
    }

    public static DatabaseReference getSharedRef(String postId){
        return getPostRef().child(postId).child(Constants.NUM_SHARES_KEY);
    }

    public static DatabaseReference getNotificationRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATION_KEY).push();
    }



}
