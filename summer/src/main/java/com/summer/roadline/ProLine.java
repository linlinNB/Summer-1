package com.summer.roadline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.summer.R;
import com.summer.adapter.SearchAdapter;
import com.summer.location.InputTask;

/**
 * Created by bestotem on 2017/3/25.
 */

public class ProLine extends AppCompatActivity implements
        TextWatcher,AdapterView.OnItemClickListener{

    private static final String TAG = "ProLine";

    private AutoCompleteTextView aot_loc;
    private ListView lv_loc;
    private SearchAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_line);
        initView();
    }

    private void initView() {
        aot_loc = (AutoCompleteTextView) findViewById(R.id.aot_location);
        lv_loc = (ListView) findViewById(R.id.lv_search);
        aot_loc.addTextChangedListener(this);
        mAdapter = new SearchAdapter(this);
        lv_loc.setAdapter(mAdapter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        InputTask.getInstance(this, mAdapter).onSearch(s.toString(), "");
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
