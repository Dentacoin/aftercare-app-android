package com.dentacoin.dentacare.adapters.viewholders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import org.zakariya.stickyheaders.SectioningAdapter;

/**
 * Created by Atanas Chervarov on 14.05.18.
 */
public class DCFriendViewHolder extends SectioningAdapter.ItemViewHolder {

    public interface IDCFriendListener {
        void onFriendClicked(DCFriend friend);
        void onAccept(DCFriend friend);
        void onDecline(DCFriend friend);
    }

    private final CardView cvFriend;
    private final SimpleDraweeView sdvAvatar;
    private final DCTextView tvName;
    private final DCTextView tvLastActivity;
    private final DCButton btnDecline;
    private final DCButton btnAccept;
    private final Drawable childDrawable;
    private final Drawable accountDrawable;
    private final IDCFriendListener listener;

    public DCFriendViewHolder(View view, IDCFriendListener listener) {
        super(view);
        cvFriend = view.findViewById(R.id.cv_friend);
        sdvAvatar = view.findViewById(R.id.sdv_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvLastActivity = view.findViewById(R.id.tv_last_activity);
        btnDecline = view.findViewById(R.id.btn_decline);
        btnAccept = view.findViewById(R.id.btn_accept);
        final Context context = sdvAvatar.getContext();
        childDrawable = context.getResources().getDrawable(R.drawable.baseline_face_black_48);
        accountDrawable = context.getResources().getDrawable(R.drawable.baseline_account_circle_black_48);
        this.listener = listener;
    }

    public void setupView(DCFriend friend) {
        btnAccept.setVisibility(friend.needsAccept() ? View.VISIBLE : View.GONE);
        btnDecline.setVisibility(friend.needsAccept() ? View.VISIBLE : View.GONE);
        sdvAvatar.setImageURI(friend.getAvatarUrl(sdvAvatar.getContext()));
        sdvAvatar.getHierarchy().setPlaceholderImage(friend.isChild() ? childDrawable : accountDrawable);
        tvName.setText(friend.getFullName());

        tvLastActivity.setText(R.string.txt_no_activity_yet);
        if (friend.getLastActivity() != null) {
            tvLastActivity.setText(DCConstants.DATE_FORMAT_LAST_ACTIVITY.format(friend.getLastActivity()));
        }

        cvFriend.setOnClickListener(v -> {
            if (listener != null)
                listener.onFriendClicked(friend);
        });

        btnAccept.setOnClickListener(v -> {
            if (listener != null)
                listener.onAccept(friend);
        });

        btnDecline.setOnClickListener(v -> {
            if (listener != null)
                listener.onDecline(friend);
        });
    }
}
