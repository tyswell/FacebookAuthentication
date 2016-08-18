package com.eightunity.facebookauthentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.eightunity.facebookauthentication.authenticator.LoginActivity;
import com.eightunity.facebookauthentication.constant.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private AccountManager accountManager;
    private Account account;
    private Bundle bnd;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        keyhash();
        setContentView(R.layout.activity_main);
        accountManager = AccountManager.get(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        account = getAccount();
//        if (account == null) {
//
//        }
//        checkAuthen();
        Log.d("TYS", String.valueOf(getAccount()));
        if (getAccount() == null) {
            mDialog = new Dialog(this);
            mDialog.setContentView(R.layout.no_account_actions);
            mDialog.setTitle("Select option");
            ((Button) mDialog.findViewById(R.id.add_account_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            mDialog.show();
        }
    }

    protected final void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public Account getAccount() {
        AccountManager am = AccountManager.get(this);
        Account [] accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }


//    private void keyhash() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.eightunity.facebookauthentication",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }

    public void checkAuthen() {
        AccountManagerFuture<Bundle> future = accountManager.getAuthTokenByFeatures(Constants.ACCOUNT_TYPE,
                "Full access",
                null,
                this,
                null,
                null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            bnd = future.getResult();
//                            Toast.makeText(getBaseContext(), bnd.getString(AccountManager.KEY_ACCOUNT_NAME), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                null);
    }
}
