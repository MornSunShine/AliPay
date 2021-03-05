package morn.xposed.alipay.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import morn.xposed.alipay.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import morn.xposed.alipay.AntBroadcastReceiver;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.CooperationIdMap;
import morn.xposed.alipay.util.FileUtils;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;

public class SettingsActivity extends Activity {
    private static final String TAG = SettingsActivity.class.getCanonicalName();
    CheckBox immediateEffect, recordLog, recordRuntimeLog, showToast, deleteStranger,
            stayAwake,
            restartService, sendXedgepro, originalMode,  collectEnergy,
            collectWateringEnergy, helpFriendCollect, delayHelpFriendCollect, helpFriendCollectWatering, receiveForestTaskAward,
            cooperateWater, grabPacket, enableFarm, rewardFriend, sendBackAnimal,
            receiveFarmToolReward, useNewEggTool, harvestProduce, donation, answerQuestion,
            receiveFarmTaskAward, feedAnimal, useAccelerateTool, notifyFriend, starGameHighScore,
            jumpGameHighScore, playFarmGame, acceptGift, collectManurePot, triggerTbTask,
            orchardSpreadManure, receivePoint, openTreasureBox, donateCharityCoin, kbSignIn,
            collectCreditFeedback, zmDonate, electricSignIn, greatYouthReceive,
            openDoorSignIn, hireAnimal;

