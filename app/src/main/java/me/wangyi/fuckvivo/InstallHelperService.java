package me.wangyi.fuckvivo;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class InstallHelperService extends AccessibilityService {

    public static final String TAG = "InstallHelperService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
        String packageName = event.getPackageName().toString();
        Log.v(TAG, packageName);
//        if (event.getPackageName().equals("com.android.packageinstaller")) {

//        }
        if ("com.vivo.secime.service".equals(packageName) || "com.android.systemui".equals(packageName)) {
            String password = PreferencesUtils.getString(getApplicationContext(),
                    PreferencesUtils.KEY_PASSWORD, "");
            if (!TextUtils.isEmpty(password)) {
                fillPassword(rootNode, password);
            }
        }
        Observable.just(rootNode).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Observer<AccessibilityNodeInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AccessibilityNodeInfo accessibilityNodeInfo) {
                installConfirm(accessibilityNodeInfo);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void fillPassword(AccessibilityNodeInfo rootNode, String password) {
        AccessibilityNodeInfo editText = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (editText == null) {
            return;
        }
        String editPackageName = editText.getPackageName().toString();
        Log.d(TAG, "editText.getPackageName() -> " + editPackageName);
        if (("com.bbk.account".equals(editPackageName) ||
                "com.coloros.safecenter".equals(editPackageName))
                && "android.widget.EditText".equals(editText.getClassName().toString())) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo
                    .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }

//        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
//        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("安装"));
//        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("确定"));
//        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
//            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//        }
    }

    private void installConfirm(AccessibilityNodeInfo rootNode) {
        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
        List<AccessibilityNodeInfo> sureBtns = rootNode.findAccessibilityNodeInfosByText("确定");
        List<AccessibilityNodeInfo> reinstallBtns = rootNode.findAccessibilityNodeInfosByText("重新安装");
        List<AccessibilityNodeInfo> continueInstallBtns = rootNode.findAccessibilityNodeInfosByText("继续安装");
        List<AccessibilityNodeInfo> installBtns = rootNode.findAccessibilityNodeInfosByText("安装");
        List<AccessibilityNodeInfo> openBtns = rootNode.findAccessibilityNodeInfosByText("打开");
        List<AccessibilityNodeInfo> openAppBtns = rootNode.findAccessibilityNodeInfosByText("打开应用");
        Log.d(TAG, "确定 -> " + sureBtns.size());
        Log.d(TAG, "重新安装 -> " + reinstallBtns.size());
        Log.d(TAG, "继续安装 -> " + continueInstallBtns.size());
        Log.d(TAG, "安装 -> " + installBtns.size());
        Log.d(TAG, "打开应用 -> " + openBtns.size());
        Log.d(TAG, "打开 -> " + openAppBtns.size());

        nodeInfoList.addAll(sureBtns);
        nodeInfoList.addAll(reinstallBtns);
        nodeInfoList.addAll(continueInstallBtns);
        nodeInfoList.addAll(installBtns);
        nodeInfoList.addAll(openBtns);

        Log.d(TAG, "=================================");
        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            try {

                Log.d(TAG, "按钮文字 -> " + nodeInfo.getText().toString());
                Log.d(TAG, "---------------------------------");

                if ("确定".equals(nodeInfo.getText().toString())
                        || "重新安装".equals(nodeInfo.getText().toString())
                        || "继续安装".equals(nodeInfo.getText().toString())
                        || "安装".equals(nodeInfo.getText().toString())
                        || "打开".equals(nodeInfo.getText().toString())
                        || "打开应用".equals(nodeInfo.getText().toString())
                ) {

                    Log.d(TAG, "找到了安装按钮 -> " + nodeInfo.toString());
                    if ("android.widget.Button".equals(nodeInfo.getClassName().toString()) &&
                            "com.android.packageinstaller".equals(nodeInfo.getPackageName().toString())
                    ) {
                        if (nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Log.d(TAG, "点击了按钮 -> " + nodeInfo.getText().toString());
                            return;
                        } else if (nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)) {
                            Log.d(TAG, "长按了按钮 -> " + nodeInfo.getText().toString());
                            return;
                        } else if (nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SELECT)) {
                            Log.d(TAG, "选择了按钮 -> " + nodeInfo.getText().toString());
                            return;
                        } else {
                            Log.e(TAG, "模拟点击 操作失败 -> " + nodeInfo.getText().toString());

                        }
                    }

                }
            } catch (Exception e) {
                Log.e(TAG, "exception ->", e);
            }
//            if ("android.widget.Button".equals(nodeInfo.getClassName().toString())) {
//            }
        }
        Log.d(TAG, "=================================");
    }

    @Override
    public void onInterrupt() {
    }
}
