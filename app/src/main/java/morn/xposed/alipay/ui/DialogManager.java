package morn.xposed.alipay.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import morn.xposed.alipay.AntBroadcastReceiver;
import morn.xposed.alipay.R;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.CooperationIdMap;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;

public class DialogManager {
    static ListView friendList;
    static List<String> selected;
    static List<Integer> datas;
    static ListAdapter.ViewHolder holder;
    static AlipayId user;
    static CharSequence title;

    public static void showAboutDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_about, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_about);
        textView.setText(R.string.update_history);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        new Builder(context).setTitle("关于").setView(view).setPositiveButton("关闭", null).create().show();
    }

    private static void showRankDialog(Context context, CharSequence title) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
        initList(view);
        initSearch(view);
        initSorts(view);

        AlertDialog dialog = new Builder(context)
                .setTitle("title")
                .setView(view)
                .setPositiveButton("关闭", null)
                .create();
        if (title != null) {
            dialog.setTitle(title);
        }
        dialog.show();
    }

    public static void showWXDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_wx, null);
        new Builder(context)
                .setTitle("支持秋风")
                .setView(view)
                .setPositiveButton("关闭", null)
                .setNegativeButton("保存", new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BitmapDrawable drawable = (BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.wx, null);
                        File pictDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
                        if (!pictDir.exists()) {
                            pictDir.mkdir();
                        }
                        File icon = new File(pictDir, "qiufeng.png");
                        try {
                            FileOutputStream out = new FileOutputStream(icon);
                            drawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.close();
                        } catch (Exception e) {
                            Log.printStackTrace(XposedHook.TAG, e);
                        }
                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(icon));
                        context.sendBroadcast(intent);
                        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }

    public static void selectFriends(Context context, CharSequence titleText, List<? extends AlipayId> friendList, List<String> selectedList, List<Integer> dataList) {
        title = titleText;
        selected = selectedList;
        datas = dataList;
        ListAdapter adapter = ListAdapter.getInstance(context);
        if (friendList != null && selectedList != null) {
            HashSet<String> set = new HashSet<>(selectedList);
            ArrayList<AlipayId> filteredList = new ArrayList<>();
            ArrayList<AlipayId> otherList = new ArrayList<>();
            for (AlipayId alipayId : friendList) {
                if (set.contains(alipayId.id)) {
                    filteredList.add(alipayId);
                } else {
                    otherList.add(alipayId);
                }
            }
            filteredList.sort(new Comparator<AlipayId>() {
                @Override
                public int compare(AlipayId o1, AlipayId o2) {
                    return selectedList.indexOf(o1.id) - selectedList.indexOf(o2.id);
                }
            });
            filteredList.addAll(otherList);
            adapter.setBaseList(filteredList);
        } else {
            adapter.setBaseList(friendList);
        }
        adapter.setSelectedList(selected);
        showRankDialog(context, titleText);
    }

    private static void initList(View v) {
        friendList = (ListView) v.findViewById(R.id.lv_list);
        friendList.setAdapter(ListAdapter.getInstance(v.getContext()));
        friendList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int paramInt, long paramLong) {
                holder = (ListAdapter.ViewHolder) view.getTag();
                user = (AlipayId) adapterView.getAdapter().getItem(paramInt);
                if (datas == null) {
                    if (holder.cb.isChecked()) {
                        if (selected.contains(user.id)) {
                            selected.remove(user.id);
                        }
                        holder.cb.setChecked(false);
                    } else {
                        if (!selected.contains(user.id)) {
                            selected.add(user.id);
                        }
                        holder.cb.setChecked(true);
                    }
                    Config.hasChanged = true;
                } else {
                    showEditDialog(adapterView.getContext());
                }
            }
        });
        friendList.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int paramInt, long paramLong) {
                user = (AlipayId) adapterView.getAdapter().getItem(paramInt);
                if (user instanceof AlipayCooperate) {
                    showDeleteDialog(adapterView.getContext());
                } else {
                    showOptionDialog(adapterView.getContext());
                }
                return true;
            }
        });
    }

    private static void initSorts(View v) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String operation = null;
                Comparator<ForestUser> comparator = null;
                switch (v.getId()) {
                    case R.id.btn_week_collect:
                        operation = "按周收排序";
                        comparator = ForestUser.getWeekCollectComparator();
                        break;
                    case R.id.btn_week:
                        operation = "按周排序";
                        comparator = ForestUser.getWeekComparator();
                        break;
                    case R.id.btn_sum_collect:
                        operation = "按总收排序";
                        comparator = ForestUser.getSumCollectComparator();
                        break;
                    case R.id.btn_sum:
                        operation = "按总排序";
                        comparator = ForestUser.getSumComparator();
                        break;
                    case R.id.btn_avg_collect:
                        operation = "按日收排序";
                        comparator = ForestUser.getAvgCollectComparator();
                        break;
                }
                if (operation != null) {
                    sortOperation(view.getContext(), operation, comparator);
                }
            }
        };
        int[] buttons = new int[]{R.id.btn_week_collect, R.id.btn_sum_collect,
                R.id.btn_avg_collect, R.id.btn_week, R.id.btn_sum};
        int visible = View.GONE;
        if (title != null && title.toString().contains("好友分析")) {
            visible = View.VISIBLE;
        }
        for (int id : buttons) {
            Button button = (Button) v.findViewById(id);
            button.setOnClickListener(listener);
            button.setVisibility(visible);
        }
    }

    private static void sortOperation(Context context, String operation, Comparator<ForestUser> comparator) {
        ListAdapter adapter = ListAdapter.getInstance(context);
        Log.recordLog(operation);
        ((List<ForestUser>) adapter.list).sort(comparator);
        adapter.notifyDataSetChanged();
        friendList.setSelection(0);
        Toast.makeText(context, operation, Toast.LENGTH_SHORT).show();
    }

    private static void initSearch(View v) {
        EditText search = (EditText) v.findViewById(R.id.edt_find);
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager != null) {
                    manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                ListAdapter listAdapter = ListAdapter.getInstance(v.getContext());
                int i = -1;
                if (search.length() > 0) {
                    switch (v.getId()) {
                        case R.id.btn_find_next:
                            i = listAdapter.findNext(search.getText().toString());
                            break;
                        case R.id.btn_find_last:
                            i = listAdapter.findLast(search.getText().toString());
                            break;
                    }
                }
                if (i >= 0) {
                    friendList.setSelection(i);
                } else if (i == -1) {
                    Toast.makeText(v.getContext(), "未找到", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ((Button) v.findViewById(R.id.btn_find_last)).setOnClickListener(listener);
        ((Button) v.findViewById(R.id.btn_find_next)).setOnClickListener(listener);
    }

    private static void deleteAll(Context context) {
        new Builder(context)
                .setTitle("删除所有")
                .setPositiveButton("确定", new OnClickListener() {
                    public void onClick(DialogInterface p1, int which) {
                        Intent it = new Intent(AntBroadcastReceiver.TAG);
                        it.putExtra("action", "deleteUnidirectionalFiends");
                        context.sendBroadcast(it);
                        ListAdapter.getInstance(context).list.clear();
                        ListAdapter.getInstance(context).exitFind();
                        ListAdapter.getInstance(context).notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    private static void showDeleteDialog(Context context) {
        AlertDialog dialog = new Builder(context)
                .setTitle(Html.fromHtml("删除 " + user.name))
                .setPositiveButton("确定", new OnClickListener() {
                    public void onClick(DialogInterface p1, int p2) {
                        if (user instanceof AlipayUser) {
                            FriendIdMap.removeIdMap(user.id);
                            AlipayUser.remove(user.id);
                        } else if (user instanceof AlipayCooperate) {
                            CooperationIdMap.remove(user.id);
                            AlipayCooperate.remove(user.id);
                        } else if (user instanceof AliFriend || user instanceof ForestUser) {
                            Intent intent = new Intent(AntBroadcastReceiver.TAG);
                            intent.putExtra("action", "deleteFriend");
                            intent.putExtra("userId", user.id);
                            context.sendBroadcast(intent);
                        }
                        if (selected.contains(user.id)) {
                            selected.remove(user.id);
                        }
                        ListAdapter.getInstance(context).list.remove(user);
                        ListAdapter.getInstance(context).exitFind();
                        ListAdapter.getInstance(context).notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }

    private static void deleteSelected(Context context) {
        new Builder(context)
                .setTitle("删除选中")
                .setPositiveButton("确定", new OnClickListener() {
                    public void onClick(DialogInterface p1, int p2) {
                        Intent intent = new Intent(AntBroadcastReceiver.TAG);
                        intent.putExtra("action", "deleteFriends");
                        intent.putExtra("userIds", TextUtils.join(",", selected));
                        context.sendBroadcast(intent);
                        Iterator<AlipayId> it = ListAdapter.getInstance(context).list.iterator();
                        while (it.hasNext()) {
                            ForestUser forestUser = (ForestUser) it.next();
                            if (selected.contains(forestUser.id)) {
                                it.remove();
                            }
                        }
                        ListAdapter.getInstance(context).selects.clear();
                        ListAdapter.getInstance(context).exitFind();
                        ListAdapter.getInstance(context).notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    private static void showEditDialog(Context context) {
        EditText editText = new EditText(context);
        if (user instanceof AlipayCooperate) {
            editText.setHint("克数");
        } else {
            editText.setHint("次数");
        }
        int index = selected.indexOf(user.id);
        if (index >= 0) {
            editText.setText(String.valueOf(datas.get(index)));
        } else {
            editText.getText().clear();
        }
        AlertDialog dialog = new Builder(context)
                .setTitle(user.name)
                .setView(editText)
                .setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        if (p2 == -1) {
                            int num = 0;
                            if (editText.length() > 0) {
                                num = Integer.parseInt(editText.getText().toString());
                            }
                            int index = selected.indexOf(user.id);
                            if (num > 0) {
                                if (index < 0) {
                                    selected.add(user.id);
                                    datas.add(num);
                                } else {
                                    datas.set(index, num);
                                }
                                holder.cb.setChecked(true);
                            } else {
                                if (index >= 0) {
                                    selected.remove(index);
                                    datas.remove(index);
                                }
                                holder.cb.setChecked(false);
                            }
                            Config.hasChanged = true;
                        }
                        ListAdapter.getInstance(context).notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }

    private static void showOptionDialog(Context context) {
        OptionsAdapter adapter;
        if (!(user instanceof AliFriend) && !(user instanceof ForestUser)) {
            adapter = OptionsAdapter.get(context);
        } else {
            ArrayList<String> options = new ArrayList<>();
            options.add("查看森林");
            options.add("查看庄园");
            options.add("删除");
            if (user instanceof AliFriend) {
                options.add("删除所有");
            } else {
                options.add("删除选中");
            }
            adapter = OptionsAdapter.newInstant(context, options);
        }
        AlertDialog dialog = new Builder(context).setTitle("操作")
                .setAdapter(adapter, new OnClickListener() {
                    public void onClick(DialogInterface p1, int p2) {
                        String url = null;
                        switch (p2) {
                            case 0:
                                url = "alipays://platformapi/startapp?saId=10000007&qrcode=https%3A%2F%2F60000002.h5app.alipay.com%2Fwww%2Fhome.html%3FuserId%3D";
                                break;
                            case 1:
                                url = "alipays://platformapi/startapp?saId=10000007&qrcode=https%3A%2F%2F66666674.h5app.alipay.com%2Fwww%2Findex.htm%3Fuid%3D";
                                break;
                            case 2:
                                showDeleteDialog(context);
                                break;
                            case 3:
                                if (user instanceof AliFriend) {
                                    deleteAll(context);
                                } else if (user instanceof ForestUser) {
                                    deleteSelected(context);
                                }
                                break;
                            default:
                                break;
                        }
                        if (url != null) {
                            Intent it = new Intent("android.intent.action.VIEW", Uri.parse(url + user.id));
                            context.startActivity(it);
                        }
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }
}