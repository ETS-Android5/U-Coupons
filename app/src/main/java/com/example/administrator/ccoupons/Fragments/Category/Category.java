package com.example.administrator.ccoupons.Fragments.Category;

/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class Category {

    private String name;
    private int resId;


    /**
     *
     * @param cname category name
     * @param cid category id
     */
    public Category(String cname,int cid) {
        this.name = cname;
        this.resId = cid;
    }

    public String getProduct() {
        return this.name;
    }
    public int getResId() {
        return this.resId;
    }
}
