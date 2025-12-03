package com.bluemoon.app.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Tiện ích bảo mật, xử lý mã hóa mật khẩu.
 */
public class SecurityUtil {

    // Chuỗi Salt bí mật để tăng cường độ mạnh cho SHA-256
    // Trong môi trường Production, chuỗi này nên được lưu trong Environment Variable
    private static final String SALT = "BlueMoon_2025_SecretSalt_#9920!";

    /**
     * Băm mật khẩu sử dụng SHA-256 kết hợp với Salt.
     * @param originalPassword Mật khẩu gốc từ người dùng
     * @return Chuỗi Hex đại diện cho mật khẩu đã băm
     */
    public static String hashPassword(String originalPassword) {
        try {
            // Thêm salt vào trước hoặc sau mật khẩu
            String input = SALT + originalPassword;
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); // Nên log ra file log hệ thống
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}