package com.dentacoin.dentacare.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.dentacoin.dentacare.BuildConfig;
import com.dentacoin.dentacare.model.DCAvatar;
import com.dentacoin.dentacare.model.DCChild;
import com.dentacoin.dentacare.model.DCChildLogin;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.model.DCGasPrice;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.model.DCInvitationAccept;
import com.dentacoin.dentacare.model.DCInvitationToken;
import com.dentacoin.dentacare.model.DCJourney;
import com.dentacoin.dentacare.model.DCOralHealthItem;
import com.dentacoin.dentacare.model.DCResetPassword;
import com.dentacoin.dentacare.model.DCRoutine;
import com.dentacoin.dentacare.model.DCTransaction;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.request.DCCaptcha;
import com.dentacoin.dentacare.network.response.DCAuthToken;
import com.dentacoin.dentacare.network.response.DCCaptchaResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
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
    private static final String ENDPOINT_USER_DELETE = "user/delete";
    private static final String ENDPOINT_DASHBOARD = "dashboard";
    private static final String ENDPOINT_TRANSACTIONS = "transactions";
    private static final String ENDPOINT_ORAL_HEALTH = "oralhealth";
    private static final String ENDPOINT_GOALS = "goals";
    private static final String ENDPOINT_RESET_PASSWORD = "reset";
    private static final String ENDPOINT_CAPTCHA = "captcha";
    private static final String ENDPOINT_EMAIL_CONFIRM = "confirm";
    private static final String ENDPOINT_JOURNEY = "journey";
    private static final String ENDPOINT_ROUTINE = "journey/routines";
    private static final String ENDPOINT_CHILDREN = "children";
    private static final String ENDPOINT_PATCH_CHILD = "children/{0}";
    private static final String ENDPOINT_FRIENDS = "friends";
    private static final String ENDPOINT_PATCH_FRIEND = "friends/{0}";
    private static final String ENDPOINT_FRIEND_GOALS = "friends/{0}/goals";
    private static final String ENDPOINT_FRIEND_DASHBOARD = "friends/{0}/dashboard";
    private static final String ENDPOINT_INVITATIONS_RETRIEVE = "invitations/request";
    private static final String ENDPOINT_INVITATIONS_REQUESTED = "invitations/requested/{0}";
    private static final String ENDPOINT_INVITATIONS_APPROVE = "invitations/approve";
    private static final String ENDPOINT_INVITATIONS_ACCEPT = "invitations/accept";
    private static final String ENDPOINT_INVITATIONS_DECLINE = "invitations/decline";
    private static final String ENDPOINT_LOGIN_CHILD = "children/login";

    private static final String ENDPOINT_GAS_PRICE = "https://dentacoin.net/gas-price";


    private static final String HEADER_KEY_TOKEN = "Authorization";
    private static final String HEADER_KEY_FBM ="FirebaseToken";

    public static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz")
            .create();

    public static final Gson gsonExopse = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz")
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
                if (jsonObject.get("email") != null) {
                    jsonObject.remove("email");
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
     * Delete user
     * @param captcha
     * @param responseListener
     */
    public void deleteUser(DCCaptcha captcha, final DCResponseListener<Void> responseListener) {
        if (captcha == null)
            return;
        
        String endpoint = buildPath(ENDPOINT_USER_DELETE, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(captcha));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(responseListener, Void.class));
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

    public void getGasPrice(DCResponseListener<DCGasPrice> listener) {
        Request request = buildRequest(RequestMethod.GET, ENDPOINT_GAS_PRICE, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCGasPrice.class));
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
     * Retrieve current journey
     * @param listener
     */
    public void getJourney(final DCResponseListener<DCJourney> listener) {
        String endpoint = buildPath(ENDPOINT_JOURNEY, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCJourney.class));
    }

    /**
     * Post a completed routine
     * @param routine
     * @param listener
     */
    public void postRoutine(DCRoutine routine, final DCResponseListener<DCRoutine> listener) {
        String endpoint = buildPath(ENDPOINT_ROUTINE, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gsonExopse.toJson(routine));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCRoutine.class));
    }

    /**
     * Create a new proxy child account
     * @param child
     * @param listener
     */
    public void postChild(DCChild child, final DCResponseListener<DCChild> listener) {
        String endpoint = buildPath(ENDPOINT_CHILDREN, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gsonExopse.toJson(child));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCChild.class));
    }

    /**
     * Update child account
     * @param id
     * @param child
     * @param listener
     */
    public void patchChild(int id, DCChild child, final DCResponseListener<DCChild> listener) {
        String path = MessageFormat.format(ENDPOINT_PATCH_CHILD, Integer.toString(id));
        String endpoint = buildPath(path, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gsonExopse.toJson(child));
        Request request = buildRequest(RequestMethod.PATCH, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCChild.class));
    }

    /**
     * Delete child account
     * @param id
     * @param listener
     */
    public void deleteChild(int id, final DCResponseListener<Void> listener) {
        String path = MessageFormat.format(ENDPOINT_PATCH_CHILD, Integer.toString(id));
        String endpoint = buildPath(path, null);
        Request request = buildRequest(RequestMethod.DELETE, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, Void.class));
    }

    /**
     * Retrieve family & friends
     * @param listener
     */
    public void getFriends(final DCResponseListener<DCFriend[]> listener) {
        String endpoint = buildPath(ENDPOINT_FRIENDS, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCFriend[].class));
    }


    /**
     * Retrieve achieved goals of a friend
     * @param friendId
     * @param listener
     */
    public void getFriendGoals(int friendId, final DCResponseListener<DCGoal[]> listener) {
        String friendGoalsPath = MessageFormat.format(ENDPOINT_FRIEND_GOALS, Integer.toString(friendId));
        String endpoint = buildPath(friendGoalsPath, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCGoal[].class));
    }

    /**
     * Retrieve dashboard of a friend
     * @param friendId
     * @param listener
     */
    public void getFriendStatistics(int friendId, final DCResponseListener<DCDashboard> listener) {
        String friendDashboardPath = MessageFormat.format(ENDPOINT_FRIEND_DASHBOARD, Integer.toString(friendId));
        String endpoint = buildPath(friendDashboardPath, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCDashboard.class));
    }


    /**
     * Retrieve new invitation token
     * @param listener
     */
    public void getInvitationToken(final DCResponseListener<DCInvitationToken> listener) {
        String endpoint = buildPath(ENDPOINT_INVITATIONS_RETRIEVE, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(new ArrayList<>()));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCInvitationToken.class));
    }

    /**
     * Retrieve invitation info
     * @param token
     * @param listener
     */
    public void getInvitationRequest(String token, final DCResponseListener<DCUser> listener) {
        String path = MessageFormat.format(ENDPOINT_INVITATIONS_REQUESTED, token);
        String endpoint = buildPath(path, null);
        Request request = buildRequest(RequestMethod.GET, endpoint, null);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCUser.class));
    }

    /**
     * Approve invitation
     * @param token
     * @param listener
     */
    public void approveInvitation(String token, final DCResponseListener<Void> listener) {
        String endpoint = buildPath(ENDPOINT_INVITATIONS_APPROVE, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(new DCInvitationToken(token)));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, Void.class));
    }

    /**
     * Accept invitation
     * @param userId
     * @param listener
     */
    public void acceptInvitation(int userId, final DCResponseListener<Void> listener) {
        String endpoint = buildPath(ENDPOINT_INVITATIONS_ACCEPT, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(new DCInvitationAccept(userId)));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, Void.class));
    }

    /**
     * Decline invitation
     * @param userId
     * @param listener
     */
    public void declineInvitation(int userId, final DCResponseListener<Void> listener) {
        String endpoint = buildPath(ENDPOINT_INVITATIONS_DECLINE, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(new DCInvitationAccept(userId)));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, Void.class));
    }

    /**
     * Login as a child
     * @param id
     * @param listener
     */
    public void loginAsChild(int id, final DCResponseListener<DCAuthToken> listener) {
        String endpoint = buildPath(ENDPOINT_LOGIN_CHILD, null);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(new DCChildLogin(id)));
        Request request = buildRequest(RequestMethod.POST, endpoint, requestBody);
        client.newCall(request).enqueue(new DCResponseHandler<>(listener, DCAuthToken.class));
    }

    /**
     * Delete friend
     * @param id
     * @param listener
     */
    public void deleteFriend(int id, final DCResponseListener<Void> listener) {
        String path = MessageFormat.format(ENDPOINT_PATCH_FRIEND, Integer.toString(id));
        String endpoint = buildPath(path, null);
        Request request = buildRequest(RequestMethod.DELETE, endpoint, null);
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
