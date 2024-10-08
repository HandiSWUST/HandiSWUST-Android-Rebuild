package org.shirakawatyu.swust;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import org.shirakawatyu.swust.widget.CourseWidget;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private ProgressBar progressBar;
    private long exitTime = 0;
    private Context mContext;
    private static final int PRESS_BACK_EXIT_GAP = 2000;
    private boolean ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        // 状态栏文字暗色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        // 绑定控件
        initView();
        // 初始化 WebView
        initWeb();
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (ready) {
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
    }

    /**
     * 绑定控件
     */
    private void initView() {
        webView = findViewById(R.id.webView);
        webView.getSettings().setTextZoom(100);
        progressBar = findViewById(R.id.progressBar);
    }


    public static final String TAG = "w";

    /**
     * 初始化 web
     */

    private void initWeb() {
        // 重写 WebViewClient
        webView.setWebViewClient(new MkWebViewClient());
        // 重写 WebChromeClient
        webView.setWebChromeClient(new MkWebChromeClient());

        CookieManager instance = CookieManager.getInstance();
        instance.setAcceptCookie(true);
        configWebView();


        // 加载首页，没有网的话就用本地缓存
        new Thread(() -> {
            try {
                InetAddress.getAllByName(Uri.parse(getResources().getString(R.string.home_url)).getHost());
            } catch (UnknownHostException e) {
                runOnUiThread(() -> webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY));
                Log.d("Initialing Webview", "initWeb: Using local cache");
            }
            runOnUiThread(() -> webView.loadUrl(getResources().getString(R.string.home_url)));
        }).start();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configWebView() {
        WebSettings settings = webView.getSettings();
        // 启用 js 功能
        settings.setJavaScriptEnabled(true);
        // 设置浏览器 UserAgent
        settings.setUserAgentString(settings.getUserAgentString() + " handiSWUST/" + getVerName(mContext));

        // 将图片调整到适合 WebView 的大小
        settings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);

        // 支持缩放，默认为true。是下面那个的前提。
        settings.setSupportZoom(false);
        // 设置内置的缩放控件。若为false，则该 WebView 不可缩放
        settings.setBuiltInZoomControls(false);
        // 隐藏原生的缩放控件
        settings.setDisplayZoomControls(false);

        // 缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置可以访问文件
        settings.setAllowFileAccess(true);
        // 支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        // 设置默认编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 本地存储
        settings.setDomStorageEnabled(true);

        // 资源混合模式
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

    }


    /**
     * 重写 WebViewClient
     */
    private class MkWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // 网页开始加载，显示进度条
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // 网页加载完毕，隐藏进度条
            progressBar.setVisibility(View.INVISIBLE);
            if (!ready) {
                setData();
                CookieManager.getInstance().flush();
                ready = true;
            }
            super.onPageFinished(view, url);
        }

        public void setData() {
            // 设置版本号
            webView.evaluateJavascript("window.localStorage.setItem('version', '" + getResources().getString(R.string.version) + "')", value -> {
            });
            // 从本地缓存读取课程表
            webView.evaluateJavascript("window.localStorage.getItem('lessons')", value -> {
                if (value != null) {
                    value = value.replace("\"[{", "[{").replace("}]\"", "}]").replace("\\", "");
                    if ("null".equals(value)) {
                        value = "[]";
                    }
                    SharedPreferences courses = getSharedPreferences("courses", MODE_PRIVATE);
                    SharedPreferences.Editor edit = courses.edit();
                    edit.putString("week_courses", value);
                    edit.apply();
                }
            });
            // 从本地缓存读取课表所属周数
            webView.evaluateJavascript("window.localStorage.getItem('isLogin')", status -> {
                webView.evaluateJavascript("window.localStorage.getItem('cur')", value -> {
                    if (value != null) {
                        if ("null".equals(value)) {
                            value = "0";
                            String s = status.replace("\"", "");
                            if (s.equals("true")) {
                                if (Objects.equals(webView.getUrl(), getResources().getString(R.string.home_url) + "course")) {
                                    webView.reload();
                                }
                            } else if (s.equals("null")) {
                                webView.reload();
                            }
                        }
                        SharedPreferences courses = getSharedPreferences("courses", MODE_PRIVATE);
                        SharedPreferences.Editor edit = courses.edit();
                        edit.putString("cur", value);
                        edit.apply();
                        Intent intent = new Intent();
                        intent.setAction("android.appwidget.action.FORCE_UPDATE");
                        intent.setComponent(new ComponentName(mContext, CourseWidget.class));
                        sendBroadcast(intent);
                    }
                });
            });
        }

    }

    /**
     * 重写 WebChromeClient
     */
    private class MkWebChromeClient extends WebChromeClient {
        private final static int WEB_PROGRESS_MAX = 100;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            // 加载进度变动，刷新进度条
            progressBar.setProgress(newProgress);
            if (newProgress == WEB_PROGRESS_MAX) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d(TAG, "onConsoleMessage: " + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }
    }

    /**
     * 返回按钮处理
     */
    @Override
    public void onBackPressed() {
        // 能够返回则返回上一页
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > PRESS_BACK_EXIT_GAP) {
                // 连点两次退出程序
                Toast.makeText(mContext, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }

        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return 当前版本名称
     */
    private static String getVerName(Context context) {
        try {
            return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "getVerName: " + e);
        }
        return "unknown";
    }
}
