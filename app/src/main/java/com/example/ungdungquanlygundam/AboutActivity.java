package com.example.ungdungquanlygundam;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.adapter.CategoryAdapter;
import java.util.Arrays;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvCategories;
    // Không cần GundamDbHelper và RecyclerView cho sản phẩm nữa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();
        setupToolbar();
        loadCategories(); // Chỉ cần tải danh mục giải thích
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_about);
        rvCategories = findViewById(R.id.rv_categories);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadCategories() {
        // Logic này giữ nguyên, nó đã rất hoàn hảo rồi
        List<String> categoryNames = Arrays.asList("HG", "RG", "MG", "PG", "SD");
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryNames);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(categoryAdapter);
        rvCategories.setNestedScrollingEnabled(false);

        categoryAdapter.setOnItemClickListener(categoryName -> {
            String title = "";
            String message = "";

            switch (categoryName) {
                case "HG":
                    title = "HG - High Grade (Tỷ lệ 1/144)";
                    message = "Là dòng Gunpla phổ biến và đa dạng nhất, phù hợp cho người mới bắt đầu. Các mô hình HG có độ chi tiết tốt, biên độ cử động linh hoạt và giá cả phải chăng.";
                    break;
                case "RG":
                    title = "RG - Real Grade (Tỷ lệ 1/144)";
                    message = "Dù cùng tỷ lệ 1/144 với HG, RG có độ chi tiết đáng kinh ngạc, gần như một phiên bản thu nhỏ của PG. RG có khung xương bên trong (inner frame), nhiều chi tiết và decal phức tạp.";
                    break;
                case "MG":
                    title = "MG - Master Grade (Tỷ lệ 1/100)";
                    message = "Là dòng sản phẩm cao cấp với kích thước lớn, độ chi tiết cao và khung xương phức tạp. MG thường có buồng lái và figure phi công đi kèm, là lựa chọn yêu thích của nhiều người chơi kinh nghiệm.";
                    break;
                case "PG":
                    title = "PG - Perfect Grade (Tỷ lệ 1/60)";
                    message = "Dòng sản phẩm đỉnh cao nhất của Gunpla. PG có kích thước khổng lồ, độ chi tiết, kỹ thuật và cơ khí phức tạp bậc nhất. Một số mẫu PG còn được tích hợp cả đèn LED.";
                    break;
                case "SD":
                    title = "SD - Super Deformed";
                    message = "Là dòng Gunpla có thiết kế 'chibi' (đầu to, thân nhỏ) rất đáng yêu. Lắp ráp đơn giản, không cần nhiều dụng cụ, phù hợp để giải trí nhanh hoặc cho trẻ em.";
                    break;
            }

            if (!title.isEmpty()) {
                showCategoryInfo(title, message);
            }
        });
    }

    private void showCategoryInfo(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đã hiểu", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
