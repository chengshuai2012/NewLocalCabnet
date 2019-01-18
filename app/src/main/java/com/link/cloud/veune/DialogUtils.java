package com.link.cloud.veune;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.link.cloud.R;
import com.link.cloud.activity.MainActivity;
import com.link.cloud.listener.DialogCancelListener;


/**
 * Created by OFX002 on 2018/9/21.
 */

public class DialogUtils implements View.OnClickListener {
    private AlertDialog dialog;
    StringBuilder builder = new StringBuilder();
    StringBuilder cabinetNo = new StringBuilder();
    StringBuilder passWord = new StringBuilder();
    Activity context;
    TextView inputTel;
    EditText inputCabinet, input_psw;
    DialogCancelListener listener;
    boolean isOpen;

    public void setDialogCanceListener(DialogCancelListener listener) {
        this.listener = listener;
    }

    private DialogUtils(Activity context) {
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    private static DialogUtils dialogUtils;

    public static synchronized DialogUtils getDialogUtils(Activity context) {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils(context);
        } else {
            dialogUtils = null;
            System.gc();
            dialogUtils = new DialogUtils(context);
        }
        return dialogUtils;
    }

    public void showManagerDialog(View view) {
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(770, 500);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView psw_login = view.findViewById(R.id.psw_login);
        ImageView close = view.findViewById(R.id.close);
        cancel.setOnClickListener(this);
        psw_login.setOnClickListener(this);
        close.setOnClickListener(this);
        dialog.setCancelable(false);
        params.leftMargin = 30;
        window.setContentView(view, params);
    }

    public void showOpenDialog(View view, boolean isOpen) {
        dialog.show();
        this.isOpen = isOpen;
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(770, 500);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView handy_venue_intro = view.findViewById(R.id.handy_venue_intro);
        TextView handy_venue = view.findViewById(R.id.handy_venue);
        TextView psw_login_open = view.findViewById(R.id.psw_login_open);
        if (isOpen) {
            handy_venue_intro.setText(context.getString(R.string.handy_venue_intro_open));
            handy_venue.setText(context.getString(R.string.handy_venue_open));
            psw_login_open.setText(context.getString(R.string.psw_open));
        } else {
            handy_venue_intro.setText(context.getString(R.string.handy_venue_intro_close));
            handy_venue.setText(context.getString(R.string.handy_venue_return));
            psw_login_open.setText(context.getString(R.string.psw_return));
        }
        TextView psw_login = view.findViewById(R.id.psw_login_open);
        ImageView close = view.findViewById(R.id.close);
        cancel.setOnClickListener(this);
        psw_login.setOnClickListener(this);
        close.setOnClickListener(this);
        dialog.setCancelable(false);
        params.leftMargin = 30;
        window.setContentView(view, params);
    }
    public void showPayOkDialog(View view,String msg) {
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        TextView pay_ok = view.findViewById(R.id.pay_ok);
        ImageView close_pay = view.findViewById(R.id.close);
        TextView back_home = view.findViewById(R.id.back_home);
        close_pay.setOnClickListener(this);
        pay_ok.setText(msg);
        back_home.setOnClickListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(770, 540);
        params.leftMargin = 30;
        window.setContentView(view, params);
    }

