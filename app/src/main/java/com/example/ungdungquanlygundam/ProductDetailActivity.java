package com.example.ungdungquanlygundam;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog; // <-- THÊM IMPORT NÀY
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ungdungquanlygundam.adapter.ProductImageAdapter;
import com.example.ungdungquanlygundam.adapter.ReviewAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.CartItem;
import com.example.ungdungquanlygundam.model.Product;
import com.example.ungdungquanlygundam.model.Review;
import com.example.ungdungquanlygundam.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity implements ReviewAdapter.UserActionListener {

    private ViewPager2 viewPagerProductImages;
    private TextView tvProductName, tvProductPrice, tvProductStock, tvProductDescription;
    private Button btnBuyNow;
    private FloatingActionButton fabView3D;
    private GundamDbHelper dbHelper;
    private Product currentProduct ;
    private int productID = -1;
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    private RatingBar ratingBarInput;
    private EditText etCommentInput;
    private Button btnSubmitComment;
    private int currentUserId = -1;
    private View layoutSubmitReview;
    private TextView tvAverageRatingValue, tvTotalReviewsCount;
    private RatingBar rbAverageRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_product_detail);
        dbHelper = new GundamDbHelper(this);
        initViews();
        setupToolbar();

        Intent intent = getIntent();
        productID = intent.getIntExtra("PRODUCT_ID", -1);
        currentUserId = intent.getIntExtra("LOGGED_IN_USER_ID", -1);

        if (productID == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            populateProductData(productID);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (productID != -1) {
            populateProductData(productID);
        }
    }
    private void initViews() {
        viewPagerProductImages = findViewById(R.id.view_pager_product_images);
        tvProductName = findViewById(R.id.tv_product_name_detail);
        tvProductPrice = findViewById(R.id.tv_product_price_detail);
        tvProductStock = findViewById(R.id.tv_product_stock_detail);
        tvProductDescription = findViewById(R.id.tv_product_description_detail);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        fabView3D = findViewById(R.id.fab_view_3d);
        rvReviews = findViewById(R.id.rv_reviews);
        ratingBarInput = findViewById(R.id.rating_bar_input);
        etCommentInput = findViewById(R.id.et_comment_input);
        btnSubmitComment = findViewById(R.id.btn_submit_comment);
        layoutSubmitReview = findViewById(R.id.layout_submit_review);
        rbAverageRating = findViewById(R.id.rb_average_rating);
        tvAverageRatingValue = findViewById(R.id.tv_average_rating_value);
        tvTotalReviewsCount = findViewById(R.id.tv_total_reviews_count);

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_product_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void populateProductData(int productID) {
        currentProduct = dbHelper.getProductById(productID);
        if(currentProduct == null){
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvProductName.setText(currentProduct.getName());
        tvProductStock.setText("Số lượng trong kho: " + currentProduct.getStock());

        DecimalFormat formatter = new DecimalFormat("###,###,###đ");
        tvProductPrice.setText(formatter.format(currentProduct.getPrice()));
        tvProductDescription.setText(currentProduct.getDescription());

        List<String> imageList = new ArrayList<>();
        String imagePathString = currentProduct.getImagePath();

        if (imagePathString != null && !imagePathString.trim().isEmpty()) {
            String[] imagePaths = imagePathString.split("\\s*,\\s*");
            imageList.addAll(Arrays.asList(imagePaths));
        } else {
            imageList.add("default_placeholder");
        }
        ProductImageAdapter imageAdapter = new ProductImageAdapter(this, imageList);
        viewPagerProductImages.setAdapter(imageAdapter);

        loadReviews();
        updateReviewSummary();
        if (currentUserId != -1 && !dbHelper.hasUserReviewedProduct(currentUserId, productID)) {
            layoutSubmitReview.setVisibility(View.VISIBLE);
        } else {
            layoutSubmitReview.setVisibility(View.GONE);
        }
        btnBuyNow.setOnClickListener(v -> {
            if (currentProduct == null) return;
            if (currentUserId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            AddToCartBottomSheetFragment quantitySheet = AddToCartBottomSheetFragment.newInstance(currentProduct, currentUserId);
            quantitySheet.setButtonText("Tiếp Tục");
            quantitySheet.setOnBuyNowListener(quantitySelected -> {
                Product checkProduct = dbHelper.getProductById(productID);
                if (checkProduct.getStock() < quantitySelected) {
                    Toast.makeText(this, "Số lượng trong kho không đủ", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<CartItem> buyNowItems = new ArrayList<>();
                CartItem singleItem = new CartItem(currentProduct, quantitySelected, false); // isSelected không quan trọng ở đây
                buyNowItems.add(singleItem);
                double buyNowTotalAmount = checkProduct.getPrice() * quantitySelected;
                CheckoutBottomSheetFragment checkoutSheet = CheckoutBottomSheetFragment.newInstance(
                        currentUserId,
                        buyNowTotalAmount,
                        buyNowItems,
                        true
                );
                checkoutSheet.setOrderPlacedListener(() -> {
                    Toast.makeText(this, "Mua hàng thành công!", Toast.LENGTH_LONG).show();
                    populateProductData(productID);
                });

                checkoutSheet.show(getSupportFragmentManager(), "CheckoutFromBuyNow");
            });

            quantitySheet.show(getSupportFragmentManager(), "SelectQuantitySheet");

        });

        findViewById(R.id.btn_add_to_cart).setOnClickListener(v -> {
            if (currentProduct != null) {
                AddToCartBottomSheetFragment bottomSheet =
                        AddToCartBottomSheetFragment.newInstance(currentProduct, currentUserId);
                bottomSheet.show(getSupportFragmentManager(), "AddToCartBottomSheet");
            }
        });

        if (currentProduct.getModelPath() != null && !currentProduct.getModelPath().isEmpty()) {
            fabView3D.setVisibility(View.GONE);
            fabView3D.setOnClickListener(v -> {
                Intent intent = new Intent(this, Model3DViewerActivity.class);
                String fullModelPath = "models/" + currentProduct.getModelPath();
                intent.putExtra("MODEL_PATH", fullModelPath);
                startActivity(intent);
            });
        } else {
            fabView3D.setVisibility(View.GONE);
        }

        btnSubmitComment.setOnClickListener(v -> submitReview());
    }
    private void loadReviews() {
        if (currentProduct != null) {
            reviewList.clear();
            reviewList.addAll(dbHelper.getReviewsForProduct(currentProduct.getId()));

            if (reviewAdapter == null) {
                reviewAdapter = new ReviewAdapter(this, reviewList, currentUserId, this);
                rvReviews.setLayoutManager(new LinearLayoutManager(this));
                rvReviews.setAdapter(reviewAdapter);
            } else {
                reviewAdapter.notifyDataSetChanged();
            }
        }
    }
    private void submitReview() {
        float rating = ratingBarInput.getRating();
        String comment = etCommentInput.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không xác định được người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.addReview(currentProduct.getId(), currentUserId, rating, comment);

        if (result != -1) {
            Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
            etCommentInput.setText("");
            ratingBarInput.setRating(0);
            loadReviews();
            updateReviewSummary();
            layoutSubmitReview.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Gửi đánh giá thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditClick(Review review) {
        showEditReviewDialog(review);
    }
    private void showEditReviewDialog(final Review reviewToEdit){

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_review, null);

        final RatingBar dialogRatingBar = view.findViewById(R.id.dialog_rating_bar_edit);
        final EditText dialogEtComment = view.findViewById(R.id.dialog_et_comment_edit);

        dialogRatingBar.setRating(reviewToEdit.getRating());
        dialogEtComment.setText(reviewToEdit.getComment());

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa đánh giá")
                .setView(view)
                .setPositiveButton("Lưu", (dialogInterface, i) -> {
                    float newRating = dialogRatingBar.getRating();
                    String newComment = dialogEtComment.getText().toString().trim();

                    if (newRating == 0) {
                        Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean isUpdated = dbHelper.updateReview(reviewToEdit.getId(), newRating, newComment);

                    if (isUpdated) {
                        Toast.makeText(this, "Cập nhật đánh giá thành công!", Toast.LENGTH_SHORT).show();
                        loadReviews();
                        updateReviewSummary();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    @Override
    public void onDeleteClick(int reviewId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa đánh giá này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean isDeleted = dbHelper.deleteReview(reviewId);
                    if (isDeleted) {
                        Toast.makeText(this, "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();
                        loadReviews();
                        updateReviewSummary();
                        if (currentUserId != -1 && !dbHelper.hasUserReviewedProduct(currentUserId, productID)) {
                            layoutSubmitReview.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void updateReviewSummary() {
        if (productID == -1) return;
        float[] summary = dbHelper.getReviewSummary(productID);
        float averageRating = summary[0];
        int totalReviews = (int) summary[1];
        rbAverageRating.setRating(averageRating);
        tvAverageRatingValue.setText(String.format("%.1f", averageRating));
        tvTotalReviewsCount.setText("(" + totalReviews + " đánh giá)");
    }
}
