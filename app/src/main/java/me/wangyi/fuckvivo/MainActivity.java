package me.wangyi.fuckvivo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText etVivoPwd;
    private EditText etHomerId;
    private EditText etHomerPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etVivoPwd = findViewById(R.id.et_password);
        etHomerId = findViewById(R.id.et_homer_id);
        etHomerPwd = findViewById(R.id.et_homer_pwd);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String password = PreferencesUtils.getString(getApplicationContext(),
                PreferencesUtils.KEY_PASSWORD, "");
        String homerId = PreferencesUtils.getString(getApplicationContext(),
                PreferencesUtils.KEY_HOMER_ID, "");
        String homerPwd = PreferencesUtils.getString(getApplicationContext(),
                PreferencesUtils.KEY_HOMER_PWD, "");
        if (!TextUtils.isEmpty(password)) {
            etVivoPwd.setText(password);
            etVivoPwd.setSelection(password.length());
        }
        etHomerId.setText(homerId);
        etHomerPwd.setText(homerPwd);
    }

    public void savePassword(View view) {
        String password = etVivoPwd.getText().toString().trim();
        String homerId = etHomerId.getText().toString().trim();
        String homerPwd = etHomerPwd.getText().toString().trim();
        PreferencesUtils.saveString(this, PreferencesUtils.KEY_PASSWORD, password);
        PreferencesUtils.saveString(this, PreferencesUtils.KEY_HOMER_ID, homerId);
        PreferencesUtils.saveString(this, PreferencesUtils.KEY_HOMER_PWD, homerPwd);
        Toast.makeText(this, R.string.toast_save_success, Toast.LENGTH_SHORT).show();
    }

    public void openSettings(View view) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);
    }
}
