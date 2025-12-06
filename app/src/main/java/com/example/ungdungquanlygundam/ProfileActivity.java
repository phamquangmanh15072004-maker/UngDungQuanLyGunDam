package com.example.ungdungquanlygundam;

import android.os.Bundle;
import android.text.TextUtils; // Import thêm
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.User;
import com.google.android.material.textfield.TextInputEditText;


public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText etUsername, etOldPassword, etNewPassword, etConfirmPassword, etEmail, etPhone, etAddress;
    private Button btnUpdate;
    private GundamDbHelper dbHelper;
    private User currentUser;
    private int loggedInUserId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        dbHelper = new GundamDbHelper(this);
        // Lấy ID người dùng từ Intent
        loggedInUserId = getIntent().getIntExtra("LOGGED_IN_USER_ID", -1);
       if(loggedInUserId == -1){
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_profile);
        etUsername = findViewById(R.id.et_profile_username);
        etOldPassword = findViewById(R.id.et_profile_old_password);
        etNewPassword = findViewById(R.id.et_profile_new_password);
        etConfirmPassword = findViewById(R.id.et_profile_confirm_password);

        etEmail = findViewById(R.id.et_profile_email);
        etPhone = findViewById(R.id.et_profile_phone);
        etAddress = findViewById(R.id.et_profile_address);
        btnUpdate = findViewById(R.id.btn_update_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút Back
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void loadUserData() {
        currentUser = dbHelper.getUserById(loggedInUserId);
        if (currentUser != null) {
            etUsername.setText(currentUser.getUsername());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhone());
            etAddress.setText(currentUser.getAddress());
        }
    }
    private void setupListeners() {
        btnUpdate.setOnClickListener(v -> updateUserProfile());
    }

    private void updateUserProfile() {
        // Lấy dữ liệu từ các trường EditText
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Lấy dữ liệu mật khẩu
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Biến cờ để kiểm tra có thay đổi mật khẩu không
        boolean isPasswordChangeAttempted = !TextUtils.isEmpty(oldPassword)
                || !TextUtils.isEmpty(newPassword)
                || !TextUtils.isEmpty(confirmPassword);

        // --- Bắt đầu khối kiểm tra và cập nhật mật khẩu ---
        if (isPasswordChangeAttempted) {
            // 1. Kiểm tra mật khẩu cũ
            if (!currentUser.getPassword().equals(oldPassword)) {
                etOldPassword.setError("Mật khẩu hiện tại không chính xác");
                etOldPassword.requestFocus();
                return;
            }

            // 2. Kiểm tra mật khẩu mới
            if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                etNewPassword.setError("Mật khẩu mới phải có ít nhất 6 ký tự");
                etNewPassword.requestFocus();
                return;
            }

            // 3. Kiểm tra xác nhận mật khẩu
            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                etConfirmPassword.requestFocus();
                return;
            }

            // Nếu tất cả đều hợp lệ, cập nhật mật khẩu mới cho đối tượng currentUser
            currentUser.setPassword(newPassword);
        }
        // --- Kết thúc khối kiểm tra và cập nhật mật khẩu ---


        // Kiểm tra tính hợp lệ của thông tin liên lạc
        if (email.isEmpty()) {
            etEmail.setError("Email không được để trống");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);
        boolean isUpdated = dbHelper.updateUser(currentUser);

        if (isUpdated) {
            Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
            // Xóa các trường mật khẩu sau khi cập nhật thành công
            etOldPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
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
