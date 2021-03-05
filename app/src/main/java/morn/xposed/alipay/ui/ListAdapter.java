package morn.xposed.alipay.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import morn.xposed.alipay.R;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private static ListAdapter instance;
    Context context;
    List<? extends AlipayId> list;
    List<String> selects;
    int findIndex = -1;
    CharSequence findWord = null;

    private ListAdapter(Context context) {
        this.context = context;
    }

    public static ListAdapter getInstance(Context context) {
        if (instance == null || instance.context != context) {
            instance = new ListAdapter(context);
        }
        return instance;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int p1) {
        return list.get(p1);
    }

    @Override
    public long getItemId(int p1) {
        return p1;
    }

    @Override
    public View getView(int p1, View p2, ViewGroup p3) {
        ViewHolder vh;
        if (p2 == null) {
            vh = new ViewHolder();
            p2 = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            vh.tv = ((TextView) p3.findViewById(R.id.tv_idn));
            vh.cb = ((CheckBox) p3.findViewById(R.id.cb_list));
            p3.setTag(vh);
        } else {
            vh = (ViewHolder) p2.getTag();
        }
        AlipayId ai = (AlipayId) list.get(p1);
        vh.tv.setText(Html.fromHtml((p1 + 1) + ". " + ai.name));
        vh.tv.setTextColor(findIndex == p1 ? Color.RED : Color.BLACK);
        vh.cb.setChecked(selects != null && selects.contains(ai.id));
        if (list.get(p1) instanceof AliFriend) {
            vh.cb.setVisibility(View.GONE);
        } else {
            vh.cb.setVisibility(View.VISIBLE);
        }
        return p2;
    }

    public int findLast(CharSequence cs) {
        if (list == null || list.size() == 0) return -1;
        if (!cs.equals(findWord)) {
            findIndex = -1;
            findWord = cs;
        }
        int i = findIndex;
        if (i < 0) i = list.size();
        for (; ; ) {
            i = (i + list.size() - 1) % list.size();
            AlipayId ai = (AlipayId) list.get(i);
            if (ai.name.contains(cs)) {
                findIndex = i;
                break;
            }
            if (findIndex < 0 && i == 0)
                break;
        }
        notifyDataSetChanged();
        return findIndex;
    }

    public void exitFind() {
        this.findIndex = -1;
    }

    public void setBaseList(List<? extends AlipayId> l) {
        if (l != list) exitFind();
        list = l;
    }

    public int findNext(CharSequence cs) {
        if (list == null || list.size() == 0) return -1;
        if (!cs.equals(findWord)) {
            findIndex = -1;
            findWord = cs;
        }
        for (int i = findIndex; ; ) {
            i = (i + 1) % list.size();
            AlipayId ai = (AlipayId) list.get(i);
            if (ai.name.contains(cs)) {
                findIndex = i;
                break;
            }
            if (findIndex < 0 && i == list.size() - 1)
                break;
        }
        notifyDataSetChanged();
        return findIndex;
    }

    public void setSelectedList(List<String> l) {
        this.selects = l;
    }

    static class ViewHolder {
        TextView tv;
        CheckBox cb;
    }
}