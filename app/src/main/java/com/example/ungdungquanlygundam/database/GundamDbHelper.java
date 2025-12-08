package com.example.ungdungquanlygundam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.ungdungquanlygundam.model.CartItem;
import com.example.ungdungquanlygundam.model.Order;
import com.example.ungdungquanlygundam.model.OrderDetail;
import com.example.ungdungquanlygundam.model.Product;
import com.example.ungdungquanlygundam.model.Review;
import com.example.ungdungquanlygundam.model.User; // Cần import User model

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class GundamDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gundam_store.db";
    private static final int DATABASE_VERSION = 6; // Giữ nguyên phiên bản

    // Bảng Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_ROLE = "role"; // 0 for User, 1 for Admin
    public static final String COLUMN_USER_ADDRESS = "user_address";
    public static final String COLUMN_USER_PHONE = "user_phone";

    // Bảng Products
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_IMAGE = "image_path";
    private static final String COLUMN_PRODUCT_STOCK = "stock";
    private static final String COLUMN_PRODUCT_CATEGORY = "category";
    private static final String COLUMN_PRODUCT_MODEL_PATH = "model_path";

    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_USER_ID = "order_user_id";
    public static final String COLUMN_ORDER_TOTAL_AMOUNT = "order_total_amount"; // TỔNG TIỀN
    public static final String COLUMN_ORDER_ADDRESS = "order_address";
    public static final String COLUMN_ORDER_PHONE = "order_phone";
    public static final String COLUMN_ORDER_STATUS = "order_status"; // "Chờ xác nhận", "Đã xác nhận", ...
    public static final String COLUMN_ORDER_DATE = "order_date";

    public static final String TABLE_REVIEWS = "reviews";
    public static final String COLUMN_REVIEW_ID = "review_id";
    public static final String COLUMN_REVIEW_PRODUCT_ID = "product_id";
    public static final String COLUMN_REVIEW_USER_ID = "user_id";
    public static final String COLUMN_REVIEW_RATING = "rating";
    public static final String COLUMN_REVIEW_COMMENT = "comment";

    public static final String TABLE_CART_ITEMS = "cart_items";
    public static final String COLUMN_CART_ID = "cart_id";
    public static final String COLUMN_CART_USER_ID = "cart_user_id";
    public static final String COLUMN_CART_PRODUCT_ID = "cart_product_id";
    public static final String COLUMN_CART_QUANTITY = "cart_quantity";
    public static final String COLUMN_REVIEW_DATE = "review_date";

    public static final String TABLE_ORDER_DETAILS = "order_details";
    public static final String COLUMN_DETAIL_ID = "detail_id";
    public static final String COLUMN_DETAIL_ORDER_ID = "detail_order_id";
    public static final String COLUMN_DETAIL_PRODUCT_ID = "detail_product_id";
    public static final String COLUMN_DETAIL_QUANTITY = "detail_quantity";
    public static final String COLUMN_DETAIL_PRICE = "detail_price";

    public GundamDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_ROLE + " INTEGER NOT NULL,"
                + COLUMN_USER_ADDRESS + " TEXT,"
                + COLUMN_USER_PHONE + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT,"
                + COLUMN_PRODUCT_PRICE + " REAL NOT NULL,"
                + COLUMN_PRODUCT_IMAGE + " TEXT,"
                + COLUMN_PRODUCT_STOCK + " INTEGER NOT NULL,"
                + COLUMN_PRODUCT_CATEGORY + " TEXT,"
                + COLUMN_PRODUCT_MODEL_PATH + " TEXT"
                + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ORDER_USER_ID + " INTEGER,"
                + COLUMN_ORDER_TOTAL_AMOUNT + " REAL,"
                + COLUMN_ORDER_ADDRESS + " TEXT,"
                + COLUMN_ORDER_PHONE + " TEXT,"
                + COLUMN_ORDER_STATUS + " TEXT,"
                + COLUMN_ORDER_DATE + " DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ");";
        db.execSQL(CREATE_TABLE_ORDERS);
        String CREATE_TABLE_REVIEWS = "CREATE TABLE " + TABLE_REVIEWS + "("
                + COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_REVIEW_PRODUCT_ID + " INTEGER,"
                + COLUMN_REVIEW_USER_ID + " INTEGER,"
                + COLUMN_REVIEW_RATING + " REAL NOT NULL,"
                + COLUMN_REVIEW_COMMENT + " TEXT,"
                + COLUMN_REVIEW_DATE + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + COLUMN_REVIEW_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "),"
                + "FOREIGN KEY(" + COLUMN_REVIEW_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ");";
        db.execSQL(CREATE_TABLE_REVIEWS);
        String CREATE_TABLE_CART_ITEMS = "CREATE TABLE " + TABLE_CART_ITEMS + "("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CART_USER_ID + " INTEGER,"
                + COLUMN_CART_PRODUCT_ID + " INTEGER,"
                + COLUMN_CART_QUANTITY + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_CART_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_CART_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")"
                + ");";
        db.execSQL(CREATE_TABLE_CART_ITEMS);
        String CREATE_TABLE_ORDER_DETAILS = "CREATE TABLE " + TABLE_ORDER_DETAILS + "("
                + COLUMN_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DETAIL_ORDER_ID + " INTEGER,"
                + COLUMN_DETAIL_PRODUCT_ID + " INTEGER,"
                + COLUMN_DETAIL_QUANTITY + " INTEGER,"
                + COLUMN_DETAIL_PRICE + " REAL,"
                + "FOREIGN KEY(" + COLUMN_DETAIL_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_DETAIL_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")"
                + ");";
        db.execSQL(CREATE_TABLE_ORDER_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAILS);
        onCreate(db);
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_ROLE, user.getRole());

        long result = db.insert(TABLE_USERS, null, values);
        //db.close();
        return result != -1;
    }

    public Bundle checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Bundle userData = null;

        String[] columns = {COLUMN_USER_ID, COLUMN_PASSWORD, COLUMN_ROLE};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String dbPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));

            if (password.equals(dbPassword)) {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                int role = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
                userData = new Bundle();
                userData.putInt("USER_ID", userId);
                userData.putInt("ROLE", role);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return userData;
    }

    public int getUserRole(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ROLE};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int role = -1;
        if (cursor.moveToFirst()) {
            role = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
        }
        cursor.close();
        //db.close();
        return role;
    }
    public void addDummyUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getInt(0) > 0) {
                cursor.close();
                //db.close();
                return; // Đã có người dùng, không thêm nữa
            }
            cursor.close();
        }
        //db.close();

        // Thêm các tài khoản mẫu
        // Tạo tài khoản Admin
        User admin = new User(0, "admin", "admin123", "admin@gundam.com", 1); // role = 1
        addUser(admin); // Gọi lại chính hàm addUser ở trên

        // Tạo một tài khoản User thường để test
        User user = new User(0, "user", "user123", "user@gundam.com", 0); // role = 0
        addUser(user);
    }
    public User getUserByName(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ?",
                    new String[]{String.valueOf(userName)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Giả sử các cột của bạn là COLUMN_USER_ID, COLUMN_USER_FULLNAME, COLUMN_USER_EMAIL...
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                String emails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                int role = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE));

                // Lấy các thông tin khác nếu cần...

                // Tạo đối tượng User (constructor của bạn có thể khác)
                user = new User(id, fullName, password,emails,role);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            //db.close();
        }
        return user;
    }
    public User getUserById(int idUser) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_USERS, null, COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(idUser)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                String emails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE));
                int role = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE));

                user = new User(id, fullName, password,emails,role);
                user.setAddress(address);
                user.setPhone(phone);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }}
        return user;
    }
    public boolean deleteUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(
                    TABLE_USERS,
                    COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected > 0;
    }
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query(TABLE_USERS, null, null, null, null, null, COLUMN_USER_ID + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)); // Lấy cả pass để đủ thuộc tính
                int role = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE));

                userList.add(new User(id, username, email, password, role));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return userList;
    }
    public boolean updateUserRole(int userId, int newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROLE, newRole);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        return rowsAffected > 0;
    }
    public List<Product> getProductsWithPagination(int limit, int offset, String searchQuery, String category) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder whereClause = new StringBuilder();
        ArrayList<String> whereArgs = new ArrayList<>();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            whereClause.append(COLUMN_PRODUCT_NAME).append(" LIKE ?");
            whereArgs.add("%" + searchQuery + "%");
        }

        if (category != null && !category.equals("Tất cả")) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }

            if ("Khác".equals(category)) {
                whereClause.append(COLUMN_PRODUCT_CATEGORY).append(" NOT IN (?, ?, ?, ?)");
                whereArgs.add("MG");
                whereArgs.add("PG");
                whereArgs.add("RG");
                whereArgs.add("HG");
            } else {
                whereClause.append(COLUMN_PRODUCT_CATEGORY).append(" = ?");
                whereArgs.add(category);
            }}

        String finalWhereClause = whereClause.length() > 0 ? whereClause.toString() : null;
        String[] finalWhereArgs = whereArgs.toArray(new String[0]);

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PRODUCTS, null, finalWhereClause, finalWhereArgs,
                    null, null, COLUMN_PRODUCT_ID + " DESC",
                    offset + ", " + limit);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // ... bên trong vòng lặp do-while của getProductsWithPagination
                    String modelPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_MODEL_PATH));
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_STOCK));
                    String cat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY));
                    productList.add(new Product(id, name, description, price, imagePath, stock, cat,modelPath));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            //db.close();
        }
        return productList;
    }
    public int getTotalProductCount(String searchQuery, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery;
        String[] selectionArgsArray = null;
        StringBuilder whereClause = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            whereClause.append(COLUMN_PRODUCT_NAME).append(" LIKE ?");
            selectionArgs.add("%" + searchQuery + "%");
        }

        if (category != null && !category.equals("Tất cả")) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            if ("Khác".equals(category)) {
                whereClause.append(COLUMN_PRODUCT_CATEGORY).append(" NOT IN (?, ?, ?, ?)");
                selectionArgs.add("MG");
                selectionArgs.add("PG");
                selectionArgs.add("RG");
                selectionArgs.add("HG");
            } else {
                whereClause.append(COLUMN_PRODUCT_CATEGORY).append(" = ?");
                selectionArgs.add(category);
            }}
        countQuery = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS;
        if (whereClause.length() > 0) {
            countQuery += " WHERE " + whereClause.toString();
            selectionArgsArray = selectionArgs.toArray(new String[0]);
        }

        Cursor cursor = db.rawQuery(countQuery, selectionArgsArray);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
    public boolean updateProductStock(int productId, int quantityChange) {
        SQLiteDatabase db = this.getWritableDatabase();
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }

        int currentStock = product.getStock();
        int newStock = currentStock + quantityChange;

        if (newStock < 0) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_STOCK, newStock);

        int rowsAffected = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)});
        return rowsAffected > 0;
    }
    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Product product = null;
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));
            int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_STOCK));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY));
            String modelPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_MODEL_PATH));

            product = new Product(id, name, description, price, imagePath, stock, category, modelPath);

            cursor.close();
        }
        return product;
    }

    public boolean addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PRODUCT_DESCRIPTION, product.getDescription());
        values.put(COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(COLUMN_PRODUCT_IMAGE, product.getImagePath());
        values.put(COLUMN_PRODUCT_STOCK, product.getStock());
        values.put(COLUMN_PRODUCT_CATEGORY, product.getCategory());
        values.put(COLUMN_PRODUCT_MODEL_PATH, product.getModelPath());
        long result = db.insert(TABLE_PRODUCTS, null, values);
        return result != -1;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PRODUCT_DESCRIPTION, product.getDescription());
        values.put(COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(COLUMN_PRODUCT_IMAGE, product.getImagePath());
        values.put(COLUMN_PRODUCT_STOCK, product.getStock());
        values.put(COLUMN_PRODUCT_CATEGORY, product.getCategory());
        values.put(COLUMN_PRODUCT_MODEL_PATH, product.getModelPath());
        int rowsAffected = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(product.getId())});
        return rowsAffected;
    }

    public void addDummyProducts() {
        if (getTotalProductCount("", "Tất cả") > 0) {
            return; // Đã có sản phẩm, không thêm nữa
        }

        String[] names = {
                "RX-78-2 Gundam", "Zaku II", "Wing Gundam Zero EW", "Gundam Barbatos Lupus Rex",
                "Nu Gundam", "Sazabi", "Unicorn Gundam", "Sinanju", "Gundam Exia", "00 Qan[T]"
        };

        String[] categories = {"HG", "RG", "MG", "PG", "SD"};
        String[] imageNames = {
                "gundam_rx782", "gundam_zaku2", "gundam_wing_zero", "gundam_barbatos", "gundam_nu",
                "gundam_sazabi", "gundam_unicorn", "gundam_sinanju", "gundam_exia", "gundam_00qant"
        };
        double[] prices = {150.0, 120.0, 480.0, 320.0, 550.0, 600.0, 450.0, 500.0, 380.0, 420.0};
        String description = "Đây là mô tả chi tiết cho mẫu Gundam. Sản phẩm được làm từ nhựa cao cấp, với độ chi tiết cao, phù hợp cho việc trưng bày và sưu tầm.";
        int stock = new Random().nextInt(46) + 5;
        for (int i = 0; i < 5; i++) {
            String name = names[i % names.length] + " #" + (i + 1);
            String category = categories[i % categories.length];
            double price = prices[i % prices.length] * 1000;
            String imageName = imageNames[i % imageNames.length];
            String modelPath = "models/rx78.glb";
            addProduct(new Product(0, name, description, price, imageName, stock,category,modelPath));
        }
    }
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
        return rowsAffected > 0;
    }
    public int getProductQuantity(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int quantity = 0;
        try {
            cursor = db.query(TABLE_PRODUCTS,
                    new String[]{COLUMN_PRODUCT_STOCK},
                    COLUMN_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(productId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_STOCK));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return quantity;
    }
    public boolean updateProductQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_STOCK, newQuantity);

        int rowsAffected = db.update(TABLE_PRODUCTS,
                values,
                COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)});

        return rowsAffected > 0;
    }

    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor orderCursor = db.query(TABLE_ORDERS, null, null, null, null, null, COLUMN_ORDER_ID + " DESC");

        if (orderCursor != null && orderCursor.moveToFirst()) {
            do {
                int orderId = orderCursor.getInt(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
                int userId = orderCursor.getInt(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_USER_ID));
                double totalAmount = orderCursor.getDouble(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL_AMOUNT));
                String address = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_ADDRESS));
                String phone = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_PHONE));
                String status = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));
                String orderDate = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));

                Order order = new Order(orderId, userId, totalAmount, address, phone, status, orderDate);

                List<OrderDetail> detailList = new ArrayList<>();
                String detailQuery = "SELECT od.*, p." + COLUMN_PRODUCT_NAME + " FROM " + TABLE_ORDER_DETAILS + " od " +
                        "INNER JOIN " + TABLE_PRODUCTS + " p ON od." + COLUMN_DETAIL_PRODUCT_ID + " = p." + COLUMN_PRODUCT_ID + " " +
                        "WHERE od." + COLUMN_DETAIL_ORDER_ID + " = ?";
                Cursor detailCursor = db.rawQuery(detailQuery, new String[]{String.valueOf(orderId)});

                if (detailCursor != null && detailCursor.moveToFirst()) {
                    do {
                        int productId = detailCursor.getInt(detailCursor.getColumnIndexOrThrow(COLUMN_DETAIL_PRODUCT_ID));
                        String productName = detailCursor.getString(detailCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                        int quantity = detailCursor.getInt(detailCursor.getColumnIndexOrThrow(COLUMN_DETAIL_QUANTITY));
                        double price = detailCursor.getDouble(detailCursor.getColumnIndexOrThrow(COLUMN_DETAIL_PRICE));

                        detailList.add(new OrderDetail(productId, productName, quantity, price));
                    } while (detailCursor.moveToNext());
                }
                if (detailCursor != null) {
                    detailCursor.close();
                }

                order.setDetails(detailList);
                orderList.add(order);

            } while (orderCursor.moveToNext());
        }

        if (orderCursor != null) {
            orderCursor.close();
        }

        return orderList;
    }
    public Bundle getRevenueStats(String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Bundle stats = new Bundle();
        Cursor cursor = null;

        double totalRevenue = 0;
        int totalOrders = 0;
        int totalProductsSold = 0;

        String dateSelection = null;
        String[] selectionArgs = null;
        if (startDate != null && endDate != null) {
            // Thêm ' 23:59:59' vào ngày kết thúc để bao gồm cả ngày đó
            dateSelection = COLUMN_ORDER_DATE + " BETWEEN ? AND ?";
            selectionArgs = new String[]{startDate + " 00:00:00", endDate + " 23:59:59"};
        }
        try {
            String revenueQuery = "SELECT " +
                    "SUM(" + COLUMN_ORDER_TOTAL_AMOUNT + ") as total_revenue, " + // Sửa: Dùng cột tổng tiền mới
                    "COUNT(" + COLUMN_ORDER_ID + ") as total_orders " +
                    "FROM " + TABLE_ORDERS +
                    (dateSelection != null ? " WHERE " + dateSelection : "");

            cursor = db.rawQuery(revenueQuery, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                totalRevenue = cursor.getDouble(cursor.getColumnIndexOrThrow("total_revenue"));
                totalOrders = cursor.getInt(cursor.getColumnIndexOrThrow("total_orders"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        try {
            String productsQuery = "SELECT SUM(od." + COLUMN_DETAIL_QUANTITY + ") as total_products " +
                    "FROM " + TABLE_ORDER_DETAILS + " od " +
                    "INNER JOIN " + TABLE_ORDERS + " o ON od." + COLUMN_DETAIL_ORDER_ID + " = o." + COLUMN_ORDER_ID +
                    (dateSelection != null ? " WHERE o." + dateSelection : ""); // Áp dụng điều kiện ngày vào bảng orders

            cursor = db.rawQuery(productsQuery, selectionArgs); // Dùng lại selectionArgs

            if (cursor != null && cursor.moveToFirst()) {
                totalProductsSold = cursor.getInt(cursor.getColumnIndexOrThrow("total_products"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        stats.putDouble("total_revenue", totalRevenue);
        stats.putInt("total_orders", totalOrders);
        stats.putInt("total_products_sold", totalProductsSold);

        return stats;
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
    public long addReview(int productId, int userId, float rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_REVIEW_PRODUCT_ID, productId);
        values.put(COLUMN_REVIEW_USER_ID, userId);
        values.put(COLUMN_REVIEW_RATING, rating);
        values.put(COLUMN_REVIEW_COMMENT, comment);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        values.put(COLUMN_REVIEW_DATE, currentDate);
        long result = -1;
        try {
            result = db.insert(TABLE_REVIEWS, null, values);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Lỗi khi chèn đánh giá: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
    public List<Review> getReviewsForProduct(int productId) {
        List<Review> reviewList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.*, u." + COLUMN_USERNAME + " FROM " + TABLE_REVIEWS + " r " +
                "INNER JOIN " + TABLE_USERS + " u ON r." + COLUMN_REVIEW_USER_ID + " = u." + COLUMN_USER_ID + " " +
                "WHERE r." + COLUMN_REVIEW_PRODUCT_ID + " = ? " +
                "ORDER BY r." + COLUMN_REVIEW_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_ID));
                int prodId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_PRODUCT_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_USER_ID));
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_RATING));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_COMMENT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_DATE));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));

                Review review = new Review(id, prodId, userId, rating, comment, date);
                review.setUsername(username); // Gán tên người dùng vào đối tượng review
                reviewList.add(review);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return reviewList;
    }
    public List<Review> getAllReviews() {
        List<Review> reviewList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.*, u." + COLUMN_USERNAME + ", p." + COLUMN_PRODUCT_NAME + " " +
                "FROM " + TABLE_REVIEWS + " r " +
                "INNER JOIN " + TABLE_USERS + " u ON r." + COLUMN_REVIEW_USER_ID + " = u." + COLUMN_USER_ID + " " +
                "INNER JOIN " + TABLE_PRODUCTS + " p ON r." + COLUMN_REVIEW_PRODUCT_ID + " = p." + COLUMN_PRODUCT_ID + " " +
                "ORDER BY r." + COLUMN_REVIEW_ID + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_ID));
                int prodId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_PRODUCT_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_USER_ID));
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_RATING));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_COMMENT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_DATE));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));

                Review review = new Review(id, prodId, userId, rating, comment, date,productName);
                review.setUsername(username);
                reviewList.add(review);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return reviewList;
    }
    public boolean deleteReview(int reviewId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_REVIEWS, COLUMN_REVIEW_ID + " = ?", new String[]{String.valueOf(reviewId)});
        return rowsAffected > 0;
    }
    public boolean updateReview(int reviewId, float newRating, String newComment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_REVIEW_RATING, newRating);
        values.put(COLUMN_REVIEW_COMMENT, newComment);
        int rowsAffected = db.update(TABLE_REVIEWS, values, COLUMN_REVIEW_ID + " = ?",
                new String[]{String.valueOf(reviewId)});

        return rowsAffected > 0;
    }
    public boolean hasUserReviewedProduct(int userId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_REVIEWS + " WHERE " +
                    COLUMN_REVIEW_USER_ID + " = ? AND " +
                    COLUMN_REVIEW_PRODUCT_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(productId)});

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public float[] getReviewSummary(int productId) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        float[] result = new float[]{0.0f, 0.0f}; // Khởi tạo giá trị mặc định

        try {
            String query = "SELECT AVG(" + COLUMN_REVIEW_RATING + "), COUNT(" + COLUMN_REVIEW_ID + ") " +
                    "FROM " + TABLE_REVIEWS + " WHERE " + COLUMN_REVIEW_PRODUCT_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});

            if (cursor != null && cursor.moveToFirst()) {
                // Lấy kết quả từ cột đầu tiên (AVG)
                result[0] = cursor.getFloat(0);
                // Lấy kết quả từ cột thứ hai (COUNT)
                result[1] = cursor.getFloat(1);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }
    public void addOrUpdateCartItem(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            // 1. Kiểm tra xem sản phẩm đã có trong giỏ hàng của user chưa
            String query = "SELECT " + COLUMN_CART_ID + ", " + COLUMN_CART_QUANTITY + " FROM " + TABLE_CART_ITEMS +
                    " WHERE " + COLUMN_CART_USER_ID + " = ? AND " + COLUMN_CART_PRODUCT_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(productId)});

            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_USER_ID, userId);
            values.put(COLUMN_CART_PRODUCT_ID, productId);

            if (cursor != null && cursor.moveToFirst()) {
                // 2a. Nếu đã có -> Cập nhật số lượng
                int existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY));
                int newQuantity = existingQuantity + quantity; // Cộng dồn số lượng
                values.put(COLUMN_CART_QUANTITY, newQuantity);

                String cartId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CART_ID));
                db.update(TABLE_CART_ITEMS, values, COLUMN_CART_ID + " = ?", new String[]{cartId});
            } else {
                values.put(COLUMN_CART_QUANTITY, quantity);
                db.insert(TABLE_CART_ITEMS, null, values);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public List<CartItem> getCartItemsByUserId(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();Cursor cursor = null;
        String query = "SELECT c." + COLUMN_CART_ID + ", c." + COLUMN_CART_USER_ID + ", c." + COLUMN_CART_PRODUCT_ID + ", c." + COLUMN_CART_QUANTITY + ", "
                + "p." + COLUMN_PRODUCT_ID + ", p." + COLUMN_PRODUCT_NAME + ", p." + COLUMN_PRODUCT_DESCRIPTION
                + ", p." + COLUMN_PRODUCT_PRICE + ", p." + COLUMN_PRODUCT_IMAGE + ", p." + COLUMN_PRODUCT_STOCK // Sửa tên cột
                + ", p." + COLUMN_PRODUCT_CATEGORY + ", p." + COLUMN_PRODUCT_MODEL_PATH
                + " FROM " + TABLE_CART_ITEMS + " c"
                + " JOIN " + TABLE_PRODUCTS + " p ON c." + COLUMN_CART_PRODUCT_ID + " = p." + COLUMN_PRODUCT_ID
                + " WHERE c." + COLUMN_CART_USER_ID + " = ?";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            while (cursor.moveToNext()) {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)); // Sửa tên cột
                int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_STOCK));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY));
                String modelPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_MODEL_PATH));

                Product product = new Product(productId, name, description, price, imagePath, stock, category, modelPath);

                int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_ID));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY));
                cartItems.add(new CartItem(cartId, userId, productId, quantity, product));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cartItems;
    }
    public boolean updateCartItemQuantity(int cartId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CART_QUANTITY, newQuantity);
        int rows = db.update(TABLE_CART_ITEMS, values, COLUMN_CART_ID + " = ?", new String[]{String.valueOf(cartId)});
        return rows > 0;
    }

    public boolean deleteCartItem(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_CART_ITEMS, COLUMN_CART_ID + " = ?", new String[]{String.valueOf(cartId)});
        return rows > 0;
    }
    public boolean clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_CART_ITEMS, COLUMN_CART_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateUserInfo(int userId, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();    values.put(COLUMN_USER_ADDRESS, address);
        values.put(COLUMN_USER_PHONE, phone);
        int rows = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }
    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhone());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_PASSWORD, user.getPassword());
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();

        return rowsAffected > 0;
    }
    public boolean createOrder(int userId, String address, String phone, List<CartItem> cartItems, boolean isFromCart) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        if (cartItems == null || cartItems.isEmpty()) {
            db.endTransaction();
            return false;
        }

        try {
            double totalAmount = 0;
            for (CartItem item : cartItems) {
                totalAmount += item.getProduct().getPrice() * item.getQuantity();
            }

            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_USER_ID, userId);
            orderValues.put(COLUMN_ORDER_TOTAL_AMOUNT, totalAmount);
            orderValues.put(COLUMN_ORDER_ADDRESS, address);
            orderValues.put(COLUMN_ORDER_PHONE, phone);
            orderValues.put(COLUMN_ORDER_STATUS, "Chờ xác nhận");
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            orderValues.put(COLUMN_ORDER_DATE, currentDate);

            long orderId = db.insert(TABLE_ORDERS, null, orderValues);

            if (orderId == -1) {
                db.endTransaction();
                return false;
            }
            for (CartItem item : cartItems) {
                ContentValues detailValues = new ContentValues();
                detailValues.put(COLUMN_DETAIL_ORDER_ID, orderId);
                detailValues.put(COLUMN_DETAIL_PRODUCT_ID, item.getProduct().getId());
                detailValues.put(COLUMN_DETAIL_QUANTITY, item.getQuantity());

                detailValues.put(COLUMN_DETAIL_PRICE, item.getProduct().getPrice());

                long detailId = db.insert(TABLE_ORDER_DETAILS, null, detailValues);
                if (detailId == -1) {
                    db.endTransaction();
                    return false;
                }
            }
            db.setTransactionSuccessful();
            return true;

        } finally {
            db.endTransaction();
        }
    }

    public List<Order> getAllOrdersByUserId(int userId) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor orderCursor = null;

        try {
            orderCursor = db.query(TABLE_ORDERS, null, COLUMN_ORDER_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null, COLUMN_ORDER_DATE + " DESC"); // Sắp xếp đơn mới nhất lên đầu

            while (orderCursor.moveToNext()) {
                int orderId = orderCursor.getInt(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
                String orderDate = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
                double totalAmount = orderCursor.getDouble(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL_AMOUNT));
                String address = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_ADDRESS));
                String phone = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_PHONE));
                String status = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));
                Order order = new Order(orderId, userId, totalAmount, address, phone, status, orderDate);
                List<OrderDetail> orderDetails = getOrderDetailsByOrderId(orderId);
                order.setDetails(orderDetails);
                orderList.add(order);
            }
        } finally {
            if (orderCursor != null) {
                orderCursor.close();
            }
        }
        return orderList;
    }
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> detailList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor detailCursor = null;

        String query = "SELECT od.*, p." + COLUMN_PRODUCT_NAME + ", p." + COLUMN_PRODUCT_IMAGE +
                " FROM " + TABLE_ORDER_DETAILS + " od" +
                " JOIN " + TABLE_PRODUCTS + " p ON od." + COLUMN_DETAIL_PRODUCT_ID + " = p." + COLUMN_PRODUCT_ID +
                " WHERE od." + COLUMN_DETAIL_ORDER_ID + " = ?";

        try {
            detailCursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
            while (detailCursor.moveToNext()) {
                int detailId = detailCursor.getInt(detailCursor.getColumnIndexOrThrow(COLUMN_DETAIL_ID));
                int productId = detailCursor.getInt(detailCursor.getColumnIndexOrThrow(COLUMN_DETAIL_PRODUCT_ID));
                int quantity = detailCursor.getInt(detailCursor.getColumnIndexOrThrow(COLUMN_DETAIL_QUANTITY));
                double price = detailCursor.getDouble(detailCursor.getColumnIndexOrThrow(COLUMN_DETAIL_PRICE));
                String productName = detailCursor.getString(detailCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                String productImage = detailCursor.getString(detailCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));
                Product productInfo = new Product();
                productInfo.setName(productName);
                productInfo.setImagePath(productImage);
                productInfo.setId(productId);
                detailList.add(new OrderDetail(detailId, orderId, productId, quantity, price, productInfo));
            }
        } finally {
            if (detailCursor != null) {
                detailCursor.close();
            }
        }
        return detailList;
    }

    public Order getOrderById(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Order order = null;

        // 1. Lấy thông tin cơ bản của đơn hàng từ bảng orders
        String query = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_USER_ID));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ADDRESS));
            double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL_AMOUNT));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_PHONE));
            order = new Order(id, userId, totalAmount, address, phone, status, orderDate);
            cursor.close();
        }

        if (order != null) {
            List<OrderDetail> details = new ArrayList<>();
            String detailsQuery = "SELECT od.*, p." + COLUMN_PRODUCT_NAME +
                    " FROM " + TABLE_ORDER_DETAILS + " od" +
                    " JOIN " + TABLE_PRODUCTS + " p ON od." + COLUMN_DETAIL_PRODUCT_ID + " = p." + COLUMN_PRODUCT_ID +
                    " WHERE od." + COLUMN_DETAIL_ORDER_ID + " = ?";
            Cursor detailsCursor = db.rawQuery(detailsQuery, new String[]{String.valueOf(orderId)});

            if (detailsCursor != null && detailsCursor.moveToFirst()) {
                do {
                    long detailId = detailsCursor.getLong(detailsCursor.getColumnIndexOrThrow(COLUMN_DETAIL_ID));
                    int productId = detailsCursor.getInt(detailsCursor.getColumnIndexOrThrow(COLUMN_DETAIL_PRODUCT_ID));
                    int quantity = detailsCursor.getInt(detailsCursor.getColumnIndexOrThrow(COLUMN_DETAIL_QUANTITY));
                    double price = detailsCursor.getDouble(detailsCursor.getColumnIndexOrThrow(COLUMN_DETAIL_PRICE));
                    String productName = detailsCursor.getString(detailsCursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));

                    OrderDetail detail = new OrderDetail(detailId, productId, productName, quantity, price);
                    detail.getProduct().setName(productName);
                    details.add(detail);
                } while (detailsCursor.moveToNext());
                detailsCursor.close();
            }
            order.setDetails(details);
        }
        return order;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_STATUS, newStatus);

        int rowsAffected = db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});

        return rowsAffected > 0;
    }
    public int countConfirmedOrders() {SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int orderCount = 0;

        String selection = COLUMN_ORDER_STATUS + " = ? OR " + COLUMN_ORDER_STATUS + " = ?";
        String[] selectionArgs = new String[]{"Đã xác nhận", "Hoàn thành"}; // Ví dụ các trạng thái hợp lệ

        try {
            cursor = db.query(TABLE_ORDERS,
                    new String[]{"COUNT(" + COLUMN_ORDER_ID + ")"},
                    selection,
                    selectionArgs,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                orderCount = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return orderCount;
    }
    public double calculateTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        double totalRevenue = 0.0;

        String selection = COLUMN_ORDER_STATUS + " = ? OR " + COLUMN_ORDER_STATUS + " = ?";
        String[] selectionArgs = new String[]{"Đã xác nhận", "Hoàn thành"};

        try {
            cursor = db.query(TABLE_ORDERS,
                    new String[]{"SUM(" + COLUMN_ORDER_TOTAL_AMOUNT + ")"}, // Tính tổng cột total_amount
                    selection,
                    selectionArgs,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                totalRevenue = cursor.getDouble(0); // Lấy kết quả tổng từ cột đầu tiên
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalRevenue;
    }
    public int countTotalProductsSold() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int totalProducts = 0;

        String query = "SELECT SUM(od." + COLUMN_DETAIL_QUANTITY + ")" +
                " FROM " + TABLE_ORDER_DETAILS + " od" +
                " JOIN " + TABLE_ORDERS + " o ON od." + COLUMN_DETAIL_ORDER_ID + " = o." + COLUMN_ORDER_ID +
                " WHERE o." + COLUMN_ORDER_STATUS + " = ? OR o." + COLUMN_ORDER_STATUS + " = ?";

        String[] selectionArgs = new String[]{"Đã xác nhận", "Hoàn thành"};

        try {
            cursor = db.rawQuery(query, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                totalProducts = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalProducts;
    }
    public int getTotalProductTypes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
    public double getTotalInventoryValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sumQuery = "SELECT SUM(" + COLUMN_PRODUCT_PRICE + " * " + COLUMN_PRODUCT_STOCK + ") FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(sumQuery, null);
        double totalValue = 0.0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalValue = cursor.getDouble(0);
            }
            cursor.close();
        }
        return totalValue;
    }
    public int getTotalInventoryStock() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sumQuery = "SELECT SUM(" + COLUMN_PRODUCT_STOCK + ") FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(sumQuery, null);
        int totalStock = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalStock = cursor.getInt(0);
            }
            cursor.close();
        }
        return totalStock;
    }
}

