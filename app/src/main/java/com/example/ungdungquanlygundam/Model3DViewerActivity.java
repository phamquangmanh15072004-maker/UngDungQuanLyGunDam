package com.example.ungdungquanlygundam;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Model3DViewerActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model3_dviewer);

        // --- Ánh xạ và cài đặt Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar_3d_viewer);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- Ánh xạ WebView và ProgressBar ---
        webView = findViewById(R.id.web_view_3d);
        progressBar = findViewById(R.id.progress_bar_3d);

        // --- Lấy đường dẫn model từ Intent ---
        // Ví dụ: modelPath = "models/gundam_exia.glb"
        String modelPath = getIntent().getStringExtra("MODEL_PATH");

        // --- Cài đặt WebView ---
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Bắt buộc để chạy Javascript
        webSettings.setAllowFileAccess(true);   // Cho phép truy cập file trong assets

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Bắt đầu tải trang HTML, hiện ProgressBar
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Trang HTML đã tải xong, bây giờ gọi Javascript để load model
                // Đường dẫn phải có dạng "file:///android_asset/models/gundam_exia.glb"
                String modelUrl = "file:///android_asset/" + modelPath;
                view.loadUrl("javascript:loadModel('" + modelUrl + "')");

                // Ẩn ProgressBar sau khi đã gọi load model
                // (Thư viện model-viewer sẽ có hiệu ứng tải của riêng nó)
                progressBar.setVisibility(View.GONE);
            }
        });

        // Tải file viewer.html từ thư mục assets
        webView.loadUrl("file:///android_asset/viewer.html");
    }
    @Override
    protected void onDestroy() {
        if (webView != null) {
            // Lấy view cha của webView
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                // Xóa webView khỏi view cha để tránh lỗi
                parent.removeView(webView);
            }
            // Dừng tải, xóa cache, xóa lịch sử và hủy webView
            webView.stopLoading();
            webView.clearCache(true);
            webView.clearHistory();
            webView.destroy();
            webView = null; // Gán lại là null để giải phóng bộ nhớ
        }
        super.onDestroy();
    }
}
