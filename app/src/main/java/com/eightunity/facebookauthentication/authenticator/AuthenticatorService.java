package com.eightunity.facebookauthentication.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by deksen on 8/16/16 AD.
 */
public class AuthenticatorService extends Service {
    private static final String TAG = "AuthenticationService";
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.v(TAG, "SyncAdapter Authentication Service started.");
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "SyncAdapter Authentication Service stopped.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "getBinder()...  returning the AccountAuthenticator binder for intent " + intent);
        return mAuthenticator.getIBinder();
    }
}
