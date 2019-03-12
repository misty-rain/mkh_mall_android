package com.mkh.mobilemall.ui.activity.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.fish.mkh.div.MkhTitleBar;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.config.Constants;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.utils.ShowToastUtils;
import com.mkh.mobilemall.utils.DialogUtil;
import com.xiniunet.api.request.system.LoginPasswordResetRequest;
import com.xiniunet.api.request.system.VerificationCodeCreateRequest;
import com.xiniunet.api.response.system.LoginPasswordResetResponse;
import com.xiniunet.api.response.system.VerificationCodeCreateResponse;

import java.lang.ref.WeakReference;

public class ForgetPasswordActivity extends BaseActivity {
    // 手机号码
    @Bind(R.id.edtTxtPhone)
    EditText edtTxtPhone;
    // 验证码
    @Bind(R.id.edtTxtCode)
    EditText edtTxtCode;
    // 新的密码
    @Bind(R.id.edtTxtPwd)
    EditText edtTxtPwd;
    // 忘记密码按钮
    @Bind(R.id.btnForget)
    Button btnForget;
    // 发送验证码按钮
    @Bind(R.id.btnSend)
    Button btnSend;

    Dialog dialog;
    MkhTitleBar mTitleBar;

    // 帐号是否输入
    private boolean accountOk = false;
    // 密码是否输入
    private boolean passwordOk = false;
    // 验证码是否输入
    private boolean codeOk = false;
    // 发送验证码是否在冷却时间内
    private boolean sendOk = true;
    // 计时器
    TimeCount timeCount = new TimeCount(Constants.SMS_WAIT_TIME, 1000);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return 0;
    }

    @Override
    protected int getActionBarCustomView() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        initTileBar();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        btnForget.setOnClickListener(new ForgetListener());
        btnForget.setClickable(false);

        btnSend.setOnClickListener(new SendListener());
        btnSend.setClickable(false);

        edtTxtPhone.addTextChangedListener(new InputWatcher());
        edtTxtPwd.addTextChangedListener(new InputWatcher());
        edtTxtCode.addTextChangedListener(new InputWatcher());
    }

    public void initTileBar() {
        mTitleBar = (MkhTitleBar) findViewById(R.id.mkh_actionbar);
        mTitleBar.setTitle(getResources().getString(R.string.toolbar_title_forget));
        mTitleBar.setLeftClickFinish(this);

    }

    public class SendListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 显示等待提示


            String phone = edtTxtPhone.getText().toString();
            if(phone.length()!=11){
                //Toast.makeText(ForgetPasswordActivity.this,"输入的手机号码有误！",Toast.LENGTH_SHORT).show();
                ShowToastUtils.showToast("输入的手机号码有误！",ForgetPasswordActivity.this);
            }else{
                dialog = DialogUtil.getRequestDialogForBlack(ForgetPasswordActivity.this, getString(R.string.loading));
                dialog.show();
                send(phone);
            }

        }

        public class SendHandle extends Handler {
            WeakReference<ForgetPasswordActivity> activity;

            SendHandle(ForgetPasswordActivity activity) {
                this.activity = new WeakReference<>(activity);
            }

            @Override
            public void handleMessage(Message msg) {
                VerificationCodeCreateResponse resetResponse = (VerificationCodeCreateResponse) msg.obj;
                if (msg.what == 1) {
                    timeCount.start();
                    Toast.makeText(ForgetPasswordActivity.this, "发送成功!", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 0) {
                    Toast.makeText(ForgetPasswordActivity.this, resetResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void send(final String phone) {
            if (phone.isEmpty()) {
                Toast.makeText(ForgetPasswordActivity.this, getString(R.string.error_empty_phone), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            final Handler handler = new SendHandle(ForgetPasswordActivity.this);
            new Thread() {
                public void run() {
                    Message msg = new Message();

                    VerificationCodeCreateRequest codeCreateRequest = new VerificationCodeCreateRequest();
                    codeCreateRequest.setPhone(phone);
                    try {
                        VerificationCodeCreateResponse resetResponse = HttpUtility.client.execute(codeCreateRequest);
                        if (resetResponse.hasError()) {
                            msg.what = 0;   // 发送失败
                        } else {
                            msg.what = 1;   // 发送成功
                        }
                        msg.obj = resetResponse;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                    }
                    dialog.dismiss();
                }
            }.start();
        }
    }

    public class ForgetListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 显示等待提示
            dialog = DialogUtil.getRequestDialogForBlack(ForgetPasswordActivity.this, getString(R.string.loading));
            dialog.show();

            String phone = edtTxtPhone.getText().toString();
            String code = edtTxtCode.getText().toString();
            String newPwd = edtTxtPwd.getText().toString();

            reset(phone, code, newPwd);
        }

        private void reset(final String phone, final String code, final String password) {
            if (password.length() < 4) {
                Toast.makeText(ForgetPasswordActivity.this, getString(R.string.error_weak_password), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            final Handler handler = new ResetHandle(ForgetPasswordActivity.this);

            new Thread() {
                public void run() {
                    Message msg = new Message();

                    LoginPasswordResetRequest checkRequest = new LoginPasswordResetRequest();
                    checkRequest.setPhone(phone);
                    checkRequest.setCode(code);
                    checkRequest.setPassword(password);
                    try {
                        LoginPasswordResetResponse resetResponse = HttpUtility.client.execute(checkRequest);
                        if (resetResponse.hasError()) {
                            msg.what = 0;   // 发送失败
                            msg.obj =getString(R.string.back_password_error);
                        } else {
                            msg.what = 1;   // 发送成功
                            msg.obj =getString(R.string.back_password_success);
                        }
                        msg.obj = resetResponse;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                    }
                    dialog.dismiss();
                }
            }.start();
        }

        public class ResetHandle extends Handler {
            WeakReference<ForgetPasswordActivity> activity;

            ResetHandle(ForgetPasswordActivity activity) {
                this.activity = new WeakReference<>(activity);
            }

            public void handleMessage(Message msg) {
                LoginPasswordResetResponse resetResponse = (LoginPasswordResetResponse) msg.obj;
                if (msg.what == 1) {
                    Toast.makeText(ForgetPasswordActivity.this,getString(R.string.back_password_success),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                } else if (msg.what == 0) {
                    Toast.makeText(ForgetPasswordActivity.this, resetResponse.getErrors().get(0).getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 文本变动观察
     */
    private class InputWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            accountOk = edtTxtPhone.getText().length() > 0;
            passwordOk = edtTxtPwd.getText().length() > 0;
            codeOk = edtTxtCode.getText().length() > 0;

            changeForget();
            changeSend();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /**
     * 改变找回按钮的样式
     */
    private void changeForget() {
        if (accountOk && passwordOk && codeOk) {
            btnForget.setBackgroundResource(R.drawable.register_button_enable);
            btnForget.setClickable(true);
        } else {
            btnForget.setBackgroundResource(R.drawable.register_button_disable);
            btnForget.setClickable(false);
        }
    }

    /**
     * 改变获取验证码按钮的样式
     */
    private void changeSend() {
        if (accountOk && sendOk) {
            btnSend.setBackgroundResource(R.drawable.send_code_button_enable);
            btnSend.setClickable(true);
        } else {
            btnSend.setBackgroundResource(R.drawable.send_code_button_disable);
            btnSend.setClickable(false);
        }
    }

    /**
     * 发送短信后的定时器
     */
    private class TimeCount extends CountDownTimer {
        /**
         * 构造方法
         *
         * @param millisInFuture    总时长
         * @param countDownInterval 时间间隔
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            sendOk = true;
            changeSend();
            btnSend.setText("重新验证");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendOk = false;
            changeSend();
            btnSend.setText(millisUntilFinished / 1000 + "秒后可重发");
        }
    }
}
