package com.mytestorg.user.infrastructure.configuration.serializer;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class JavaDateTimeFormatterConstants {
    /**
     * Formatter for date-times with millisecond precision and explicit offset in the form {@code +HH:MM}.
     * <br>
     * Pattern: {@code yyyy-MM-dd'T'HH:mm:ss.SSS+HH:MM} (e.g., 2024-04-22T15:45:12.123+05:30)
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSS_PLUS_HH_COLON_MM = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
            .appendOffset("+HH:MM", "+00:00").toFormatter();

}
