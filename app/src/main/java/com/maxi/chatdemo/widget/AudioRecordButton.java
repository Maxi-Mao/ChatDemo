package com.maxi.chatdemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.maxi.chatdemo.R;
import com.maxi.chatdemo.utils.AudioManager;
import com.maxi.chatdemo.utils.FileSaveUtil;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class AudioRecordButton extends Button implements AudioManager.AudioStageListener {
	private static final int STATE_NORMAL = 1;
	private static final int STATE_RECORDING = 2;
	private static final int STATE_WANT_TO_CANCEL = 3;
	private static final int DISTANCE_Y_CANCEL = 50;
	private static final int OVERTIME = 60;
	private int mCurrentState = STATE_NORMAL;
	// 已经开始录音
	private boolean isRecording = false;
	private DialogManager mDialogManager;
	private float mTime = 0;
	// 是否触发了onlongclick，准备好了
	private boolean mReady;
	private AudioManager mAudioManager;
	private String saveDir = FileSaveUtil.voice_dir;

	private Handler mp3handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case AudioManager.MSG_ERROR_AUDIO_RECORD:
				Toast.makeText(getContext(), "录音权限被屏蔽或者录音设备损坏！\n请在设置中检查是否开启权限！",
						Toast.LENGTH_SHORT).show();
				mDialogManager.dimissDialog();
				mAudioManager.cancel();
				reset();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 先实现两个参数的构造方法，布局会默认引用这个构造方法， 用一个 构造参数的构造方法来引用这个方法 * @param context
	 */

	public AudioRecordButton(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public AudioRecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mDialogManager = new DialogManager(getContext());

		try {
			FileSaveUtil.createSDDirectory(FileSaveUtil.voice_dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAudioManager = AudioManager.getInstance(FileSaveUtil.voice_dir);
		mAudioManager.setOnAudioStageListener(this);
		mAudioManager.setHandle(mp3handler);
		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method
				try {
					FileSaveUtil.createSDDirectory(saveDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mAudioManager.setVocDir(saveDir);
				mListener.onStart();
				mReady = true;
				mAudioManager.prepareAudio();
				return false;
			}
		});
		// TODO Auto-generated constructor stub
	}

	public void setSaveDir(String saveDir) {
		this.saveDir =  saveDir + saveDir;
	}

	/**
	 * 录音完成后的回调，回调给activiy，可以获得mtime和文件的路径
	 */
	public interface AudioFinishRecorderListener {
		void onStart();

		void onFinished(float seconds, String filePath);
	}

	private AudioFinishRecorderListener mListener;

	public void setAudioFinishRecorderListener(
			AudioFinishRecorderListener listener) {
		mListener = listener;
	}

	// 获取音量大小的runnable
	private Runnable mGetVoiceLevelRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isRecording) {
				try {
					Thread.sleep(100);
					mTime += 0.1f;
					mhandler.sendEmptyMessage(MSG_VOICE_CHANGE);
					if (mTime >= OVERTIME) {
						mTime = 60;
						mhandler.sendEmptyMessage(MSG_OVERTIME_SEND);
						isRecording = false;
						break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	// 准备三个常量
	private static final int MSG_AUDIO_PREPARED = 0X110;
	private static final int MSG_VOICE_CHANGE = 0X111;
	private static final int MSG_DIALOG_DIMISS = 0X112;
	private static final int MSG_OVERTIME_SEND = 0X113;

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:
				// 显示应该是在audio end prepare之后回调
				if (isTouch) {
					mTime = 0;
					mDialogManager.showRecordingDialog();
					isRecording = true;
					new Thread(mGetVoiceLevelRunnable).start();
				}
				// 需要开启一个线程来变换音量
				break;
			case MSG_VOICE_CHANGE:
				mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(3));
				break;
			case MSG_DIALOG_DIMISS:
				isRecording = false;
				mDialogManager.dimissDialog();
				break;
			case MSG_OVERTIME_SEND:
				mDialogManager.tooLong();
				mhandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);// 持续1.3s
				if (mListener != null) {// 并且callbackActivity，保存录音
					File file = new File(mAudioManager.getCurrentFilePath());
					if (FileSaveUtil.isFileExists(file)) {
						mListener.onFinished(mTime,
								mAudioManager.getCurrentFilePath());
					}else{
						mp3handler.sendEmptyMessage(AudioManager.MSG_ERROR_AUDIO_RECORD);
					}
				}
				isRecording = false;
				reset();// 恢复标志位
				break;
			}
		};
	};

	// 在这里面发送一个handler的消息
	@Override
	public void wellPrepared() {
		// TODO Auto-generated method stub
		mhandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
	}

	/**
	 * 直接复写这个监听函数
	 */
	private boolean isTouch = false;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			isTouch = true;
			changeState(STATE_RECORDING);
			break;
		case MotionEvent.ACTION_MOVE:

			if (isRecording) {

				// 根据x，y来判断用户是否想要取消
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_TO_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}

			}

			break;
		case MotionEvent.ACTION_UP:
			// 首先判断是否有触发onlongclick事件，没有的话直接返回reset
			isTouch = false;
			if (!mReady) {
				reset();
				return super.onTouchEvent(event);
			}
			// 如果按的时间太短，还没准备好或者时间录制太短，就离开了，则显示这个dialog
			if (!isRecording || mTime < 0.6f) {
				mDialogManager.tooShort();
				mAudioManager.cancel();
				mhandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);// 持续1.3s
			} else if (mCurrentState == STATE_RECORDING) {// 正常录制结束
				mDialogManager.dimissDialog();
				mAudioManager.release();// release释放一个mediarecorder
				if (mListener != null) {// 并且callbackActivity，保存录音
					BigDecimal b = new BigDecimal(mTime);
					float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP)
							.floatValue();
					File file = new File(mAudioManager.getCurrentFilePath());
					if (FileSaveUtil.isFileExists(file)) {
						mListener.onFinished(f1,mAudioManager.getCurrentFilePath());
					}else{
						mp3handler.sendEmptyMessage(AudioManager.MSG_ERROR_AUDIO_RECORD);
					}
				}
			} else if (mCurrentState == STATE_WANT_TO_CANCEL) {
				mAudioManager.cancel();
				mDialogManager.dimissDialog();
			}
			isRecording = false;
			reset();// 恢复标志位

			break;
		case MotionEvent.ACTION_CANCEL:
			isTouch = false;
			reset();
			break;
			
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 回复标志位以及状态
	 */
	private void reset() {
		// TODO Auto-generated method stub
		isRecording = false;
		changeState(STATE_NORMAL);
		mReady = false;
		mTime = 0;
	}

	private boolean wantToCancel(int x, int y) {
		// TODO Auto-generated method stub

		if (x < 0 || x > getWidth()) {// 判断是否在左边，右边，上边，下边
			return true;
		}
		if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
			return true;
		}

		return false;
	}

	private void changeState(int state) {
		// TODO Auto-generated method stub
		if (mCurrentState != state) {
			mCurrentState = state;
			switch (mCurrentState) {
			case STATE_NORMAL:
				setBackgroundResource(R.drawable.button_recordnormal);
				setText(R.string.normal);

				break;
			case STATE_RECORDING:
				setBackgroundResource(R.drawable.button_recording);
				setText(R.string.recording);
				if (isRecording) {
					mDialogManager.recording();
					// 复写dialog.recording();
				}
				break;

			case STATE_WANT_TO_CANCEL:
				setBackgroundResource(R.drawable.button_recording);
				setText(R.string.want_to_cancle);
				// dialog want to cancel
				mDialogManager.wantToCancel();
				break;

			}
		}

	}

	@Override
	public boolean onPreDraw() {
		// TODO Auto-generated method stub
		return false;
	}

}
