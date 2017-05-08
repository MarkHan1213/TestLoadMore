package com.loadmore.mark.testloadmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loadmore.mark.testloadmore.bean.Results;

import java.util.List;

/**
 * Created by Mark.Han on 2017/5/8.
 */
public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private List<Results> mResultses;
    private LayoutInflater mInflater;

    public MyAdapter(Context context, List<Results> resultses) {
        this.mContext = context;
        this.mResultses = resultses;
        mInflater = LayoutInflater.from(context);
    }

    public void clear() {
        mResultses.clear();
    }

    public void addAll(List<Results> resultses) {
        mResultses.addAll(resultses);
    }

    @Override
    public int getCount() {
        return mResultses.size();
    }

    @Override
    public Results getItem(int position) {
        return mResultses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
            viewHolder.mTextView = ((TextView) convertView.findViewById(android.R.id.text1));

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextView.setText(getItem(position).getTitle());
        return convertView;
    }

    class ViewHolder {
        TextView mTextView;
    }
}
