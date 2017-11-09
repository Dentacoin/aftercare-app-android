package com.dentacoin.dentacare.network;

import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.response.DCAuthToken;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.google.gson.JsonSyntaxException;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 8/3/17.
 */

public class DCSession {

    private static DCSession instance;

    private DCAuthToken authToken;
    private DCUser user;

    public synchronized static DCSession getInstance() {
        if (instance == null)
            instance = new DCSession();

        return instance;
    }

    private DCSession() { }

    public void setUser(DCUser user) {
        this.user = user;
        if (user != null) {
            DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.USER, DCApiManager.gson.toJson(user));

            String lastLoggedIn = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.LAST_LOGGED_EMAIL);

            if (lastLoggedIn != null && lastLoggedIn.compareTo(user.getEmail()) != 0) {
                DCSharedPreferences.removeKey(DCSharedPreferences.DCSharedKey.SHOWED_TUTORIALS);
            }
            
            DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.LAST_LOGGED_EMAIL, user.getEmail());

            if (DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.FIRST_LOGIN_DATE) == null) {
                Date now = new Date();
                DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.FIRST_LOGIN_DATE, now.toString());
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
    }
}
