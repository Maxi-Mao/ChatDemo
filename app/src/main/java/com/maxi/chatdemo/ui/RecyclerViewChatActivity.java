package com.maxi.chatdemo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maxi.chatdemo.R;
import com.maxi.chatdemo.adapter.ChatListViewAdapter;
import com.maxi.chatdemo.adapter.ChatRecyclerAdapter;
import com.maxi.chatdemo.adapter.DataAdapter;
import com.maxi.chatdemo.adapter.ExpressionAdapter;
import com.maxi.chatdemo.adapter.ExpressionPagerAdapter;
import com.maxi.chatdemo.animator.SlideInOutBottomItemAnimator;
import com.maxi.chatdemo.common.ChatConst;
import com.maxi.chatdemo.db.ChatDbManager;
import com.maxi.chatdemo.db.ChatMessageBean;
import com.maxi.chatdemo.utils.FileSaveUtil;
import com.maxi.chatdemo.utils.ImageCheckoutUtil;
import com.maxi.chatdemo.utils.KeyBoardUtils;
import com.maxi.chatdemo.utils.PictureUtil;
import com.maxi.chatdemo.utils.ScreenUtil;
import com.maxi.chatdemo.utils.SmileUtils;
import com.maxi.chatdemo.widget.AudioRecordButton;
import com.maxi.chatdemo.widget.ChatBottomView;
import com.maxi.chatdemo.widget.ExpandGridView;
import com.maxi.chatdemo.widget.HeadIconSelectorView;
import com.maxi.chatdemo.widget.MediaManager;
import com.maxi.chatdemo.widget.pulltorefresh.PullToRefreshLayout;
import com.maxi.chatdemo.widget.pulltorefresh.PullToRefreshRecyclerView;
import com.maxi.chatdemo.widget.pulltorefresh.WrapContentLinearLayoutManager;
import com.maxi.chatdemo.widget.pulltorefresh.base.PullToRefreshView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewChatActivity extends AppCompatActivity {
    private PullToRefreshLayout pullList;
    private boolean isDown = false;
    private boolean CAN_WRITE_EXTERNAL_STORAGE = true;
    private boolean CAN_RECORD_AUDIO = true;
    private int bottomStatusHeight = 0;
    private ListView mess_lv;
    private PullToRefreshRecyclerView myList;
    private ChatRecyclerAdapter tbAdapter;
    private WrapContentLinearLayoutManager wcLinearLayoutManger;
    private DataAdapter adapter;
    private AudioRecordButton voiceBtn;
    private EditText mEditTextContent;
    private ViewPager expressionViewpager;
    private LinearLayout emoji_group;
    private TextView send_emoji_icon;
    private ImageView emoji;
    private ImageView mess_iv;
    private ImageView voiceIv;
    private SendMessageHandler sendMessageHandler;
    private File mCurrentPhotoFile;
    private ChatBottomView tbbv;
    private View activityRootView;
    private Toast mToast;
    private int listSlideHeight = 0;//滑动距离
    private String permissionInfo;
    private String camPicPath;
    private String item[] = {"你好!", "我正忙着呢,等等", "有啥事吗？", "有时间聊聊吗", "再见！"};
    private List<ChatMessageBean> tblist = new ArrayList<ChatMessageBean>();
    private List<String> reslist;
    private ChatDbManager mChatDbManager;
    private static final int SDK_PERMISSION_REQUEST = 127;
    private static final int IMAGE_SIZE = 100 * 1024;// 300kb
    private static final int SEND_OK = 0x1110;
    private static final int REFRESH = 0x0011;
    private static final int RECERIVE_OK = 0x1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findView();
        initpop();
        init();
        // after andrioid m,must request Permiision on runtime
        getPersimmions();
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
//            /***
//             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
//             */
//            // 定位精确位置
//            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 麦克风权限
            if (addPermission(permissions, Manifest.permission.RECORD_AUDIO)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
//            // 读取电话状态权限
//            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
//                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
//            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SDK_PERMISSION_REQUEST:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission Denied
                    CAN_WRITE_EXTERNAL_STORAGE = false;
                    Toast.makeText(RecyclerViewChatActivity.this, "禁用图片权限将导致发送图片功能无法使用！", Toast.LENGTH_SHORT)
                            .show();
                }
                if (perms.get(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    CAN_RECORD_AUDIO = false;
                    Toast.makeText(RecyclerViewChatActivity.this, "禁用录制音频权限将导致语音功能无法使用！", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void findView() {
        pullList = (PullToRefreshLayout) findViewById(R.id.content_lv);
        pullList.setSlideView(new PullToRefreshView(this).getSlideView(PullToRefreshView.RECYCLERVIEW));
        assert pullList != null;
        myList = (PullToRefreshRecyclerView) pullList.returnMylist();
        activityRootView = findViewById(R.id.layout_tongbao_rl);
        mEditTextContent = (EditText) findViewById(R.id.mess_et);
        mess_iv = (ImageView) findViewById(R.id.mess_iv);
        emoji = (ImageView) findViewById(R.id.emoji);
        voiceIv = (ImageView) findViewById(R.id.voice_iv);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        voiceBtn = (AudioRecordButton) findViewById(R.id.voice_btn);
        emoji_group = (LinearLayout) findViewById(R.id.emoji_group);
        send_emoji_icon = (TextView) findViewById(R.id.send_emoji_icon);
        tbbv = (ChatBottomView) findViewById(R.id.other_lv);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        MediaManager.pause();
        MediaManager.release();
        tblist.clear();
        tbAdapter.notifyDataSetChanged();
        myList.setAdapter(null);
        handler.removeCallbacksAndMessages(null);
        sendMessageHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        cancelToast();
    }

    @SuppressLint({"NewApi", "InflateParams"})
    private void initpop() {
        mess_lv = (ListView) findViewById(R.id.mess_lv);
        adapter = new DataAdapter(this, item);
        mess_lv.setAdapter(adapter);
        mess_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                mEditTextContent.setText(item[arg2]);
                sendMsgText();
            }

        });
    }

    private void init() {
        mChatDbManager = new ChatDbManager();
        PullToRefreshLayout.pulltorefreshNotifier pullNotifier = new PullToRefreshLayout.pulltorefreshNotifier() {
            @Override
            public void onPull() {
                // TODO Auto-generated method stub
                downLoad();
            }
        };
        pullList.setpulltorefreshNotifier(pullNotifier);
        tbAdapter = new ChatRecyclerAdapter(this, tblist);
        wcLinearLayoutManger = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myList.setLayoutManager(wcLinearLayoutManger);
        myList.setItemAnimator(new SlideInOutBottomItemAnimator(myList));
        myList.setAdapter(tbAdapter);
        sendMessageHandler = new SendMessageHandler(this);
        tbAdapter.isPicRefresh = true;
        tbAdapter.notifyDataSetChanged();
        tbAdapter.setSendErrorListener(new ChatRecyclerAdapter.SendErrorListener() {

            @Override
            public void onClick(int position) {
                // TODO Auto-generated method stub
                ChatMessageBean tbub = tblist.get(position);
                if (tbub.getType() == ChatRecyclerAdapter.TO_USER_VOICE) {
                    upVoice(tbub.getUserVoiceTime(), tbub.getUserVoicePath());
                    tblist.remove(position);
                } else if (tbub.getType() == ChatRecyclerAdapter.TO_USER_IMG) {
                    upImage(tbub.getImageLocal());
                    tblist.remove(position);
                }
            }

        });
        tbAdapter.setVoiceIsReadListener(new ChatRecyclerAdapter.VoiceIsRead() {

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
        voiceIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (voiceBtn.getVisibility() == View.GONE) {
                    emoji.setBackgroundResource(R.mipmap.emoji);
                    mess_iv.setBackgroundResource(R.mipmap.tb_more);
                    mEditTextContent.setVisibility(View.GONE);
                    emoji_group.setVisibility(View.GONE);
                    tbbv.setVisibility(View.GONE);
                    mess_lv.setVisibility(View.GONE);
                    voiceBtn.setVisibility(View.VISIBLE);
                    KeyBoardUtils.hideKeyBoard(RecyclerViewChatActivity.this,
                            mEditTextContent);
                    voiceIv.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                } else {
                    mEditTextContent.setVisibility(View.VISIBLE);
                    voiceBtn.setVisibility(View.GONE);
                    voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
                    KeyBoardUtils.showKeyBoard(RecyclerViewChatActivity.this, mEditTextContent);
                }
            }

        });
        voiceBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {

            @Override
            public void onFinished(float seconds, String filePath) {
                // TODO Auto-generated method stub
                upVoice(seconds, filePath);
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                tbAdapter.stopPlayVoice();
            }
        });
        mess_iv.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                emoji_group.setVisibility(View.GONE);
                if (tbbv.getVisibility() == View.GONE
                        && mess_lv.getVisibility() == View.GONE) {
                    mEditTextContent.setVisibility(View.VISIBLE);
                    mess_iv.setFocusable(true);
                    voiceBtn.setVisibility(View.GONE);
                    emoji.setBackgroundResource(R.mipmap.emoji);
                    voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
                    tbbv.setVisibility(View.VISIBLE);
                    KeyBoardUtils.hideKeyBoard(RecyclerViewChatActivity.this,
                            mEditTextContent);
                    mess_iv.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                } else {
                    tbbv.setVisibility(View.GONE);
                    KeyBoardUtils.showKeyBoard(RecyclerViewChatActivity.this, mEditTextContent);
                    mess_iv.setBackgroundResource(R.mipmap.tb_more);
                    if (mess_lv.getVisibility() != View.GONE) {
                        mess_lv.setVisibility(View.GONE);
                        KeyBoardUtils.showKeyBoard(RecyclerViewChatActivity.this, mEditTextContent);
                        mess_iv.setBackgroundResource(R.mipmap.tb_more);
                    }
                }
            }
        });
        tbbv.setOnHeadIconClickListener(new HeadIconSelectorView.OnHeadIconClickListener() {

            @SuppressLint("InlinedApi")
            @Override
            public void onClick(int from) {
                switch (from) {
                    case ChatBottomView.FROM_CAMERA:
                        if (!CAN_WRITE_EXTERNAL_STORAGE) {
                            Toast.makeText(RecyclerViewChatActivity.this, "权限未开通\n请到设置中开通相册权限", Toast.LENGTH_SHORT).show();
                        } else {
                            final String state = Environment.getExternalStorageState();
                            if (Environment.MEDIA_MOUNTED.equals(state)) {
                                camPicPath = getSavePicPath();
                                Intent openCameraIntent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri uri = Uri.fromFile(new File(camPicPath));
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(openCameraIntent,
                                        ChatBottomView.FROM_CAMERA);
                            } else {
                                showToast("请检查内存卡");
                            }
                        }
                        break;
                    case ChatBottomView.FROM_GALLERY:
                        if (!CAN_WRITE_EXTERNAL_STORAGE) {
                            Toast.makeText(RecyclerViewChatActivity.this, "权限未开通\n请到设置中开通相册权限", Toast.LENGTH_SHORT).show();
                        } else {
                            String status = Environment.getExternalStorageState();
                            if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
                                Intent intent = new Intent();
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                } else {
                                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.putExtra("crop", "true");
                                    intent.putExtra("scale", "true");
                                    intent.putExtra("scaleUpIfNeeded", true);
                                }
                                intent.setType("image/*");
                                startActivityForResult(intent,
                                        ChatBottomView.FROM_GALLERY);
                            } else {
                                showToast("没有SD卡");
                            }
                        }
                        break;

                    case ChatBottomView.FROM_PHRASE:
                        if (mess_lv.getVisibility() == View.GONE) {
                            tbbv.setVisibility(View.GONE);
                            emoji.setBackgroundResource(R.mipmap.emoji);
                            voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
                            mess_lv.setVisibility(View.VISIBLE);
                            KeyBoardUtils.hideKeyBoard(RecyclerViewChatActivity.this,
                                    mEditTextContent);
                            mess_iv.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                        }
                }
            }

        });
        emoji.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mess_lv.setVisibility(View.GONE);
                tbbv.setVisibility(View.GONE);
                if (emoji_group.getVisibility() == View.GONE) {
                    mEditTextContent.setVisibility(View.VISIBLE);
                    voiceBtn.setVisibility(View.GONE);
                    voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
                    mess_iv.setBackgroundResource(R.mipmap.tb_more);
                    emoji_group.setVisibility(View.VISIBLE);
                    emoji.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                    KeyBoardUtils.hideKeyBoard(RecyclerViewChatActivity.this,
                            mEditTextContent);
                } else {
                    emoji_group.setVisibility(View.GONE);
                    emoji.setBackgroundResource(R.mipmap.emoji);
                    KeyBoardUtils.showKeyBoard(RecyclerViewChatActivity.this, mEditTextContent);
                }
            }
        });
        // 表情list
        reslist = getExpressionRes(40);
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));

        mEditTextContent.setOnKeyListener(onKeyListener);
        mEditTextContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                emoji_group.setVisibility(View.GONE);
                tbbv.setVisibility(View.GONE);
                mess_lv.setVisibility(View.GONE);
                emoji.setBackgroundResource(R.mipmap.emoji);
                mess_iv.setBackgroundResource(R.mipmap.tb_more);
                voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
            }

        });
        send_emoji_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                sendMsgText();
            }

        });
        myList.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                // TODO Auto-generated method stub
                switch (scrollState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(true);
                        tbAdapter.isPicRefresh = false;
                        tbAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(false);
                        tbAdapter.isPicRefresh = true;
                        reset();
                        KeyBoardUtils.hideKeyBoard(RecyclerViewChatActivity.this,
                                mEditTextContent);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        controlKeyboardLayout(activityRootView, pullList);
        bottomStatusHeight = ScreenUtil.getBottomStatusHeight(this);
        //加载本地聊天记录
        page = (int) mChatDbManager.getPages(number);
        loadRecords();
    }

    /**
     * @param root             最外层布局
     * @param needToScrollView 要滚动的布局,就是说在键盘弹出的时候,你需要试图滚动上去的View,在键盘隐藏的时候,他又会滚动到原来的位置的布局
     */
    private void controlKeyboardLayout(final View root, final View needToScrollView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                //获取当前界面可视部分
                RecyclerViewChatActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = RecyclerViewChatActivity.this.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                int recyclerHeight = 0;
                if (wcLinearLayoutManger != null) {
                    recyclerHeight = wcLinearLayoutManger.getRecyclerHeight();
                }
                if (heightDifference == bottomStatusHeight) {
                    needToScrollView.scrollTo(0, 0);
                } else {
                    if (heightDifference < recyclerHeight) {
                        int contentHeight = wcLinearLayoutManger == null ? 0 : wcLinearLayoutManger.getHeight();
                        if (recyclerHeight < contentHeight) {
                            listSlideHeight = heightDifference - (contentHeight - recyclerHeight);
                            needToScrollView.scrollTo(0, listSlideHeight);
                        } else {
                            listSlideHeight = heightDifference;
                            needToScrollView.scrollTo(0, listSlideHeight);
                        }
                    } else {
                        listSlideHeight = 0;
                    }
                }
            }
        });
    }

    private int page = 0;
    private int number = 10;
    private List<ChatMessageBean> pagelist = new ArrayList<ChatMessageBean>();

    private void loadRecords() {
        isDown = true;
        if (pagelist != null) {
            pagelist.clear();
        }
        pagelist = mChatDbManager.loadPages(page, number);
        if (pagelist.size() != 0) {
            handler.sendEmptyMessage(1);
            if (page == 0) {
                pullList.refreshComplete();
                pullList.setPullGone();
            } else {
                page--;
            }
        }
    }

    private void downLoad() {
        if (!isDown) {
            isDown = true;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    loadRecords();
                }
            }).start();
        }
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                sendMsgText();
                return true;
            }
            return false;
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int position = pagelist.size();
                    pagelist.addAll(tblist);
                    tblist.clear();
                    tblist.addAll(pagelist);
                    pullList.refreshComplete();
                    tbAdapter.notifyDataSetChanged();
                    myList.smoothScrollToPosition(position - 1);
                    isDown = false;
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            tbbv.setVisibility(View.GONE);
            mess_iv.setBackgroundResource(R.mipmap.tb_more);
            switch (requestCode) {
                case ChatBottomView.FROM_CAMERA:
                    FileInputStream is = null;
                    try {
                        is = new FileInputStream(camPicPath);
                        File camFile = new File(camPicPath); // 图片文件路径
                        if (camFile.exists()) {
                            int size = ImageCheckoutUtil
                                    .getImageSize(ChatListViewAdapter
                                            .getLoacalBitmap(camPicPath));
                            if (size > IMAGE_SIZE) {
                                showDialog(camPicPath);
                            } else {
                                upImage(camPicPath);
                            }
                        } else {
                            showToast("该文件不存在!");
                        }
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        // 关闭流
                        try {
                            is.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                case ChatBottomView.FROM_GALLERY:
                    Uri uri = data.getData();
                    String path = FileSaveUtil.getPath(getApplicationContext(), uri);
                    mCurrentPhotoFile = new File(path); // 图片文件路径
                    if (mCurrentPhotoFile.exists()) {
                        int size = ImageCheckoutUtil.getImageSize(ChatListViewAdapter
                                .getLoacalBitmap(path));
                        if (size > IMAGE_SIZE) {
                            showDialog(path);
                        } else {
                            upImage(path);
                        }
                    } else {
                        showToast("该文件不存在!");
                    }

                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            // Toast.makeText(this, "操作取消", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSavePicPath() {
        final String dir = FileSaveUtil.SD_CARD_PATH + "image_data/";
        try {
            FileSaveUtil.createSDDirectory(dir);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String fileName = String.valueOf(System.currentTimeMillis() + ".png");
        return dir + fileName;
    }

    private void reset() {
        emoji_group.setVisibility(View.GONE);
        tbbv.setVisibility(View.GONE);
        mess_lv.setVisibility(View.GONE);
        emoji.setBackgroundResource(R.mipmap.emoji);
        mess_iv.setBackgroundResource(R.mipmap.tb_more);
        voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    static class SendMessageHandler extends Handler {
        WeakReference<RecyclerViewChatActivity> mActivity;

        SendMessageHandler(RecyclerViewChatActivity activity) {
            mActivity = new WeakReference<RecyclerViewChatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            RecyclerViewChatActivity theActivity = mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {
                    case REFRESH:
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyDataSetChanged();
                        int position = theActivity.tbAdapter.getItemCount() - 1 < 0 ? 0 : theActivity.tbAdapter.getItemCount() - 1;
                        theActivity.myList.smoothScrollToPosition(position);
                        break;
                    case SEND_OK:
                        theActivity.mEditTextContent.setText("");
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyItemInserted(theActivity.tblist
                                .size() - 1);
                        theActivity.myList.smoothScrollToPosition(theActivity.tbAdapter.getItemCount() - 1);
                        break;
                    case RECERIVE_OK:
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyItemInserted(theActivity.tblist
                                .size() - 1);
                        theActivity.myList.smoothScrollToPosition(theActivity.tbAdapter.getItemCount() - 1);
                        break;
                    default:
                        break;
                }
            }
        }

    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.layout_expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
                1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (filename != "delete_expression") { // 不是删除键，显示表情
                        // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                        @SuppressWarnings("rawtypes")
                        Class clz = Class
                                .forName("com.maxi.chatdemo.utils.SmileUtils");
                        Field field = clz.getField(filename);
                        String oriContent = mEditTextContent.getText()
                                .toString();
                        int index = Math.max(
                                mEditTextContent.getSelectionStart(), 0);
                        StringBuilder sBuilder = new StringBuilder(oriContent);
                        Spannable insertEmotion = SmileUtils.getSmiledText(
                                RecyclerViewChatActivity.this,
                                (String) field.get(null));
                        sBuilder.insert(index, insertEmotion);
                        mEditTextContent.setText(sBuilder.toString());
                        mEditTextContent.setSelection(index
                                + insertEmotion.length());
                    } else { // 删除文字或者表情
                        if (!TextUtils.isEmpty(mEditTextContent.getText())) {

                            int selectionStart = mEditTextContent
                                    .getSelectionStart();// 获取光标的位置
                            if (selectionStart > 0) {
                                String body = mEditTextContent.getText()
                                        .toString();
                                String tempStr = body.substring(0,
                                        selectionStart);
                                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                if (i != -1) {
                                    CharSequence cs = tempStr.substring(i,
                                            selectionStart);
                                    if (SmileUtils.containsKey(cs.toString()))
                                        mEditTextContent.getEditableText()
                                                .delete(i, selectionStart);
                                    else
                                        mEditTextContent.getEditableText()
                                                .delete(selectionStart - 1,
                                                        selectionStart);
                                } else {
                                    mEditTextContent.getEditableText().delete(
                                            selectionStart - 1, selectionStart);
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "f" + x;
            reslist.add(filename);
        }
        return reslist;

    }

    private void showDialog(final String path) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // // TODO Auto-generated method stub
                try {
                    String GalPicPath = getSavePicPath();
                    Bitmap bitmap = PictureUtil.compressSizeImage(path);
                    boolean isSave = FileSaveUtil.saveBitmap(
                            PictureUtil.reviewPicRotate(bitmap, GalPicPath),
                            GalPicPath);
                    File file = new File(GalPicPath);
                    if (file.exists() && isSave) {
                        upImage(GalPicPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    String userName = "test";

    /**
     * 发送文字
     */
    private void sendMsgText() {
        String content = mEditTextContent.getText().toString();
        tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_MSG, content, null, null,
                null, null, null, 0f, ChatConst.COMPLETED));
        sendMessageHandler.sendEmptyMessage(SEND_OK);
        this.content = content;
        receriveHandler.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * 接收文字
     */
    String content = "";

    private void receriveMsgText(String content) {
        content = "回复：" + content;
        ChatMessageBean tbub = new ChatMessageBean();
        tbub.setUserName(userName);
        String time = returnTime();
        tbub.setUserContent(content);
        tbub.setTime(time);
        tbub.setType(ChatRecyclerAdapter.FROM_USER_MSG);
        tblist.add(tbub);
        sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
        mChatDbManager.insert(tbub);
    }

    /**
     * 发送图片
     */
    int i = 0;

    private void upImage(String filePath) {
        if (i == 0) {
            tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                    0f, ChatConst.SENDING));
        } else if (i == 1) {
            tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                    0f, ChatConst.SENDERROR));
        } else if (i == 2) {
            tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                    0f, ChatConst.COMPLETED));
            i = -1;
        }
        sendMessageHandler.sendEmptyMessage(SEND_OK);
        this.filePath = filePath;
        receriveHandler.sendEmptyMessageDelayed(1, 3000);
        i++;
    }

    /**
     * 接收图片
     */
    String filePath = "";

    private void receriveImageText(String filePath) {
        ChatMessageBean tbub = new ChatMessageBean();
        tbub.setUserName(userName);
        String time = returnTime();
        tbub.setTime(time);
        tbub.setImageLocal(filePath);
        tbub.setType(ChatRecyclerAdapter.FROM_USER_IMG);
        tblist.add(tbub);
        sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
        mChatDbManager.insert(tbub);
    }

    /**
     * 发送语音
     */
    private void upVoice(float seconds, String filePath) {
        tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_VOICE, null, null, null, null, filePath,
                null, seconds, ChatConst.SENDING));
        sendMessageHandler.sendEmptyMessage(SEND_OK);
        this.seconds = seconds;
        voiceFilePath = filePath;
        receriveHandler.sendEmptyMessageDelayed(2, 3000);
    }

    /**
     * 接收语音
     */
    float seconds = 0.0f;
    String voiceFilePath = "";

    private void receriveVoiceText(float seconds, String filePath) {
        ChatMessageBean tbub = new ChatMessageBean();
        tbub.setUserName(userName);
        String time = returnTime();
        tbub.setTime(time);
        tbub.setUserVoiceTime(seconds);
        tbub.setUserVoicePath(filePath);
        tbAdapter.unReadPosition.add(tblist.size() + "");
        tbub.setType(ChatRecyclerAdapter.FROM_USER_VOICE);
        tblist.add(tbub);
        sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
        mChatDbManager.insert(tbub);
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

    private ChatMessageBean getTbub(String username, int type,
                                    String Content, String imageIconUrl, String imageUrl,
                                    String imageLocal, String userVoicePath, String userVoiceUrl,
                                    Float userVoiceTime, @ChatConst.SendState int sendState) {
        ChatMessageBean tbub = new ChatMessageBean();
        tbub.setUserName(username);
        String time = returnTime();
        tbub.setTime(time);
        tbub.setType(type);
        tbub.setUserContent(Content);
        tbub.setImageIconUrl(imageIconUrl);
        tbub.setImageUrl(imageUrl);
        tbub.setUserVoicePath(userVoicePath);
        tbub.setUserVoiceUrl(userVoiceUrl);
        tbub.setUserVoiceTime(userVoiceTime);
        tbub.setSendState(sendState);
        tbub.setImageLocal(imageLocal);
        mChatDbManager.insert(tbub);
        return tbub;
    }

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }
}