    public void showPsdDialog(View view) {
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(770, 869);
        inputTel = view.findViewById(R.id.input_tel);
        TextView bind_keypad_0 = view.findViewById(R.id.bind_keypad_0);
        TextView bind_keypad_1 = view.findViewById(R.id.bind_keypad_1);
        TextView bind_keypad_2 = view.findViewById(R.id.bind_keypad_2);
        TextView bind_keypad_3 = view.findViewById(R.id.bind_keypad_3);
        TextView bind_keypad_4 = view.findViewById(R.id.bind_keypad_4);
        TextView bind_keypad_5 = view.findViewById(R.id.bind_keypad_5);
        TextView bind_keypad_6 = view.findViewById(R.id.bind_keypad_6);
        TextView bind_keypad_7 = view.findViewById(R.id.bind_keypad_7);
        TextView bind_keypad_8 = view.findViewById(R.id.bind_keypad_8);
        TextView bind_keypad_9 = view.findViewById(R.id.bind_keypad_9);
        TextView bind_keypad_ok = view.findViewById(R.id.bind_keypad_ok);
        TextView bind_keypad_delect = view.findViewById(R.id.bind_keypad_delect);
        TextView confirm = view.findViewById(R.id.confirm);
        TextView venue_login = view.findViewById(R.id.venue_login);
        ImageView close = view.findViewById(R.id.close);
        close.setOnClickListener(this);
        venue_login.setOnClickListener(this);
        confirm.setOnClickListener(this);
        bind_keypad_0.setOnClickListener(this);
        bind_keypad_1.setOnClickListener(this);
        bind_keypad_2.setOnClickListener(this);
        bind_keypad_3.setOnClickListener(this);
        bind_keypad_4.setOnClickListener(this);
        bind_keypad_5.setOnClickListener(this);
        bind_keypad_6.setOnClickListener(this);
        bind_keypad_7.setOnClickListener(this);
        bind_keypad_8.setOnClickListener(this);
        bind_keypad_9.setOnClickListener(this);
        bind_keypad_ok.setOnClickListener(this);
        bind_keypad_delect.setOnClickListener(this);
        confirm.setOnClickListener(this);
        inputTel.setText(context.getResources().getString(R.string.manager_pwd));
        params.leftMargin = 30;
        window.setContentView(view, params);
    }

