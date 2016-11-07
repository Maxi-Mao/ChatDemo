package com.maxi.chatdemo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.maxi.chatdemo.common.ChatConst;
import com.maxi.chatdemo.db.ChatMessageBean;
import com.maxi.chatdemo.ui.ImageViewActivity;
import com.maxi.chatdemo.utils.FileSaveUtil;
import com.maxi.chatdemo.utils.ImageCheckoutUtil;
import com.maxi.chatdemo.widget.BubbleImageView;
import com.maxi.chatdemo.widget.GifTextView;
import com.maxi.chatdemo.widget.MediaManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mao Jiqing on 2016/9/28.
 */
public class ChatListViewAdapter extends BaseAdapter {
    private Context context;
    private List<ChatMessageBean> userList = new ArrayList<ChatMessageBean>();
    private ArrayList<String> imageList = new ArrayList<String>();
    private HashMap<Integer,Integer> imagePosition = new HashMap<Integer,Integer>();
    public static final int FROM_USER_MSG = 0;//接收消息类型
    public static final int TO_USER_MSG = 1;//发送消息类型
    public static final int FROM_USER_IMG = 2;//接收消息类型
    public static final int TO_USER_IMG = 3;//发送消息类型
    public static final int FROM_USER_VOICE = 4;//接收消息类型
    public static final int TO_USER_VOICE = 5;//发送消息类型
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

