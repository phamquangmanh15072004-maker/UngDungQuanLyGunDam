package com.example.ungdungquanlygundam;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.adapter.ProductAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Product;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementActivity extends AppCompatActivity implements ProductAdapter.OnProductListener{

    // Khai báo các thành phần UI
    private MaterialToolbar toolbar;
    private SearchView searchView;
    private ChipGroup categoryChipGroup;
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private RelativeLayout paginationControls;
    private ImageButton btnPrevPage, btnNextPage;
    private TextView tvPageInfo;
    private FloatingActionButton fabAddProduct;

    // Khai báo các thành phần xử lý dữ liệu
    private GundamDbHelper dbHelper;
    private ProductAdapter adapter;
    private List<Product> productList;
    private TextView tvProductCount;
    private int totalProducts = 0;
    private String currentSearchQuery = "";
    private String currentCategory = "Tất cả";
    private int currentPage = 1;
    private int totalPages = 1;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private static final int EDIT_PRODUCT_REQUEST = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);
        initViews();
        initObjects();
        setupUIListeners();
        resetAndLoadFirstPage();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_product_management);
        searchView = findViewById(R.id.search_view);
        categoryChipGroup = findViewById(R.id.category_chip_group);
        rvProducts = findViewById(R.id.rv_products);
        progressBar = findViewById(R.id.progress_bar);
        paginationControls = findViewById(R.id.pagination_controls);
        btnPrevPage = findViewById(R.id.btn_prev_page);
        btnNextPage = findViewById(R.id.btn_next_page);
        tvPageInfo = findViewById(R.id.tv_page_info);
        fabAddProduct = findViewById(R.id.fab_add_product);
        tvProductCount = findViewById(R.id.tv_product_count);
    }

    private void initObjects() {
        dbHelper = new GundamDbHelper(this);
        productList = new ArrayList<>();
        dbHelper.addDummyProducts();
        adapter = new ProductAdapter(this, productList,this);

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PRODUCT_REQUEST && resultCode == Activity.RESULT_OK) {
            loadProductsForPage(currentPage);
        }
    }
    @Override
    public void onEditClick(Product product) {
        Intent intent = new Intent(ProductManagementActivity.this, EditProductActivity.class);
        intent.putExtra("PRODUCT_ID", product.getId());
        startActivityForResult(intent, EDIT_PRODUCT_REQUEST);
    }

    @Override
    public void onDeleteClick(Product product) {
        // Tạo một AlertDialog.Builder
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa") // Tiêu đề hộp thoại
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getName() + "\"?") // Nội dung
                .setIcon(android.R.drawable.ic_dialog_alert) // Icon cảnh báo
                .setPositiveButton("Có", (dialog, which) -> {
                    boolean isSuccess = dbHelper.deleteProduct(product.getId());

                    if (isSuccess) {
                        Toast.makeText(this, "Đã xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        loadProductsForPage(currentPage);
                    } else {
                        Toast.makeText(this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }
    private void setupUIListeners() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query.trim();
                resetAndLoadFirstPage();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && !currentSearchQuery.isEmpty()) {
                    currentSearchQuery = "";
                    resetAndLoadFirstPage();
                }
                return true;
            }
        });

        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = group.findViewById(checkedId);
            if (selectedChip != null && selectedChip.isChecked()) {
                currentCategory = selectedChip.getText().toString();
                resetAndLoadFirstPage();
            }
        });
        btnPrevPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadProductsForPage(currentPage);
            }
        });
        btnNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadProductsForPage(currentPage);
            }
        });
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProductActivity.class);
            startActivityForResult(intent, EDIT_PRODUCT_REQUEST);
        });
    }

    private void resetAndLoadFirstPage() {
        currentPage = 1;
        loadProductsForPage(currentPage);
    }

    private void calculateTotalPages() {
         this.totalProducts = dbHelper.getTotalProductCount(currentSearchQuery, currentCategory);
        if (totalProducts == 0) {
            totalPages = 1;
        } else {
            totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        }
    }

    private void loadProductsForPage(int page) {progressBar.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.GONE);
        paginationControls.setVisibility(View.INVISIBLE);
        tvProductCount.setVisibility(View.GONE);

        calculateTotalPages();


        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            int offset = (page - 1) * PAGE_SIZE;
            List<Product> newProducts = dbHelper.getProductsWithPagination(PAGE_SIZE, offset, currentSearchQuery, currentCategory);
            productList.clear();
            productList.addAll(newProducts);
            adapter.notifyDataSetChanged();
            if (totalProducts > 0) {
                int startItem = (currentPage - 1) * PAGE_SIZE + 1;
                String countText = "Tổng có " + totalProducts + " sản phẩm";
                tvProductCount.setText(countText);
                tvProductCount.setVisibility(View.VISIBLE);
            } else {tvProductCount.setText("Không tìm thấy sản phẩm nào phù hợp.");
                tvProductCount.setVisibility(View.VISIBLE);
            }
            rvProducts.scrollToPosition(0);

            progressBar.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
            updatePaginationUI();

        }, 500);
    }




    private void updatePaginationUI() {
        tvPageInfo.setText(String.format("Trang %d / %d", currentPage, totalPages));
        btnPrevPage.setEnabled(currentPage > 1);
        btnPrevPage.setAlpha(currentPage > 1 ? 1.0f : 0.5f);
        btnNextPage.setEnabled(currentPage < totalPages);
        btnNextPage.setAlpha(currentPage < totalPages ? 1.0f : 0.5f);
        paginationControls.setVisibility(totalPages > 1 || productList.size() > 0 ? View.VISIBLE : View.INVISIBLE);
    }


}

