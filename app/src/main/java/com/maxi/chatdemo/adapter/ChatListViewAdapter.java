package com.maxi.chatdemo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxi.chatdemo.R;
import com.maxi.chatdemo.entity.ChatBean;
import com.maxi.chatdemo.ui.ImageViewActivity;
import com.maxi.chatdemo.utils.FileSaveUtil;
import com.maxi.chatdemo.widget.BubbleImageView;
import com.maxi.chatdemo.widget.GifTextView;
import com.maxi.chatdemo.widget.MediaManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mao Jiqing on 2016/9/28.
 */
public class ChatListViewAdapter extends BaseAdapter {
    private Context context;
    private List<ChatBean> userList = new ArrayList<ChatBean>();
    public static int FROM_USER = 0;//接收消息类型
    public static int TO_USER = 1;//发送消息类型
    public static int TEXT_MSG = 0;//文字表情消息
    public static int VOICE_MSG = 1;//语音消息
    public static int IMG_MSG = 2;//图片消息
    private int mMinItemWith;// 设置对话框的最大宽度和最小宽度
    private int mMaxItemWith;
    public MyHandler handler;
    private Animation an;
    private SendErrorListener sendErrorListener;
    private VoiceIsRead voiceIsRead;
    public List<String> unReadPosition = new ArrayList<String>();
    private int voicePlayPosition = -1;
    private LayoutInflater mLayoutInflater;
    private boolean isGif = true;
    public boolean isPicRefresh = true;

    public interface SendErrorListener {
        public void onClick(int position);
    }

    public void setSendErrorListener(SendErrorListener sendErrorListener) {
        this.sendErrorListener = sendErrorListener;
    }

    public interface VoiceIsRead {
        public void voiceOnClick(int position);
    }

    public void setVoiceIsReadListener(VoiceIsRead voiceIsRead) {
        this.voiceIsRead = voiceIsRead;
    }

