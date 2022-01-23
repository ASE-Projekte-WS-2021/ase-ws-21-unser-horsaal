package com.example.unser_hoersaal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass {


    public static boolean checkEmailForValidity(String email) {

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();

    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    public static int multiplicationNumbers(int num1, int num2) {
        return num1 * num2;
    }

}