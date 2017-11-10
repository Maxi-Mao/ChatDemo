package com.maxi.chatdemo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.AbsListView;

import com.maxi.chatdemo.adapter.ChatListViewAdapter;
import com.maxi.chatdemo.common.ChatConst;
import com.maxi.chatdemo.db.ChatMessageBean;
import com.maxi.chatdemo.ui.base.BaseActivity;
import com.maxi.chatdemo.utils.KeyBoardUtils;
import com.maxi.chatdemo.widget.AudioRecordButton;
import com.maxi.chatdemo.widget.pulltorefresh.PullToRefreshListView;
import com.maxi.chatdemo.widget.pulltorefresh.base.PullToRefreshView;

import java.lang.ref.WeakReference;

/**
 * Created by Mao Jiqing on 2016/10/10.
 */
public class ListViewChatActivity extends BaseActivity {
    public PullToRefreshListView myList;
    public ChatListViewAdapter tbAdapter;
    private SendMessageHandler sendMessageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        tblist.clear();
        tbAdapter.notifyDataSetChanged();
        myList.setAdapter(null);
        sendMessageHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void findView() {
        super.findView();
        pullList.setSlideView(new PullToRefreshView(this).getSlideView(PullToRefreshView.LISTVIEW));
        myList = (PullToRefreshListView) pullList.returnMylist();
    }

