package com.fish.mkh.div;



import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mkh.mobilemall.R;

/**
 * 底部tab 
 * @author Wang
 */
public class TopTablet extends RelativeLayout implements View.OnClickListener {

	private TextView tvTab1, tvTab2, tvTab3, tvTab4,tvTab5;
	private View vTab1, vTab2, vTab3, vTab4,vTab5;
	private RelativeLayout rlTab1, rlTab2, rlTab3, rlTab4,rlTab5;
	private int mColorOrange;
	private int mColorGrey;
	private int mPrevIndex = 0;
	public SelectChangeListener changeListener;

	public SelectChangeListener getChangeListener() {
		return changeListener;
	}

	public void setChangeListener(SelectChangeListener selectChangeListener) {
		this.changeListener = selectChangeListener;
	}

	public int  getIndex(){
		return mPrevIndex;
	}
	public TopTablet(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.layout_toptab, this);
		tvTab1 = (TextView) findViewById(R.id.tv_tab1);
		tvTab2 = (TextView) findViewById(R.id.tv_tab2);
		tvTab3 = (TextView) findViewById(R.id.tv_tab3);
		tvTab4 = (TextView) findViewById(R.id.tv_tab5);
		tvTab5 = (TextView) findViewById(R.id.tv_tab6);
		vTab1 = (View)findViewById(R.id.v_tab1);
		vTab2 = (View)findViewById(R.id.v_tab2);
		vTab3 = (View)findViewById(R.id.v_tab3);
		vTab4 = (View)findViewById(R.id.v_tab5);
		vTab5 = (View)findViewById(R.id.v_tab6);
		mColorOrange = context.getResources().getColor(R.color.category_selected_color);
		mColorGrey = context.getResources().getColor(R.color.color_txt_black);
		rlTab1 = (RelativeLayout) findViewById(R.id.ll_tab1);
		rlTab2 = (RelativeLayout) findViewById(R.id.ll_tab2);
		rlTab3 = (RelativeLayout) findViewById(R.id.ll_tab3);
		rlTab4 = (RelativeLayout) findViewById(R.id.ll_tab5);
		rlTab5 = (RelativeLayout) findViewById(R.id.ll_tab6);
		rlTab1.setOnClickListener(this);
		rlTab2.setOnClickListener(this);
		rlTab3.setOnClickListener(this);
		rlTab4.setOnClickListener(this);
		rlTab5.setOnClickListener(this);
		setItemSelected(0, true);
	}

	public void setSelected(int index) {
		if (mPrevIndex == index) {
			return;
		}
		setItemSelected(mPrevIndex, false);
		setItemSelected(index, true);
		mPrevIndex = index;
		changeListener.onSelectChanged(index);
		
	}
	
	private void setItemSelected(int index, boolean isSelected) {
		switch (index) {
			case 0:
				setTab1Selected(isSelected);
				break;
			case 1:
				setTab2Selected(isSelected);
				break;
			case 2:
				setTab3Selected(isSelected);
				break;
			case 3:
				setTab4Selected(isSelected);
				break;
			case 4:
				setTab5Selected(isSelected);
				break;
		}
	}
	
	private void setTab1Selected(boolean isSelected) {
		if (isSelected) {
			vTab1.setVisibility(View.VISIBLE);
			tvTab1.setTextColor(mColorOrange);
		} else {
			vTab1.setVisibility(View.GONE);
			tvTab1.setTextColor(mColorGrey);
		}
	}
	
	private void setTab2Selected(boolean isSelected) {
		if (isSelected) {
			vTab2.setVisibility(View.VISIBLE);
			tvTab2.setTextColor(mColorOrange);
		} else {
			vTab2.setVisibility(View.GONE);
			tvTab2.setTextColor(mColorGrey);
		}
	}
	
	private void setTab3Selected(boolean isSelected) {
		if (isSelected) {
			vTab3.setVisibility(View.VISIBLE);
			tvTab3.setTextColor(mColorOrange);
		} else {
			vTab3.setVisibility(View.GONE);
			tvTab3.setTextColor(mColorGrey);
		}
	}
	
	private void setTab4Selected(boolean isSelected) {
		if (isSelected) {
			vTab4.setVisibility(View.VISIBLE);
			tvTab4.setTextColor(mColorOrange);
		} else {
			vTab4.setVisibility(View.GONE);
			tvTab4.setTextColor(mColorGrey);
		}
	}

	private void setTab5Selected(boolean isSelected) {
		if (isSelected) {
			vTab5.setVisibility(View.VISIBLE);
			tvTab5.setTextColor(mColorOrange);
		} else {
			vTab5.setVisibility(View.GONE);
			tvTab5.setTextColor(mColorGrey);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_tab1) {
			setSelected(0);
		} else if (id == R.id.ll_tab2) {
			setSelected(1);
		} else if (id == R.id.ll_tab3) {
			setSelected(2);
		} else if (id == R.id.ll_tab5) {
			setSelected(3);
		}else if (id == R.id.ll_tab6) {
			setSelected(4);
		}
	}
	
	public   interface  SelectChangeListener {   
	    void  onSelectChanged(int index);   
	}   

}