    public void showPsdOpenDialog(View view) {
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(770, 869);
        inputCabinet = view.findViewById(R.id.input_tel);
        input_psw = view.findViewById(R.id.input_psw);
        TextView bind_keypad_0 = view.findViewById(R.id.bind_keypad_0_open);
        TextView bind_keypad_1 = view.findViewById(R.id.bind_keypad_1_open);
        TextView bind_keypad_2 = view.findViewById(R.id.bind_keypad_2_open);
        TextView bind_keypad_3 = view.findViewById(R.id.bind_keypad_3_open);
        TextView bind_keypad_4 = view.findViewById(R.id.bind_keypad_4_open);
        TextView bind_keypad_5 = view.findViewById(R.id.bind_keypad_5_open);
        TextView bind_keypad_6 = view.findViewById(R.id.bind_keypad_6_open);
        TextView bind_keypad_7 = view.findViewById(R.id.bind_keypad_7_open);
        TextView bind_keypad_8 = view.findViewById(R.id.bind_keypad_8_open);
        TextView bind_keypad_9 = view.findViewById(R.id.bind_keypad_9_open);
        TextView bind_keypad_ok = view.findViewById(R.id.bind_keypad_ok_open);
        TextView bind_keypad_delect = view.findViewById(R.id.bind_keypad_delect_open);
        TextView confirm = view.findViewById(R.id.confirm_open);
        TextView venue_login = view.findViewById(R.id.venue_login_open);
        ImageView close = view.findViewById(R.id.close);
        close.setOnClickListener(this);
        venue_login.setOnClickListener(this);
        confirm.setOnClickListener(this);
        bind_keypad_0.setOnClickListener(this);
        bind_keypad_1.setOnClickListener(this);
        bind_keypad_2.setOnClickListener(this);
        bind_keypad_3.setOnClickListener(this);
        bind_keypad_4.setOnClickListener(this);
        bind_keypad_5.setOnClickListener(this);
        bind_keypad_6.setOnClickListener(this);
        bind_keypad_7.setOnClickListener(this);
        bind_keypad_8.setOnClickListener(this);
        bind_keypad_9.setOnClickListener(this);
        bind_keypad_ok.setOnClickListener(this);
        bind_keypad_delect.setOnClickListener(this);
        confirm.setOnClickListener(this);
        if (isOpen) {
            venue_login.setText(context.getResources().getString(R.string.open_finger));
        } else {
            venue_login.setText(context.getResources().getString(R.string.handy_venue_return));
        }
        params.leftMargin = 30;
        window.setContentView(view, params);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
            case R.id.close:
            case R.id.back_home:
                dissMiss();
                break;
            case R.id.psw_login:
                dissMiss();
                builder.delete(0, builder.length());
                View psw_dialog = View.inflate(context, R.layout.psw_dialog, null);
                showPsdDialog(psw_dialog);
                break;
            case R.id.venue_login:
                dialog.dismiss();
                View veune_dialog = View.inflate(context, R.layout.veune_dialog, null);
                showManagerDialog(veune_dialog);
                break;
            case R.id.bind_keypad_0:
            case R.id.bind_keypad_1:
            case R.id.bind_keypad_2:
            case R.id.bind_keypad_3:
            case R.id.bind_keypad_4:
            case R.id.bind_keypad_5:
            case R.id.bind_keypad_6:
            case R.id.bind_keypad_7:
            case R.id.bind_keypad_8:
            case R.id.bind_keypad_9:
                builder.append(((TextView) view).getText());
                if (inputTel != null) {
                    inputTel.setText(builder.toString());
                }
                break;
            case R.id.bind_keypad_ok:
                builder.delete(0, builder.length());
                inputTel.setText(context.getResources().getString(R.string.manager_pwd));
                break;
            case R.id.bind_keypad_delect:
                if (builder.length() >= 1) {
                    builder.deleteCharAt(builder.length() - 1);
                    inputTel.setText(builder.toString());
                } else {
                    inputTel.setText(context.getResources().getString(R.string.manager_pwd));
                }

                break;

            case R.id.confirm:
                ((MainActivity) context).gotoSetting(builder.toString());
                dissMiss();
                break;
            case R.id.bind_keypad_0_open:
            case R.id.bind_keypad_1_open:
            case R.id.bind_keypad_2_open:
            case R.id.bind_keypad_3_open:
            case R.id.bind_keypad_4_open:
            case R.id.bind_keypad_5_open:
            case R.id.bind_keypad_6_open:
            case R.id.bind_keypad_7_open:
            case R.id.bind_keypad_8_open:
            case R.id.bind_keypad_9_open:
                if (inputCabinet.isFocused()) {
                    cabinetNo.append(((TextView) view).getText());
                    inputCabinet.setText(cabinetNo.toString());
                    inputCabinet.setSelection(cabinetNo.length());
                } else {
                    passWord.append(((TextView) view).getText());
                    input_psw.setText(passWord.toString());
                    input_psw.setSelection(passWord.length());
                }
                break;
            case R.id.bind_keypad_ok_open:
                if (inputCabinet.isFocused()) {
                    cabinetNo.delete(0, cabinetNo.length());
                    inputCabinet.setText(context.getResources().getString(R.string.please_input_lock_num));
                } else {
                    passWord.delete(0, passWord.length());
                    input_psw.setText(context.getResources().getString(R.string.input_password));
                }

                break;
            case R.id.bind_keypad_delect_open:
                if (inputCabinet.isFocused()) {
                    if (cabinetNo.length() >= 1) {
                        cabinetNo.deleteCharAt(cabinetNo.length() - 1);
                        inputCabinet.setText(cabinetNo.toString());
                        inputCabinet.setSelection(cabinetNo.length());
                    } else {
                        inputCabinet.setText(context.getResources().getString(R.string.please_input_lock_num));
                    }

                } else {
                    if (passWord.length() >= 1) {
                        passWord.deleteCharAt(passWord.length() - 1);
                        input_psw.setText(passWord.toString());
                        input_psw.setSelection(passWord.length());
                    } else {
                        input_psw.setText(context.getResources().getString(R.string.input_password));
                    }


                }
                break;

            case R.id.confirm_open:
                ((MainActivity) context).openLock(cabinetNo.toString(), passWord.toString());
                dissMiss();
                break;
            case R.id.venue_login_open:
                dissMiss();
                View openveune = View.inflate(context, R.layout.veune_open_dialog, null);
                showOpenDialog(openveune,isOpen);
                break;
            case R.id.psw_login_open:
                dissMiss();
                cabinetNo.delete(0, cabinetNo.length());
                passWord.delete(0, passWord.length());
                View pswOpen = View.inflate(context, R.layout.psw_dialog_open, null);
                showPsdOpenDialog(pswOpen);
                break;
        }
    }

    public void dissMiss() {
        if (dialog.isShowing()) {
            listener.dialogCancel();
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
