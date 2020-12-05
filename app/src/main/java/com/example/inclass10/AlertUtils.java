package com.example.inclass10;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class AlertUtils
{
    public static void showOKDialog(Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

}
