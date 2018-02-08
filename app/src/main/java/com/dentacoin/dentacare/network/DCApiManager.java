package com.dentacoin.dentacare.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.dentacoin.dentacare.BuildConfig;
import com.dentacoin.dentacare.model.DCActivityRecord;
import com.dentacoin.dentacare.model.DCAvatar;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.model.DCOralHealthItem;
import com.dentacoin.dentacare.model.DCResetPassword;
import com.dentacoin.dentacare.model.DCTransaction;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.response.DCActivityRecordsResponse;
import com.dentacoin.dentacare.network.response.DCAuthToken;
import com.dentacoin.dentacare.network.response.DCCaptchaResponse;
import com.dentacoin.dentacare.network.response.DCRecordsSyncResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Atanas Chervarov on 8/6/17.
 * Singleton class for managing the Api requests
 */

public class DCApiManager {
    private static final String TAG = DCApiManager.class.getSimpleName();

    public enum RequestMethod {

        GET("GET"),
        POST("POST"),
        PATCH("PATCH"),
        DELETE("DELETE"),
        UPDATE("UPDATE");

        private String method;

        RequestMethod(String method) {
            this.method = method;
        }
        public String getMethod() { return method; }
    }

    private static DCApiManager instance;
    private final OkHttpClient client = new OkHttpClient();

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String ENDPOINT_UPLOAD_AVATAR = "upload_avatar";
    private static final String ENDPOINT_REGISTER_USER = "userreg";
    private static final String ENDPOINT_LOGIN_USER = "login";
    private static final String ENDPOINT_LOGOUT = "logout";
    private static final String ENDPOINT_USER = "user";
    private static final String ENDPOINT_DASHBOARD = "dashboard";
    private static final String ENDPOINT_RECORDS = "records";
    private static final String ENDPOINT_SYNC_RECORDS = "multiple-records";
    private static final String ENDPOINT_TRANSACTIONS = "transactions";
    private static final String ENDPOINT_ORAL_HEALTH = "oralhealth";
    private static final String ENDPOINT_GOALS = "goals";
    private static final String ENDPOINT_RESET_PASSWORD = "reset";
    private static final String ENDPOINT_CAPTCHA = "captcha";
    private static final String ENDPOINT_EMAIL_CONFIRM = "confirm";

    private static final String HEADER_KEY_TOKEN = "Authorization";
    private static final String HEADER_KEY_FBM ="FirebaseToken";

    public static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public static final Gson gsonExopse = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static synchronized DCApiManager getInstance() {
        if (instance == null)
            instance = new DCApiManager();

        return instance;
    }

    private DCApiManager() {
    }

    /**
     * Checks if there is an active internet connection
     * @param context
     * @return
     */
    public static boolean hasInternetConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Create API endpoint with given params (optional)
     * @param path
     * @param params
     * @return
     */
    private static String buildPath(String path, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(BuildConfig.HOST + path).buildUpon();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build().toString();
    }

