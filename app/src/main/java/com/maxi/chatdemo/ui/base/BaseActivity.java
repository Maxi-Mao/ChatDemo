package com.maxi.chatdemo.ui.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maxi.chatdemo.R;
import com.maxi.chatdemo.adapter.DataAdapter;
import com.maxi.chatdemo.adapter.ExpressionAdapter;
import com.maxi.chatdemo.adapter.ExpressionPagerAdapter;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mao Jiqing on 2016/10/20.
 */
public abstract class BaseActivity extends Activity {
    public PullToRefreshLayout pullList;
    public boolean isDown = false;
    private boolean CAN_WRITE_EXTERNAL_STORAGE = true;
    private boolean CAN_RECORD_AUDIO = true;
    public int position; //加载滚动刷新位置
    public int bottomStatusHeight = 0;
    public int listSlideHeight = 0;//滑动距离
    public TextView send_emoji_icon;
    public ImageView emoji;
    public ImageView mess_iv;
    public ImageView voiceIv;
    public ListView mess_lv;
    public ChatBottomView tbbv;
    private DataAdapter adapter;
    public AudioRecordButton voiceBtn;
    public EditText mEditTextContent;
    public ViewPager expressionViewpager;
    public LinearLayout emoji_group;
    private File mCurrentPhotoFile;
    public View activityRootView;
    private Toast mToast;
    public String userName = "test";//聊天对象昵称
    private String permissionInfo;
    private String camPicPath;
    public String item[] = {"你好!", "我正忙着呢,等等", "有啥事吗？", "有时间聊聊吗", "再见！"};
    public List<ChatMessageBean> tblist = new ArrayList<ChatMessageBean>();
    private List<String> reslist;
    public ChatDbManager mChatDbManager;
    public int page = 0;
    public int number = 10;
    public List<ChatMessageBean> pagelist = new ArrayList<ChatMessageBean>();
    public ArrayList<String> imageList = new ArrayList<String>();//adapter图片数据
    public HashMap<Integer, Integer> imagePosition = new HashMap<Integer, Integer>();//图片下标位置
    private static final int SDK_PERMISSION_REQUEST = 127;
    private static final int IMAGE_SIZE = 100 * 1024;// 300kb
    public static final int SEND_OK = 0x1110;
    public static final int REFRESH = 0x0011;
    public static final int RECERIVE_OK = 0x1111;
    public static final int PULL_TO_REFRESH_DOWN = 0x0111;

    /**
     * 发送文本消息
     */
    protected abstract void sendMessage();

    /**
     * 发送图片文件
     *
     * @param filePath
     */
    protected abstract void sendImage(String filePath);

    /**
     * 发送语音消息
     *
     * @param seconds
     * @param filePath
     */
    protected abstract void sendVoice(float seconds, String filePath);

    protected abstract void loadRecords();

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

    @Override
    protected void onDestroy() {
        MediaManager.pause();
        MediaManager.release();
        cancelToast();
        super.onDestroy();
    }

    protected void findView() {
        pullList = (PullToRefreshLayout) findViewById(R.id.content_lv);
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
        initActionBar();
    }

