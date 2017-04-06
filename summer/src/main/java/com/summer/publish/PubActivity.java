package com.summer.publish;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.summer.R;

/**
 * Created by bestotem on 2017/3/25.
 */

public class PubActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "PubActivity";

    private static final int TAKE_PHOTO_REQUEST_THREE = 555;

    private Uri imageUri;
    private ContentResolver resolver = null;
    private byte[] mImageBytes = null;

    private Toolbar toolbar;
    private ImageView ivPubSend;
    private TextView tvLength;
    private EditText edtTitle;
    private EditText edtContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub);
    }

    @Override
    public void onClick(View v) {

    }
}