    public ChatListViewAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        // 获取系统宽度
        WindowManager wManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.5f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
        handler = new MyHandler(this);
    }

    public static class MyHandler extends Handler {
        private final WeakReference<ChatListViewAdapter> mTbAdapter;

        public MyHandler(ChatListViewAdapter tbAdapter) {
            mTbAdapter = new WeakReference<ChatListViewAdapter>(tbAdapter);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatListViewAdapter tbAdapter = mTbAdapter.get();

            if (tbAdapter != null) {
            }
        }
    }

    public void setIsGif(boolean isGif) {
        this.isGif = isGif;
    }

    public void setUserList(List<ChatBean> userList) {
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return userList.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        final int itemViewType = getItemViewType(i);
        if (view == null) {
            if (itemViewType == FROM_USER) {
                holder = new ViewHolder();
                view = mLayoutInflater.inflate(
                        R.layout.layout_msgfrom_list_item, null);
                holder.headicon = (ImageView) view
                        .findViewById(R.id.tb_other_user_icon);
                holder.chat_time = (TextView) view.findViewById(R.id.chat_time);
                holder.content = (GifTextView) view.findViewById(R.id.content);
                holder.voice_group = (LinearLayout) view
                        .findViewById(R.id.voice_group);
                holder.voice_time = (TextView) view
                        .findViewById(R.id.voice_time);
                holder.receiver_voice_unread = (View) view
                        .findViewById(R.id.receiver_voice_unread);
                holder.voice_image = (FrameLayout) view
                        .findViewById(R.id.voice_receiver_image);
                holder.voice_anim = (View) view
                        .findViewById(R.id.id_receiver_recorder_anim);
                holder.image_group = (LinearLayout) view
                        .findViewById(R.id.image_group);
                holder.image_Msg = (BubbleImageView) view
                        .findViewById(R.id.image_message);
            } else {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(
                        R.layout.layout_msgto_list_item, null);
                holder.headicon = (ImageView) view
                        .findViewById(R.id.tb_my_user_icon);
                holder.chat_time = (TextView) view
                        .findViewById(R.id.mychat_time);
                holder.content = (GifTextView) view
                        .findViewById(R.id.mycontent);
                holder.voice_group = (LinearLayout) view
                        .findViewById(R.id.voice_group);
                holder.voice_time = (TextView) view
                        .findViewById(R.id.voice_time);
                holder.voice_image = (FrameLayout) view
                        .findViewById(R.id.voice_image);
                holder.voice_anim = (View) view
                        .findViewById(R.id.id_recorder_anim);
                holder.sendFailImg = (ImageView) view
                        .findViewById(R.id.mysend_fail_img);
                holder.image_group = (LinearLayout) view
                        .findViewById(R.id.image_group);
                holder.image_Msg = (BubbleImageView) view
                        .findViewById(R.id.image_message);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final ChatBean tbub = userList.get(i);
        /* headimage */
        if (itemViewType == FROM_USER) {
            holder.headicon.setBackgroundResource(R.mipmap.tongbao_hiv);
        } else {
            holder.headicon.setBackgroundResource(R.mipmap.grzx_tx_s);
            switch (tbub.getSendState()) {
                case SENDING:
                    an = AnimationUtils.loadAnimation(context,
                            R.anim.update_loading_progressbar_anim);
                    LinearInterpolator lin = new LinearInterpolator();
                    an.setInterpolator(lin);
                    an.setRepeatCount(-1);
                    holder.sendFailImg
                            .setBackgroundResource(R.mipmap.xsearch_loading);
                    holder.sendFailImg.startAnimation(an);
                    an.startNow();
                    holder.sendFailImg.setVisibility(View.VISIBLE);
                    break;

                case COMPLETED:
                    holder.sendFailImg.clearAnimation();
                    holder.sendFailImg.setVisibility(View.GONE);
                    break;

                case SENDERROR:
                    holder.sendFailImg.clearAnimation();
                    holder.sendFailImg
                            .setBackgroundResource(R.mipmap.msg_state_fail_resend_pressed);
                    holder.sendFailImg.setVisibility(View.VISIBLE);
                    holder.sendFailImg.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Auto-generated method stub
                            if (sendErrorListener != null) {
                                sendErrorListener.onClick(i);
                            }
                        }

                    });
                    break;
                default:
                    break;
            }

            holder.headicon.setImageDrawable(context.getResources()
                    .getDrawable(R.mipmap.grzx_tx_s));
        }
        /* time */
        if (i != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(i - 1)
                    .getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }
		/* message */
        if (tbub.getMessagetype() == TEXT_MSG) {
            holder.content.setVisibility(View.VISIBLE);
            holder.voice_group.setVisibility(View.GONE);
            holder.image_group.setVisibility(View.GONE);
            holder.content.setSpanText(handler, tbub.getUserContent(), isGif);
        } else if (tbub.getMessagetype() == VOICE_MSG) {
            holder.content.setVisibility(View.GONE);
            holder.voice_group.setVisibility(View.VISIBLE);
            holder.image_group.setVisibility(View.GONE);
            if (holder.receiver_voice_unread != null)
                holder.receiver_voice_unread.setVisibility(View.GONE);
            if (holder.receiver_voice_unread != null && unReadPosition != null) {
                for (String unRead : unReadPosition) {
                    if (unRead.equals(i + "")) {
                        holder.receiver_voice_unread
                                .setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
            AnimationDrawable drawable;
            holder.voice_anim.setId(i);
            if (i == voicePlayPosition) {
                if (itemViewType == FROM_USER) {
                    holder.voice_anim
                            .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
                } else {
                    holder.voice_anim.setBackgroundResource(R.mipmap.adj);
                }
                if (itemViewType == FROM_USER) {
                    holder.voice_anim
                            .setBackgroundResource(R.drawable.voice_play_receiver);
                    drawable = (AnimationDrawable) holder.voice_anim
                            .getBackground();
                    drawable.start();
                } else {
                    holder.voice_anim
                            .setBackgroundResource(R.drawable.voice_play_send);
                    drawable = (AnimationDrawable) holder.voice_anim
                            .getBackground();
                    drawable.start();
                }
            } else {
                if (itemViewType == FROM_USER) {
                    holder.voice_anim
                            .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
                } else {
                    holder.voice_anim.setBackgroundResource(R.mipmap.adj);
                }
            }
            holder.voice_group.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (holder.receiver_voice_unread != null)
                        holder.receiver_voice_unread.setVisibility(View.GONE);
                    if (itemViewType == FROM_USER) {
                        holder.voice_anim
                                .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
                    } else {
                        holder.voice_anim.setBackgroundResource(R.mipmap.adj);
                    }
                    stopPlayVoice();
                    voicePlayPosition = holder.voice_anim.getId();
                    AnimationDrawable drawable;
                    if (itemViewType == FROM_USER) {
                        holder.voice_anim
                                .setBackgroundResource(R.drawable.voice_play_receiver);
                        drawable = (AnimationDrawable) holder.voice_anim
                                .getBackground();
                        drawable.start();
                    } else {
                        holder.voice_anim
                                .setBackgroundResource(R.drawable.voice_play_send);
                        drawable = (AnimationDrawable) holder.voice_anim
                                .getBackground();
                        drawable.start();
                    }
                    String voicePath = tbub.getUserVoiceUrl() == null ? ""
                            : tbub.getUserVoiceUrl();
                    if (itemViewType != FROM_USER) {
                        voicePath = tbub.getUserVoicePath() == null ? "" : tbub
                                .getUserVoicePath();
                        File file = new File(voicePath);
                        if (!(!voicePath.equals("") && FileSaveUtil
                                .isFileExists(file))) {
                            voicePath = tbub.getUserVoiceUrl() == null ? ""
                                    : tbub.getUserVoiceUrl();
                        }
                    }
                    if (voiceIsRead != null) {
                        voiceIsRead.voiceOnClick(i);
                    }
                    MediaManager.playSound(voicePath,
                            new MediaPlayer.OnCompletionListener() {

                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    voicePlayPosition = -1;
                                    if (itemViewType == FROM_USER) {
                                        holder.voice_anim
                                                .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
                                    } else {
                                        holder.voice_anim
                                                .setBackgroundResource(R.mipmap.adj);
                                    }
                                }
                            });
                }

            });
            float voiceTime = tbub.getUserVoiceTime();
            BigDecimal b = new BigDecimal(voiceTime);
            float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            holder.voice_time.setText(f1 + "\"");
            ViewGroup.LayoutParams lParams = holder.voice_image
                    .getLayoutParams();
            lParams.width = (int) (mMinItemWith + mMaxItemWith / 60f
                    * tbub.getUserVoiceTime());
            holder.voice_image.setLayoutParams(lParams);
        } else if (tbub.getMessagetype() == IMG_MSG && isPicRefresh) {
            holder.image_Msg.setImageBitmap(null);
            holder.content.setVisibility(View.GONE);
            holder.voice_group.setVisibility(View.GONE);
            holder.image_group.setVisibility(View.VISIBLE);
            final String imageSrc = tbub.getImageLocal() == null ? "" : tbub
                    .getImageLocal();
            final String imageUrlSrc = tbub.getImageUrl() == null ? "" : tbub
                    .getImageUrl();
            final String imageIconUrl = tbub.getImageIconUrl() == null ? ""
                    : tbub.getImageIconUrl();
            File file = new File(imageSrc);
            final boolean hasLocal = !imageSrc.equals("")
                    && FileSaveUtil.isFileExists(file);
            int res;
            if (itemViewType == FROM_USER) {
                res = R.drawable.chatfrom_bg_focused;
            } else {
                res = R.drawable.chatto_bg_focused;
            }
            if (hasLocal) {
                holder.image_Msg.setLocalImageBitmap(getLoacalBitmap(imageSrc),
                        res);
            } else {
                holder.image_Msg.load(imageIconUrl, res, R.mipmap.cygs_cs);
            }
            holder.image_Msg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
                    stopPlayVoice();
                    ArrayList<String> mDatas = new ArrayList<String>();
                    if (hasLocal) {
                        mDatas.add(imageSrc);
                    } else {
                        mDatas.add(imageUrlSrc);
                    }
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putStringArrayListExtra("images", mDatas);
                    intent.putExtra("clickedIndex", 0);
                    context.startActivity(intent);
                }

            });
        }
        return view;
    }

    public class ViewHolder {
        public ImageView headicon;
        public TextView chat_time;
        public GifTextView content;
        public LinearLayout voice_group;
        public TextView voice_time;
        public FrameLayout voice_image;
        public View voice_anim;
        public View receiver_voice_unread;
        public ImageView sendFailImg;
        public LinearLayout image_group;
        public BubbleImageView image_Msg;
    }

    @SuppressLint("SimpleDateFormat")
    public String getTime(String time, String before) {
        String show_time = null;
        if (before != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date now = df.parse(time);
                java.util.Date date = df.parse(before);
                long l = now.getTime() - date.getTime();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                if (min >= 1) {
                    show_time = time.substring(11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            show_time = time.substring(11);
        }
        String getDay = getDay(time);
        if (show_time != null && getDay != null)
            show_time = getDay + " " + show_time;
        return show_time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public String getDay(String time) {
        String showDay = null;
        String nowTime = returnTime();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date now = df.parse(nowTime);
            java.util.Date date = df.parse(time);
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            if (day >= 365) {
                showDay = time.substring(0, 10);
            } else if (day >= 1 && day < 365) {
                showDay = time.substring(5, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showDay;
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            ByteArrayOutputStream out;
            FileInputStream fis = new FileInputStream(url);
            BufferedInputStream bis = new BufferedInputStream(fis);
            out = new ByteArrayOutputStream();
            @SuppressWarnings("unused")
            int hasRead = 0;
            byte[] buffer = new byte[1024 * 2];
            while ((hasRead = bis.read(buffer)) > 0) {
                // 读出多少数据，向输出流中写入多少
                out.write(buffer);
                out.flush();
            }
            out.close();
            fis.close();
            bis.close();
            byte[] data = out.toByteArray();
            // 长宽减半
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 3;
            return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public void stopPlayVoice() {
        if (voicePlayPosition != -1) {
            View voicePlay = (View) ((Activity) context)
                    .findViewById(voicePlayPosition);
            if (voicePlay != null) {
                if (getItemViewType(voicePlayPosition) == FROM_USER) {
                    voicePlay
                            .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
                } else {
                    voicePlay.setBackgroundResource(R.mipmap.adj);
                }
            }
            MediaManager.pause();
            voicePlayPosition = -1;
        }
    }
}
