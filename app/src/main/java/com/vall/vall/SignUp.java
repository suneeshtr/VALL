package com.vall.vall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;


public class SignUp extends BaseActivity {
    private ProgressDialog progressDialog;
    private boolean isWifiConnected;
    private String usr,pws,name;
    private static final String TAG = SignUp.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        QBSettings.getInstance().fastConfigInit(Consts.APP_ID, Consts.AUTH_KEY, Consts.AUTH_SECRET);

        final EditText ph = (EditText) findViewById(R.id.input_email);
        final EditText pw = (EditText) findViewById(R.id.input_password);
        final EditText user = (EditText) findViewById(R.id.input_name);
        TextView sign_in = (TextView) findViewById(R.id.link_login);
        Button sign_up = (Button) findViewById(R.id.btn_signup);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWifiConnected) {
                    pws = pw.getText().toString();
                    usr = ph.getText().toString();
                    name = user.getText().toString();
                    QbAuthe();
                } else {
                    showToast(R.string.internet_not_connected);
                }
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void QbAuthe() {
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {

            @Override
            public void onSuccess(QBSession session, Bundle params) {
                final QBUser user = new QBUser(usr, pws);
                user.setFullName(name);
                StringifyArrayList<String> tags = new StringifyArrayList<String>();
                tags.add("webrtcusersts");
                tags.add("webrtcusers");
                user.setTags(tags);

                QBUsers.signUp(user, new QBEntityCallbackImpl<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {
                        startIncomeCallListenerService(usr, pws, Consts.LOGIN);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        Toast.makeText(getApplicationContext(), errors.get(0),
                            Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {

            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
