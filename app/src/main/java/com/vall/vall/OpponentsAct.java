package com.vall.vall;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class OpponentsAct extends BaseActivity {


    private static final String TAG = OpponentsAct.class.getSimpleName();
    private opAdapter opponentsAdapter;
//    private QBUser user;
    private ProgressDialog progressDialog;
    private RecyclerView opponentsListView;
    private ArrayList<QBUser> opponentsList,user;
    private boolean isWifiConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rec_opponents);

        initActionBar();
        initUI();
        initProgressDialog();
        initOpponentListAdapter();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this) {
            @Override
            public void onBackPressed() {
                Toast.makeText(OpponentsAct.this, getString(R.string.wait_until_loading_finish), Toast.LENGTH_SHORT).show();
            }
        };
        progressDialog.setMessage(getString(R.string.load_opponents));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void initOpponentListAdapter() {
        final ListView opponentsList = (ListView) findViewById(R.id.opponentsList);
        List<QBUser> users = getOpponentsList();

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPerPage(100);

        if (users == null) {
            List<String> tags = new LinkedList<>();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if(cur.getCount()>0)

            {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone = phone.replaceAll("\\s","");
                            String usr = phone.length() > 10 ? phone.substring(phone.length() - 10) : phone;
                            tags.add(usr);
                        }
                        pCur.close();

                    }
                }
            }
            QBUsers.getUsersByLogins(tags, requestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                    ArrayList<QBUser> orderedUsers = reorderUsersByName(qbUsers);
                    setOpponentsList(orderedUsers);
                    prepareUserList(opponentsList, orderedUsers);
                    hideProgressDialog();
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(List<String> strings) {
                    for (String s : strings) {
                        Log.d(TAG, s);
                    }
                    OpponentsAdapter.i = 0;
                    stopIncomeCallListenerService();
                    clearUserDataFromPreferences();
                    startListUsersActivity();
                    finish();
                }
            });
        } else {
            ArrayList<QBUser> userList = getOpponentsList();
            prepareUserList(opponentsList, userList);
            hideProgressDialog();
        }
    }
    public void setOpponentsList(ArrayList<QBUser> qbUsers) {
        this.opponentsList = qbUsers;
    }

    public ArrayList<QBUser> getOpponentsList() {
        return opponentsList;
    }

    private void hideProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void prepareUserList(ListView opponentsList, List<QBUser> users) {
        QBUser currentUser = QBChatService.getInstance().getUser();

        if (users.contains(currentUser)) {
            users.remove(currentUser);
        }

        // Prepare users list for simple adapter.
        opponentsAdapter = new opAdapter(users);
        opponentsListView.setAdapter(opponentsAdapter);
    }

    private void initUI() {

//        btnAudioCall = (Button) findViewById(R.id.btnAudioCall);
//        btnVideoCall = (Button) findViewById(R.id.btnVideoCall);
////
////        btnAudioCall.setOnClickListener(this);
//        btnVideoCall.setOnClickListener(this);

        opponentsListView = (RecyclerView) findViewById(R.id.opponentsList);
        opponentsListView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        opponentsListView.setLayoutManager(mLayoutManager);
        opponentsListView.addOnItemTouchListener(
                new RecyclerItemClickListener(OpponentsAct.this, opponentsListView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        vCall(position);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }

                })
        );
    }

    private void vCall(int position) {
        QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
        setActionButtonsClickable(false);
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("any_custom_data", "some data");
        userInfo.put("my_avatar_url", "avatar_reference");

        Log.d(TAG, "QBChatService.getInstance().isLoggedIn() = " + String.valueOf(QBChatService.getInstance().isLoggedIn()));
        user.clear();
        user.add(opponentsList.get(position));

        if (!isWifiConnected){
            showToast(R.string.internet_not_connected);
            setActionButtonsClickable(true);
        } else if (!QBChatService.getInstance().isLoggedIn()){
            showToast(R.string.initializing_in_chat);
            setActionButtonsClickable(true);
        }else if (isWifiConnected && QBChatService.getInstance().isLoggedIn()) {
            CallActivity.start(this, qbConferenceType, getOpponentsIds(user),
                    userInfo, Consts.CALL_DIRECTION_TYPE.OUTGOING);
        }
    }

