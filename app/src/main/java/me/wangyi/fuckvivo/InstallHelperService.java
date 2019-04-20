package me.wangyi.fuckvivo;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if ("com.vivo.secime.service".equals(packageName) ||
                "com.coloros.safecenter".equals(packageName) ||
                "com.bbk.account".equals(packageName) ||
                "com.android.systemui".equals(packageName)) {
            String password = PreferencesUtils.getString(getApplicationContext(),
                    PreferencesUtils.KEY_PASSWORD, "");
            if (!TextUtils.isEmpty(password)) {
                fillPassword(rootNode, password);
            }
        }
        installConfirm(rootNode);
        rootNode.recycle();
    }

    private boolean fillPassword(AccessibilityNodeInfo rootNode, String password) {
        AccessibilityNodeInfo editText = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (editText == null) {
            return false;
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
            return true;
        }

        return false;
    }

    List<String> btnInstallText = Arrays.asList("确定", "重新安装", "继续安装", "安装", "打开", "打开应用");

    private boolean installConfirm(AccessibilityNodeInfo rootNode) {
        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();

        for (int i = 0; i < btnInstallText.size(); i++) {
            String text = btnInstallText.get(i);
            List<AccessibilityNodeInfo> accessibilityNodeInfosByText = rootNode.findAccessibilityNodeInfosByText(text);
            Log.d(TAG, text + " -> " + accessibilityNodeInfosByText.size());
            nodeInfoList.addAll(accessibilityNodeInfosByText);
        }


        Log.d(TAG, "=================================");
        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            try {

                CharSequence charSequence = nodeInfo.getText();
                if (null == charSequence) {
                    continue;
                }
                String text = charSequence.toString();
                Log.d(TAG, "按钮文字 -> " + text);
                Log.d(TAG, "---------------------------------");

                if (btnInstallText.contains(text)) {
                    Log.d(TAG, "找到了安装按钮 -> " + nodeInfo.toString());
                    Log.d(TAG, "Button.class.getName() -> " + Button.class.getName());
                    Log.d(TAG, "nodeInfo.getClassName().toString() -> " + nodeInfo.getClassName().toString());
                    if (Button.class.getName().equals(nodeInfo.getClassName().toString())) {
                        try {
                            nodeInfo.setClickable(true);
                        } catch (Exception e) {
                            Log.e(TAG, "setClickable fail");
                        }
                        if (nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Log.d(TAG, "点击了按钮 -> " + text);
                            return true;
                        } else {
                            Log.e(TAG, "模拟点击 操作失败 -> " + text);
                        }
                    }

                }
            } catch (Exception e) {
                Log.e(TAG, "exception ->", e);
            }
        }
        Log.d(TAG, "=================================");
        return false;
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "**************onInterrupt***************");

    }
}
