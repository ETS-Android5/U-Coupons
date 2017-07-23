package com.example.administrator.ccoupons.User;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ccoupons.Main.Coupon;
import com.example.administrator.ccoupons.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/21 0021.
 */

public class UserCouponInfoAdapter extends RecyclerView.Adapter<UserCouponInfoAdapter.UserCouponInfoViewHolder> {

    private Context mContext;
    private ArrayList<Coupon> mUserCouponInfoList;

    public static class UserCouponInfoViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CardView rootView;
        public UserCouponInfoViewHolder(View view) {
            super(view);
            rootView = (CardView)view;
            textView = (TextView) view.findViewById(R.id.city_name_textview);
        }
    }


    public UserCouponInfoAdapter(ArrayList<Coupon> cList) {
        this.mUserCouponInfoList = cList;
    }

    @Override
    public UserCouponInfoAdapter.UserCouponInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.usercouponinfo_item, parent, false);
        final UserCouponInfoAdapter.UserCouponInfoViewHolder holder = new UserCouponInfoAdapter.UserCouponInfoViewHolder(view);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Coupon coupon = mUserCouponInfoList.get(position);
                Toast.makeText(mContext, "UserCouponInfo = " + coupon.getName(), Toast.LENGTH_SHORT).show();
                //Todo:获得当前Coupon编号，跳转到Coupon页面
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(UserCouponInfoAdapter.UserCouponInfoViewHolder holder, int position) {
        Coupon coupon = mUserCouponInfoList.get(position);
        holder.textView.setText(coupon.getName());
        //TODO:有待完善
    }

    @Override
    public int getItemCount() {
        return mUserCouponInfoList.size();
    }
}
