package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.cache.RoomDataManger;
import com.github.tvbox.osc.cache.StorageDrive;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.util.StorageDriveType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

public class AlistDriveDialog extends BaseDialog {

    private StorageDrive drive = null;
    private EditText etName;
    private EditText etUrl;
    private EditText etInitPath;
    private EditText etPassword;

    public AlistDriveDialog(@NonNull @NotNull Context context, StorageDrive drive) {
        super(context);
        setContentView(R.layout.dialog_alistdrive);
        if (drive != null)
            this.drive = drive;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etName = findViewById(R.id.etName);
        etUrl = findViewById(R.id.etUrl);
        etInitPath = findViewById(R.id.etInitPath);
        etPassword = findViewById(R.id.etPassword);
        if (drive != null) {
            etName.setText(drive.name);
            try {
                JsonObject config = JsonParser.parseString(drive.configJson).getAsJsonObject();
                initSavedData(etUrl, config, "url");
                initSavedData(etInitPath, config, "initPath");
                initSavedData(etPassword, config, "password");
            } catch (Exception ignored) {
            }
        }
        findViewById(R.id.btnConfirm).setOnClickListener(view -> {
            String name = etName.getText().toString();
            String url = etUrl.getText().toString();
            String initPath = etInitPath.getText().toString();
            String password = etPassword.getText().toString();
            if (name.length() == 0) {
                Toast.makeText(AlistDriveDialog.this.getContext(), "请赋予一个空间名称", Toast.LENGTH_SHORT).show();
                return;
            }
            if (url.length() == 0) {
                Toast.makeText(AlistDriveDialog.this.getContext(), "请务必填入Alist网页地址", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!url.endsWith("/"))
                url += "/";
            JsonObject config = new JsonObject();
            config.addProperty("url", url);
            if (initPath.length() > 0 && initPath.startsWith("/"))
                initPath = initPath.substring(1);
            if (initPath.length() > 0 && initPath.endsWith("/"))
                initPath = initPath.substring(0, initPath.length() - 1);
            config.addProperty("initPath", initPath);
            config.addProperty("password", password);
            if (drive != null) {
                drive.name = name;
                drive.configJson = config.toString();
                RoomDataManger.updateDriveRecord(drive);
            } else {
                RoomDataManger.insertDriveRecord(name, StorageDriveType.TYPE.ALISTWEB, config);
            }
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_DRIVE_REFRESH));
            AlistDriveDialog.this.dismiss();
        });
        findViewById(R.id.btnCancel).setOnClickListener(view -> AlistDriveDialog.this.dismiss());
    }

    private void initSavedData(EditText etField, JsonObject config, String fieldName) {
        if (config.has(fieldName))
            etField.setText(config.get(fieldName).getAsString());
    }

}