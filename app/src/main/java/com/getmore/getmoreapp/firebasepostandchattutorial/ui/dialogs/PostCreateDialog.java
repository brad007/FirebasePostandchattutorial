package com.getmore.getmoreapp.firebasepostandchattutorial.ui.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.getmore.getmoreapp.firebasepostandchattutorial.R;
import com.getmore.getmoreapp.firebasepostandchattutorial.models.Post;
import com.getmore.getmoreapp.firebasepostandchattutorial.models.User;
import com.getmore.getmoreapp.firebasepostandchattutorial.utils.Constants;
import com.getmore.getmoreapp.firebasepostandchattutorial.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

/**
 * Created by brad on 2017/02/03.
 */

public class PostCreateDialog extends DialogFragment implements View.OnClickListener {
    private static final int RC_PHOTO_PICKER = 1;
    private View mRootView;
    private Post mPost;

    private ProgressDialog mProgressDialog;
    private Uri mSelectedImageUri;
    private ImageView mPostDisplay;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mPost = new Post();
        mProgressDialog = new ProgressDialog(getContext());

        mRootView = getActivity().getLayoutInflater().inflate(R.layout.post_create_dialog, null);
        mPostDisplay = (ImageView) mRootView.findViewById(R.id.post_dialog_display);
        mRootView.findViewById(R.id.post_dialog_select_imageview).setOnClickListener(this);
        mRootView.findViewById(R.id.post_dialog_send_imageview).setOnClickListener(this);
        builder.setView(mRootView);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_dialog_send_imageview:
                sendPost();
                break;
            case R.id.post_dialog_select_imageview:
                selectImage();
                break;
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    private void sendPost() {
        mProgressDialog.setMessage("Sending post...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        final String postUid = FirebaseUtils.getUid();
                        TextView postDialogTextView = (TextView) mRootView.findViewById(R.id.post_dialog_edittext);
                        String text = postDialogTextView.getText().toString();

                        mPost.setUser(user);
                        mPost.setNumComments(0);
                        mPost.setNumLikes(0);
                        mPost.setTimeCreated(System.currentTimeMillis());
                        mPost.setPostId(postUid);
                        mPost.setPostText(text);

                        //Now we need to add the deep link value to our post model when we create
                        // a new post

                        mPost.setDeeplink(FirebaseUtils.generateDeepLink(postUid));
                        mPost.setNumShares(0);

                        if (mSelectedImageUri != null) {
                            FirebaseUtils.getImagesSRef()
                                    .child(mSelectedImageUri.getLastPathSegment())
                                    .putFile(mSelectedImageUri)
                                    .addOnSuccessListener(getActivity(),
                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    String url = Constants.POST_IMAGES + "/" + mSelectedImageUri.getLastPathSegment();
                                                    //String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                                    mPost.setPostImageUrl(url);
                                                    addToMyPostList(postUid);
                                                }


                                            });
                        } else {
                            addToMyPostList(postUid);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void addToMyPostList(final String postUid) {

        //With this set, we'll be alerted to any activity that happens on your post
        FirebaseMessaging.getInstance().subscribeToTopic(postUid);
        FirebaseUtils.getPostRef().child(postUid)
                .setValue(mPost);
        FirebaseUtils.getMyPostRef().child(postUid).setValue(true)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        dismiss();
                    }
                });

        FirebaseUtils.addToMyRecord(Constants.POSTS_KEY, postUid);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER) {
            if (resultCode == RESULT_OK) {
                mSelectedImageUri = data.getData();
                mPostDisplay.setImageURI(mSelectedImageUri);
            }
        }
    }
}
