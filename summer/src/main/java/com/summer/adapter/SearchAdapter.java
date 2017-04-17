package com.summer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.summer.R;
import com.summer.location.AddressBean;

import java.util.List;

/**
 * Created by bestotem on 2017/4/17.
 */

public class SearchAdapter extends BaseAdapter {

    private List<AddressBean> data;
    private LayoutInflater mInflater;

    public SearchAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<AddressBean> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return (data == null) ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.address_item, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_item_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_item_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_title.setText(data.get(position).getTitle());
        holder.tv_content.setText(data.get(position).getText());
        return convertView;
    }


    private class ViewHolder {
        public TextView tv_title;
        public TextView tv_content;
    }
}
