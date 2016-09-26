package il.ac.huji.ins;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nimoshe on 9/13/2016.
 */
public class PinpointActivity extends Activity {

    private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
    private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
    private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
    private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;

    public static int PINPOINT_ACTIVITY = 2;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // holds instance of the wifi helper
    private WifiHelper _wifiHelper;

    private List<ScanResult> _scanResult;

    private Context _context;

    private boolean hasPicture = false;

    private String _encodedPic;

    private class PinPointClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            pinpointLocation();
        }
    }

    /**
     * Loads all the activity UI (3 imaages...)
     */
    private void loadUI() {
        final Button pinpointBtn = (Button) findViewById(R.id.done_btn_pin);
        pinpointBtn.setOnClickListener(new PinPointClick());

        final ImageView pic = (ImageView) findViewById(R.id.picture_img_pin);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinpoint_layout);

        _context = this;

        loadUI();
        Firebase.setAndroidContext(this);
    }

    private boolean checkRequiredFields(final String place, final String desc) {
        return place.isEmpty() || desc.isEmpty();
    }

    private void pinpointLocation() {
        EditText name = (EditText) findViewById(R.id.place_txt_pin);
        String placeName = name.getText().toString();

        EditText desc = (EditText) findViewById(R.id.desc_txt_pin);
        String placeDesc = desc.getText().toString();

        if (checkRequiredFields(placeName, placeDesc)) {
            Utils.showINSDialog(_context, null, R.string.error_dialog_pinpoint, null,
                    null, null);
            return;
        }

        _wifiHelper = WifiHelper.getInstance();
        _scanResult = _wifiHelper.getScanResults();

        Firebase location = new Firebase(FirebaseConstants.FIREBASE_LOACATIONS_TABELE);
        Location newLocation = new Location(placeName, placeDesc, _scanResult);

        if (hasPicture)
            newLocation.setPicture(_encodedPic);

        location.child(newLocation.getId()).setValue(newLocation);

        for (ScanResult sr : _scanResult) {
            Firebase bssid = new Firebase(FirebaseConstants.FIREBASE_BSSID_TABLE + sr.BSSID);
            bssid.child(newLocation.getId()).setValue(sr.level);
        }

        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            hasPicture = true;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap rotateBitmap = imageBitmap;


            try {
                // rotate if needned
                File temp = File.createTempFile("temp", ".jpg", _context.getCacheDir());
                FileOutputStream fw = new FileOutputStream(temp);

                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fw);
                fw.close();

                ExifInterface exif = new ExifInterface(temp.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotateBitmap = Utils.rotateImage(imageBitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotateBitmap = Utils.rotateImage(imageBitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotateBitmap = Utils.rotateImage(imageBitmap, 270);
                        break;
                    case ORIENTATION_PORTRAIT_INVERTED:
                        rotateBitmap = Utils.rotateImage(imageBitmap, 270);
                        break;
                    case ORIENTATION_LANDSCAPE_INVERTED:
                        rotateBitmap = Utils.rotateImage(imageBitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotateBitmap = Utils.rotateImage(imageBitmap, 90);
                        break;
                }
            } catch (IOException e) {
                Log.e("ROTATE", "Failed to rotate!");
                rotateBitmap = imageBitmap;
            }

            ImageView show = (ImageView) findViewById(R.id.show_pic_pin);
            show.setImageBitmap(rotateBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            rotateBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] temp = stream.toByteArray();
            _encodedPic = Base64.encodeToString(temp, Base64.DEFAULT);
        }
    }

}
