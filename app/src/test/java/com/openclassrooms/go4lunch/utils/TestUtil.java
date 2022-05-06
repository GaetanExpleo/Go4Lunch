package com.openclassrooms.go4lunch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class TestUtil {

    public static final double TEST_LATITUDE = 48.78772085349926;
    public static final double TEST_LONGITUDE = 2.0462565270287523;

    public static final String TEST_RESTAURANT_ID = "ChIJgfe3nCuH5kcREKTd0qOKTdI";

    public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
