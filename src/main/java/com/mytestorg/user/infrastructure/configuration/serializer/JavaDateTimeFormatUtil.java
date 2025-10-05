package com.mytestorg.user.infrastructure.configuration.serializer;


import java.time.OffsetDateTime;

import static com.mytestorg.user.infrastructure.configuration.serializer.JavaDateTimeFormatterConstants.YYYY_MM_DD_T_HH_MM_SS_SSS_PLUS_HH_COLON_MM;


public class JavaDateTimeFormatUtil {

    public static String serializeOffsetDateTimeWithOffset(OffsetDateTime value) {
        return null != value ? value.format(YYYY_MM_DD_T_HH_MM_SS_SSS_PLUS_HH_COLON_MM) : null;
    }


}
