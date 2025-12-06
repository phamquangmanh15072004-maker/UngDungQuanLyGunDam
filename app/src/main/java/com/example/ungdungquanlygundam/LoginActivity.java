package com.example.ungdungquanlygundam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private GundamDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo DbHelper
        dbHelper = new GundamDbHelper(this);

        // Ánh xạ các view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Ẩn TextView "Quên mật khẩu" vì chúng ta không dùng Firebase
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setVisibility(View.GONE);
        dbHelper.addDummyUsers();
        // --- Xử lý sự kiện ---

        // Khi nhấn nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            loginUser();
        });
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
    private void loginUser() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle userData = dbHelper.checkUser(username, password);

        if (userData != null) {
            int userId = userData.getInt("USER_ID");
            User user = dbHelper.getUserById(userId);
            int role = userData.getInt("ROLE");
            String userName = user.getUsername();
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            Bundle options = android.app.ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            ).toBundle();
            Intent intent;
            if (role == 1) {
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }
            intent.putExtra("LOGGED_IN_USER_ID", userId);
            intent.putExtra("USER_NAME", userName);
            startActivity(intent, options);
            finish();
        } else {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

}
