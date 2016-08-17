package com.eightunity.facebookauthentication.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.eightunity.facebookauthentication.constant.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by deksen on 8/12/16 AD.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String PARAM_USERNAME = "fb_email";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    private AccountManager mAccountManager;
    protected Button mCloseButton;
    private String mUsername;
    protected ProgressDialog mLoading;
    protected boolean mRequestNewAccount = false;
    private CallbackManager mCallbackManager;
    public final Handler mHandler = new Handler();
    private SessionStatusCallback mStatusCallback = new SessionStatusCallback();
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mCloseButton = new Button(this);
        mCloseButton.setText("Close");
        mCloseButton.setPadding(15, 20, 15, 15);
        mCloseButton.setGravity(Gravity.CENTER);
        mCloseButton.setVisibility(View.GONE);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(mCloseButton);

        setContentView(layout);

        mLoading = new ProgressDialog(this);
        mLoading.setTitle("TEST FACEBBBOOOK");
        mLoading.setMessage("Loading ... ");
        mLoading.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mHandler.post(new Runnable() {
                    public void run() {
                        LoginActivity.this.finish();
                    }
                });
            }
        });

        mAccountManager = AccountManager.get(this);

        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(PARAM_USERNAME);
        mRequestNewAccount = (mUsername == null);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, mStatusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionStatusCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLoading.show();
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,first_name,middle_name,last_name,link,email");
            GraphRequest gr = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "me", parameters, HttpMethod.GET, new getUserInfo());
            gr.executeAsync();
        }

        @Override
        public void onCancel() {
            mCloseButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(FacebookException error) {
            mCloseButton.setVisibility(View.VISIBLE);
        }
    }

    public class getUserInfo implements GraphRequest.Callback {

        @Override
        public void onCompleted(GraphResponse response) {
            Log.e("response profile", response.toString());

            if (response.getError() == null) {
                try {
                    JSONObject userInfo = response.getJSONObject();
                    String username = userInfo.getString("email");
                    if (username == null ||
                            username.length() == 0) {
                        username = AccessToken.getCurrentAccessToken().getToken();
                    }
                    final String accessToken = AccessToken.getCurrentAccessToken().getToken();

                    Account account;
                    if (mRequestNewAccount) {
                        account = new Account(username, Constants.ACCOUNT_TYPE);
                        mAccountManager.addAccountExplicitly(account, accessToken, null);
                    } else {
                        account = new Account(mUsername, Constants.ACCOUNT_TYPE);
                        mAccountManager.setPassword(account, accessToken);
                    }
                    final String finalUsername = username;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mLoading != null) {
                                mLoading.dismiss();
                            }
                            final Intent intent = new Intent();
                            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, finalUsername);
                            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
                            intent.putExtra(AccountManager.KEY_AUTHTOKEN, accessToken);
                            setAccountAuthenticatorResult(intent.getExtras());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoading != null) {
            try {
                mLoading.dismiss();
            } catch (Exception e) { }
        }
    }
}
