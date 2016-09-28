package com.maxi.chatdemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.maxi.chatdemo.R;

public class HeadIconSelectorView extends RelativeLayout implements
		GestureDetector.OnGestureListener {

	private View baseView;

	private RelativeLayout mainRl;
	private LinearLayout bottomLl;
	private LinearLayout cameraLl;
	private LinearLayout galleryLl;
	private LinearLayout cancelLl;

	private GestureDetector gestureDetector; // 手势检测器
	private boolean isAnimationing = false;
	private OnHeadIconClickListener onHeadIconClickListener;
	public static final int FROM_CAMERA = 2;
	public static final int FROM_GALLERY = 3;
	public static final int CANCEL = 4;
	public static final int BLANK_CANCEL = 5;

	public HeadIconSelectorView(Context context) {
		super(context);
		init();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void init() {
		findView();
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		bottomLl.setVisibility(View.INVISIBLE);
		mainRl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onHeadIconClickListener) {
					onHeadIconClickListener.onClick(BLANK_CANCEL);
				}
				cancel();
			}
		});
		cancelLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onHeadIconClickListener) {
					onHeadIconClickListener.onClick(CANCEL);
				}
				cancel();
			}
		});
		cameraLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onHeadIconClickListener) {
					onHeadIconClickListener.onClick(FROM_CAMERA);
				}
				cancel();
			}
		});
		galleryLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (null != onHeadIconClickListener) {
					onHeadIconClickListener.onClick(FROM_GALLERY);
				}
				cancel();
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void findView() {
		gestureDetector = new GestureDetector(this);
		baseView = LayoutInflater.from(getContext()).inflate(
				R.layout.layout_view_headicon, this);
		mainRl = (RelativeLayout) baseView.findViewById(R.id.head_icon_main_rl);
		bottomLl = (LinearLayout) baseView.findViewById(R.id.head_icon_main_ll);
		cameraLl = (LinearLayout) baseView
				.findViewById(R.id.head_icon_camera_ll);
		galleryLl = (LinearLayout) baseView
				.findViewById(R.id.head_icon_gallery_ll);
		cancelLl = (LinearLayout) baseView
				.findViewById(R.id.head_icon_cancel_ll);
	}

	protected void bottomViewFlyIn() {
		final ScaleAnimation sa1 = new ScaleAnimation(1, 1, 0, 1.2f,
				Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 1f);
		sa1.setDuration(250);
		final ScaleAnimation sa2 = new ScaleAnimation(1, 1, 1.2f, 1,
				Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 1f);
		sa2.setDuration(150);
		sa1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isAnimationing = true;
				bottomLl.setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimationing = false;
				bottomLl.startAnimation(sa2);
			}
		});
		bottomLl.startAnimation(sa1);
	}

	protected void bottomViewFlyOut() {
		final ScaleAnimation sa1 = new ScaleAnimation(1, 1, 1, 1.2f,
				Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 1f);
		sa1.setDuration(150);
		final ScaleAnimation sa2 = new ScaleAnimation(1, 1, 1.2f, 0,
				Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 1f);
		sa2.setDuration(250);
		sa1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isAnimationing = true;
				bottomLl.setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				bottomLl.startAnimation(sa2);
			}
		});
		sa2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimationing = false;
				bottomLl.setVisibility(INVISIBLE);
				flyOut();
			}
		});
		bottomLl.startAnimation(sa1);
	}

	protected void cancel() {
		if (!isAnimationing) {
			if (bottomLl.getVisibility() == VISIBLE) {
				bottomViewFlyOut();
				return;
			}
		}
	}

	public void flyIn() {
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(300);
		aa.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isAnimationing = true;
				setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimationing = false;
				bottomViewFlyIn();
			}
		});
		startAnimation(aa);
	}

	public void flyOut() {
		AlphaAnimation aa = new AlphaAnimation(1, 0);
		aa.setDuration(300);
		aa.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isAnimationing = true;
				setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimationing = false;
				destroy();
			}
		});
		startAnimation(aa);
	}

	protected void destroy() {
		if (this.getParent() != null) {
			((ViewGroup) this.getParent()).removeView(this);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isAnimationing) {
				if (bottomLl.getVisibility() == VISIBLE) {
					bottomViewFlyOut();
					return true;
				}
			} else {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public OnHeadIconClickListener getOnHeadIconClickListener() {
		return onHeadIconClickListener;
	}

	public void setOnHeadIconClickListener(
			OnHeadIconClickListener onHeadIconClickListener) {
		this.onHeadIconClickListener = onHeadIconClickListener;
	}

	public interface OnHeadIconClickListener {
		public void onClick(int from);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	private float minVelocityY = 100f;// 10个像素每秒
	private float minDistanceY = 100f;// 100个像素

	// 用户按下触摸屏、快速移动后松开,由1个MotionEvent ACTION_DOWN,
	// 多个ACTION_MOVE, 1个ACTION_UP触发
	// e1：第1个ACTION_DOWN MotionEvent
	// e2：最后一个ACTION_MOVE MotionEvent
	// velocityX：X轴上的移动速度，像素/秒
	// velocityY：Y轴上的移动速度，像素/秒
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// 手势从上到下且移动速度较快
		if (e2.getY() - e1.getY() > minDistanceY && velocityY > minVelocityY) {
			cancel();
		}
		return false;
	}
}
