package com.vuiya.implicitintent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity {

    private static final int CALL_REQUEST_CODE = 999;
    private static final int STROAGE_REQUEST_CODE = 112;
    static final int REQUEST_TAKE_PHOTO = 1;
    private final String phoneNumber="01924250146";
    private final String address ="Rupnagar Abasik,Mirpur 2";

    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void dialnumber(View view) {

        Uri phnUri =Uri.parse("tel:"+phoneNumber);
        Intent dialIntent = new Intent(Intent.ACTION_DIAL,phnUri);

        if (dialIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(dialIntent);
        }
        else
        {
            Toast.makeText(this, "No Component Found!", Toast.LENGTH_SHORT).show();
        }


    }

    public void CallNumber(View view) {

        dispatchcallIntent();


    }

    public void ShowMap(View view) {

        Uri addressUri = Uri.parse("geo:0,0?q="+address);

        Intent addressIntent = new Intent(Intent.ACTION_VIEW,addressUri);

        if (addressIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(addressIntent);
        }
        else
        {
            Toast.makeText(this, "No Component Found!", Toast.LENGTH_SHORT).show();
        }


    }


    public void TakePhoto(View view) {

        if (isStorageRequestAccepted())
        {
            dispatchCameraIntent();
        }



    }

    private void dispatchCameraIntent() {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.vuiya.implicitintent",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK )
        {
            ImageView imageView = findViewById(R.id.myImage);
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void dispatchcallIntent()
    {
        Uri phnUri =Uri.parse("tel:"+phoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL,phnUri);

        if (callIntent.resolveActivity(getPackageManager()) != null)
        {
            if (IsCallRequestedAcceped())
            {
                startActivity(callIntent);
            }
        }
        else
        {
            Toast.makeText(this, "No Component Found!", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean IsCallRequestedAcceped()
    {
        String[] premissionList = {Manifest.permission.CALL_PHONE};

        if (checkSelfPermission(Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(premissionList,CALL_REQUEST_CODE);

            return false;
        }

        return true;
    }

    private boolean isStorageRequestAccepted()
    {
        String[] premissionList = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(premissionList,STROAGE_REQUEST_CODE);

            return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case CALL_REQUEST_CODE:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    dispatchcallIntent();
                }
                else
                {
                    Toast.makeText(this, R.string.call_text, Toast.LENGTH_SHORT).show();
                }

            case  STROAGE_REQUEST_CODE:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    dispatchCameraIntent();
                }
                else
                {
                    Toast.makeText(this, R.string.call_text, Toast.LENGTH_SHORT).show();
                }

        }
    }

    public void SendSms(View view) {


        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"+phoneNumber));  // This ensures only SMS apps respond
        //intent.putExtra("sms_body", "Hello how are you");
       // intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    
    
    

}
