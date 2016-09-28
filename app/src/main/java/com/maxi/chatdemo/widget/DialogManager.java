package com.maxi.chatdemo.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.maxi.chatdemo.R;
import com.maxi.chatdemo.utils.ScreenUtil;
@SuppressLint("InflateParams")
public class DialogManager {

	/**
	 * 以下为dialog的初始化控件，包括其中的布局文件
	 */

	private Dialog mDialog;

	private ImageView mIcon;
	private ImageView mVoice;

	private TextView mLable;

	private Context mContext;

	public DialogManager(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public void showRecordingDialog() {
		// TODO Auto-generated method stub

		mDialog = new Dialog(mContext, R.style.Theme_audioDialog);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 用layoutinflater来引用布局
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.layout_voice_dialog_manager, null);
		mDialog.setContentView(view);

		mIcon = (ImageView) mDialog.findViewById(R.id.dialog_icon);
		mVoice = (ImageView) mDialog.findViewById(R.id.dialog_voice);
		mLable = (TextView) mDialog.findViewById(R.id.recorder_dialogtext);
		
		Window dialogWindow = mDialog.getWindow();
	    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	    int width = ScreenUtil.getScreenWidth(mContext) / 2;
	    lp.width = width; // 宽度
        lp.height = width; // 高度
        dialogWindow.setAttributes(lp);
        mDialog.setCancelable(false);
		mDialog.show();
		
	}

	/**
	 * 设置正在录音时的dialog界面
	 */
	public void recording() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.GONE);
			mVoice.setVisibility(View.VISIBLE);
			mLable.setVisibility(View.VISIBLE);
			mLable.setText(R.string.shouzhishanghua);
		}
	}

	/**
	 * 取消界面
	 */
	public void wantToCancel() {
		// TODO Auto-generated method stub
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.mipmap.cancel);
			mLable.setText(R.string.want_to_cancle);
		}

	}

	// 时间过短
	public void tooShort() {
		// TODO Auto-generated method stub
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.mipmap.voice_to_short);
			mLable.setText(R.string.tooshort);
		}

	}
	// 时间过长
		public void tooLong() {
			// TODO Auto-generated method stub
			if (mDialog != null && mDialog.isShowing()) {
				mIcon.setVisibility(View.VISIBLE);
				mVoice.setVisibility(View.GONE);
				mLable.setVisibility(View.VISIBLE);

				mIcon.setImageResource(R.mipmap.voice_to_short);
				mLable.setText(R.string.toolong);
			}

		}
	// 隐藏dialog
	public void dimissDialog() {
		// TODO Auto-generated method stub

		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}

	}

	public void updateVoiceLevel(int level) {
		// TODO Auto-generated method stub

		if (mDialog != null && mDialog.isShowing()) {
			int resId;
			if(level >= 1 && level < 2){
				resId = mContext.getResources().getIdentifier("tb_voice1",
					"mipmap", mContext.getPackageName());
			}else if(level >= 2 && level < 3){
				resId = mContext.getResources().getIdentifier("tb_voice2",
						"mipmap", mContext.getPackageName());
			}else{
				resId = mContext.getResources().getIdentifier("tb_voice3",
						"mipmap", mContext.getPackageName());
			}
			mVoice.setImageResource(resId);
		}

	}

}