    private void selectFriends(Context context, String title) {
        Intent intent = new Intent(AntBroadcastReceiver.TAG);
        intent.putExtra("action", "getGroupList");
        String fileName = System.currentTimeMillis() + ".json";
        intent.putExtra("fileName", fileName);
        context.sendBroadcast(intent);
        File file = new File(FileUtils.getConfigDirectoryFile(), fileName);
        ArrayList<AlipayId> members = new ArrayList<>();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000L) {
            try {
                Thread.sleep(10L);
                if (file.exists()) {
                    JSONArray ja = new JSONArray(FileUtils.readFromFile(file));
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.optJSONObject(i);
                        AliMember member = new AliMember();
                        member.id = jo.optString("id");
                        member.name = jo.optString("name");
                        member.isCurrentUserQuit = jo.optBoolean("isCurrentUserQuit");
                        member.canAddNum = jo.optInt("canAddNum");
                        member.friendNum = jo.optInt("friendNum");
                        member.memberNum = jo.optInt("memberNum");
                        if (member.isCurrentUserQuit) {
                            member.name = "<font color='#FF0000'>" + member.name + "</font>";
                        }
                        members.add(member);
                    }
                    file.delete();
                }
            } catch (Exception e) {
                Log.printStackTrace(TAG, e);
            }
        }
        DialogManager.selectFriends(this, title + "（" + members.size() + "）", members, Config.getAddFriendGroupList(), null);
    }

    public void onClick(View v) {
        CheckBox checkBox;
        if (v instanceof CheckBox) {
            checkBox = (CheckBox) v;
        } else {
            checkBox = null;
        }
        Button button;
        if (v instanceof Button) {
            button = (Button) v;
        } else {
            button = null;
        }
        switch (v.getId()) {
            case R.id.btn_avg_collect:
            case R.id.btn_farm_log:
            case R.id.btn_find_last:
            case R.id.btn_find_next:
            case R.id.btn_forest_log:
            case R.id.btn_friend_log:
            case R.id.btn_other_log:
            case R.id.btn_rank:
            case R.id.btn_sum:
            case R.id.btn_sum_collect:
            case R.id.btn_view_help:
            case R.id.btn_week:
            case R.id.btn_week_collect:
            case R.id.cb_list:
            default:
                return;
            case R.id.cb_zmDonate:
                Config.setZmDonate(checkBox.isChecked());
                return;
            case R.id.cb_useNewEggTool:
                Config.setUseNewEggTool(checkBox.isChecked());
                return;
            case R.id.cb_useAccelerateTool:
                Config.setUseAccelerateTool(checkBox.isChecked());
                return;
            case R.id.cb_triggerBbTask:
                Config.setTriggerTbTask(checkBox.isChecked());
                return;
            case R.id.cb_stayAwake:
                Config.setStayAwake(checkBox.isChecked());
                return;
            case R.id.cb_starGameHighScore:
                Config.setStarGameHighScore(checkBox.isChecked());
                return;
            case R.id.cb_showToast:
                Config.setShowToast(checkBox.isChecked());
                return;
            case R.id.cb_sendBroadcast:
                Config.setSendXedgepro(checkBox.isChecked());
                return;
            case R.id.cb_driveAnimal:
                Config.setSendBackAnimal(checkBox.isChecked());
                return;
            case R.id.cb_rewardFriend:
                Config.setRewardFriend(checkBox.isChecked());
                return;
            case R.id.cb_autoRestart:
                Config.setRestartService(checkBox.isChecked());
                return;
            case R.id.cb_recordRuntimeLog:
                Config.setRecordRuntimeLog(checkBox.isChecked());
                return;
            case R.id.cb_recordLog:
                Config.setRecordLog(checkBox.isChecked());
                return;
            case R.id.cb_receivePoint:
                Config.setReceivePoint(checkBox.isChecked());
                return;
            case R.id.cb_receiveForestAward:
                Config.setReceiveForestTaskAward(checkBox.isChecked());
                return;
            case R.id.cb_receiveFarmTool:
                Config.setReceiveFarmToolReward(checkBox.isChecked());
                return;
            case R.id.cb_receiveFarmTaskAward:
                Config.setReceiveFarmTaskAward(checkBox.isChecked());
                return;
            case R.id.cb_playFarmGame:
                Config.setPlayFarmGame(checkBox.isChecked());
                return;
            case R.id.cb_originalMode:
                Config.setOriginalMode(checkBox.isChecked());
                return;
            case R.id.cb_bbFertilization:
                Config.setOrchardSpreadManure(checkBox.isChecked());
                return;
            case R.id.cb_openTreasureBox:
                Config.setOpenTreasureBox(checkBox.isChecked());
                return;
            case R.id.cb_openDoorSignIn:
                Config.setOpenDoorSignIn(checkBox.isChecked());
                return;
            case R.id.cb_notifyDrive:
                Config.setNotifyFriend(checkBox.isChecked());
                return;
            case R.id.cb_kbSignIn:
                Config.setKbSignIn(checkBox.isChecked());
                return;
            case R.id.cb_jumpGameHighScore:
                Config.setJumpGameHighScore(checkBox.isChecked());
                return;
            case R.id.cb_immediateEffect:
                Config.setImmediateEffect(checkBox.isChecked());
                return;
            case R.id.cb_hireAnimal:
                Config.setHireAnimal(checkBox.isChecked());
                return;
            case R.id.cb_helpCollectGold:
                Config.setHelpFriendCollectWatering(checkBox.isChecked());
                return;
            case R.id.cb_helpCollect:
                Config.setHelpFriendCollect(checkBox.isChecked());
                return;
            case R.id.cb_harvestProduce:
                Config.setHarvestProduce(checkBox.isChecked());
                return;
            case R.id.cb_greatYouthReceive:
                Config.setGreatyouthReceive(checkBox.isChecked());
                return;
            case R.id.cb_collectPacket:
                Config.setGrabPacket(checkBox.isChecked());
                return;
            case R.id.cb_autoFeed:
                Config.setFeedAnimal(checkBox.isChecked());
                return;
            case R.id.cb_enableFarm:
                Config.setEnableFarm(checkBox.isChecked());
                return;
            case R.id.cb_electricSignIn:
                Config.setElectricSignIn(checkBox.isChecked());
                return;
            case R.id.cb_donate_eggs:
                Config.setDonation(checkBox.isChecked());
                return;
            case R.id.cb_donateCharityCoin:
                Config.setDonateCharityCoin(checkBox.isChecked());
                return;
            case R.id.cb_deleteStranger:
                Config.setDeleteStranger(checkBox.isChecked());
                return;
            case R.id.cb_delayHelpCollect:
                Config.setDelayHelpFriendCollect(checkBox.isChecked());
                return;
            case R.id.cb_cooperateWater:
                Config.setCooperateWater(checkBox.isChecked());
                return;
            case R.id.cb_collectGold:
                Config.setCollectWateringEnergy(checkBox.isChecked());
                return;
            case R.id.cb_collectManure:
                Config.setCollectManurePot(checkBox.isChecked());
                return;
            case R.id.cb_collectEnergy:
                Config.setCollectEnergy(checkBox.isChecked());
                return;
            case R.id.cb_collectCreditFeedback:
                Config.setCollectCreditFeedback(checkBox.isChecked());
                return;
            case R.id.cb_answerQuestion:
                Config.setAnswerQuestion(checkBox.isChecked());
                return;
            case R.id.cb_collectWheat:
                Config.setAcceptGift(checkBox.isChecked());
                return;
            case R.id.btn_broadcastContent:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.XEDGEPRO_DATA);
                return;
            case R.id.btn_helpWaterList:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getWaterFriendList(), Config.getWaterFriendNumList());
                return;
            case R.id.btn_plantList:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getVisitFriendList(), null);
                return;
            case R.id.btn_threadCount:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.THREAD_COUNT);
                return;
            case R.id.btn_smallAccount:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getSmallAccountList(), null);
                return;
            case R.id.btn_driveType:
                ChoiceDialog.showSendTypeDialog(this, button.getText());
                return;
            case R.id.btn_returnWater30:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.RETURN_WATER_30);
                return;
            case R.id.btn_returnWater20:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.RETURN_WATER_20);
                return;
            case R.id.btn_returnWater10:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.RETURN_WATER_10);
                return;
            case R.id.btn_recallType:
                ChoiceDialog.showRecallAnimalDialog(this, button.getText());
                return;
            case R.id.btn_pin:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.PIN);
                return;
            case R.id.btn_minExchangeSteps:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.MIN_EXCHANGE_COUNT);
                return;
            case R.id.btn_latestExchangeTime:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.LATEST_EXCHANGE_TIME);
                return;
            case R.id.btn_hookStep:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.HOOK_STEP);
                return;
            case R.id.btn_groupList:
                selectFriends(this, button.getText().toString());
                return;
            case R.id.btn_helpFeedList:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getFeedFriendAnimalList(), Config.getFeedFriendAnimalNumList());
                return;
            case R.id.btn_downloadQiufeng:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://xqe.lanzous.com/b00z6haji")));
                return;
            case R.id.btn_noDriveList:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getDontSendFriendList(), null);
                return;
            case R.id.btn_noNotifyList:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getDontNotifyFriendList(), null);
                return;
            case R.id.btn_noHelpCollectList:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getDontHelpCollectList(), null);
                return;
            case R.id.btn_noCollectList:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getDontCollectList(), null);
                return;
            case R.id.btn_donationQiufeng:
                DialogManager.showWXDialog(this);
                return;
            case R.id.btn_donationDeveloper:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("alipays://platformapi/startapp?saId=10000007&qrcode=https%3A%2F%2Fqr.alipay.com%2Ftsx00339eflkuhhtfctcn48")));
                return;
            case R.id.btn_cooperateList:
                DialogManager.selectFriends(this, button.getText(), AlipayUser.getList(), Config.getCooperateWaterList(), Config.getCooperateWaterNumList());
                return;
            case R.id.btn_collectTimeout:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.COLLECT_TIMEOUT);
                return;
            case R.id.btn_collectInterval:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.COLLECT_INTERVAL);
                return;
            case R.id.btn_checkInterval:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.CHECK_INTERVAL);
                return;
            case R.id.btn_restartUnlockedScreen:
                ChoiceDialog.backend(this, button.getText());
                return;
            case R.id.btn_restartLockedScreen:
                ChoiceDialog.front(this, button.getText());
                return;
            case R.id.btn_advanceTime:
                EditDialog.showEditDialog(this, button.getText(), EditDialog.EditMode.ADVANCE_TIME);
                return;
        }
        DialogManager.showAboutDialog(this);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);
        Config.shouldReload = true;
        FriendIdMap.shouldReload = true;
        CooperationIdMap.shouldReload = true;
        this.immediateEffect = (CheckBox) findViewById(R.id.cb_immediateEffect);
        this.recordLog = (CheckBox) findViewById(R.id.cb_recordLog);
        this.recordRuntimeLog = (CheckBox) findViewById(R.id.cb_recordRuntimeLog);
        this.showToast = (CheckBox) findViewById(R.id.cb_showToast);
        this.stayAwake = (CheckBox) findViewById(R.id.cb_stayAwake);
        this.restartService = (CheckBox) findViewById(R.id.cb_autoRestart);
        this.sendXedgepro = (CheckBox) findViewById(R.id.cb_sendBroadcast);
        this.originalMode = (CheckBox) findViewById(R.id.cb_originalMode);
        this.deleteStranger = (CheckBox) findViewById(R.id.cb_deleteStranger);
        this.collectEnergy = (CheckBox) findViewById(R.id.cb_collectEnergy);
        this.collectWateringEnergy = (CheckBox) findViewById(R.id.cb_collectGold);
        this.helpFriendCollect = (CheckBox) findViewById(R.id.cb_helpCollect);
        this.delayHelpFriendCollect = (CheckBox) findViewById(R.id.cb_delayHelpCollect);
        this.helpFriendCollectWatering = (CheckBox) findViewById(R.id.cb_helpCollectGold);
        this.receiveForestTaskAward = (CheckBox) findViewById(R.id.cb_receiveForestAward);
        this.cooperateWater = (CheckBox) findViewById(R.id.cb_cooperateWater);
        this.grabPacket = (CheckBox) findViewById(R.id.cb_collectPacket);
        this.enableFarm = (CheckBox) findViewById(R.id.cb_enableFarm);
        this.rewardFriend = (CheckBox) findViewById(R.id.cb_rewardFriend);
        this.sendBackAnimal = (CheckBox) findViewById(R.id.cb_driveAnimal);
        this.receiveFarmToolReward = (CheckBox) findViewById(R.id.cb_receiveFarmTool);
        this.useNewEggTool = (CheckBox) findViewById(R.id.cb_useNewEggTool);
        this.harvestProduce = (CheckBox) findViewById(R.id.cb_harvestProduce);
        this.donation = (CheckBox) findViewById(R.id.cb_donate_eggs);
        this.answerQuestion = (CheckBox) findViewById(R.id.cb_answerQuestion);
        this.receiveFarmTaskAward = (CheckBox) findViewById(R.id.cb_receiveFarmTaskAward);
        this.feedAnimal = (CheckBox) findViewById(R.id.cb_autoFeed);
        this.useAccelerateTool = (CheckBox) findViewById(R.id.cb_useAccelerateTool);
        this.notifyFriend = (CheckBox) findViewById(R.id.cb_notifyDrive);
        this.starGameHighScore = (CheckBox) findViewById(R.id.cb_starGameHighScore);
        this.jumpGameHighScore = (CheckBox) findViewById(R.id.cb_jumpGameHighScore);
        this.playFarmGame = (CheckBox) findViewById(R.id.cb_playFarmGame);
        this.acceptGift = (CheckBox) findViewById(R.id.cb_collectWheat);
        this.collectManurePot = (CheckBox) findViewById(R.id.cb_collectManure);
        this.hireAnimal = (CheckBox) findViewById(R.id.cb_hireAnimal);
        this.triggerTbTask = (CheckBox) findViewById(R.id.cb_triggerBbTask);
        this.orchardSpreadManure = (CheckBox) findViewById(R.id.cb_bbFertilization);
        this.receivePoint = (CheckBox) findViewById(R.id.cb_receivePoint);
        this.openTreasureBox = (CheckBox) findViewById(R.id.cb_openTreasureBox);
        this.donateCharityCoin = (CheckBox) findViewById(R.id.cb_donateCharityCoin);
        this.kbSignIn = (CheckBox) findViewById(R.id.cb_kbSignIn);
        this.collectCreditFeedback = (CheckBox) findViewById(R.id.cb_collectCreditFeedback);
        this.zmDonate = (CheckBox) findViewById(R.id.cb_zmDonate);
        this.electricSignIn = (CheckBox) findViewById(R.id.cb_electricSignIn);
        this.greatYouthReceive = (CheckBox) findViewById(R.id.cb_greatYouthReceive);
        this.openDoorSignIn = (CheckBox) findViewById(R.id.cb_openDoorSignIn);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Config.hasChanged) {
            Config.hasChanged = !Config.write2ConfigFile();
            Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
        }
        FriendIdMap.saveIdMap();
        CooperationIdMap.saveIdMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.immediateEffect.setChecked(Config.getImmediateEffect());
        this.recordLog.setChecked(Config.getRecordLog());
        this.recordRuntimeLog.setChecked(Config.getRecordRuntimeLog());
        this.showToast.setChecked(Config.getShowToast());
        this.stayAwake.setChecked(Config.getStayAwake());
        this.restartService.setChecked(Config.getRestartService());
        this.sendXedgepro.setChecked(Config.getSendXedgepro());
        this.originalMode.setChecked(Config.getOriginalMode());
        this.deleteStranger.setChecked(Config.getDeleteStranger());
        this.collectEnergy.setChecked(Config.getCollectEnergy());
        this.collectWateringEnergy.setChecked(Config.getCollectWateringEnergy());
        this.helpFriendCollect.setChecked(Config.getHelpFriendCollect());
        this.delayHelpFriendCollect.setChecked(Config.getDelayHelpFriendCollect());
        this.helpFriendCollectWatering.setChecked(Config.getHelpFriendCollectWatering());
        this.receiveForestTaskAward.setChecked(Config.getReceiveForestTaskAward());
        this.cooperateWater.setChecked(Config.getCooperateWater());
        this.grabPacket.setChecked(Config.getGrabPacket());
        this.enableFarm.setChecked(Config.getEnableFarm());
        this.rewardFriend.setChecked(Config.getRewardFriend());
        this.sendBackAnimal.setChecked(Config.getSendBackAnimal());
        this.receiveFarmToolReward.setChecked(Config.getReceiveFarmToolReward());
        this.useNewEggTool.setChecked(Config.getUseNewEggTool());
        this.harvestProduce.setChecked(Config.getHarvestProduce());
        this.donation.setChecked(Config.getDonation());
        this.answerQuestion.setChecked(Config.getAnswerQuestion());
        this.receiveFarmTaskAward.setChecked(Config.getReceiveFarmTaskAward());
        this.feedAnimal.setChecked(Config.getFeedAnimal());
        this.useAccelerateTool.setChecked(Config.getUseAccelerateTool());
        this.notifyFriend.setChecked(Config.getNotifyFriend());
        this.starGameHighScore.setChecked(Config.getStarGameHighScore());
        this.jumpGameHighScore.setChecked(Config.getJumpGameHighScore());
        this.playFarmGame.setChecked(Config.getPlayFarmGame());
        this.acceptGift.setChecked(Config.getAcceptGift());
        this.collectManurePot.setChecked(Config.getCollectManurePot());
        this.hireAnimal.setChecked(Config.getHireAnimal());
        this.triggerTbTask.setChecked(Config.getTriggerTbTask());
        this.orchardSpreadManure.setChecked(Config.getOrchardSpreadManure());
        this.receivePoint.setChecked(Config.getReceivePoint());
        this.openTreasureBox.setChecked(Config.getOpenTreasureBox());
        this.donateCharityCoin.setChecked(Config.getDonateCharityCoin());
        this.kbSignIn.setChecked(Config.getKbSignIn());
        this.collectCreditFeedback.setChecked(Config.getCollectCreditFeedback());
        this.zmDonate.setChecked(Config.getZmDonate());
        this.electricSignIn.setChecked(Config.getElectricSignIn());
        this.greatYouthReceive.setChecked(Config.getGreatyouthReceive());
        this.openDoorSignIn.setChecked(Config.getOpenDoorSignIn());
    }
}