    public void setUserList(List<ChatMessageBean> userList) {
        this.userList = userList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }
    public void setImagePosition(HashMap<Integer,Integer> imagePosition) {
        this.imagePosition = imagePosition;
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
        return 6;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChatMessageBean tbub = userList.get(i);
        switch (getItemViewType(i)) {
            case FROM_USER_MSG:
                FromUserMsgViewHolder holder;
                if (view == null) {
                    holder = new FromUserMsgViewHolder();
                    view = mLayoutInflater.inflate(R.layout.layout_msgfrom_list_item, null);
                    holder.headicon = (ImageView) view
                            .findViewById(R.id.tb_other_user_icon);
                    holder.chat_time = (TextView) view.findViewById(R.id.chat_time);
                    holder.content = (GifTextView) view.findViewById(R.id.content);
                    view.setTag(holder);
                } else {
                    holder = (FromUserMsgViewHolder) view.getTag();
                }
                fromMsgUserLayout((FromUserMsgViewHolder) holder, tbub, i);
                break;
            case FROM_USER_IMG:
                FromUserImageViewHolder holder1;
                if (view == null) {
                    holder1 = new FromUserImageViewHolder();
                    view = mLayoutInflater.inflate(R.layout.layout_imagefrom_list_item, null);
                    holder1.headicon = (ImageView) view
                            .findViewById(R.id.tb_other_user_icon);
                    holder1.chat_time = (TextView) view.findViewById(R.id.chat_time);
                    holder1.image_Msg = (BubbleImageView) view
                            .findViewById(R.id.image_message);
                    view.setTag(holder1);
                } else {
                    holder1 = (FromUserImageViewHolder) view.getTag();
                }
                fromImgUserLayout((FromUserImageViewHolder) holder1, tbub, i);
                break;
            case FROM_USER_VOICE:
                FromUserVoiceViewHolder holder2;
                if (view == null) {
                    holder2 = new FromUserVoiceViewHolder();
                    view = mLayoutInflater.inflate(R.layout.layout_voicefrom_list_item, null);
                    holder2.headicon = (ImageView) view
                            .findViewById(R.id.tb_other_user_icon);
                    holder2.chat_time = (TextView) view.findViewById(R.id.chat_time);
                    holder2.voice_group = (LinearLayout) view
                            .findViewById(R.id.voice_group);
                    holder2.voice_time = (TextView) view
                            .findViewById(R.id.voice_time);
                    holder2.receiver_voice_unread = (View) view
                            .findViewById(R.id.receiver_voice_unread);
                    holder2.voice_image = (FrameLayout) view
                            .findViewById(R.id.voice_receiver_image);
                    holder2.voice_anim = (View) view
                            .findViewById(R.id.id_receiver_recorder_anim);
                    view.setTag(holder2);
                } else {
                    holder2 = (FromUserVoiceViewHolder) view.getTag();
                }
                fromVoiceUserLayout((FromUserVoiceViewHolder) holder2, tbub, i);
                break;
            case TO_USER_MSG:
                ToUserMsgViewHolder holder3;
                if (view == null) {
                    holder3 = new ToUserMsgViewHolder();
                    view = mLayoutInflater.inflate(R.layout.layout_msgto_list_item, null);
                    holder3.headicon = (ImageView) view
                            .findViewById(R.id.tb_my_user_icon);
                    holder3.chat_time = (TextView) view
                            .findViewById(R.id.mychat_time);
                    holder3.content = (GifTextView) view
                            .findViewById(R.id.mycontent);
                    holder3.sendFailImg = (ImageView) view
                            .findViewById(R.id.mysend_fail_img);
                    view.setTag(holder3);
                } else {
                    holder3 = (ToUserMsgViewHolder) view.getTag();
                }
                toMsgUserLayout((ToUserMsgViewHolder) holder3, tbub, i);
                break;
            case TO_USER_IMG:
                ToUserImgViewHolder holder4;
                if (view == null) {
                    holder4 = new ToUserImgViewHolder();
                    view = mLayoutInflater.inflate(R.layout.layout_imageto_list_item, null);
                    holder4.headicon = (ImageView) view
                            .findViewById(R.id.tb_my_user_icon);
                    holder4.chat_time = (TextView) view
                            .findViewById(R.id.mychat_time);
                    holder4.sendFailImg = (ImageView) view
                            .findViewById(R.id.mysend_fail_img);
                    holder4.image_group = (LinearLayout) view
                            .findViewById(R.id.image_group);
                    holder4.image_Msg = (BubbleImageView) view
                            .findViewById(R.id.image_message);
                    view.setTag(holder4);
                } else {
                    holder4 = (ToUserImgViewHolder) view.getTag();
                }
                toImgUserLayout((ToUserImgViewHolder) holder4, tbub, i);
                break;
            case TO_USER_VOICE:
                ToUserVoiceViewHolder holder5;
                if (view == null) {
                    holder5 = new ToUserVoiceViewHolder();
                    view = mLayoutInflater.inflate(R.layout.layout_voiceto_list_item, null);
                    holder5.headicon = (ImageView) view
                            .findViewById(R.id.tb_my_user_icon);
                    holder5.chat_time = (TextView) view
                            .findViewById(R.id.mychat_time);
                    holder5.voice_group = (LinearLayout) view
                            .findViewById(R.id.voice_group);
                    holder5.voice_time = (TextView) view
                            .findViewById(R.id.voice_time);
                    holder5.voice_image = (FrameLayout) view
                            .findViewById(R.id.voice_image);
                    holder5.voice_anim = (View) view
                            .findViewById(R.id.id_recorder_anim);
                    holder5.sendFailImg = (ImageView) view
                            .findViewById(R.id.mysend_fail_img);
                    view.setTag(holder5);
                } else {
                    holder5 = (ToUserVoiceViewHolder) view.getTag();
                }
                toVoiceUserLayout((ToUserVoiceViewHolder) holder5, tbub, i);
                break;
        }

        return view;
    }

    public class FromUserMsgViewHolder {
        public ImageView headicon;
        public TextView chat_time;
        public GifTextView content;
    }

    public class FromUserImageViewHolder {
        public ImageView headicon;
        public TextView chat_time;
        public BubbleImageView image_Msg;
    }

    public class FromUserVoiceViewHolder {
        public ImageView headicon;
        public TextView chat_time;
        public LinearLayout voice_group;
        public TextView voice_time;
        public FrameLayout voice_image;
        public View receiver_voice_unread;
        public View voice_anim;
    }

    public class ToUserMsgViewHolder {
        public ImageView headicon;
        public TextView chat_time;
        public GifTextView content;
        public ImageView sendFailImg;
    }

    public class ToUserImgViewHolder {
        public ImageView headicon;
        public TextView chat_time;
        public LinearLayout image_group;
        public BubbleImageView image_Msg;
        public ImageView sendFailImg;
    }

    public class ToUserVoiceViewHolder {
        public ImageView headicon;
        public TextView chat_time;
        public LinearLayout voice_group;
        public TextView voice_time;
        public FrameLayout voice_image;
        public View receiver_voice_unread;
        public View voice_anim;
        public ImageView sendFailImg;
    }