//    @Override
//    public void onClick(View v) {
//
//        if (opponentsAdapter.getSelected().size() == 1) {
//            QBRTCTypes.QBConferenceType qbConferenceType = null;
//
//            //Init conference type
//            switch (v.getId()) {
//
//                case R.id.opponentsList:
//                    qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
//                    setActionButtonsClickable(false);
//                    break;
//            }
//
//            Map<String, String> userInfo = new HashMap<>();
//            userInfo.put("any_custom_data", "some data");
//            userInfo.put("my_avatar_url", "avatar_reference");
//
//            Log.d(TAG, "QBChatService.getInstance().isLoggedIn() = " + String.valueOf(QBChatService.getInstance().isLoggedIn()));
//
//            if (!isWifiConnected){
//                showToast(R.string.internet_not_connected);
//                setActionButtonsClickable(true);
//            } else if (!QBChatService.getInstance().isLoggedIn()){
//                showToast(R.string.initializing_in_chat);
//                setActionButtonsClickable(true);
//            }else if (isWifiConnected && QBChatService.getInstance().isLoggedIn()) {
//                CallActivity.start(this, qbConferenceType, getOpponentsIds(opponentsAdapter.getSelected()),
//                        userInfo, Consts.CALL_DIRECTION_TYPE.OUTGOING);
//            }
//
//        } else if (opponentsAdapter.getSelected().size() > 1){
//            Toast.makeText(this, getString(R.string.only_peer_to_peer_calls), Toast.LENGTH_LONG).show();
//        } else if (opponentsAdapter.getSelected().size() < 1){
//            Toast.makeText(this, getString(R.string.choose_one_opponent), Toast.LENGTH_LONG).show();
//        }
//    }

    private void setActionButtonsClickable(boolean isClickable) {
//        btnAudioCall.setClickable(isClickable);
        opponentsListView.setClickable(isClickable);
    }

    public static ArrayList<Integer> getOpponentsIds(List<QBUser> opponents){
        ArrayList<Integer> ids = new ArrayList<>();
        for(QBUser user : opponents){
            ids.add(user.getId());
        }
        return ids;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setActionButtonsClickable(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(progressDialog != null && progressDialog.isShowing()) {
            hideProgressDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                showLogOutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ArrayList<QBUser> reorderUsersByName(ArrayList<QBUser> qbUsers) {
        // Make clone collection to avoid modify input param qbUsers
        ArrayList<QBUser> resultList = new ArrayList<>(qbUsers.size());
        resultList.addAll(qbUsers);

        // Rearrange list by user IDs
        Collections.sort(resultList, new Comparator<QBUser>() {
            @Override
            public int compare(QBUser firstUsr, QBUser secondUsr) {
                if (firstUsr.getId().equals(secondUsr.getId())) {
                    return 0;
                } else if (firstUsr.getId() < secondUsr.getId()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return resultList;
    }
    @Override
    public void onBackPressed() {
        minimizeApp();
    }

    private void showLogOutDialog(){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle(R.string.log_out_dialog_title);
        quitDialog.setMessage(R.string.log_out_dialog_message);

        quitDialog.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OpponentsAdapter.i = 0;
                stopIncomeCallListenerService();
                clearUserDataFromPreferences();
                startListUsersActivity();
                finish();
            }
        });

        quitDialog.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }

    @Override
    void processCurrentConnectionState(boolean isConncted) {
        if (!isConncted) {
            Log.d(TAG, "Internet is turned off");
            isWifiConnected = false;
        } else {
            Log.d(TAG, "Internet is turned on");
            isWifiConnected = true;
        }
    }

    private void initConnectionErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OpponentsAct.this);
        builder.setMessage(R.string.NETWORK_ABSENT)
                .setCancelable(false)
                .setNegativeButton(R.string.ok_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                setActionButtonsClickable(false);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

