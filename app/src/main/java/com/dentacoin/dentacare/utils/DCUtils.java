package com.dentacoin.dentacare.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Atanas Chervarov on 8/14/17.
 */

public class DCUtils {

    public static final String REGEX_NAME = "^[\\p{L} .'-]+$";

    public static boolean isValidName(String name) {
        if (!TextUtils.isEmpty(name)) {
            Pattern pattern = Pattern.compile(REGEX_NAME, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(name);
            return matcher.find();
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        if (context != null) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        v.draw(canvas);
        return bitmap;
    }

    public static void openURL(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    /**
     * Converts IBAN to eth address
     * @param iban
     * @return
     */
    public static String ibanToAdress(String iban) {
        if (iban != null && (iban.length() == 34 || iban.length() == 35)) {
            String base36 = iban.substring(4);

            try {
                BigInteger asBn = new BigInteger(base36, 36);
                String result = asBn.toString(16);

                if (result != null) {
                    while (result.length() < 40) {
                        result = "0" + result;
                    }
                }

                return "0x" + result;
            } catch (NullPointerException | NumberFormatException e) { }
        }

        return null;
    }

    /**
     * Date picker
     * @param context
     * @param from
     * @param to
     * @param listener
     */
    public static void pickDate(Context context, Date set, Date from, Date to, final IDatePickerListener listener) {
        final Calendar calendar = Calendar.getInstance();
        if (set != null) {
            calendar.setTime(set);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                if (listener != null) {
                    calendar.set(Calendar.YEAR, y);
                    calendar.set(Calendar.MONTH, m);
                    calendar.set(Calendar.DAY_OF_MONTH, d);
                    listener.onPickedDate(calendar.getTime());
                }
            }
        }, year, month, day);

        if (from != null) {
            datePickerDialog.getDatePicker().setMinDate(from.getTime());
        }

        if (to != null) {
            datePickerDialog.getDatePicker().setMaxDate(to.getTime());
        }

        datePickerDialog.show();
    }

    /**
     * Pick user birthday
     * @param context
     * @param birthday
     * @param listener
     */
    public static void pickBirthday(Context context, Date birthday, final IDatePickerListener listener) {
        final Calendar calendar = Calendar.getInstance();
        if (birthday != null) {
            calendar.setTime(birthday);
        } else {
            calendar.add(Calendar.YEAR, -18);
        }

        Date startDate = calendar.getTime();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -DCConstants.MIN_AGE);
        Date maxDate = calendar.getTime();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -DCConstants.MAX_AGE);
        Date minDate = calendar.getTime();
        pickDate(context,startDate, minDate, maxDate, listener);
    }

    /**
     * Encode image to base64 string
     * @param uri
     * @return  either null or base64 encoded image
     */
    public static String base64Bitmap(Uri uri) {
        if (uri != null) {
            Bitmap bm = BitmapFactory.decodeFile(uri.getPath());
            return base64Bitmap(bm);
        }
        return null;
    }

    public static String base64Bitmap(Bitmap bm) {
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
        return null;
    }

    public static String secondsToTime(int seconds) {
        if (seconds > 0) {

            int h = seconds / 3600;
            int m = (seconds % 3600) / 60;
            int s = (seconds % 3600) % 60;

            if (h > 0) {
                return String.format(Locale.ENGLISH, "%dh %02d", h, m);
            } else {
                return String.format(Locale.ENGLISH, "%d:%02d", m, s);
            }
        }
        return "0:00";
    }

    public static DCConstants.DCAutoMode getAutoModeForNow() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay >= 2 && hourOfDay < 11) {
            return DCConstants.DCAutoMode.MORNING;
        } else if (hourOfDay >= 17 && hourOfDay < 24) {
            return DCConstants.DCAutoMode.EVENING;
        }

        return null;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
