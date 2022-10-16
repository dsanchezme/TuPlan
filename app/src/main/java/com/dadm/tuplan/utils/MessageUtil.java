package com.dadm.tuplan.utils;

import android.content.Context;
import android.widget.Toast;

public class MessageUtil {

    public static void  displayToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
