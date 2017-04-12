package com.summer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.summer.R;
import com.summer.publish.PointDetail;

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
    public void onBindViewHolder(LinePointHolder holder,final int position) {
        holder.point_Title.setText((CharSequence) mList.get(position).get("pub_title"));
        holder.point_Content.setText((CharSequence)mList.get(position).get("pub_content"));
        holder.point_Location.setText((CharSequence)mList.get(position).get("pub_location"));
        holder.point_Time.setText((CharSequence)mList.get(position).get("pub_time"));

        holder.point_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PointDetail.class);
                intent.putExtra("summerObjectId",mList.get(position).getObjectId());
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class LinePointHolder extends RecyclerView.ViewHolder{
        private TextView point_Location;
        private TextView point_Content;
        private TextView point_Title;
        private TextView point_Time;
        private CardView point_Item;

        public LinePointHolder(View itemView) {
            super(itemView);

            point_Location = (TextView) itemView.findViewById(R.id.tv_item_location);
            point_Content = (TextView) itemView.findViewById(R.id.tv_item_line_content);
            point_Title = (TextView) itemView.findViewById(R.id.tv_item_line_title);
            point_Time = (TextView) itemView.findViewById(R.id.tv_item_line_time);
            point_Item = (CardView) itemView.findViewById(R.id.card_point_main);

        }
    }
}
