package bharatia.com.bharatia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChoosePhotoAppDialogActivity extends AppCompatActivity {

    ImageView camera,gallery;

    File file;
    Uri fileUri;
    private int RC_TAKE_PHOTO_CAMERA = 1;
    private int RC_TAKE_PHOTO_GALLERY = 2;

    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 24;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 25;

    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo_app_dialog2);

        camera = findViewById(R.id.chooserDialogCamera);
        gallery = findViewById(R.id.chooserDialogGallery);

        // Get max available VM memory, exceeding this amount will throw an
// OutOfMemory exception. Stored in kilobytes as LruCache takes an
// int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

// Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = ContextCompat.checkSelfPermission(ChoosePhotoAppDialogActivity.this,
                        Manifest.permission.CAMERA);

                if (permissionCheck==PackageManager.PERMISSION_GRANTED) {



                    takePhoto();

                }else{
                    getCameraPermission();
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = ContextCompat.checkSelfPermission(ChoosePhotoAppDialogActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionCheck== PackageManager.PERMISSION_GRANTED) {
                    Intent pictureActionIntent = null;

                    getPhoto();

                }else{
                    getReadStoragePermission();
                }

            }
        });
    }

    private void getPhoto() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RC_TAKE_PHOTO_GALLERY);

    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        fileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RC_TAKE_PHOTO_CAMERA);

    }

    private void getReadStoragePermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

    private void getCameraPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_TAKE_PHOTO_CAMERA && resultCode == RESULT_OK) {

            //do whatever you need with taken photo using file or fileUri
//            Uri selectedImage = data.getData();
            //do whatever you need with taken photo using file or fileUri
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

                File f = getFilefromBitmap(bitmap);

                Log.e("path",f.getPath());

                Intent returnIntent = new Intent();
                returnIntent.putExtra("file", f);
                returnIntent.putExtra("name", fileUri.getLastPathSegment());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ChoosePhotoAppDialogActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }


        }else if (requestCode == RC_TAKE_PHOTO_GALLERY && resultCode == RESULT_OK) {

            Uri selectedImage = data.getData();
            //do whatever you need with taken photo using file or fileUri
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                File f = getFilefromBitmap(bitmap);

                Log.e("path",f.getPath());

                File imgFile = new File(getRealPathFromURI(selectedImage));

                Intent returnIntent = new Intent();
                returnIntent.putExtra("file", imgFile);
                returnIntent.putExtra("name", selectedImage.getLastPathSegment());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ChoosePhotoAppDialogActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    getPhoto();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    getReadStoragePermission();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    takePhoto();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    getCameraPermission();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    public File getFilefromBitmap(Bitmap photo) {

        //create a file to write bitmap data
        File f = new File(getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");


        try {

            f.createNewFile();

//Convert bitmap to byte array
            Bitmap bitmap = photo;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        }catch (Exception e) {

            Toast.makeText(ChoosePhotoAppDialogActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

        }

        return f;
    }
}

