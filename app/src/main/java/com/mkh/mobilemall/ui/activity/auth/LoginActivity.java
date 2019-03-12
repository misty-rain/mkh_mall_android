package com.mkh.mobilemall.ui.activity.auth;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.fish.mkh.MemberCenterActivity;
import com.fish.mkh.div.MkhTitleBar;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.appconfig.AppManager;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.utils.DialogUtil;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.xiniunet.api.request.system.LoginRequest;
import com.xiniunet.api.response.system.LoginResponse;

public class LoginActivity extends BaseActivity {
    // 用户名的输入框
    @Bind(R.id.edtTxtName)
    EditText edtTxtName;

    // 密码的输入框
    @Bind(R.id.edtTxtPwd)
    EditText edtTxtPwd;

    // 登录按钮
    @Bind(R.id.btnLogin)
    Button btnLogin;

    // 忘记密码链接
    @Bind(R.id.tvForget)
    TextView tvForget;

    // 注册链接
    @Bind(R.id.tvRegister)
    TextView tvRegister;

    Dialog dialog;
    private SharePreferenceUtil sharePreferenceUtil;
    MkhTitleBar mTitleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
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
        sharePreferenceUtil= GlobalContext.getInstance().getSpUtil();
        initView();
        initTileBar();



    }


    /**
     * 监听返回键事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        return false;

    }


    private void initView() {

        btnLogin.setOnClickListener(new LoginListener());        // 登录按钮
        tvForget.setOnClickListener(new ForgetListener());       // 忘记密码
        tvRegister.setOnClickListener(new RegisterListener());   // 快速注册
        edtTxtName.setText(sharePreferenceUtil.getUserName());
        edtTxtName.addTextChangedListener(new InputWatcher());
        edtTxtPwd.addTextChangedListener(new InputWatcher());
        if(!TextUtils.isEmpty(edtTxtName.getText().toString())){

            edtTxtName.setSelection(edtTxtName.getText().toString().length());
        }
    }


    public void initTileBar() {
        mTitleBar = (MkhTitleBar) findViewById(R.id.mkh_actionbar);
        mTitleBar.setTitle(getResources().getString(R.string.toolbar_title_login));
        mTitleBar.setLeftClickFinish(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private class InputWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean hasName = edtTxtName.getText().length() > 0;
            boolean hasPassword = edtTxtPwd.getText().length() > 0;
            if(hasName && hasPassword) {
                btnLogin.setBackgroundResource(R.drawable.login_button_enable);
            } else {
                btnLogin.setBackgroundResource(R.drawable.login_button_disable);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /**
     * 登录事件
     */
    private class LoginListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String userName = edtTxtName.getText().toString();
            // 原始密码
            String password = edtTxtPwd.getText().toString();

            if (userName.isEmpty()) {
                return;
            }
            if (password.isEmpty()) {
                return;
            }

            // 显示等待提示
            dialog = DialogUtil.getRequestDialogForBlack(LoginActivity.this, getString(R.string.loading));
            dialog.show();
            login(userName, password);
        }

        private void login(final String userName, final String pwd) {
            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    dialog.cancel();
                    LoginResponse loginResponse = (LoginResponse) msg.obj;
                    if (msg.what == 1) {
                        sharePreferenceUtil.saveUserInfo(loginResponse.getPassport());
                        Intent intent = new Intent(LoginActivity.this, MemberCenterActivity.class);
                        intent.putExtra("passportId", loginResponse.getPassport().getId());
                        sharePreferenceUtil.setUserName(edtTxtName.getText().toString());
                        sharePreferenceUtil.setPassword(edtTxtPwd.getText().toString().trim());
                        startActivity(intent);
                        finish();
                    } else if (msg.what == 0) {
                        Toast.makeText(LoginActivity.this, loginResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            new Thread() {
                public void run() {
                    Message msg = new Message();

                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setAccount(userName);
                    loginRequest.setPassword(pwd);
                    try {
                        LoginResponse loginResponse = HttpUtility.client.execute(loginRequest);
                        if (loginResponse.hasError()) {
                            msg.what = 0;   // 登录失败
                        } else {
                            msg.what = 1;   // 登录成功
                        }
                        msg.obj = loginResponse;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                    }
                    dialog.dismiss();
                }
            }.start();
        }
    }

    /**
     * 跳转至忘记密码页面
     */
    private class ForgetListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 跳转至注册页面
     */
    private class RegisterListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
