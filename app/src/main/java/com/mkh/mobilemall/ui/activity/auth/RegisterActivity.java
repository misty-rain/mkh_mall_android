package com.mkh.mobilemall.ui.activity.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.fish.mkh.div.MkhTitleBar;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.config.Constants;
import com.mkh.mobilemall.dao.ActivitiesDao;
import com.mkh.mobilemall.dao.MoblieCheckDao;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.ui.adapter.ActivityAdapter;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.utils.ShowToastUtils;
import com.mkh.mobilemall.utils.DialogUtil;
import com.xiniunet.api.domain.master.Activities;
import com.xiniunet.api.domain.master.Member;
import com.xiniunet.api.request.system.MemberUserCreateRequest;
import com.xiniunet.api.request.system.VerificationCodeCreateRequest;
import com.xiniunet.api.response.system.MemberUserCreateResponse;
import com.xiniunet.api.response.system.VerificationCodeCreateResponse;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {
    // 手机号
    @Bind(R.id.edtTxtPhone)
    EditText edtTxtPhone;
    // 密码
    @Bind(R.id.edtTxtPwd)
    EditText edtTxtPwd;
    //再次输入密码
    //@Bind(R.id.reTxtPwd)
   // EditText reTextPwd;
    // 验证码
    @Bind(R.id.edtTxtCode)
    EditText edtTxtCode;
    @Bind(R.id.linear_gone)
    LinearLayout linearLayout;
    // 实体卡号
    @Bind(R.id.edtTxtCardNumber)
    EditText edtTxtCardNumber;
    @Bind(R.id.edtTxtCardNumber2)
    EditText edtTxtCardNumber2;
    @Bind(R.id.edtTxtCardNumber3)
    EditText edtTxtCardNumber3;
    // 是否绑定实体卡
    @Bind(R.id.checkBindCard)
    CheckBox checkBindCard;
    // 注册按钮
    @Bind(R.id.btnRegister)
    Button register;
    // 发送短信
    @Bind(R.id.btnSend)
    Button send;
    // 是否同意协议
    @Bind(R.id.checkAgreeProtocol)
    CheckBox agreeProtocol;
    // 会员章程
    @Bind(R.id.tvMemberProtocol)
    TextView memberProtocol;
    // 微笑店协议
    @Bind(R.id.tvStoreProtocol)
    TextView storeProtocol;

    // 等待框
    Dialog dialog;

    // 帐号是否输入
    private boolean accountOk = false;
    // 密码是否输入
    private boolean passwordOk = false;
    // 验证码是否输入
    private boolean codeOk = false;
    // 是否同意协议
    private boolean checkAgree = true;
    // 实体卡是否绑定,如果绑定是否填写卡号
    private boolean cardNumberOk = false;
    // 发送验证码是否在冷却时间内
    private boolean sendOk = true;
    // 计时器
    TimeCount timeCount = new TimeCount(Constants.SMS_WAIT_TIME, 1000);
    MkhTitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        initTileBar();
    }

    private void initView() {
        register.setOnClickListener(new RegisterListener());    // 注册按钮
        register.setClickable(false);
        send.setOnClickListener(new SendListener());            // 发送短信
        send.setClickable(false);
        checkBindCard.setChecked(false);
        linearLayout.setVisibility(View.GONE);
        agreeProtocol.setChecked(true);

        checkBindCard.setOnCheckedChangeListener(new BindListener());   // 是否绑定实体卡
        agreeProtocol.setOnCheckedChangeListener(new AgreeListener());   // 是否同意协议
        memberProtocol.setOnClickListener(new MemberProtocolLinkListener());    // 跳转至会员章程
        storeProtocol.setOnClickListener(new StoreProtocolLinkListener());      // 跳转至门店协议

        // 四个文本输入框的监听器
        edtTxtPhone.addTextChangedListener(new InputWatcher3());
        edtTxtPwd.addTextChangedListener(new InputWatcher3());
        /*edtTxtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String phone=edtTxtPhone.getText().toString();
                if(!hasFocus&&phone.length()!=0){
                    if(phone.length()==11) {
                        new MobileDaoTask().execute(phone);
                    }else{
                        ShowToastUtils.showToast("手机号码格式不正确",RegisterActivity.this);
                    }
                }

            }
        });*/
        /*edtTxtPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String TxtPwd=edtTxtPwd.getText().toString();
                if(!hasFocus&&TxtPwd.length()!=0){
                    Pattern p = Pattern.compile("[a-zA-Z]{1,}[0-9]{4,}[0-9a-zA-Z]{0,}");
                    Pattern p1=Pattern.compile("[0-9a-zA-Z]{1,3}");
                    Pattern p2=Pattern.compile("[0-9a-zA-Z]{0,}\\w{1,}[0-9a-zA-Z]{0,}");
                    Matcher m = p.matcher(TxtPwd);
                    Matcher m1=p1.matcher(TxtPwd);
                    String regex = "^[a-z0-9A-Z]+$";
                    String regex1="[0-9]{1,}[a-zA-Z]{0,}";
                    String regex2="[a-zA-Z]{1,}";
                    if(!m.matches()){
                        if(m1.matches()) {
                            Toast.makeText(RegisterActivity.this, "密码不能小于四位", Toast.LENGTH_SHORT).show();
                        }else if(!TxtPwd.matches(regex)){
                            Toast.makeText(RegisterActivity.this, "密码只能包含英文字母与数字", Toast.LENGTH_SHORT).show();
                        }
                        else if(TxtPwd.matches(regex1)){
                            Toast.makeText(RegisterActivity.this, "密码必须以英文字母开头", Toast.LENGTH_SHORT).show();
                        }else if(TxtPwd.matches(regex2)){
                            Toast.makeText(RegisterActivity.this, "密码必须包含字母和数字", Toast.LENGTH_SHORT).show();
                        }
                    }
                     else{
                        passwordOk = true;

                    }

                }
            }
        });*/
        /*reTextPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String rePwd=reTextPwd.getText().toString();
                if(!hasFocus&&rePwd.length()!=0){

                    if(!rePwd.equals(edtTxtPwd.getText().toString())){
                        ShowToastUtils.showToast("两次密码输入不一致",RegisterActivity.this);

                    }


                }
            }
        });*/
        edtTxtCode.addTextChangedListener(new InputWatcher3());
        edtTxtCardNumber.addTextChangedListener(new InputWatcher());
        edtTxtCardNumber2.addTextChangedListener(new InputWatcher2());
        edtTxtCardNumber3.addTextChangedListener(new InputWatcher3());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
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

    /**
     * 注册按钮监听器
     */
    private class RegisterListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String phone = edtTxtPhone.getText().toString();
            String password = edtTxtPwd.getText().toString();
            //String repassword =reTextPwd.getText().toString();
            String code = edtTxtCode.getText().toString();
            Boolean isBindCard = checkBindCard.isChecked();
            String cardNumber = edtTxtCardNumber.getText().toString()+edtTxtCardNumber2.getText().toString()+edtTxtCardNumber3.getText().toString();

            /*if (password.length() < 4) {
                Toast.makeText(RegisterActivity.this, getString(R.string.error_weak_password), Toast.LENGTH_SHORT).show();
                return;
            }*/
            if(phone.length()==0){

                ShowToastUtils.showToast("手机号码为空！", RegisterActivity.this);
            }
            /*if(repassword.length()==0){

                ShowToastUtils.showToast("请再次输入密码", RegisterActivity.this);
            }else if(!repassword.equals(password)){

                ShowToastUtils.showToast("两次密码输入不一致",RegisterActivity.this);
            }
*/
            if(phone.length()!=11){
                Toast.makeText(RegisterActivity.this, getString(R.string.phonenum_error), Toast.LENGTH_SHORT).show();
                return;
            }


            dialog = DialogUtil.getRequestDialogForBlack(RegisterActivity.this, getString(R.string.loading));
            dialog.show();

            register(phone, password, code, isBindCard, cardNumber);
        }
        /**
         * 注册方法
         *
         * @param phone      手机号
         * @param password   密码
         * @param code       短信验证码
         * @param isBindCard 是否绑定实体卡
         * @param cardNumber 实体卡号
         */
        private void register(final String phone, final String password, final String code, final Boolean isBindCard, final String cardNumber) {
            final Handler handler = new RegisterHandle(RegisterActivity.this);
            new Thread() {
                public void run() {
                    Message msg = new Message();


                    MemberUserCreateRequest request = new MemberUserCreateRequest();
                    request.setAccount(phone);
                    request.setPassword(password);
                    request.setCode(code);
                    request.setIsCardBind(isBindCard);
                    request.setCardNumber(cardNumber);

                    try {
                        MemberUserCreateResponse response = HttpUtility.client.execute(request);
                        dialog.cancel();
                        if (response.hasError()) {
                            msg.what = 0;   // 注册失败
                        } else {
                            msg.what = 1;   // 注册成功
                        }
                        msg.obj = response;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                    }

                }
            }.start();
        }

        //注册处理器
        public class RegisterHandle extends Handler {
            WeakReference<RegisterActivity> activity;

            RegisterHandle(RegisterActivity activity) {
                this.activity = new WeakReference<>(activity);
            }

            @Override
            public void handleMessage(Message msg) {
                MemberUserCreateResponse response = (MemberUserCreateResponse) msg.obj;
                if (msg.what == 1) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if (msg.what == 0) {
                    Toast.makeText(RegisterActivity.this, response.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 发送短信监听器
     */
    private class SendListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String phone = edtTxtPhone.getText().toString();
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(RegisterActivity.this,getString(R.string.phonenum_notnull),Toast.LENGTH_SHORT).show();
                return;
            }
            if(phone.length()!=11){
                Toast.makeText(RegisterActivity.this,getString(R.string.phonenum_error),Toast.LENGTH_SHORT).show();
                return;
            }

            // 显示等待提示
            dialog = DialogUtil.getRequestDialogForBlack(RegisterActivity.this, getString(R.string.loading));
            dialog.show();



            send(phone);
        }

        public class SendHandle extends Handler {
            WeakReference<RegisterActivity> activity;

            SendHandle(RegisterActivity activity) {
                this.activity = new WeakReference<>(activity);
            }

            @Override
            public void handleMessage(Message msg) {
                VerificationCodeCreateResponse resetResponse = (VerificationCodeCreateResponse) msg.obj;
                if (msg.what == 1) {
                    timeCount.start();
                    Toast.makeText(RegisterActivity.this, "发送成功!", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 0) {
                    Toast.makeText(RegisterActivity.this, resetResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void send(final String phone) {
            if (phone.isEmpty()) {
                Toast.makeText(RegisterActivity.this, getString(R.string.error_empty_phone), Toast.LENGTH_SHORT).show();
                dialog.cancel();
                return;
            }

            final Handler handler = new SendHandle(RegisterActivity.this);
            new Thread() {
                public void run() {
                    Message msg = new Message();

                    VerificationCodeCreateRequest codeCreateRequest = new VerificationCodeCreateRequest();
                    codeCreateRequest.setPhone(phone);
                    try {
                        VerificationCodeCreateResponse resetResponse = HttpUtility.client.execute(codeCreateRequest);
                        dialog.cancel();
                        if (resetResponse.hasError()) {
                            msg.what = 0;   // 发送失败
                        } else {
                            msg.what = 1;   // 发送成功
                        }
                        msg.obj = resetResponse;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                    }

                }
            }.start();
        }
    }

    /**
     * 绑定实体卡监听器
     */
    private class BindListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 如果绑定实体卡
            if (checkBindCard.isChecked()) {
                cardNumberOk = edtTxtCardNumber3.getText().length() > 0;
                //edtTxtCardNumber.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                edtTxtCardNumber.setText("");
                edtTxtCardNumber2.setText("");
                edtTxtCardNumber3.setText("");
            } else {
                // 如果不绑定实体卡
                cardNumberOk = true;
                //edtTxtCardNumber.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
            }
            changeRegister();
        }
    }

    public void initTileBar() {
        mTitleBar = (MkhTitleBar) findViewById(R.id.mkh_actionbar);
        mTitleBar.setTitle(getResources().getString(R.string.toolbar_title_register));
        mTitleBar.setLeftClickFinish(this);

    }
    /**
     * 同意协议监听器
     */
    private class AgreeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            checkAgree = agreeProtocol.isChecked();
            changeRegister();
        }
    }
    /**
     * 获取是否是否手机号已注册
     */
    private class MobileDaoTask extends
            MyAsyncTask<String, Void, Long> {

        @Override
        protected Long doInBackground(String... params) {

            Long activitiesList = MoblieCheckDao.getCheckMobile(edtTxtPhone.getText().toString());

            return activitiesList;
        }

        @Override
        protected void onPostExecute(Long aList) {
             if(aList==1){
                 ShowToastUtils.showToast("该手机号已经被注册！",RegisterActivity.this);

             }else if(aList==0){
                 accountOk=true;


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
            if(s.length()==6){
                edtTxtCardNumber2.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    private class InputWatcher2 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==6){
                edtTxtCardNumber3.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    private class InputWatcher3 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            accountOk = edtTxtPhone.getText().length() > 0;
            codeOk = edtTxtCode.getText().length() > 0;
            cardNumberOk = !checkBindCard.isChecked() || edtTxtCardNumber3.getText().length() > 0;
            passwordOk = edtTxtPwd.getText().toString().length()>0;
            changeRegister();
            changeSend();
        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    }

    /**
     * 改变注册按钮的样式
     */
    private void changeRegister() {
        if (accountOk && passwordOk && codeOk && cardNumberOk && checkAgree) {
            register.setBackgroundResource(R.drawable.register_button_enable);
            register.setClickable(true);
        } else {
            register.setBackgroundResource(R.drawable.register_button_disable);
            register.setClickable(false);
        }
    }

    /**
     * 改变获取验证码按钮的样式
     */
    private void changeSend() {
        if (accountOk && sendOk) {
            send.setBackgroundResource(R.drawable.send_code_button_enable);
            send.setClickable(true);
        } else {
            send.setBackgroundResource(R.drawable.send_code_button_disable);
            send.setClickable(false);
        }
    }

    /**
     * 跳转至会员章程页面
     */
    private class MemberProtocolLinkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RegisterActivity.this, MemberProtocolActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 跳转至微笑店协议页面
     */
    private class StoreProtocolLinkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RegisterActivity.this, MemberProtocolActivity.class);
            startActivity(intent);
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
            send.setText("获取验证码");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendOk = false;
            changeSend();
            send.setText(millisUntilFinished / 1000 + "秒后可重发");
        }
    }
}