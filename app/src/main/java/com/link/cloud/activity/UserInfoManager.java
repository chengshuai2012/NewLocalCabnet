package com.link.cloud.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.link.cloud.R;
import com.link.cloud.bean.AllUser;
import com.link.cloud.bean.CabinetRecord;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by 49488 on 2019/1/19.
 */

public class UserInfoManager extends Activity{
    private RealmResults<AllUser> cabinetUserDatails;
    ArrayList<AllUser> cabinetUserDatailArrayList = new ArrayList<>();
    private EditText no;
    private Realm realm;
    private MyBaseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_manager);
        no = findViewById(R.id.cabinetNo);
        findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        realm = Realm.getDefaultInstance();
        findViewById(R.id.query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = no.getText().toString();
                if(TextUtils.isEmpty(text)){
                    Toast.makeText(UserInfoManager.this,"请输入柜号",Toast.LENGTH_LONG).show();
                    return;
                };
                cabinetUserDatails = realm.where(AllUser.class).equalTo("phone",text).findAll();
                cabinetUserDatailArrayList.clear();
                cabinetUserDatailArrayList.addAll(realm.copyFromRealm(cabinetUserDatails));
                adapter.notifyDataSetChanged();
            }
        });


        ListView viewById = findViewById(R.id.lv);
        adapter = new MyBaseAdapter();
        cabinetUserDatails = realm.where(AllUser.class).findAll();
        cabinetUserDatailArrayList.clear();
        cabinetUserDatailArrayList.addAll(realm.copyFromRealm(cabinetUserDatails));
        viewById.setAdapter(adapter);
    }
    class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return cabinetUserDatailArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final  int i, View view, ViewGroup viewGroup) {
            View view1 =null;
            if(view==null){
                view1 = View.inflate(UserInfoManager.this,R.layout.user_detail_item,null);
            }else {
                view1= view;
            }
            TextView phone = view1.findViewById(R.id.phone);
            TextView time = view1.findViewById(R.id.time);
            TextView delete = view1.findViewById(R.id.delete);

            phone.setText(cabinetUserDatailArrayList.get(i).getPhone());
            time.setText(cabinetUserDatailArrayList.get(i).getCreateTime());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(UserInfoManager.this);
                    builder1.setMessage("确定清除该会员？");
                    builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int iii) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    cabinetUserDatails.get(i).deleteFromRealm();
                                    cabinetUserDatailArrayList.clear();
                                    cabinetUserDatailArrayList.addAll(realm.copyFromRealm(cabinetUserDatails));
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    builder1.show();

                }
            });
            return view1;
        }
    }
}
