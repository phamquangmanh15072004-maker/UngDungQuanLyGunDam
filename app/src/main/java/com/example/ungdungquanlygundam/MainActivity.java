package com.example.ungdungquanlygundam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.adapter.ProductUserAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Product;
import com.example.ungdungquanlygundam.model.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductUserAdapter.OnProductClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private SearchView searchView;
    private ChipGroup categoryChipGroup;
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private RelativeLayout paginationControls;
    private ImageButton btnPrevPage, btnNextPage;
    private TextView tvPageInfo;
    private TextView tvProductCount;

    private GundamDbHelper dbHelper;
    private ProductUserAdapter adapter;
    private List<Product> productList;

    private String currentSearchQuery = "";
    private String currentCategory = "Tất cả";
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalProducts = 0;
    private static final int PAGE_SIZE = 10;

    private ActivityResultLauncher<Intent> productDetailLauncher;
    private int loggedInUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initObjects();
        setupUIListeners();
        resetAndLoadFirstPage();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        categoryChipGroup = findViewById(R.id.category_chip_group_user);
        rvProducts = findViewById(R.id.rv_products_user);
        progressBar = findViewById(R.id.progress_bar);
        paginationControls = findViewById(R.id.pagination_controls);
        btnPrevPage = findViewById(R.id.btn_prev_page);
        btnNextPage = findViewById(R.id.btn_next_page);
        tvPageInfo = findViewById(R.id.tv_page_info);
        tvProductCount = findViewById(R.id.tv_product_count_user);
        searchView = findViewById(R.id.search_view);
    }

    private void initObjects() {
        dbHelper = new GundamDbHelper(this);
        productList = new ArrayList<>();
        adapter = new ProductUserAdapter(this, productList, this);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOGGED_IN_USER_ID")) {
            loggedInUserId = intent.getIntExtra("LOGGED_IN_USER_ID", -1);
        }
        // Khởi tạo ActivityResultLauncher
        productDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                }
        );
    }

    private void setupUIListeners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setupUserInNavHeader();
        setupSearchViewListener();
        categoryChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                Chip selectedChip = group.findViewById(checkedId);
                if (selectedChip != null) {
                    String selectedCategory = selectedChip.getText().toString();
                    if (!selectedCategory.equals(currentCategory)) {
                        currentCategory = selectedCategory;
                        resetAndLoadFirstPage();
                    }
                }
            }
        });

        btnPrevPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadProductsForPage(currentPage);
            }
        });

        // Sự kiện cho nút "Trang sau"
        btnNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadProductsForPage(currentPage);
            }
        });
    }

    private void setupSearchViewListener() {
        if (searchView != null) {
            searchView.setQueryHint("Tìm kiếm sản phẩm...");
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
                    if (newText.trim().isEmpty() && !currentSearchQuery.isEmpty()) {
                        currentSearchQuery = "";
                        resetAndLoadFirstPage();
                    }
                    return true;
                }
            });
        }
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

    private void loadProductsForPage(int page) {
        progressBar.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.GONE);
        paginationControls.setVisibility(View.INVISIBLE);
        tvProductCount.setVisibility(View.GONE);

        calculateTotalPages();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int offset = (page - 1) * PAGE_SIZE;
            List<Product> newProducts = dbHelper.getProductsWithPagination(PAGE_SIZE, offset, currentSearchQuery, currentCategory);

            productList.clear();
            productList.addAll(newProducts);

            if (totalProducts > 0) {
                tvProductCount.setText("Tìm thấy " + totalProducts + " sản phẩm");
                tvProductCount.setVisibility(View.VISIBLE);
            } else {
                tvProductCount.setText("Không tìm thấy sản phẩm nào phù hợp");
                tvProductCount.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
            rvProducts.scrollToPosition(0);
            adapter.notifyDataSetChanged();
            updatePaginationUI();
        }, 500);
    }

    private void updatePaginationUI() {
        if (totalProducts > 0) {
            paginationControls.setVisibility(View.VISIBLE);
        } else {
            paginationControls.setVisibility(View.INVISIBLE);
        }

        tvPageInfo.setText(String.format("Trang %d / %d", currentPage, totalPages));
        btnPrevPage.setEnabled(currentPage > 1);
        btnPrevPage.setAlpha(currentPage > 1 ? 1.0f : 0.5f);
        btnNextPage.setEnabled(currentPage < totalPages);
        btnNextPage.setAlpha(currentPage < totalPages ? 1.0f : 0.5f);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
        intent.putExtra("PRODUCT_ID", product.getId());
        if (loggedInUserId != -1) {
            intent.putExtra("LOGGED_IN_USER_ID", loggedInUserId);
        }
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cart){
            if (loggedInUserId != -1) {
                Intent intent = new Intent(this, CartActivity.class);
                intent.putExtra("LOGGED_IN_USER_ID", loggedInUserId);
                startActivity(intent);
            } else {
                // Trường hợp không xác định được người dùng
                Toast.makeText(this, "Lỗi: Không thể mở giỏ hàng.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.nav_orders){
            if (loggedInUserId != -1) {
                // Mở màn hình "Đơn hàng của tôi"
                Intent intent = new Intent(this, MyOrderActivity.class);
                intent.putExtra("LOGGED_IN_USER_ID", loggedInUserId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if (id == R.id.nav_profile){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USER_ID", loggedInUserId);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout) logout();
        else if (id == R.id.nav_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
    }

    private void setupUserInNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tv_username);
        TextView tvUserEmail = headerView.findViewById(R.id.tv_user_email);
        int userId = getIntent().getIntExtra("LOGGED_IN_USER_ID", -1);
        if (userId != -1) {
            User currentUser = dbHelper.getUserById(userId);
            if (currentUser != null) {
                tvUserName.setText(currentUser.getUsername());
                tvUserEmail.setText(currentUser.getEmail());
            }
        }
    }
}
