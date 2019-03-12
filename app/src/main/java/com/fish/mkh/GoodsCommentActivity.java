package com.fish.mkh;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fish.mkh.bean.OrdersBean;
import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.ImageLoaderUtil;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.request.ecommerce.ItemEvaluationCreateRequest;
import com.xiniunet.api.response.ecommerce.ItemEvaluationCreateResponse;
import com.mkh.mobilemall.R;

public class GoodsCommentActivity extends BaseActivity {

	private MkhTitleBar titleBar;
	private OrdersBean data;
	private ListView list;
	private int height_screen;
	private Button btnCommit;
	private Handler handler;
	private EditText etComment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_comment);
		initView();
	}

	private void initView() {
		titleBar = (MkhTitleBar) findViewById(R.id.title_comment);
		list = (ListView) findViewById(R.id.goods_list);
		btnCommit = (Button) findViewById(R.id.btn_commit);
		etComment = (EditText)findViewById(R.id.et_comment);

		titleBar.setTitle("评价商品");
		titleBar.setLeftClickFinish(GoodsCommentActivity.this);
		height_screen = getResources().getDisplayMetrics().heightPixels;
		

		data = (OrdersBean) getIntent().getSerializableExtra("data");
		
		if(data != null && data.getData() != null){
			resetListViewHeight();
			list.setAdapter(new listAdapter());
		}

		btnCommit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(etComment.getText().toString() == null || etComment.getText().toString().trim().equals("")){
                	Toast.makeText(GoodsCommentActivity.this, "请输入您的评论！", Toast.LENGTH_SHORT).show();
                	return;
				}
				UIUtil.showWaitDialog(GoodsCommentActivity.this);
				handler = new Handler() {
			        public void handleMessage(Message msg) {
			        	UIUtil.dismissDlg();
			        	ItemEvaluationCreateResponse memRsp = (ItemEvaluationCreateResponse)msg.obj;
			        	if(msg.what == 2){
		                	Toast.makeText(GoodsCommentActivity.this, "发表成功！", Toast.LENGTH_SHORT).show();
			        		GoodsCommentActivity.this.finish();
			        	}else if(msg.what == 1){
		                	Toast.makeText(GoodsCommentActivity.this, memRsp.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
			        	}
			        }
				};
				
				new Thread() {
		            public void run() {
		                Message msg = new Message();

		                ItemEvaluationCreateRequest msgReq = new ItemEvaluationCreateRequest();
		                msgReq.setOrderId(data.getOrderId());
		                msgReq.setEvaluation(etComment.getText().toString());
		                List<Long> ids = new ArrayList<Long>();
		                for(int i = 0;i<data.getData().size();i++){
		                	ids.add(data.getData().get(i).getGoodsId());
		                }
		                msgReq.setItemId(ids);
		                try {
		                	ItemEvaluationCreateResponse msgRsp = AuthDao.client.execute(msgReq, GlobalContext.getInstance().getSpUtil().getUserInfo());
		                    if (msgRsp.hasError()) {
		                        msg.what = 1;   // 获取失败
		                    } else {
		                        msg.what = 2;   // 登录成功
		                    }
		                    msg.obj = msgRsp;
		                    handler.sendMessage(msg);
		                } catch (Exception e) {
		                }
		            }
		        }.start();

			}
		});
		
		
	}
	
	

	private void resetListViewHeight() {
		int listHieght = (int) (data.getData().size() * dip2pxf(this, 91));
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) list
				.getLayoutParams();
		if ((listHieght + dip2pxf(this, 219)) > (height_screen - getStatusHeight(this))) {
			listHieght = height_screen - getStatusHeight(this)
					- (int) dip2pxf(this, 219);
		}
		if (params == null) {
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, listHieght);
		} else {
			params.height = listHieght;
		}
		list.setLayoutParams(params);
	}

	public class listAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return data.getData().size();
		}

		@Override
		public Object getItem(int position) {

			return data.getData().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				view = View.inflate(GoodsCommentActivity.this,
						R.layout.activity_goods_comment_item, null);
				holder.imageView = (ImageView) view
						.findViewById(R.id.image_goods);
				holder.tvTitle = (TextView) view.findViewById(R.id.title_goods);
				holder.tvPrice = (TextView) view.findViewById(R.id.price_goods);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.tvTitle.setText(data.getData().get(position).getTitle());
			holder.tvPrice.setText(data.getData().get(position).getCost());
			ImageLoaderUtil.loadImg(data.getData().get(position).getImageUrl(),
					holder.imageView, GoodsCommentActivity.this);
			return view;
		}
	}

	static class ViewHolder {
		ImageView imageView;
		TextView tvTitle;
		TextView tvPrice;
	}

	private static int getStatusHeight(Context mContext) {
		int statusHeight = 0;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = mContext.getResources()
						.getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	private static float dip2pxf(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (dipValue * scale + 0.5f);
	}
}