    protected void init() {
        mEditTextContent.setOnKeyListener(onKeyListener);
        mChatDbManager = new ChatDbManager();
        PullToRefreshLayout.pulltorefreshNotifier pullNotifier = new PullToRefreshLayout.pulltorefreshNotifier() {
            @Override
            public void onPull() {
                // TODO Auto-generated method stub
                downLoad();
            }
        };
        pullList.setpulltorefreshNotifier(pullNotifier);
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
                    KeyBoardUtils.hideKeyBoard(BaseActivity.this,
                            mEditTextContent);
                    voiceIv.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                } else {
                    mEditTextContent.setVisibility(View.VISIBLE);
                    voiceBtn.setVisibility(View.GONE);
                    voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
                    KeyBoardUtils.showKeyBoard(BaseActivity.this, mEditTextContent);
                }
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
                    KeyBoardUtils.hideKeyBoard(BaseActivity.this,
                            mEditTextContent);
                    mess_iv.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                } else {
                    tbbv.setVisibility(View.GONE);
                    KeyBoardUtils.showKeyBoard(BaseActivity.this, mEditTextContent);
                    mess_iv.setBackgroundResource(R.mipmap.tb_more);
                    if (mess_lv.getVisibility() != View.GONE) {
                        mess_lv.setVisibility(View.GONE);
                        KeyBoardUtils.showKeyBoard(BaseActivity.this, mEditTextContent);
                        mess_iv.setBackgroundResource(R.mipmap.tb_more);
                    }
                }
            }
        });
        send_emoji_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                sendMessage();
            }

        });
        tbbv.setOnHeadIconClickListener(new HeadIconSelectorView.OnHeadIconClickListener() {

            @SuppressLint("InlinedApi")
            @Override
            public void onClick(int from) {
                switch (from) {
                    case ChatBottomView.FROM_CAMERA:
                        if (!CAN_WRITE_EXTERNAL_STORAGE) {
                            Toast.makeText(BaseActivity.this, "权限未开通\n请到设置中开通相册权限", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(BaseActivity.this, "权限未开通\n请到设置中开通相册权限", Toast.LENGTH_SHORT).show();
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
                            KeyBoardUtils.hideKeyBoard(BaseActivity.this,
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
                    KeyBoardUtils.hideKeyBoard(BaseActivity.this,
                            mEditTextContent);
                } else {
                    emoji_group.setVisibility(View.GONE);
                    emoji.setBackgroundResource(R.mipmap.emoji);
                    KeyBoardUtils.showKeyBoard(BaseActivity.this, mEditTextContent);
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

        mess_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                mEditTextContent.setText(item[arg2]);
                sendMessage();
            }

        });
//        controlKeyboardLayout(activityRootView, pullList);
        bottomStatusHeight = ScreenUtil.getNavigationBarHeight(this);
        //加载本地聊天记录
        page = (int) mChatDbManager.getPages(number);
        loadRecords();
    }

    private void initActionBar() {
        if (getActionBar() == null) {
            return;
        }
//        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg));//ActionBar的背景图片
        getActionBar().setCustomView(R.layout.layout_action_bar);//ActionBar的自定义布局文件
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ivLeft:
                        doLeft();
                        break;
                    case R.id.ivRight:
                        doRight();
                        break;
                    case R.id.llRight:
                        doRight();
                        break;
                }
            }
        };
        getActionBar().getCustomView().findViewById(R.id.ivLeft).setOnClickListener(listener);
        getActionBar().getCustomView().findViewById(R.id.ivRight).setOnClickListener(listener);
        getActionBar().getCustomView().findViewById(R.id.llRight).setOnClickListener(listener);
        ((TextView) getActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(getTitle().toString());
    }

    @TargetApi(23)
    protected void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 麦克风权限
            if (addPermission(permissions, Manifest.permission.RECORD_AUDIO)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
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
                    Toast.makeText(this, "禁用图片权限将导致发送图片功能无法使用！", Toast.LENGTH_SHORT)
                            .show();
                }
                if (perms.get(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    CAN_RECORD_AUDIO = false;
                    Toast.makeText(this, "禁用录制音频权限将导致语音功能无法使用！", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setTitle(CharSequence title) {
        ((TextView) getActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(title);
    }

    protected void showLeft(boolean flag) {
        if (getActionBar() == null) {
            return;
        }
        getActionBar().getCustomView().findViewById(R.id.ivLeft).setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    protected void showRight(boolean flag) {
        if (getActionBar() == null) {
            return;
        }
        getActionBar().getCustomView().findViewById(R.id.llRight).setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    protected void doLeft() {
        finish();
    }

    protected void doRight() {

    }

    protected void setRight(int drawRes) {
        if (getActionBar() == null) {
            return;
        }
        ((ImageView) getActionBar().getCustomView().findViewById(R.id.ivRight)).setImageResource(drawRes);
        getActionBar().getCustomView().findViewById(R.id.ivRight).setVisibility(View.VISIBLE);
    }

    protected void setRightText(String text){
        if(getActionBar()==null){
            return;
        }
        ((TextView)getActionBar().getCustomView().findViewById(R.id.tvRight)).setText(text);
        getActionBar().getCustomView().findViewById(R.id.tvRight).setVisibility(View.VISIBLE);
    }

    /**
     * 常用语列表初始化
     */
    @SuppressLint({"NewApi", "InflateParams"})
    private void initpop() {
        mess_lv = (ListView) findViewById(R.id.mess_lv);
        adapter = new DataAdapter(this, item);
        mess_lv.setAdapter(adapter);
    }

    private void downLoad() {
        if (!isDown) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    loadRecords();
                }
            }).start();
        }
    }


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
                                    .getImageSize(ImageCheckoutUtil
                                            .getLoacalBitmap(camPicPath));
                            if (size > IMAGE_SIZE) {
                                showDialog(camPicPath);
                            } else {
                                sendImage(camPicPath);
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
                        int size = ImageCheckoutUtil.getImageSize(ImageCheckoutUtil.getLoacalBitmap(path));
                        if (size > IMAGE_SIZE) {
                            showDialog(path);
                        } else {
                            sendImage(path);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 界面复位
     */
    protected void reset() {
        emoji_group.setVisibility(View.GONE);
        tbbv.setVisibility(View.GONE);
        mess_lv.setVisibility(View.GONE);
        emoji.setBackgroundResource(R.mipmap.emoji);
        mess_iv.setBackgroundResource(R.mipmap.tb_more);
        voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
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
                                BaseActivity.this,
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
                        sendImage(GalPicPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    public ChatMessageBean getTbub(String username, int type,
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

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                sendMessage();
                return true;
            }
            return false;
        }
    };

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }
}
