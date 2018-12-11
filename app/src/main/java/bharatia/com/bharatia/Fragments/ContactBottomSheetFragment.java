package bharatia.com.bharatia.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bharatia.com.bharatia.PostDetailsActivity;
import bharatia.com.bharatia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactBottomSheetFragment extends BottomSheetDialogFragment {


    private final int MY_PERMISSIONS_REQUEST_CALL = 32;
    private final int MY_PERMISSIONS_REQUEST_MESSAGE = 30;

    TextView phoneText,messageText, emailText;

    RelativeLayout call, message, email;

    String phone,emailAddress;


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(),R.layout.fragment_contact_bottom_sheet,null);
        dialog.setContentView(contentView);
    }

    public ContactBottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_bottom_sheet, container, false);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            phone = bundle.getString("phone");
            emailAddress = bundle.getString("email");
        }

        phoneText = (TextView) v.findViewById(R.id.bottomSheetCallMainText);
        messageText = (TextView) v.findViewById(R.id.bottomSheetMessageMainText);
        emailText = (TextView) v.findViewById(R.id.bottomSheetEmailMainText);

        call = (RelativeLayout) v.findViewById(R.id.postDetailsCall);
        message = (RelativeLayout) v.findViewById(R.id.postDetailsMesage);
        email = (RelativeLayout) v.findViewById(R.id.postDetailsEmail);



        phoneText.setText(phone);
        messageText.setText(phone);
        emailText.setText(emailAddress);



        call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

                int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE);

                if (permissionCheck== PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);

                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }else{
                    //getCallPermission();
                }

            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
//                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
//                smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                smsIntent.setType("vnd.android-dir/mms-sms");
//                smsIntent.putExtra("address",phone );
//                //smsIntent.putExtra("sms_body","Body of Message");
//                startActivity(smsIntent);
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.SEND_SMS);

                if (permissionCheck==PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));
                }else {
                    //getMessagePermission();
                }
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ emailAddress });
//                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Client from Bharatia.");
//                startActivity(Intent.createChooser(i, "Send email"));
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: "+emailAddress));
                startActivity(Intent.createChooser(emailIntent, "Choose email sender"));
            }
        });

        return v;
    }




}