    @Override
    protected void init() {
        setTitle("ListView");
        sendMessageHandler = new SendMessageHandler(this);
        tbAdapter = new ChatListViewAdapter(this);
        tbAdapter.setUserList(tblist);
        myList.setAdapter(tbAdapter);
        tbAdapter.isPicRefresh = true;
        tbAdapter.notifyDataSetChanged();
        tbAdapter.setSendErrorListener(new ChatListViewAdapter.SendErrorListener() {

            @Override
            public void onClick(int position) {
                // TODO Auto-generated method stub
                ChatMessageBean tbub = tblist.get(position);
                if (tbub.getType() == ChatListViewAdapter.TO_USER_VOICE) {
                    sendVoice(tbub.getUserVoiceTime(), tbub.getUserVoicePath());
                    tblist.remove(position);
                } else if (tbub.getType() == ChatListViewAdapter.TO_USER_IMG) {
                    sendImage(tbub.getImageLocal());
                    tblist.remove(position);
                }
            }

        });
        tbAdapter.setVoiceIsReadListener(new ChatListViewAdapter.VoiceIsRead() {

            @Override
            public void voiceOnClick(int position) {
                // TODO Auto-generated method stub
                for (int i = 0; i < tbAdapter.unReadPosition.size(); i++) {
                    if (tbAdapter.unReadPosition.get(i).equals(position + "")) {
                        tbAdapter.unReadPosition.remove(i);
                        break;
                    }
                }
            }

        });
        myList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(true);
                        tbAdapter.isPicRefresh = false;
                        tbAdapter.notifyDataSetChanged();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(false);
                        tbAdapter.isPicRefresh = true;
                        reset();
                        KeyBoardUtils.hideKeyBoard(ListViewChatActivity.this,
                                mEditTextContent);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        voiceBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {

            @Override
            public void onFinished(float seconds, String filePath) {
                // TODO Auto-generated method stub
                sendVoice(seconds, filePath);
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                tbAdapter.stopPlayVoice();
            }
        });
        super.init();
    }


    static class SendMessageHandler extends Handler {
        WeakReference<ListViewChatActivity> mActivity;

        SendMessageHandler(ListViewChatActivity activity) {
            mActivity = new WeakReference<ListViewChatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            ListViewChatActivity theActivity = mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {
                    case REFRESH:
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyDataSetChanged();
                        theActivity.myList.setSelection(theActivity.tblist
                                .size() - 1);
                        break;
                    case SEND_OK:
                        theActivity.mEditTextContent.setText("");
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyDataSetChanged();
                        theActivity.myList.setSelection(theActivity.tblist
                                .size() - 1);
                        break;
                    case RECERIVE_OK:
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyDataSetChanged();
                        theActivity.myList.setSelection(theActivity.tblist
                                .size() - 1);
                        break;
                    case PULL_TO_REFRESH_DOWN:
                        theActivity.pullList.refreshComplete();
                        theActivity.tbAdapter.notifyDataSetChanged();
                        theActivity.myList.setSelection(theActivity.position - 1);
                        theActivity.isDown = false;
                        break;
                    default:
                        break;
                }
            }
        }

    }

    @Override
    protected void loadRecords() {
        isDown = true;
        if (pagelist != null) {
            pagelist.clear();
        }
        pagelist = mChatDbManager.loadPages(page, number);
        position = pagelist.size();
        if (pagelist.size() != 0) {
            pagelist.addAll(tblist);
            tblist.clear();
            tblist.addAll(pagelist);
            if (imageList != null) {
                imageList.clear();
            }
            if (imagePosition != null) {
                imagePosition.clear();
            }
            int key = 0;
            int position = 0;
            for (ChatMessageBean cmb : tblist) {
                if (cmb.getType() == ChatListViewAdapter.FROM_USER_IMG || cmb.getType() == ChatListViewAdapter.TO_USER_IMG) {
                    imageList.add(cmb.getImageLocal());
                    imagePosition.put(key, position);
                    position++;
                }
                key++;
            }
            tbAdapter.setImageList(imageList);
            tbAdapter.setImagePosition(imagePosition);
            sendMessageHandler.sendEmptyMessage(PULL_TO_REFRESH_DOWN);
            if (page == 0) {
                pullList.refreshComplete();
                pullList.setPullGone();
            } else {
                page--;
            }
        } else {
            if (page == 0) {
                pullList.refreshComplete();
                pullList.setPullGone();
            }
        }
    }

    /**
     * 发送文字
     */
    @Override
    protected void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String content = mEditTextContent.getText().toString();
                tblist.add(getTbub(userName, ChatListViewAdapter.TO_USER_MSG, content, null, null,
                        null, null, null, 0f, ChatConst.COMPLETED));
                sendMessageHandler.sendEmptyMessage(SEND_OK);
                ListViewChatActivity.this.content = content;
                receriveHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }).start();
    }

    /**
     * 接收文字
     */
    String content = "";

    private void receriveMsgText(final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "回复：" + content;
                ChatMessageBean tbub = new ChatMessageBean();
                tbub.setUserName(userName);
                String time = returnTime();
                tbub.setUserContent(message);
                tbub.setTime(time);
                tbub.setType(ChatListViewAdapter.FROM_USER_MSG);
                tblist.add(tbub);
                sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
                mChatDbManager.insert(tbub);
            }
        }).start();
    }

    /**
     * 发送图片
     */
    int i = 0;

    @Override
    protected void sendImage(final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (i == 0) {
                    tblist.add(getTbub(userName, ChatListViewAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                            0f, ChatConst.SENDING));
                } else if (i == 1) {
                    tblist.add(getTbub(userName, ChatListViewAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                            0f, ChatConst.SENDERROR));
                } else if (i == 2) {
                    tblist.add(getTbub(userName, ChatListViewAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                            0f, ChatConst.COMPLETED));
                    i = -1;
                }
                imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                sendMessageHandler.sendEmptyMessage(SEND_OK);
                ListViewChatActivity.this.filePath = filePath;
                receriveHandler.sendEmptyMessageDelayed(1, 3000);
                i++;
            }
        }).start();
    }

    /**
     * 接收图片
     */
    String filePath = "";

    private void receriveImageText(final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatMessageBean tbub = new ChatMessageBean();
                tbub.setUserName(userName);
                String time = returnTime();
                tbub.setTime(time);
                tbub.setImageLocal(filePath);
                tbub.setType(ChatListViewAdapter.FROM_USER_IMG);
                tblist.add(tbub);
                imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
                mChatDbManager.insert(tbub);
            }
        }).start();
    }

    /**
     * 发送语音
     */
    @Override
    protected void sendVoice(final float seconds, final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tblist.add(getTbub(userName, ChatListViewAdapter.TO_USER_VOICE, null, null, null, null, filePath,
                        null, seconds, ChatConst.SENDING));
                sendMessageHandler.sendEmptyMessage(SEND_OK);
                ListViewChatActivity.this.seconds = seconds;
                voiceFilePath = filePath;
                receriveHandler.sendEmptyMessageDelayed(2, 3000);
            }
        }).start();
    }

    /**
     * 接收语音
     */
    float seconds = 0.0f;
    String voiceFilePath = "";

    private void receriveVoiceText(final float seconds, final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatMessageBean tbub = new ChatMessageBean();
                tbub.setUserName(userName);
                String time = returnTime();
                tbub.setTime(time);
                tbub.setUserVoiceTime(seconds);
                tbub.setUserVoicePath(filePath);
                tbAdapter.unReadPosition.add(tblist.size() + "");
                tbub.setType(ChatListViewAdapter.FROM_USER_VOICE);
                tblist.add(tbub);
                sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
                mChatDbManager.insert(tbub);
            }
        }).start();
    }

    /**
     * 为了模拟接收延迟
     */
    private Handler receriveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    receriveMsgText(content);
                    break;
                case 1:
                    receriveImageText(filePath);
                    break;
                case 2:
                    receriveVoiceText(seconds, voiceFilePath);
                    break;
                default:
                    break;
            }
        }
    };


}
