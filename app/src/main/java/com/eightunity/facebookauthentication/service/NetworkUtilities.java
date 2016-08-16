package com.eightunity.facebookauthentication.service;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

import com.eightunity.facebookauthentication.authenticator.Authenticator;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deksen on 8/12/16 AD.
 */
public class NetworkUtilities {

    private String mAccessToken;

    public NetworkUtilities(String token, Context context) {
        FacebookSdk.sdkInitialize(context);
        mAccessToken = token;
    }

    public boolean checkAccessToken() throws NetworkErrorException {
        try {
            Bundle param = new Bundle();
            param.putString("access_token", mAccessToken);
            param.putString("fields", "permission,status");

            GraphRequest gr = new GraphRequest(null, "me/permissions", param, HttpMethod.GET, null);
            GraphResponse rs = gr.executeAndWait();

            if (rs.getError() != null) {
                if (rs.getError().getErrorCode() == 190) {
                    return false;
                } else {
                    throw new NetworkErrorException(rs.getError().getErrorMessage());
                }
            }

            JSONObject json = rs.getJSONObject();
            JSONArray permissions = json.getJSONArray("data");
            List<String> aquiredPermission = new ArrayList<>();

            for (int i = 0; i < permissions.length(); i++) {
                JSONObject permission = permissions.getJSONObject(i);
                if (permission != null && permission.getString("status").equals("granted")) {
                    aquiredPermission.add(permission.getString("permission"));
                }
            }

            for (int i = 0; i < Authenticator.REQUIRED_PERMISSIONS.length; i++) {
                if (!aquiredPermission.contains(Authenticator.REQUIRED_PERMISSIONS[i])) {
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
