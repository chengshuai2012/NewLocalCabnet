package com.link.cloud.activity;

import android.app.Activity;
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
import com.link.cloud.bean.CabinetRecord;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by 49488 on 2019/1/19.
 */

public class UseDetailActivity extends Activity{
    private RealmResults<CabinetRecord> cabinetUserDatails;
    ArrayList<CabinetRecord> cabinetUserDatailArrayList = new ArrayList<>();
    private EditText no;
    private Realm realm;
    private MyBaseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_detail);
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
                    Toast.makeText(UseDetailActivity.this,"请输入柜号",Toast.LENGTH_LONG).show();
                    return;
                };
                cabinetUserDatails = realm.where(CabinetRecord.class).equalTo("cabinetNumber",text).findAll().sort("creatTime", Sort.DESCENDING);
                cabinetUserDatailArrayList.clear();
                cabinetUserDatailArrayList.addAll(realm.copyFromRealm(cabinetUserDatails));
                adapter.notifyDataSetChanged();
            }
        });


        ListView viewById = findViewById(R.id.lv);
        adapter = new MyBaseAdapter();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 =null;
            if(view==null){
                view1 = View.inflate(UseDetailActivity.this,R.layout.user_detail,null);
            }else {
                view1= view;
            }
            TextView phone = view1.findViewById(R.id.phone);
            TextView time = view1.findViewById(R.id.time);
            TextView open_o = view1.findViewById(R.id.open_o);

            phone.setText(cabinetUserDatailArrayList.get(i).getPhoneNum());
            time.setText(cabinetUserDatailArrayList.get(i).getOpentime());
            open_o.setText(cabinetUserDatailArrayList.get(i).getCabinetStating());
            return view1;
        }
    }

}