    /**
     * Uploads an avatar image
     * Returns either the created object or an error
     * @param avatar    Avatar file
     * @param responseListener  DCResponseListener for handling the response
     */
    @Deprecated
    public void uploadAvatar(File avatar,  DCResponseListener<DCAvatar> responseListener) {
        String endpoint = buildPath(ENDPOINT_UPLOAD_AVATAR, null);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pic", "avatar.jpg", RequestBody.create(MEDIA_TYPE_JPG, avatar))
                .build();

        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(responseListener, DCAvatar.class));
    }

    /**
     * Registers a new User
     * @param user
     * @param responseListener
     */
    public void registerUser(DCUser user, DCResponseListener<DCAuthToken> responseListener) {
        if (user == null)
            return;

        String endpoint = buildPath(ENDPOINT_REGISTER_USER, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gsonExopse.toJson(user));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(responseListener, DCAuthToken.class));
    }

    /**
     * Login a user
     * @param user
     * @param responseListener
     */
    public void loginUSer(DCUser user, DCResponseListener<DCAuthToken> responseListener) {
        if (user == null)
            return;

        String endpoint = buildPath(ENDPOINT_LOGIN_USER, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gsonExopse.toJson(user));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(responseListener, DCAuthToken.class));
    }

    /**
     * Retrieve data for the current user
     * @param responseListener
     */
    public void getUser(final DCResponseListener<DCUser> responseListener) {
        String endpoint = buildPath(ENDPOINT_USER, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);

        DCResponseListener<DCUser> listener = new DCResponseListener<DCUser>() {
            @Override
            public void onFailure(DCError error) {
                if (responseListener != null)
                    responseListener.onFailure(error);
            }

            @Override
            public void onResponse(DCUser object) {
                if (object != null)
                    DCSession.getInstance().setUser(object);

                if (responseListener != null)
                    responseListener.onResponse(object);
            }
        };

        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCUser.class));
    }

    /**
     * Update the user object
     * @param user
     * @param responseListener
     */
    public void patchUser(DCUser user, final DCResponseListener<DCUser> responseListener) {
        if (user == null)
            return;

        String endpoint = buildPath(ENDPOINT_USER, null);
        String payload = gsonExopse.toJson(user);

        //FIXME: fix null values serialization
        try {
            JsonObject jsonObject = (JsonObject) gsonExopse.toJsonTree(user);
            if (jsonObject != null) {
                if (jsonObject.get("avatar_64") == null && jsonObject.get("avatar") == null) {
                    jsonObject.addProperty("avatar_64", false);
                    jsonObject.addProperty("avatar",false);
                }
                payload = jsonObject.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, payload);
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);

        DCResponseListener<DCUser> listener = new DCResponseListener<DCUser>() {
            @Override
            public void onFailure(DCError error) {
                if (responseListener != null)
                    responseListener.onFailure(error);
            }

            @Override
            public void onResponse(DCUser object) {
                if (object != null)
                    DCSession.getInstance().setUser(object);

                if (responseListener != null)
                    responseListener.onResponse(object);
            }
        };

        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCUser.class));
    }

    /**
     * Logout the current user / invalidate the jwt token
     * @param listener
     */
    public void logout(final DCResponseListener<Void> listener) {
        String endpoint = buildPath(ENDPOINT_LOGOUT, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, Void.class));
    }

    /**
     * Retrieve current dashboard data
     * @param listener
     */
    public void getDashboard(final DCResponseListener<DCDashboard> listener) {
        String endpoint = buildPath(ENDPOINT_DASHBOARD, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCDashboard.class));
    }

    /**
     * Retrieves list of user records
     * @param listener
     */
    public void getRecords(DCResponseListener<DCActivityRecordsResponse> listener) {
        String endpoint = buildPath(ENDPOINT_RECORDS, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCActivityRecordsResponse.class));
    }

    /**
     * Adds an activity records
     * @param record
     * @param listener
     */
    public void postRecord(DCActivityRecord record, DCResponseListener<DCActivityRecord> listener) {
        if (record == null)
            return;

        String endpoint = buildPath(ENDPOINT_RECORDS, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(record));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);

        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCActivityRecord.class));
    }

    public void syncRecords(DCActivityRecord[] records, DCResponseListener<DCRecordsSyncResponse> listener) {
        if (records == null || records.length == 0)
            return;

        String endpoint = buildPath(ENDPOINT_SYNC_RECORDS, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(records));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);

        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCRecordsSyncResponse.class));
    }

    /**
     * Enqueues a transaction request
     * @param transaction
     * @param listener
     */
    public void postTransaction(DCTransaction transaction, DCResponseListener<Void> listener) {
        if (transaction == null)
            return;

        String endpoint = buildPath(ENDPOINT_TRANSACTIONS, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(transaction));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, Void.class));
    }

    /**
     * Retrieve user requested transactions
     */
    public void getTransactions(DCResponseListener<DCTransaction[]> listener) {
        String endpoint = buildPath(ENDPOINT_TRANSACTIONS, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCTransaction[].class));
    }

    /**
     * Retrieve oral health news
     * @param listener
     */
    public void getOralHealth(DCResponseListener<DCOralHealthItem[]> listener) {
        String endpoint = buildPath(ENDPOINT_ORAL_HEALTH, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCOralHealthItem[].class));
    }

    /**
     * Retrieve goals
     * @param listener
     */
    public void getGoals(DCResponseListener<DCGoal[]> listener) {
        String endpoint = buildPath(ENDPOINT_GOALS, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCGoal[].class));
    }


    /**
     * Send reset password request for the given email
     * @param resetPassword
     * @param listener
     */
    public void resetPassword(DCResetPassword resetPassword, DCResponseListener<Void> listener) {
        String endpoint = buildPath(ENDPOINT_RESET_PASSWORD, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(resetPassword));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, Void.class));
    }

    /**
     * Downloads bitmap from given url
     * NB! intended for small files, avatars and etc
     * @param src
     * @param listener
     */
    public void downloadBitmap(String src, final DCResponseListener<Bitmap> listener) {
        final Request request = new Request.Builder()
                .url(src)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null)
                    listener.onFailure(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bitmap result = null;
                try {
                    InputStream stream = response.body().byteStream();
                    result = BitmapFactory.decodeStream(stream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    if (result != null) {
                        listener.onResponse(result);
                    } else {
                        listener.onFailure(null);
                    }
                }
            }
        });
    }

    /**
     * Request a new captcha
     * @param listener
     */
    public void getCaptcha(final DCResponseListener<DCCaptchaResponse> listener) {
        String endpoint = buildPath(ENDPOINT_CAPTCHA, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCCaptchaResponse.class));
    }

    /**
     * Request confirmation email
     * @param listener
     */
    public void confirmEmail(final DCResponseListener<Void> listener) {
        String endpoint = buildPath(ENDPOINT_EMAIL_CONFIRM, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, Void.class));
    }

    /**
     * Create a http request
     * @param method
     * @param endpoint
     * @param body
     * @return
     */
    public Request buildRequest(RequestMethod method, String endpoint, RequestBody body) {
        Request.Builder builder = new Request.Builder();
        builder.url(endpoint);
        builder.method(method.getMethod(), body);

        if (DCSession.getInstance().getAuthTokenString() != null)
            builder.addHeader(HEADER_KEY_TOKEN, "Bearer " + DCSession.getInstance().getAuthTokenString());

        String fbmToken = FirebaseInstanceId.getInstance().getToken();
        if (fbmToken != null) {
            builder.addHeader(HEADER_KEY_FBM, fbmToken);
        }

        return builder.build();
    }
}
