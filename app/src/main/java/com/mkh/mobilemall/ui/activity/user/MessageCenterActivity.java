package com.mkh.mobilemall.ui.activity.user;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.fish.mkh.client.AuthDao;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.ui.adapter.MessageAdapter;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.utils.ListViewUtil;
import com.xiniunet.api.XiniuResponse;
import com.xiniunet.api.domain.system.Message;

import java.util.List;

public class MessageCenterActivity extends BaseActivity {
    // 消息列表
    @Bind(R.id.messageList)
    ListView messageListView;

    // 消息
    List<Message> messageList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_center;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.toolbar_title_message;
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
    }

    private void initView() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    XiniuResponse response = (XiniuResponse) msg.obj;
                    Toast.makeText(MessageCenterActivity.this, response.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                } else if(msg.what == 1) {
                    // 设置消息列表
                    messageListView.setAdapter(new MessageAdapter(messageList, MessageCenterActivity.this));
                    ListViewUtil.setListViewHeight(messageListView);
                }
            }
        };
        new Thread() {
            public void run() {
                android.os.Message message = new android.os.Message();
//                MessageFindRequest request = new MessageFindRequest();
//
//                MessageFindResponse response = AuthDao.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
//                if(response.hasError()) {
//                    message.what = 0;
//                } else {
//                    message.what = 1;
//                    messageList = response.getResult();
//                }
//                message.obj = response;
                handler.sendMessage(message);
            }
        }.start();
    }


}
