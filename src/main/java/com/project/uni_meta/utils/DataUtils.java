package com.project.uni_meta.utils;

import java.security.SecureRandom;

public class DataUtils {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    public static String generateTempPwd(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Sử dụng ít nhất một ký tự từ mỗi nhóm ký tự
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Lấy các ký tự còn lại từ tất cả các nhóm ký tự
        for (int i = 4; i < length; i++) {
            String allChars = UPPER + LOWER + DIGITS + SPECIAL;
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Trộn mật khẩu
        for (int i = 0; i < length; i++) {
            int randomIndexToSwap = random.nextInt(length);
            char temp = password.charAt(randomIndexToSwap);
            password.setCharAt(randomIndexToSwap, password.charAt(i));
            password.setCharAt(i, temp);
        }

        return password.toString();
    }
}
