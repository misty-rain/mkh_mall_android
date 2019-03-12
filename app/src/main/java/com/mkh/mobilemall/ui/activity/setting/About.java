package com.mkh.mobilemall.ui.activity.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.utils.Utility;


/**
 * 关于我们
 *
 * @author Administrator
 */
@SuppressLint("NewApi")
public class About extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.about;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.app_name;
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle("关于");
        TextView txtVersion = (TextView) findViewById(R.id.version);
        txtVersion.setText(getString(R.string.app_version) + Utility.getVersionName(About.this));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

}
