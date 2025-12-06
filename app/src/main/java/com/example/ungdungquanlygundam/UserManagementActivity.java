package com.example.ungdungquanlygundam;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.adapter.UserAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.User; // SỬA LẠI: Dùng model.User của bạn

import java.util.List;


public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener, UserAdapter.OnUserLongClickListener {
    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private GundamDbHelper dbHelper;
    private TextView tvEmptyUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        dbHelper = new GundamDbHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar_user_management);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        rvUsers = findViewById(R.id.rv_users);
        tvEmptyUsers = findViewById(R.id.tv_empty_users);
        loadUsers();
    }

    private void loadUsers() {
        userList = dbHelper.getAllUsers();

        if (userList != null && !userList.isEmpty()) {
            rvUsers.setVisibility(View.VISIBLE);
            tvEmptyUsers.setVisibility(View.GONE);

            userAdapter = new UserAdapter(this, userList, this,this);
            rvUsers.setAdapter(userAdapter);
        } else {
            rvUsers.setVisibility(View.GONE);
            tvEmptyUsers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUserClick(User user) {
        if (user.getRole() == 1) {
            Toast.makeText(this, "Không thể thay đổi vai trò của Admin.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] roles = {"User", "Admin"};
        final int[] roleValues = {0, 1};
        int currentRoleIndex = (user.getRole() == 1) ? 1 : 0;
        new AlertDialog.Builder(this)
                .setTitle("Chọn vai trò cho " + user.getUsername())
                .setSingleChoiceItems(roles, currentRoleIndex, (dialog, which) -> {
                    int newRole = roleValues[which];

                    if (newRole == user.getRole()) {
                        dialog.dismiss();
                        return;
                    }

                    boolean success = dbHelper.updateUserRole(user.getId(), newRole);

                    if (success) {
                        Toast.makeText(this, "Cập nhật vai trò thành công!", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    @Override
    public void onUserLongClick(User user) {
        if (user.getRole() == 1) {
            Toast.makeText(this, "Không thể xóa tài khoản Admin.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản '" + user.getUsername() + "' không?\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean success = dbHelper.deleteUser(user.getId());
                    if (success) {
                        Toast.makeText(this, "Đã xóa tài khoản: " + user.getUsername(), Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Xóa tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
