package com.audioapplication.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankurkhandelwal on 17/05/17.
 */

public class Product{
    public String product_name;
    public String product_image;
    public String discount_info;
    public String used_today;
    public String added_time;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getDiscount_info() {
        return discount_info;
    }

    public void setDiscount_info(String discount_info) {
        this.discount_info = discount_info;
    }

    public String getUsed_today() {
        return used_today;
    }

    public void setUsed_today(String used_today) {
        this.used_today = used_today;
    }

    public String getAdded_time() {
        return added_time;
    }

    public void setAdded_time(String added_time) {
        this.added_time = added_time;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String coupon_code;

    public Product(String product_name,String product_image, String discount_info, String used_today, String added_time,String coupon_code){
        this.product_name= product_name;
        this.product_image=product_image;
        this.discount_info=discount_info;
        this.used_today=used_today;
        this.added_time=added_time;
        this.coupon_code=coupon_code;
    }

}
