package morn.xposed.alipay.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OptionsAdapter extends BaseAdapter {
    private static OptionsAdapter adapter;
    Context context;
    List<String> list;

    private OptionsAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList();
        this.list.add("查看森林");
        this.list.add("查看庄园");
        this.list.add("删除");
    }

    private OptionsAdapter(Context context, List<String> paramList) {
        this.context = context;
        this.list = paramList;
    }

    public static OptionsAdapter get(Context context) {
        if (adapter == null || adapter.context != context) {
            adapter = new OptionsAdapter(context);
        }
        return adapter;
    }

    public static OptionsAdapter newInstant(Context context, List<String> list) {
        return new OptionsAdapter(context, list);
    }

    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public Object getItem(int index) {
        return this.list.get(index);
    }

    public long getItemId(int id) {
        return id;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(android.R.layout.simple_list_item_1, null);
        }
        ((TextView) convertView).setText(getItem(position).toString());
        return convertView;
    }
}