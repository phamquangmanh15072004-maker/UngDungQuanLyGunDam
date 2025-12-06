package com.example.ungdungquanlygundam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.adapter.ImagePreviewAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Product;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditProductActivity extends AppCompatActivity implements ImagePreviewAdapter.OnImageRemoveListener {

    private MaterialToolbar toolbar;
    private TextInputEditText etName, etDescription, etPrice, etStock, etModelPath;
    private AutoCompleteTextView spinnerCategoryExposed;
    private Button btnSave, btnAddImage;
    private RecyclerView rvImagePreviews;
    private ImagePreviewAdapter imageAdapter;

    private List<Uri> selectedImageUris = new ArrayList<>();
    private GundamDbHelper dbHelper;
    private String mode = "ADD";
    private int productIdToEdit = -1;
    private Product currentProduct;
    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        initViews();
        dbHelper = new GundamDbHelper(this);

        setupImagePreviews();
        setupToolbar();
        setupCategorySpinner();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PRODUCT_ID)) {
            mode = "EDIT";
            toolbar.setTitle("Sửa sản phẩm");
            productIdToEdit = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
            if (productIdToEdit != -1) {
                loadProductDataForEdit(productIdToEdit);
            }
        } else {
            mode = "ADD";
            toolbar.setTitle("Thêm sản phẩm mới");
        }

        btnSave.setOnClickListener(v -> saveProduct());
        btnAddImage.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            selectedImageUris.add(imageUri);
            imageAdapter.notifyItemInserted(selectedImageUris.size() - 1);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_add_edit);
        etName = findViewById(R.id.et_product_name);
        etDescription = findViewById(R.id.et_product_description);
        etPrice = findViewById(R.id.et_product_price);
        etStock = findViewById(R.id.et_product_stock);
        spinnerCategoryExposed = findViewById(R.id.spinner_category_exposed);
        btnSave = findViewById(R.id.btn_save_product);
        btnAddImage = findViewById(R.id.btn_add_image);
        etModelPath = findViewById(R.id.et_product_model_path);
    }

    private void setupImagePreviews() {
        rvImagePreviews = findViewById(R.id.rv_image_previews);
        imageAdapter = new ImagePreviewAdapter(this, selectedImageUris, this);
        rvImagePreviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImagePreviews.setAdapter(imageAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        String[] categories = getResources().getStringArray(R.array.product_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerCategoryExposed.setAdapter(adapter);
    }

    private void loadProductDataForEdit(int productId) {
        Product productToEdit = dbHelper.getProductById(productId);
        if (productToEdit != null) {
            this.currentProduct = productToEdit;

            etName.setText(productToEdit.getName());
            etDescription.setText(productToEdit.getDescription());
            etPrice.setText(String.valueOf(productToEdit.getPrice()));
            etStock.setText(String.valueOf(productToEdit.getStock()));
            spinnerCategoryExposed.setText(productToEdit.getCategory(), false);
            etModelPath.setVisibility(View.GONE);

            String imagePathString = productToEdit.getImagePath();
            if (imagePathString != null && !imagePathString.isEmpty()) {
                List<String> uriStrings = Arrays.asList(imagePathString.split(","));
                selectedImageUris.clear();
                for (String uriString : uriStrings) {
                    selectedImageUris.add(Uri.parse(uriString));
                }
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String category = spinnerCategoryExposed.getText().toString();
        String modelPath = etModelPath.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Tên, Giá và Số lượng", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá và Số lượng phải là số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder imagePathBuilder = new StringBuilder();
        for (int i = 0; i < selectedImageUris.size(); i++) {
            imagePathBuilder.append(selectedImageUris.get(i).toString());
            if (i < selectedImageUris.size() - 1) {
                imagePathBuilder.append(",");
            }
        }
        String imagePath = imagePathBuilder.toString();

        if ("EDIT".equals(mode)) {
            if (currentProduct != null) {
                Product updatedProduct = new Product(
                        currentProduct.getId(), name, description, price, imagePath, stock, category, modelPath
                );

                int rowsAffected = dbHelper.updateProduct(updatedProduct);
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Product newProduct = new Product(0, name, description, price, imagePath, stock, category, modelPath);
            boolean isSuccess = dbHelper.addProduct(newProduct);
            if (isSuccess) {
                Toast.makeText(this, "Đã thêm sản phẩm mới thành công!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Thêm sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onImageRemoved(int position) {
        if (position >= 0 && position < selectedImageUris.size()) {
            selectedImageUris.remove(position);
            imageAdapter.notifyItemRemoved(position);
            imageAdapter.notifyItemRangeChanged(position, selectedImageUris.size());
        }
    }
}
