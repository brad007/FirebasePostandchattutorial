package com.getmore.getmoreapp.firebasepostandchattutorial.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.getmore.getmoreapp.firebasepostandchattutorial.R;
import com.getmore.getmoreapp.firebasepostandchattutorial.models.Comment;
import com.getmore.getmoreapp.firebasepostandchattutorial.models.Notification;
import com.getmore.getmoreapp.firebasepostandchattutorial.models.Post;
import com.getmore.getmoreapp.firebasepostandchattutorial.models.User;
import com.getmore.getmoreapp.firebasepostandchattutorial.utils.Constants;
import com.getmore.getmoreapp.firebasepostandchattutorial.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String BUNDLE_COMMENT = "mComent";
    private Post mPost;
    private EditText mCommentEditTextView;
    private Comment mComent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if (savedInstanceState != null) {
            mComent = (Comment) savedInstanceState.getSerializable(BUNDLE_COMMENT);
        }

        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra(Constants.EXTRA_POST);

        init();
        initPost(mPost);
        initCommentSection();
    }

    private void init() {
        mCommentEditTextView = (EditText) findViewById(R.id.et_comment);
        findViewById(R.id.iv_send).setOnClickListener(this);
    }

    private void initCommentSection() {
        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));

        FirebaseRecyclerAdapter<Comment, CommentHolder> commentAdapter =
                new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                        Comment.class,
                        R.layout.row_comment,
                        CommentHolder.class,
                        FirebaseUtils.getCommentRef(mPost.getPostId())) {
                    @Override
                    protected void populateViewHolder(CommentHolder viewHolder, Comment model, int position) {
                        viewHolder.setUsername(model.getUser().getUser());
                        viewHolder.setComment(model.getComment());
                        viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                        Glide.with(PostActivity.this)
                                .load(model.getUser().getPhotUrl())
                                .into(viewHolder.commentOwnerDisplay);
                    }
                };

        commentRecyclerView.setAdapter(commentAdapter);
    }

    private void initPost(Post post) {
        ImageView postOwnerDisplayImageView = (ImageView) findViewById(R.id.iv_post_owner_display);
        TextView postOwnerUsernameTextView = (TextView) findViewById(R.id.tv_post_username);
        TextView postTimeCreatedTextView = (TextView) findViewById(R.id.tv_time);
        ImageView postDisplayImageView = (ImageView) findViewById(R.id.iv_post_display);
        LinearLayout postLikeLayout = (LinearLayout) findViewById(R.id.like_layout);
        LinearLayout postCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        TextView postNumLikesTextView = (TextView) findViewById(R.id.tv_likes);
        TextView postNumCommentsTextView = (TextView) findViewById(R.id.tv_comments);
        TextView postTextTextView = (TextView) findViewById(R.id.tv_post_text);

        //Add these to lines here
        TextView postNumSharesTextView = (TextView) findViewById(R.id.tv_shares);
        postNumSharesTextView.setText(String.valueOf((post).getNumShares()));
        //

        postOwnerUsernameTextView.setText(post.getUser().getUser());
        postTimeCreatedTextView.setText(DateUtils.getRelativeTimeSpanString(post.getTimeCreated()));
        postTextTextView.setText(post.getPostText());
        postNumLikesTextView.setText(String.valueOf(post.getNumLikes()));
        postNumCommentsTextView.setText(String.valueOf(post.getNumComments()));

        Glide.with(PostActivity.this)
                .load(post.getUser().getPhotUrl())
                .into(postOwnerDisplayImageView);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(post.getPostImageUrl());

        Glide.with(PostActivity.this)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(postDisplayImageView);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                sendComment();
        }
    }

    private void sendComment() {
        final ProgressDialog progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("Sending comment..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mComent = new Comment();
        final String uid = FirebaseUtils.getUid();
        String strComment = mCommentEditTextView.getText().toString();

        mComent.setCommentId(uid);
        mComent.setComment(strComment);
        mComent.setTimeCreated(System.currentTimeMillis());
        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mComent.setUser(user);
                        FirebaseUtils.getCommentRef(mPost.getPostId())
                                .child(uid)
                                .setValue(mComent);

                        FirebaseUtils.getPostRef().child(mPost.getPostId())
                                .child("numComments")
                                .runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        long num = (long) mutableData.getValue();
                                        mutableData.setValue(num + 1);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                        progressDialog.dismiss();
                                        FirebaseUtils.addToMyRecord(Constants.COMMENTS_KEY,uid);
                                    }
                                });

                        Notification notifs = new Notification();
                        notifs.setImageUrl(user.getPhotUrl());
                        notifs.setEmail(user.getEmail());
                        notifs.setUsername(user.getUser());
                        //The uid and topic are the same. since the topic of this notification is
                        // the postId of this post, any subscribed to this post will recieve this
                        // notification
                        notifs.setUid(uid);
                        notifs.setTopic(uid);

                        notifs.setText("Your post was commented on");

                        FirebaseUtils.getNotificationRef().setValue(notifs);

                        FirebaseMessaging.getInstance().subscribeToTopic(uid);

                        //And that's all folks! Notifications with firebase in a nutshell!
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;

        public CommentHolder(View itemView) {
            super(itemView);
            commentOwnerDisplay = (ImageView) itemView.findViewById(R.id.iv_comment_owner_display);
            usernameTextView = (TextView) itemView.findViewById(R.id.tv_username);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            commentTextView = (TextView) itemView.findViewById(R.id.tv_comment);
        }

        public void setUsername(String username) {
            usernameTextView.setText(username);
        }

        public void setTime(CharSequence time) {
            timeTextView.setText(time);
        }

        public void setComment(String comment) {
            commentTextView.setText(comment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_COMMENT, mComent);
        super.onSaveInstanceState(outState);
    }
}
