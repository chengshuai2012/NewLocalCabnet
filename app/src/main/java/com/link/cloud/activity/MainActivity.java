package com.link.cloud.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.link.cloud.CabinetApplication;
import com.link.cloud.R;
import com.link.cloud.bean.AllUser;
import com.link.cloud.bean.CabinetInfo;
import com.link.cloud.listener.DialogCancelListener;
import com.link.cloud.veune.DialogUtils;
import com.link.cloud.veune.RxTimerUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends Activity implements View.OnClickListener, DialogCancelListener {

    private RxTimerUtil rxTimerUtil;
    String phone;
    private RealmResults<AllUser> managersRealm;
    private RealmResults<CabinetInfo> cabinetInfos;
    List<AllUser> managers = new ArrayList<>();
    private Realm realm;
    private DialogUtils dialogUtils;
    boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView bind = findViewById(R.id.bind);
        LinearLayout return_lock = findViewById(R.id.return_lock);
        LinearLayout open_lcok = findViewById(R.id.open_lock);
        TextView manager = findViewById(R.id.manager);
        manager.setOnClickListener(this);
        bind.setOnClickListener(this);
        return_lock.setOnClickListener(this);
        open_lcok.setOnClickListener(this);
        rxTimerUtil = new RxTimerUtil();
        realm = Realm.getDefaultInstance();
        managersRealm = realm.where(AllUser.class).equalTo("isadmin", 1).findAll();
        managers.addAll(realm.copyFromRealm(managersRealm));
        managersRealm.addChangeListener(new RealmChangeListener<RealmResults<AllUser>>() {
            @Override
            public void onChange(RealmResults<AllUser> allUsers) {
                managers.clear();
                managers.addAll(realm.copyFromRealm(managersRealm));
            }
        });
        dialogUtils = DialogUtils.getDialogUtils(this);
        dialogUtils.setDialogCanceListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind:
                View okView = View.inflate(this,R.layout.verify_success,null);
                dialogUtils.showPayOkDialog(okView,"aaaaaa");
                //startActivity(new Intent(this, BindActivity.class));
                break;
            case R.id.return_lock:
                isOpen =false;
                View veune_return_dialog = View.inflate(this, R.layout.veune_open_dialog, null);
                dialogUtils.showOpenDialog(veune_return_dialog,isOpen);
                isOpen = false;
                break;
            case R.id.open_lock:
                isOpen = true;
                View veune_open_dialog = View.inflate(this, R.layout.veune_open_dialog, null);
                dialogUtils.showOpenDialog(veune_open_dialog,isOpen);

                break;

            case R.id.manager:
                rxTimerUtil.timer(1000, new RxTimerUtil.IRxNext() {
                    @Override
                    public void doNext(long number) {
                        Log.e(TAG, "doNext: ");
                        int state = CabinetApplication.getVenueUtils().getState();

                        if (state == 3) {
                            phone = null;
                            phone = CabinetApplication.getVenueUtils().identifyNewImg(managers);
                            if (phone != null) {
                                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.no_manager), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }
                });
                View manager = View.inflate(this, R.layout.veune_dialog, null);
                dialogUtils.showManagerDialog(manager);
                break;
        }
    }

    public void openLock(String cabinet, String password) {
        Log.e("openLock: ", cabinet+password);
    }

    public void gotoSetting(String password) {
        Log.e("gotoSetting: ", password);
    }

    @Override
    public void dialogCancel() {
        rxTimerUtil.cancel();
        Log.e("dialogCancel: ", "cancel");
    }

    @Override
    public void onVenuePay() {

    }
}
