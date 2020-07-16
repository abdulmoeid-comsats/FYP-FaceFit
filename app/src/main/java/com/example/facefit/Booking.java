package com.example.facefit;

import androidx.annotation.NonNull;

public class Booking {
    public String book_id;
    public String user_email;
    public String frame_name;
    public String order_date;
    public String bill_amount;
    public String frame_count;
    public String status;

    public Booking(){

    }

    public Booking(String book_id,String user_email, String frame_name, String order_date, String bill_amount, String frame_count,String status) {
        this.book_id=book_id;
        this.user_email = user_email;
        this.frame_name = frame_name;
        this.order_date = order_date;
        this.bill_amount = bill_amount;
        this.frame_count = frame_count;
        this.status=status;
    }

}
