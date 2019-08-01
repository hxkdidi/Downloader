package com.didi.art.appdownloader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private NumberProgressBar pb;
    private TextView tvDesc;
//    private EditText et_url;

    /**
     * 设置透明状态栏
     */
    public static void setTransparentStatusBar(AppCompatActivity activity) {
        activity.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setTransparentStatusBar(this);
        setContentView(R.layout.activity_main);
        init(this);
        pb = (NumberProgressBar) findViewById(R.id.pb);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
//        Button btDownload = (Button) findViewById(R.id.bt_download);
//        et_url = (EditText) findViewById(R.id.et_url);
//        btDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
        PermissionHelper.create()
                .addPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestMuti(new PermissionHelper.RequestCallback() {
                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void onFinish() {
                        String apkUrl;
//                                if (TextUtils.isEmpty(et_url.getText().toString())) {

//                        apkUrl = "https://apk.ikuyoo.cn/1004_7.apk";
                        apkUrl = "https://apk.ikuyoo.cn/1050_8.apk";
//                        apkUrl = "https://apk.ikuyoo.cn/1052_5.apk";

//                                } else {
//                                    apkUrl = et_url.getText().toString();
//                                }
                        downLoad(apkUrl);
                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });

//    }
//        });
    }

    /**
     * 兼容4.4以下系统的https访问
     *
     * @param params
     */
    public static void setSSL(RequestParams params) {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            if (sslcontext != null) {
                sslcontext.init(null, null, null);
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLSocketFactory sslSocketFactory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
        if (sslSocketFactory != null) {
            params.setSslSocketFactory(sslSocketFactory);
        }
    }

    /**
     * 初始化SDCard文件目录和私有文件目录
     *
     * @param context
     */
    public static void init(Context context) {
        File file = context.getExternalFilesDir(null);
        if (file != null) {
            SDCARD_FILES_ROOT_DIR = file.getPath();
        }
        DATA_ROOT_DIR = context.getFilesDir().toString();
        CACHE_DIR = getDiskCacheDir(context);
        SDCARD_ROOT_DIR = getSdCardRootDir();
    }

    /**
     * 通过Context.getExternalFilesDir()方法可以获取到 SDCard/Android/data/你的应用的包名/files/ 目录，
     * 一般放一些长时间保存的数据
     * 通过Context.getExternalCacheDir()方法可以获取到 SDCard/Android/data/你的应用包名/cache/目录，
     * 一般存放临时缓存数据.如果使用上面的方法，当你的应用在被用户卸载后，
     * SDCard/Android/data/你的应用的包名/ 这个目录下的所有文件都会被删除，不会留下垃圾信息
     *
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cacheDir;
        if (hasSDCard() || !Environment.isExternalStorageRemovable()) {
            cacheDir = context.getExternalCacheDir().toString();
        } else {
            cacheDir = context.getCacheDir().toString();
        }
        return cacheDir;
    }

    /**
     * 判断SDCard是否正常挂载
     *
     * @return
     */
    private static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSdCardRootDir() {
        String sdCardDir;
        if (hasSDCard()) {
            File sdCardFile = Environment.getExternalStorageDirectory();
            if (sdCardFile != null) {
                sdCardDir = sdCardFile.getAbsolutePath();
            } else {
                return null;
            }
        } else {
            return null;
        }
        return sdCardDir;
    }


    private static String SDCARD_FILES_ROOT_DIR;
    private static String SDCARD_ROOT_DIR;
    private static String DATA_ROOT_DIR;
    private static String CACHE_DIR;
    private static final String FILES = "/files";
    private static final String RES = "/res";
    private static final String RECORD = "/record";
    private static final String APK = "/apk";
    private static final String ANDROID_RESOURCE = "android.resource://";


    /**
     * 根据指定文件夹目录和类型创建文件夹
     *
     * @param rootDir
     * @param type
     * @return
     */
    public static File getFilesDirectory(String rootDir, String type) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(SDCARD_FILES_ROOT_DIR)
                && TextUtils.equals(SDCARD_FILES_ROOT_DIR, rootDir)) {
            stringBuilder.append(SDCARD_FILES_ROOT_DIR).append(type);
        } else {
            stringBuilder.append(DATA_ROOT_DIR).append(type);
        }
        File destDir = new File(stringBuilder.toString());
        if (!destDir.exists()) {
            if (destDir.mkdirs()) {
            } else {
            }
        }
        return destDir;
    }

    /**
     * 获取SDCard文件目录下的apk目录
     *
     * @return
     */
    public static File getAPKDirectory() {
        return getFilesDirectory(SDCARD_FILES_ROOT_DIR, APK);
    }

    public static String getFileNameFromURL(String fileUrl) {
        if (fileUrl == null)
            return null;
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }

    private File mFile;

    private final double ONE_MILLION_BYTE = 1024 * 1024.0;

    private void downLoad(String apkUrl) {
        String fileName = getFileNameFromURL(apkUrl);
        //加载的文件名改成非数字开头
        fileName = "a" + fileName;
        RequestParams params = new RequestParams(apkUrl);
        File apkDirectory = getAPKDirectory();
        if (apkDirectory == null) {
            return;
        }
        params.setSaveFilePath(apkDirectory + "/" + fileName);
        params.setAutoRename(false);
        setSSL(params);
        params.setConnectTimeout(30);//21s超时
        //进度条
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
                Log.d(TAG, "onWaiting");
            }

            @Override
            public void onStarted() {
                Log.d(TAG, "onStarted");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.d(TAG, "onLoading current = " + current + " total = " + total);
                pb.setProgress((int) (100 * current / total));
            }

            @Override
            public void onSuccess(File file) {
                Log.d(TAG, "onSuccess");
                mFile = file;
                update(MainActivity.this, file);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "cex" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished");
            }
        });
    }

    /**
     * 兼容7.0和8.0安装的权限
     *
     * @param context
     * @param file
     */
    private void update(final Context context, final File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = context.getPackageManager().canRequestPackageInstalls();
            if (b) {
                installApp(context, file);
            } else {
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions((AppCompatActivity) context,
                        new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                        10001);
            }
        } else {
            installApp(context, file);
        }
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file
     */
    public void installApp(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(getUriForFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
        tvDesc.setText("安装完成");
        finish();
    }

    /**
     * 长按保存图片兼容7.0
     *
     * @param file
     * @return
     */
    public static Uri getUriForFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(MyApp.getInstance(), BuildConfig.APPLICATION_ID +
                    ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10001:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mFile == null) {
                        return;
                    }
                    installApp(this, mFile);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            //注意这个是8.0新API
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, 10002);
                        } catch (Exception e) {
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10002:
                if (mFile == null) {
                    return;
                }
                installApp(this, mFile);
                break;
            default:
                break;
        }
    }
}
