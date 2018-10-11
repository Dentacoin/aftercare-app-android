package com.dentacoin.dentacare.network;

import android.content.Context;

import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.response.DCAuthToken;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.DCUtils;
import com.google.gson.JsonSyntaxException;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 8/3/17.
 */

public class DCSession {

    private static DCSession instance;
    private DCAuthToken authToken;
    private DCUser user;
    private String socialAvatar;
    private String invitationToken;
    private String civicUUID;


    public synchronized static DCSession getInstance() {
        if (instance == null)
            instance = new DCSession();

        return instance;
    }

    private DCSession() { }


    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setCivicUUID(String civicUUID) {
        this.civicUUID = civicUUID;
    }

    public String getCivicUUID() {
        return civicUUID;
    }

    public void loadSocialAvatar(Context context) {
        DCUtils.getUserAvatarFromSocialMedia(context, url -> {
            socialAvatar = url;
        });
    }

    public boolean isChildUser() {
        if (getUser() != null) {
            return getUser().isChild();
        }
         return false;
    }
    public String getCurrentUserSocialAvatar() {
        return socialAvatar;
    }

    public void setUser(DCUser user) {
        this.user = user;
        if (user != null) {
            DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.USER, DCApiManager.gson.toJson(user));

            if (DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.FIRST_LOGIN_DATE) == null) {
                Date now = new Date();
                String stringDate = DCConstants.DATE_FORMAT.format(now);
                DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.FIRST_LOGIN_DATE, stringDate);
            }
        }
    }

    public DCUser getUser() {
        if (user == null) {
            String userString = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.USER);
            if (userString != null) {
                try {
                    user = DCApiManager.gson.fromJson(userString, DCUser.class);
                } catch (JsonSyntaxException e) {
                }
            }
        }
        return user;
    }

    public void setAuthToken(DCAuthToken authToken) {
        this.authToken = authToken;
        if (authToken != null) {
            DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.AUTH_TOKEN, DCApiManager.gson.toJson(authToken));
        }
    }

    public DCAuthToken getAuthToken() {
        if (authToken == null) {
            String token = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.AUTH_TOKEN);
            if (token != null) {
                try {
                    authToken = DCApiManager.gson.fromJson(token, DCAuthToken.class);
                } catch (JsonSyntaxException e) { }
            }
        }
        return authToken;
    }

    public String getAuthTokenString() {
        if (getAuthToken() != null) {
            return getAuthToken().getToken();
        }
        return null;
    }

    /**
     * Returns if the current session is valid
     * @return
     */
    public boolean isValid() {
        return getAuthToken() != null && getAuthToken().isValid();
    }

    /**
     * Clear the session
     */
    public void clear() {
        DCSharedPreferences.clean();
        DCDashboardDataProvider.getInstance().clear();
        authToken = null;
        user = null;
        socialAvatar = null;
    }

    /**
     * Clear session & partially prefs for child account switch
     */
    public void partialClear() {
        DCSharedPreferences.partialClean();
        DCDashboardDataProvider.getInstance().clear();;
        authToken = null;
        user = null;
        socialAvatar = null;
    }
}
