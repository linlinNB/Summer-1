package com.summer.publish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.summer.R;
import com.summer.constant.SP_Constant;
import com.summer.utils.SharedPreferencesUtil;
import com.summer.utils.ShowToast;
import com.summer.utils.TakePhotoUtils;

/**
 * Created by bestotem on 2017/3/25.
 */

public class PubActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PubActivity";

    private static final int TAKE_PHOTO_REQUEST_THREE = 555;

    private Uri imageUri;
    private byte[] mImageBytes = null;

    private Toolbar toolbar;
    private RelativeLayout rlt_camera;
    private RelativeLayout rlt_line_add;

    private EditText edtTitle;
    private EditText edtContent;
    private ImageView iv_pub_add;

    private Context context = PubActivity.this;
    SharedPreferencesUtil sp;

    private TextView tv_Long;
    private TextView tv_Lat;
    private TextView tv_location;

    public static BitmapFactory.Options getOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 4;      //此项参数可以根据需求进行计算
        options.inJustDecodeBounds = false;
        return options;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub);
        sp = new SharedPreferencesUtil(context, SP_Constant.SP_NAME);

        initView();
        addListener();
        initEvent();
        initBar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("");
        Log.e(TAG, "-- onResume --");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "-- onDestroy --");
        this.finish();
    }

    private void initBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(PubActivity.this.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(edtTitle.getWindowToken(), 0);
            }
        });
    }

    private void initView() {
        rlt_camera = (RelativeLayout) findViewById(R.id.rlt_camera);
        rlt_line_add = (RelativeLayout) findViewById(R.id.rlt_line_add);

        edtTitle = (EditText) findViewById(R.id.edt_pub_title);
        edtContent = (EditText) findViewById(R.id.edt_pub_content);
        iv_pub_add = (ImageView) findViewById(R.id.iv_pub_add);

        tv_Long = (TextView) findViewById(R.id.tv_show_Long);
        tv_Lat = (TextView) findViewById(R.id.tv_show_Lat);
        tv_location = (TextView) findViewById(R.id.tv_location);

    }

    private void addListener() {
        rlt_camera.setOnClickListener(this);
        rlt_line_add.setOnClickListener(this);

    }

    private void initEvent() {
        edtTitle.setFocusable(true);
        edtTitle.setFocusableInTouchMode(true);
        edtTitle.requestFocus();
        edtTitle.requestFocusFromTouch();

        String mapLong = sp.getStringByKey("MapLong");
        String mapLat = sp.getStringByKey("MapLat");
        String mapLocation = sp.getStringByKey("MapLoc");

        tv_Long.setText(mapLong);
        tv_Lat.setText(mapLat);
        tv_location.setText(mapLocation);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlt_camera:
                showOptionDialog();
                break;

            case R.id.rlt_line_add:
                String pubTitle = edtTitle.getText().toString().trim();
                String pubContent = edtContent.getText().toString().trim();
                String pubLocation = sp.getStringByKey("MapLoc");
                sendPublish(pubTitle, pubContent,pubLocation);
                break;

            default:
                break;
        }
    }


    private void sendPublish(final String pubTitle, final String pubContent,final String pubLocation) {
        if ("".equals(pubTitle)) {
            ShowToast.ColorToast(PubActivity.this, "= Waring = No Title", 1200);
            return;
        }

        if ("".equals(pubContent)) {
            ShowToast.ColorToast(PubActivity.this, "= Waring = No Content", 1200);
            return;
        }

        // TODO add a loading View Start

        AVObject summer_pub = new AVObject("Summer_Pub");
        summer_pub.put("pub_title", pubTitle);
        summer_pub.put("pub_content", pubContent);
        summer_pub.put("pub_location",pubLocation);
        summer_pub.put("owner", AVUser.getCurrentUser());
        summer_pub.put("image", new AVFile("pub_image", mImageBytes));

        summer_pub.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.e(TAG, "== Save Pub Success ==" + e);

                    // TODO add a loading View finish

                    ShowToast.ColorToast(PubActivity.this, "Save Pub Success", 1200);

                    // TODO jump to new Activity
                    Intent intent = new Intent(PubActivity.this,LinePointActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                    // TODO add a loading View finish
                    Log.e(TAG, "== Save Pub Failed ==" + e);
                }
            }
        });
    }

    private void showOptionDialog() {
        String[] items = new String[]{"拍照", "选择本地图片"};

        DialogInterface.OnClickListener click =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0://拍照
                                try {
                                    imageUri = TakePhotoUtils.takePhoto(
                                            PubActivity.this,
                                            TAKE_PHOTO_REQUEST_THREE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                break;

                            case 1://选择本地图片
                                pickImageFromAlbum();
                                break;
                        }
                    }
                };

        new AlertDialog.Builder(this).setItems(items, click).show();
    }

    public void pickImageFromAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 222);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 222:
                if (resultCode == RESULT_CANCELED) {
                    ShowToast.ColorToast(PubActivity.this, "点击取消从相册选择", 1500);
                    return;
                }

                try {
                    Uri imageUri = data.getData();
                    Log.e("TAG", imageUri.toString());
                    iv_pub_add.setImageURI(imageUri);

                    mImageBytes = getBytes(getContentResolver()
                            .openInputStream(data.getData()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case TAKE_PHOTO_REQUEST_THREE:
                if (resultCode == RESULT_CANCELED) {
                    ShowToast.ColorToast(PubActivity.this, "点击取消从相册选择", 1500);
                    return;
                }

                Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),
                        getOptions(imageUri.getPath()));
                iv_pub_add.setImageBitmap(bitmap);

                mImageBytes = Bitmap2Bytes(bitmap);
                break;

            default:
                break;
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;

        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public static byte[] Bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


}
