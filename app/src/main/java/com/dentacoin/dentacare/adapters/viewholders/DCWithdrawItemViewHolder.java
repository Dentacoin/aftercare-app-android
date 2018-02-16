package com.dentacoin.dentacare.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCTransaction;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 16.02.18.
 */

public class DCWithdrawItemViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private final DCTextView tvAmount;
    private final DCTextView tvStatus;
    private final DCTextView tvWallet;
    private final DCTextView tvDate;

    public DCWithdrawItemViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        tvAmount = view.findViewById(R.id.tv_amount);
        tvStatus = view.findViewById(R.id.tv_status);
        tvWallet = view.findViewById(R.id.tv_wallet);
        tvDate = view.findViewById(R.id.tv_date);
    }

    public void setup(DCTransaction transaction) {
        if (transaction != null) {
            tvAmount.setText(context.getString(R.string.txt_dcn, transaction.getAmount()));
            tvWallet.setText(transaction.getWallet());

            tvDate.setVisibility(View.GONE);
            if (transaction.getDate() != null) {
                tvDate.setText(DCConstants.DATE_FORMAT.format(transaction.getDate()));
                tvDate.setVisibility(View.VISIBLE);
            }

            switch (transaction.getStatus()) {
                case APPROVED:
                    tvStatus.setText(R.string.withdraws_status_approved);
                    tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                    break;
                case DECLINED:
                    tvStatus.setText(R.string.withdraws_status_declined);
                    tvStatus.setTextColor(context.getResources().getColor(R.color.pinkishRed));
                    break;
                default:
                    tvStatus.setText(R.string.withdraws_status_pending);
                    tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
                    break;
            }
        }
    }
}