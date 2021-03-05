package morn.xposed.alipay.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import morn.xposed.alipay.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import morn.xposed.alipay.AntBroadcastReceiver;
import morn.xposed.alipay.hook.FriendsManager;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.FileUtils;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.Statistics;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getCanonicalName();
    TextView tv_statistics;


    private void setModuleActive(boolean b) {
        b = b || isExpModuleActive(this);
        TextView inactive = (TextView) findViewById(R.id.tv_inactive);
        inactive.setVisibility(b ? View.GONE : View.VISIBLE);
    }


    private static boolean isExpModuleActive(Context context) {
        boolean isExp = false;
        if (context == null)
            throw new IllegalArgumentException("context must not be null!!");

        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
            Bundle result = null;
            try {
                result = contentResolver.call(uri, "active", null, null);
            } catch (RuntimeException e) {
                // TaiChi is killed, try invoke
                try {
                    Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Throwable e1) {
                    return false;
                }
            }
            if (result == null)
                result = contentResolver.call(uri, "active", null, null);

            if (result == null)
                return false;
            isExp = result.getBoolean("active", false);
        } catch (Throwable ignored) {
        }
        return isExp;
    }


    private void versionValidation() {
        int versionCode;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            versionCode = 0;
        }
        if (versionCode != getSharedPreferences("qiufeng", 0).getInt("versionCode", 0)) {
            SharedPreferences.Editor editor = getSharedPreferences("qiufeng", 0).edit();
            editor.putInt("versionCode", versionCode);
            editor.apply();
            DialogManager.showAboutDialog(this);
        }
    }

    private void friendAnalysis() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<ForestUser> friendList = new ArrayList<>();
        String title = "好友分析";
        try {
            String data = FileUtils.readFromFile(FileUtils.getRankFile());
            if (data.isEmpty()) {
                data = "{}";
            }
            JSONObject userInfo = new JSONObject(data).getJSONObject(Statistics.getUserId());
            JSONArray ja = userInfo.getJSONArray("rank");
            StringBuilder sb = new StringBuilder("好友分析 ");
            sb.append(ja.length())
                    .append("（")
                    .append(userInfo.getString("time").substring(5))
                    .append("）");
            title = sb.toString();
            JSONObject jo;
            ForestUser user;
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                user = new ForestUser();
                user.id = jo.optString("id");
                user.friendStatus = jo.optInt("friendStatus");
                user.energyWeek = jo.optInt("energyWeek");
                user.energySum = jo.optInt("energySum");
                user.collectEnergyWeek = jo.optInt("collectEnergyWeek");
                user.collectEnergySum = jo.optInt("collectEnergySum");
                user.week = jo.optInt("week");
                user.addTime = jo.optLong("addTime");
                user.name = jo.optString("name") + "（" + format.format(new Date(user.addTime)) + "）" + user.week + "<br/>";
                if (user.friendStatus == 1 && user.energySum < 0) {
                    user.name += "未开通森林";
                    break;
                }
                user.name += "周收" + Log.translate(user.collectEnergyWeek) + "  总收" + Log.translate(user.collectEnergyWeek + user.collectEnergySum)
                        + "  日收" + Log.translate(ForestUser.getAvgCollect(user)) + "  周" + Log.translate(user.energyWeek)
                        + "  总" + user.energySum;
                if (user.friendStatus == 2) {
                    user.name += "<br/><font color='#FF0000'>单删好友</font>";
                } else {
                    user.name += "<br/><font color='#FF0000'>黑名单好友</font>";
                }
            }
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
        }
        DialogManager.selectFriends(this, title, friendList, new ArrayList(), null);
    }

    public void onClick(View v) {
        String data = "file://";
        switch (v.getId()) {
            case R.id.btn_forest_log:
                data += FileUtils.getForestLogFile().getAbsolutePath();
                break;
            case R.id.btn_farm_log:
                data += FileUtils.getFarmLogFile().getAbsolutePath();
                break;
            case R.id.btn_friend_log:
                data += FileUtils.getFriendFile().getAbsolutePath();
                break;
            case R.id.btn_other_log:
                data += FileUtils.getOtherLogFile().getAbsolutePath();
                break;
            case R.id.btn_rank:
                friendAnalysis();
                break;
            case R.id.btn_view_help:
                data = "https://github.com/pansong291/XQuickEnergy/wiki";
                break;
            default:
                break;
        }
        Intent it = new Intent(this, HtmlViewerActivity.class);
        it.setData(Uri.parse(data));
        startActivity(it);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        setModuleActive(false);

        tv_statistics = (TextView) findViewById(R.id.tv_statistics);
        versionValidation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int state = getPackageManager()
                .getComponentEnabledSetting(new ComponentName(this, getClass().getCanonicalName() + "Alias"));
        menu.add(0, 1, 0, "隐藏应用图标")
                .setCheckable(true)
                .setChecked(state > PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        menu.add(0, 2, 0, "导出统计文件");
        menu.add(0, 3, 0, "导入统计文件");
        menu.add(0, 4, 0, "单删好友检测");
        menu.add(0, 5, 0, "设置");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                boolean hidden = item.isChecked();
                int state = hidden ? PackageManager.COMPONENT_ENABLED_STATE_DEFAULT : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                getPackageManager()
                        .setComponentEnabledSetting(new ComponentName(this, getClass().getCanonicalName() + "Alias"), state, PackageManager.DONT_KILL_APP);
                item.setChecked(!hidden);
                break;
            case 2:
                if (FileUtils.copyTo(FileUtils.getStatisticsFile(), FileUtils.getExportedStatisticsFile()))
                    Toast.makeText(this, "导出成功", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (FileUtils.copyTo(FileUtils.getExportedStatisticsFile(), FileUtils.getStatisticsFile())) {
                    tv_statistics.setText(Statistics.getText());
                    Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 5:
                new Thread() {
                    @Override
                    public void run() {
                        if (FriendIdMap.forestUserMap.isEmpty()) {
                            FriendIdMap.updateForestInfo();
                        }
                        List<AliFriend> friends = getUnidirectionalFiendsList();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        for (AlipayId user : friends) {
                            ForestUser forest = FriendIdMap.forestUserMap.get(user.id);
                            if (forest != null) {
                                user.name += "（" + format.format(new Date(forest.addTime)) + "）<br/>"
                                        + "周收" + Log.translate(forest.collectEnergyWeek)
                                        + "  总收" + (Log.translate(forest.collectEnergyWeek + forest.collectEnergySum))
                                        + "  周" + Log.translate(forest.energyWeek) + "  总" + Log.translate(forest.energySum) + "  " + Log.translate(forest.week);
                            }
                        }
                        DialogManager.selectFriends(MainActivity.this, "单删好友列表（" + friends.size() + "）", friends, new ArrayList<>(), null);
                    }
                }.start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        Intent intent = new Intent(AntBroadcastReceiver.TAG);
        intent.putExtra("action", "forceRefreshMyFriends");
        sendBroadcast(intent);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.tv_statistics.setText(Statistics.getText());
    }

    public static List<AliFriend> getUnidirectionalFiendsList() {
        List<AliFriend> friends = FriendsManager.getAllFriends(XposedHook.getLoader());
        return friends.stream().filter(friend -> friend.getFriendStatus() == 2).collect(Collectors.toList());
    }
}