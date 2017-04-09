package com.summer.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.summer.R;

import java.util.List;

/**
 * Created by bestotem on 2017/4/9.
 */

public class LinePointAdapter extends
        RecyclerView.Adapter<LinePointAdapter.LinePointHolder>{

    private Context mContext;
    private List<AVObject> mList;

    public LinePointAdapter(List<AVObject> list,Context context){
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public LinePointHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_line_point,parent,false);

        LinePointHolder holder = new LinePointHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LinePointHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class LinePointHolder extends RecyclerView.ViewHolder{
        private TextView point_Location;
        private TextView point_Name;
        private TextView point_Content;
        private ImageView point_Image;
        private CardView point_Item;

        public LinePointHolder(View itemView) {
            super(itemView);
        }
    }
}
