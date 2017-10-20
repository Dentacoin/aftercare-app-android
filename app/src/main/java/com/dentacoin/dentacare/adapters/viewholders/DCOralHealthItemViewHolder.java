package com.dentacoin.dentacare.adapters.viewholders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCOralHealthItem;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Atanas Chervarov on 10/1/17.
 */

public class DCOralHealthItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface IDCOralHealthItemListener {
        void onOralHealthItemClicked(DCOralHealthItem item);
    }

    private final CardView cvOralHealthItem;
    private final SimpleDraweeView sdvOralHealthItem;
    private final DCTextView tvOralHealthItemTitle;
    private final DCTextView tvOralHealthDescription;
    private IDCOralHealthItemListener listener;

    public DCOralHealthItemViewHolder(View view) {
        super(view);
        sdvOralHealthItem = (SimpleDraweeView) view.findViewById(R.id.sdv_oral_health_item);
        tvOralHealthItemTitle = (DCTextView) view.findViewById(R.id.tv_oral_health_item_title);
        tvOralHealthDescription = (DCTextView) view.findViewById(R.id.tv_oral_health_description);
        cvOralHealthItem = (CardView) view.findViewById(R.id.cv_oral_health_item);
        cvOralHealthItem.setOnClickListener(this);
    }

    public void setup(DCOralHealthItem item, IDCOralHealthItemListener listener) {
        if (item != null) {
            sdvOralHealthItem.setImageURI(item.getImageURL());
            tvOralHealthItemTitle.setText(item.getTitle());
            tvOralHealthDescription.setText(item.getDescription());
            cvOralHealthItem.setTag(item);
        }
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_oral_health_item:
                if (listener != null) {
                    if (view.getTag() instanceof DCOralHealthItem) {
                        listener.onOralHealthItemClicked((DCOralHealthItem) view.getTag());
                    }
                }
                break;
        }
    }
}
