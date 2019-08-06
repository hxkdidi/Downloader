package com.didi.art.appdownloader.download;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;


import com.didi.art.appdownloader.R;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class PermissionHelper {

    private final HiPermission permissionManager;
    private String[] permissions;

    public static PermissionHelper create() {
        return new PermissionHelper();
    }

    private PermissionHelper() {
        Context context = MyApp.getInstance();
        permissionManager = HiPermission.create(context)
                .title("授权管理")
                .animStyle(R.style.PermissionAnimModal);//设置动画
    }

    public void requestSingle(String permission, final RequestCallback callback) {
        this.permissionManager.checkSinglePermission(permission, new PermissionCallback() {
            @Override
            public void onClose() {
                if (callback != null) {
                    callback.onClose();
                }
            }

            @Override
            public void onFinish() {
                if (callback != null) {
                    callback.onFinish();
                }
            }

            @Override
            public void onDeny(String permission, int position) {
                if (callback != null) {
                    callback.onDeny(permission, position);
                }
            }

            @Override
            public void onGuarantee(String permission, int position) {
                if (callback != null) {
                    callback.onGuarantee(permission, position);
                }
            }
        });
    }

    public void requestMuti(final RequestCallback callback) {
        if (permissions == null || permissions.length <= 0) {
            return;
        }
        List<PermissionItem> permissionItems = new ArrayList();
        for (int i = 0; i < permissions.length; i++) {
            String permissionName;
            int resId;
            String permission = permissions[i];
            if (TextUtils.equals(permission, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionName = PermissionName.ACCESS_FINE_LOCATION;
                resId = R.drawable.permission_ic_location;
            } else if (TextUtils.equals(permission, android.Manifest.permission.CAMERA)) {
                permissionName = PermissionName.CAMERA;
                resId = R.drawable.permission_ic_camera;
            } else if (TextUtils.equals(permission, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionName = PermissionName.WRITE_EXTERNAL_STORAGE;
                resId = R.drawable.permission_ic_storage;
            } else if (TextUtils.equals(permission, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionName = PermissionName.READ_EXTERNAL_STORAGE;
                resId = R.drawable.permission_ic_storage;
            } else if (TextUtils.equals(permission, Manifest.permission.READ_PHONE_STATE)) {
                permissionName = PermissionName.READ_PHONE_STATE;
                resId = R.drawable.permission_ic_phone;
            } else if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                permissionName = PermissionName.RECORD_AUDIO;
                resId = R.drawable.permission_ic_sensors;
            } else {
                permissionName = PermissionName.UNKNOWN;
                resId = R.mipmap.ic_launcher;
            }
            permissionItems.add(new PermissionItem(permission, permissionName, resId));
        }
        permissionManager.permissions(permissionItems);
        permissionManager.checkMutiPermission(new PermissionCallback() {
            @Override
            public void onClose() {
                if (callback != null) {
                    callback.onClose();
                }
            }

            @Override
            public void onFinish() {
                if (callback != null) {
                    callback.onFinish();
                }
            }

            @Override
            public void onDeny(String permission, int position) {
                if (callback != null) {
                    callback.onDeny(permission, position);
                }
            }

            @Override
            public void onGuarantee(String permission, int position) {
                if (callback != null) {
                    callback.onGuarantee(permission, position);
                }
            }
        });
    }

    public interface RequestCallback {

        void onClose();

        void onFinish();

        void onDeny(String permission, int position);

        void onGuarantee(String permission, int position);

    }

    public String[] getPermissions() {
        return permissions;
    }

    public PermissionHelper addPermissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }
}
