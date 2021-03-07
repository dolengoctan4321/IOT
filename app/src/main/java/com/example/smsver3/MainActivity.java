package com.example.smsver3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements TextWatcher {

    EditText txt_pNumber, txt_message;//tao bien ham EditText
    String[] arrcontact = null;
    AutoCompleteTextView contactnames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //truy suat toi control bat ky
        txt_message = (EditText) findViewById(R.id.txt_message);
        txt_pNumber = (EditText) findViewById(R.id.txt_phone_number);
        contactnames = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);


        loadContacts();

        contactnames.addTextChangedListener(this);
        contactnames.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,arrcontact));
    }

    private void loadContacts() {
        Cursor cursor = getContacts();
        arrcontact = new String[cursor.getCount()];

        int count = 0;
        while (cursor.moveToNext()){
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            arrcontact[count]=displayName;
            count++;
        }
        cursor.close();
    }

    private Cursor getContacts() {
        final ContentResolver cr = getContentResolver();
        String[] projection = {ContactsContract.Contacts.DISPLAY_NAME,ContactsContract.Contacts._ID};
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + "= ?";
        String[] selectionArgs = {"1"};
        final Cursor contacts = cr.query(ContactsContract.Contacts.CONTENT_URI,projection,selection,selectionArgs,"UPPER("+ContactsContract.Contacts.DISPLAY_NAME+") ASC");
        return contacts;
    }

    public void btn_send(View view) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);//Cho phep app duoc quyen send SMS

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            MyMessage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
    }

    private void MyMessage() {
        String phoneNumber = txt_pNumber.getText().toString().trim();
        String Message = txt_message.getText().toString().trim();

        if (!txt_pNumber.getText().toString().equals("") || !txt_message.getText().toString().equals("")) { //Tao dieu kien kiem tra tin nhan hoac sdt duoc nhap vao hay khong
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, Message, null, null);

            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter number or message please", Toast.LENGTH_SHORT).show();
        }
        txt_message.getText().clear();
        txt_pNumber.getText().clear();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                MyMessage();//neu co quyen se hien thi tin nhan

            } else {
                Toast.makeText(this, "You don't have required permission to implement this action", Toast.LENGTH_SHORT).show();
            }//neu khong co quyen se nhan dong thong bao ko duoc thuc thi lenh nay
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}