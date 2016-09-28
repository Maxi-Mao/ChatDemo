package com.maxi.chatdemo.utils;

import android.media.MediaRecorder;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AudioManager {
	/**
	 * 录音的时候出错
	 */
	public static final int MSG_ERROR_AUDIO_RECORD = -4;
	private MediaRecorder mRecorder;
	private String mDirString;
	private String mCurrentFilePathString;
	private Handler handler;
	private boolean isPrepared;// 是否准备好了

	/**
	 * 单例化的方法 1 先声明一个static 类型的变量a 2 在声明默认的构造函数 3 再用public synchronized static
	 * 类名 getInstance() { if(a==null) { a=new 类();} return a; } 或者用以下的方法
	 */

	/**
	 * 单例化这个类
	 */
	private static AudioManager mInstance;

	private AudioManager(String dir) {
		mDirString = dir;
	}

	public static AudioManager getInstance(String dir) {
		if (mInstance == null) {
			synchronized (AudioManager.class) {
				if (mInstance == null) {
					mInstance = new AudioManager(dir);

				}
			}
		}
		return mInstance;

	}

	public void setHandle(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 回调函数，准备完毕，准备好后，button才会开始显示录音框
	 * 
	 */
	public interface AudioStageListener {
		void wellPrepared();
	}

	public AudioStageListener mListener;

	public void setOnAudioStageListener(AudioStageListener listener) {
		mListener = listener;
	}

	public void setVocDir(String dir) {
		mDirString = dir;
	}

	// 准备方法
	@SuppressWarnings("deprecation")
	public void prepareAudio() {
		try {
			// 一开始应该是false的
			isPrepared = false;

			File dir = new File(mDirString);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileNameString = generalFileName();
			File file = new File(dir, fileNameString);

			mCurrentFilePathString = file.getAbsolutePath();

			mRecorder = new MediaRecorder();
			// 设置输出文件
			mRecorder.setOutputFile(file.getAbsolutePath());
			// 设置meidaRecorder的音频源是麦克风
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置文件音频的输出格式为amr
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			// 设置音频的编码格式为amr
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			// 严格遵守google官方api给出的mediaRecorder的状态流程图
			mRecorder.prepare();

			mRecorder.start();
			// 准备结束
			// 已经准备好了，可以录制了
			if (mListener != null) {
				mListener.wellPrepared();
			}
			isPrepared = true;

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (handler != null) {
				handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (handler != null) {
				handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (handler != null) {
				handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
			}
		}

	}

	/**
	 * 随机生成文件的名称
	 * 
	 * @return
	 */
	private String generalFileName() {
		// TODO Auto-generated method stub

		return UUID.randomUUID().toString() + ".amr";
	}

	private int vocAuthority[] = new int[10];
	private int vocNum = 0;
	private boolean check = true;

	// 获得声音的level
	public int getVoiceLevel(int maxLevel) {
		// mRecorder.getMaxAmplitude()这个是音频的振幅范围，值域是0-32767
		if (isPrepared) {
			try {
				int vocLevel = mRecorder.getMaxAmplitude();
				if (check) {
					if (vocNum >= 10) {
						Set<Integer> set = new HashSet<Integer>();
						for (int i = 0; i < vocNum; i++) {
							set.add(vocAuthority[i]);
						}
						if (set.size() == 1) {
							if (handler != null)
								handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
							vocNum = 0;
							vocAuthority = null;
							vocAuthority = new int[10];
						} else {
							check = false;
						}
					} else {
						vocAuthority[vocNum] = vocLevel;
						vocNum++;
					}
				}
				return maxLevel * vocLevel / 32768 + 1;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if (handler != null)
					handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
			}
		}

		return 1;
	}

	// 释放资源
	public void release() {
		// 严格按照api流程进行
		if (null != mRecorder) {
			isPrepared = false;
			try {
				mRecorder.stop();
				mRecorder.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mRecorder = null;
		}
	}

	// 取消,因为prepare时产生了一个文件，所以cancel方法应该要删除这个文件，
	// 这是与release的方法的区别
	public void cancel() {
		release();
		if (mCurrentFilePathString != null) {
			File file = new File(mCurrentFilePathString);
			file.delete();
			mCurrentFilePathString = null;
		}

	}

	public String getCurrentFilePath() {
		// TODO Auto-generated method stub
		return mCurrentFilePathString;
	}

}
