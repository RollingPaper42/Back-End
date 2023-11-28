package com.strcat.utils;

import java.util.stream.Stream;

public class TestUtils {
    public static Stream<String> generateInvalidToken() {
        return Stream.of(
                "0", "01", "012", "0123", "01234", "012345", "0123456", "01234567", "012345678", "Bearer ", "Bearer"
        );
    }
}
