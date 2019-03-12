package com.mkh.mobilemall.ui.activity.commodity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.ui.adapter.EvaluationAdapter;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.utils.ListViewUtil;
import com.xiniunet.api.XiniuResponse;
import com.xiniunet.api.domain.ecommerce.ItemEvaluation;
import com.xiniunet.api.request.ecommerce.ItemEvaluationFindRequest;
import com.xiniunet.api.response.ecommerce.ItemEvaluationFindResponse;

import java.util.List;

public class EvaluationListActivity extends BaseActivity {
    // 评价列表
    @Bind(R.id.evaluationList)
    ListView list;
    @Bind(R.id.empty_list)
    View emptyView;
    @Bind(R.id.txtemptyMessage)
    TextView txtEmptyMessage;
    @Bind(R.id.imgicon)
    ImageView imageIcon;


    // 当前商品的评价列表
    List<ItemEvaluation> evaluationList;
    // 当前商品的评价总数
    Long evaluationCount = 0L;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_evaluation_list;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.toolbar_title_evaluation;
    }

    @Override
    protected int getActionBarCustomView() {
        return R.layout.custom_toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        final Long id = getIntent().getLongExtra("id", 0L);

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    XiniuResponse response = (XiniuResponse) msg.obj;
                    Toast.makeText(EvaluationListActivity.this, response.getErrors().get(0).getMessage(),Toast.LENGTH_SHORT).show();
                } else if(msg.what == 1) {
                    // 重新设置标题
                    setActionBarTitle(getString(R.string.toolbar_title_evaluation) + "(" + evaluationCount + ")");

                    if(evaluationList!=null) {
                        // 设置评价列表
                        list.setAdapter(new EvaluationAdapter(EvaluationListActivity.this, evaluationList));
                        ListViewUtil.setListViewHeight(list);

                        // 设置高度(我也不知道为什么,本来是要设置布局的高度的,layout写成了list,竟然可以!)
                        list.getLayoutParams().height = list.getHeight();
                    }else{
                        list.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        txtEmptyMessage.setText("暂无数据");
                        imageIcon.setImageResource(R.mipmap.icon_no_ticket);

                    }
                }
            }
        };

        new Thread() {
            public void run() {
                Message msg = new Message();
                try {

                    // 查询物料评价
                    ItemEvaluationFindRequest findRequest = new ItemEvaluationFindRequest();
                    findRequest.setItemId(id);
                    findRequest.setPageSize(0);
                    ItemEvaluationFindResponse findResponse = HttpUtility.client.execute(findRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
                    if(findResponse.hasError()) {
                        msg.what = 0;
                        msg.obj = findResponse;
                        handler.sendMessage(msg);
                        return;
                    } else {
                        evaluationList = findResponse.getResult();
                        evaluationCount = findResponse.getTotalCount();
                    }

                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                }
            }
        }.start();
    }
}