    private void fromMsgUserLayout(final FromUserMsgViewHolder holder, final ChatMessageBean tbub, final int position) {
        holder.headicon.setBackgroundResource(R.mipmap.tongbao_hiv);
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
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
        holder.content.setVisibility(View.VISIBLE);
        holder.content.setSpanText(handler, tbub.getUserContent(), isGif);
    }

    private void fromImgUserLayout(final FromUserImageViewHolder holder, final ChatMessageBean tbub, final int position) {
        holder.headicon.setBackgroundResource(R.mipmap.tongbao_hiv);
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
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
        if (isPicRefresh) {
//            holder.image_Msg.setImageBitmap(null);
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
            res = R.drawable.chatfrom_bg_focused;
            if (hasLocal) {
                holder.image_Msg.setLocalImageBitmap(ImageCheckoutUtil.getLoacalBitmap(imageSrc),
                        res);
            } else {
                holder.image_Msg.load(imageIconUrl, res, R.mipmap.cygs_cs);
            }
            holder.image_Msg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
                    stopPlayVoice();
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putStringArrayListExtra("images", imageList);
                    intent.putExtra("clickedIndex", imagePosition.get(position));
                    context.startActivity(intent);
                }

            });
        }

    }

    private void fromVoiceUserLayout(final FromUserVoiceViewHolder holder, final ChatMessageBean tbub, final int position) {
        holder.headicon.setBackgroundResource(R.mipmap.tongbao_hiv);
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
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

        holder.voice_group.setVisibility(View.VISIBLE);
        if (holder.receiver_voice_unread != null)
            holder.receiver_voice_unread.setVisibility(View.GONE);
        if (holder.receiver_voice_unread != null && unReadPosition != null) {
            for (String unRead : unReadPosition) {
                if (unRead.equals(position + "")) {
                    holder.receiver_voice_unread
                            .setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
        AnimationDrawable drawable;
        holder.voice_anim.setId(position);
        if (position == voicePlayPosition) {
            holder.voice_anim
                    .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
            holder.voice_anim
                    .setBackgroundResource(R.drawable.voice_play_receiver);
            drawable = (AnimationDrawable) holder.voice_anim
                    .getBackground();
            drawable.start();
        } else {
            holder.voice_anim
                    .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
        }
        holder.voice_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (holder.receiver_voice_unread != null)
                    holder.receiver_voice_unread.setVisibility(View.GONE);
                holder.voice_anim
                        .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
                stopPlayVoice();
                voicePlayPosition = holder.voice_anim.getId();
                AnimationDrawable drawable;
                holder.voice_anim
                        .setBackgroundResource(R.drawable.voice_play_receiver);
                drawable = (AnimationDrawable) holder.voice_anim
                        .getBackground();
                drawable.start();
                String voicePath = tbub.getUserVoicePath() == null ? "" : tbub
                        .getUserVoicePath();
                File file = new File(voicePath);
                if (!(!voicePath.equals("") && FileSaveUtil
                        .isFileExists(file))) {
                    voicePath = tbub.getUserVoiceUrl() == null ? ""
                            : tbub.getUserVoiceUrl();
                }
                if (voiceIsRead != null) {
                    voiceIsRead.voiceOnClick(position);
                }
                MediaManager.playSound(voicePath,
                        new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                voicePlayPosition = -1;
                                holder.voice_anim
                                        .setBackgroundResource(R.mipmap.receiver_voice_node_playing003);
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
    }

    private void toMsgUserLayout(final ToUserMsgViewHolder holder, final ChatMessageBean tbub, final int position) {
        holder.headicon.setBackgroundResource(R.mipmap.grzx_tx_s);
        holder.headicon.setImageDrawable(context.getResources()
                .getDrawable(R.mipmap.grzx_tx_s));
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
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

        holder.content.setVisibility(View.VISIBLE);
        holder.content.setSpanText(handler, tbub.getUserContent(), isGif);
    }

    private void toImgUserLayout(final ToUserImgViewHolder holder, final ChatMessageBean tbub, final int position) {
        holder.headicon.setBackgroundResource(R.mipmap.grzx_tx_s);
        switch (tbub.getSendState()) {
            case ChatConst.SENDING:
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

            case ChatConst.COMPLETED:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setVisibility(View.GONE);
                break;

            case ChatConst.SENDERROR:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg
                        .setBackgroundResource(R.mipmap.msg_state_fail_resend_pressed);
                holder.sendFailImg.setVisibility(View.VISIBLE);
                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Auto-generated method stub
                        if (sendErrorListener != null) {
                            sendErrorListener.onClick(position);
                        }
                    }

                });
                break;
            default:
                break;
        }
        holder.headicon.setImageDrawable(context.getResources()
                .getDrawable(R.mipmap.grzx_tx_s));

        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
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

        if (isPicRefresh) {
//            holder.image_Msg.setImageBitmap(null);
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
            res = R.drawable.chatto_bg_focused;
            if (hasLocal) {
                holder.image_Msg.setLocalImageBitmap(ImageCheckoutUtil.getLoacalBitmap(imageSrc),
                        res);
            } else {
                holder.image_Msg.load(imageIconUrl, res, R.mipmap.cygs_cs);
            }
            holder.image_Msg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
                    stopPlayVoice();
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putStringArrayListExtra("images", imageList);
                    intent.putExtra("clickedIndex", imagePosition.get(position));
                    context.startActivity(intent);
                }

            });
        }
    }

    private void toVoiceUserLayout(final ToUserVoiceViewHolder holder, final ChatMessageBean tbub, final int position) {
        holder.headicon.setBackgroundResource(R.mipmap.grzx_tx_s);
        switch (tbub.getSendState()) {
            case ChatConst.SENDING:
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

            case ChatConst.COMPLETED:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setVisibility(View.GONE);
                break;

            case ChatConst.SENDERROR:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg
                        .setBackgroundResource(R.mipmap.msg_state_fail_resend_pressed);
                holder.sendFailImg.setVisibility(View.VISIBLE);
                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Auto-generated method stub
                        if (sendErrorListener != null) {
                            sendErrorListener.onClick(position);
                        }
                    }

                });
                break;
            default:
                break;
        }
        holder.headicon.setImageDrawable(context.getResources()
                .getDrawable(R.mipmap.grzx_tx_s));

        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
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
        holder.voice_group.setVisibility(View.VISIBLE);
        if (holder.receiver_voice_unread != null)
            holder.receiver_voice_unread.setVisibility(View.GONE);
        if (holder.receiver_voice_unread != null && unReadPosition != null) {
            for (String unRead : unReadPosition) {
                if (unRead.equals(position + "")) {
                    holder.receiver_voice_unread
                            .setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
        AnimationDrawable drawable;
        holder.voice_anim.setId(position);
        if (position == voicePlayPosition) {
            holder.voice_anim.setBackgroundResource(R.mipmap.adj);
            holder.voice_anim
                    .setBackgroundResource(R.drawable.voice_play_send);
            drawable = (AnimationDrawable) holder.voice_anim
                    .getBackground();
            drawable.start();
        } else {
            holder.voice_anim.setBackgroundResource(R.mipmap.adj);
        }
        holder.voice_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (holder.receiver_voice_unread != null)
                    holder.receiver_voice_unread.setVisibility(View.GONE);
                holder.voice_anim.setBackgroundResource(R.mipmap.adj);
                stopPlayVoice();
                voicePlayPosition = holder.voice_anim.getId();
                AnimationDrawable drawable;
                holder.voice_anim
                        .setBackgroundResource(R.drawable.voice_play_send);
                drawable = (AnimationDrawable) holder.voice_anim
                        .getBackground();
                drawable.start();
                String voicePath = tbub.getUserVoiceUrl() == null ? ""
                        : tbub.getUserVoiceUrl();
                if (voiceIsRead != null) {
                    voiceIsRead.voiceOnClick(position);
                }
                MediaManager.playSound(voicePath,
                        new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                voicePlayPosition = -1;
                                holder.voice_anim
                                        .setBackgroundResource(R.mipmap.adj);
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

    public void stopPlayVoice() {
        if (voicePlayPosition != -1) {
            View voicePlay = (View) ((Activity) context)
                    .findViewById(voicePlayPosition);
            if (voicePlay != null) {
                if (getItemViewType(voicePlayPosition) == FROM_USER_VOICE) {
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
