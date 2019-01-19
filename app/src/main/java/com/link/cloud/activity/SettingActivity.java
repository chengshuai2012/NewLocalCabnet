package com.link.cloud.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.link.cloud.CabinetApplication;
import com.link.cloud.R;
import com.link.cloud.bean.CabinetNumber;
import com.link.cloud.bean.CabinetRecord;
import com.link.cloud.veune.OpenDoorUtil;

import java.io.IOException;
import java.util.List;

import android_serialport_api.SerialPort;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by OFX002 on 2018/8/27.
 */

public class SettingActivity extends Activity {
    @BindView(R.id.et_input)
    EditText et_input;
    @BindView(R.id.clear_one)
    EditText clear_one;
    @BindView(R.id.password_manager)
    EditText password_manager;
    @BindView(R.id.open_cabinetnum)
    Button openCabinetnum;
    @BindView(R.id.open_lockPlate)
    Button openLockPlate;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.et_lockPlate)
    EditText et_lockPlate;
    @BindView(R.id.et_cabinet_count)
    EditText et_cabinet_count;
    @BindView(R.id.start_lock)
    EditText start_lock;
    @BindView(R.id.start_number)
    EditText start_num;
    @BindView(R.id.cabinet_add)
    Button cabinetAdd;
    @BindView(R.id.cabinet_delect)
    Button cabinetDelect;
    @BindView(R.id.adminmessage)
    LinearLayout adminmessage;
    private Realm realm;
    OpenDoorUtil openDoorUtil;
    public SerialPort serialpprt_wk1 = null;
    public SerialPort serialpprt_wk2 = null;
    public SerialPort serialpprt_wk3 = null;
    private String input;
    SerialPort serialPort;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        realm = Realm.getDefaultInstance();
        bind = ButterKnife.bind(this);
        openDoorUtil = new OpenDoorUtil();
        serialpprt_wk1 = ((CabinetApplication) getApplicationContext()).serialPortOne;
        serialpprt_wk2 = ((CabinetApplication) getApplicationContext()).serialPortTwo;
        serialpprt_wk3 = ((CabinetApplication) getApplicationContext()).serialPortThree;

    }

    @OnClick({R.id.cabinet_add, R.id.cabinet_delect, R.id.open_lockPlate, R.id.open_cabinetnum, R.id.back,
            R.id.add_manager, R.id.back_system_setting, R.id.back_system_main, R.id.clear_all, R.id.searh_lock_info,
            R.id.user_detail, R.id.confirm_modify, R.id.clear_one_confirm, R.id.user_info
    })

    public void OnClick(View view) {
        input = et_input.getText().toString().trim();
        switch (view.getId()) {
            case R.id.back_system_setting:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;
            case R.id.back_system_main:
                Intent intent1 = new Intent(Intent.ACTION_MAIN, null);
                intent1.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent1);
                break;
            case R.id.clear_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("确定清除所有柜子？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       clean_all();
                    }
                });
                builder.show();

                break;
            case R.id.searh_lock_info:
                startActivity(new Intent(this,LockInfoActivity.class));
                break;
            case R.id.user_detail:
                startActivity(new Intent(this,UseDetailActivity.class));
                break;
            case R.id.confirm_modify:
                String s = password_manager.getText().toString();
                if(TextUtils.isEmpty(s)){
                    Toast.makeText(this, "请输入要修改的密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences userInfo= getSharedPreferences("user_info", 0);
                userInfo.edit().putString("devicepwd", s).commit();
                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear_one_confirm:
                String s1 = clear_one.getText().toString();
                if(TextUtils.isEmpty(s1)){
                    Toast.makeText(this, "请输入柜号", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("确定清除该柜子？");
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clean_cabinetnum();
                    }
                });
                builder1.show();
                break;
            case R.id.user_info:
                startActivity(new Intent(this,UserInfoManager.class));
                break;
            case R.id.add_manager:
                startActivity(new Intent(this, BindManagerActivity.class));
                finish();
                break;
            case R.id.cabinet_add:
                Log.e("OnClick: ", "caac");
                if (TextUtils.isEmpty(et_lockPlate.getText().toString().trim())) {
                    Toast.makeText(SettingActivity.this, "请输入锁板号", Toast.LENGTH_LONG).show();
                } else if (!TextUtils.isEmpty(et_lockPlate.getText().toString().trim()) && TextUtils.isEmpty(et_cabinet_count.getText().toString().trim())) {
                    Toast.makeText(SettingActivity.this, "请输入添加的柜子数量", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(start_lock.getText().toString().trim())) {
                    Toast.makeText(SettingActivity.this, "请输入起始线路号", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(start_num.getText().toString().trim())) {
                    Toast.makeText(SettingActivity.this, "请输入起始柜号", Toast.LENGTH_LONG).show();
                } else if (!TextUtils.isEmpty(start_num.getText().toString().trim()) && !TextUtils.isEmpty(start_lock.getText().toString().trim()) && !TextUtils.isEmpty(et_lockPlate.getText().toString().trim()) && !TextUtils.isEmpty(et_cabinet_count.getText().toString().trim())) {

                    if (Integer.parseInt(start_lock.getText().toString().trim()) > 10 || Integer.parseInt(start_lock.getText().toString().trim()) < 1 || Integer.parseInt(start_num.getText().toString().trim()) < 1) {
                        Toast.makeText(SettingActivity.this, "请输入正确的线路号或柜号", Toast.LENGTH_LONG).show();
                    } else {
                        long cabinetNumber = realm.where(CabinetNumber.class).equalTo("cabinetNumber", start_num.getText().toString().trim()).count();
                        if (cabinetNumber != 0) {
                            Toast.makeText(SettingActivity.this, "不能添加相同的柜号", Toast.LENGTH_LONG).show();
                        } else {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (int i = 0; i < Integer.parseInt(et_cabinet_count.getText().toString().trim()); i++) {
                                        CabinetNumber cabinetNumber = new CabinetNumber();
                                        cabinetNumber.setCabinetLockPlate(et_lockPlate.getText().toString());
                                        cabinetNumber.setCircuitNumber(Integer.parseInt(start_lock.getText().toString().trim()) + i + "");
                                        cabinetNumber.setIsUser("可用");
                                        cabinetNumber.setCabinetNumber(Integer.parseInt(start_num.getText().toString().trim()) + i + "");
                                        realm.copyToRealm(cabinetNumber);
                                    }
                                }
                            });

                        }
                    }
                } else if (Integer.parseInt(et_lockPlate.getText().toString()) > 30 && Integer.parseInt(et_lockPlate.getText().toString()) < 0) {
                    Toast.makeText(SettingActivity.this, "不能添加超过范围的锁板", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.open_cabinetnum:
                if (!TextUtils.isEmpty(input)) {
                    List<CabinetNumber> list = realm.where(CabinetNumber.class).equalTo("cabinetNumber", input).findAll();
                    if (list.size() > 0) {
                        int lockPlate = Integer.parseInt(list.get(0).getCabinetLockPlate());
                        int circuit = Integer.parseInt(list.get(0).getCircuitNumber());
                        if (lockPlate <= 10) {
                            try {
                                serialpprt_wk1.getOutputStream().write(openDoorUtil.openOneDoor(lockPlate, circuit));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (lockPlate < 20 && lockPlate > 10) {
                            try {
                                serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(lockPlate % 10, circuit));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (lockPlate < 30 && lockPlate > 20) {
                            try {
                                serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(lockPlate % 10, circuit));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (lockPlate == 20) {
                            try {
                                serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(10, circuit));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (lockPlate == 30) {
                            try {
                                serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(10, circuit));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                CabinetRecord cabinetRecord1 = new CabinetRecord();
                                cabinetRecord1.setMemberName(getResources().getString(R.string.manager));
                                cabinetRecord1.setPhoneNum("***********");
                                cabinetRecord1.setCabinetNumber(input + "");
                                cabinetRecord1.setCabinetStating(getResources().getString(R.string.manager_open));
                                cabinetRecord1.setOpentime(opentime);
                                realm.copyToRealm(cabinetRecord1);
                            }
                        });
                    } else {
                        Toast.makeText(SettingActivity.this, "柜号不存在", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "请输入柜号", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.open_lockPlate:
                try {

                    serialpprt_wk1.getOutputStream().write(openDoorUtil.openAllDoor());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {

                    serialpprt_wk2.getOutputStream().write(openDoorUtil.openAllDoor());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    serialpprt_wk3.getOutputStream().write(openDoorUtil.openAllDoor());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.back:
                finish();
                break;

            case R.id.cabinet_delect:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("确定清除该锁板？");
                builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(et_lockPlate.getText().toString().trim())) {
                            final RealmResults<CabinetNumber> cabinetNumber = realm.where(CabinetNumber.class).equalTo("cabinetLockPlate", et_lockPlate.getText().toString().trim()).findAll();
                            if (cabinetNumber.size() > 0) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        cabinetNumber.deleteAllFromRealm();
                                    }
                                });
                            } else {
                                Toast.makeText(SettingActivity.this, "锁板号不存在", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(SettingActivity.this, "请输入锁板号", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                Toast.makeText(SettingActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                builder2.show();

                break;
        }
    }

    String opentime;
    private void clean_all(){
        final RealmResults<CabinetNumber> allUseCabinetNumber = realm.where(CabinetNumber.class).equalTo("isUser", "使用中").findAll();
        if (allUseCabinetNumber.size()>0){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    while (allUseCabinetNumber.size()>0){
                        allUseCabinetNumber.get(0).setIsUser("可用");
                    }
                }
            });
        }
        Toast.makeText(SettingActivity.this,"清除成功",Toast.LENGTH_LONG).show();
    }
    private void clean_cabinetnum(){
        String cbnum =clear_one.getText().toString().trim();
        final RealmResults<CabinetNumber> clearCabinetNumber = realm.where(CabinetNumber.class).equalTo("cabinetNumber", cbnum).findAll();
        if (clearCabinetNumber.size()>0){
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        clearCabinetNumber.get(0).setIsUser("可用");
                        clearCabinetNumber.get(0).setPhone("");
                        clearCabinetNumber.get(0).setPassWord("");
                        realm.copyToRealm(clearCabinetNumber);
                        Toast.makeText(SettingActivity.this,"清除成功",Toast.LENGTH_LONG).show();
                    }
                });
        }else {
            Toast.makeText(SettingActivity.this,"该柜号不存在",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        bind.unbind();
    }


}
