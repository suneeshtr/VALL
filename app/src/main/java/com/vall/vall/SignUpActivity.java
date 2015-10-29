package com.vall.vall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.quickblox.chat.QBChatService;

import io.fabric.sdk.android.Fabric;


/**
 * Created by tereha on 25.01.15.
 */
public class SignUpActivity extends BaseActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private boolean isWifiConnected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (QBChatService.isInitialized() && QBChatService.getInstance().isLoggedIn()) {
            startOpponentsActivity();
            finish();
        } else {
            Fabric.with(this, new Crashlytics());
            setContentView(R.layout.login_screen);

            signIn();
        }
    }

    private void signIn() {
        final EditText user = (EditText) findViewById(R.id.input_email);
        final EditText pw = (EditText) findViewById(R.id.input_password);
        Button sign_in = (Button) findViewById(R.id.btn_login);
        final TextView sign_up = (TextView) findViewById(R.id.link_signup);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWifiConnected) {
                    String login = user.getText().toString();
                    String password = pw.getText().toString();
                    initProgressDialog();
                    startIncomeCallListenerService(login, password, Consts.LOGIN);
                } else {
                    showToast(R.string.internet_not_connected);
                }
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWifiConnected) {
                    Intent i = new Intent(SignUpActivity.this, SignUp.class);
                    startActivity(i);
                    finish();
                } else {
                    showToast(R.string.internet_not_connected);
                }
            }
        });

    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this) {
            @Override
            public void onBackPressed() {
                Toast.makeText(SignUpActivity.this, getString(R.string.wait_until_login_finish), Toast.LENGTH_SHORT).show();
            }
        };
        progressDialog.setMessage(getString(R.string.processes_login));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideProgressDialog(boolean isLoginSuccess) {
        if (progressDialog != null){
            progressDialog.dismiss();
            if (isLoginSuccess) {
                finish();
            }
        }
    }

    private void startOpponentsActivity(){
        Intent intent = new Intent(SignUpActivity.this, OpponentsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    void processCurrentConnectionState(boolean isConnected) {
        if (!isConnected) {
            isWifiConnected = false;
            Log.d(TAG, "Internet is turned off");
        } else {
            isWifiConnected = true;
            Log.d(TAG, "Internet is turned on");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Consts.CALL_ACTIVITY_CLOSE){
            if (resultCode == Consts.CALL_ACTIVITY_CLOSE_WIFI_DISABLED) {
                Toast.makeText(this, getString(R.string.WIFI_DISABLED),Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == Consts.LOGIN_RESULT_CODE){
            boolean isLoginSuccess = data.getBooleanExtra(Consts.LOGIN_RESULT, false);
            hideProgressDialog(isLoginSuccess);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
