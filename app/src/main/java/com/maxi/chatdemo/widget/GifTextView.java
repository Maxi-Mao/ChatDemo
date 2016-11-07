package com.maxi.chatdemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.maxi.chatdemo.utils.FaceData;
import com.maxi.chatdemo.utils.GifOpenHelper;
import com.maxi.chatdemo.utils.ScreenUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GifTextView extends EditText {
    /**
     * 注：如果获取的gif帧与帧之间的时间间隔都不相同，建议调个固定的，最好的方法是将gif图的间隔设置相同
     */
    private static final int DELAYED = 300;

    /**
     * @author Dragon SpanInfo
     *         类用于存储一个要显示的图片（动态或静态）的信息，包括分解后的每一帧mapList、替代文字的起始位置、终止位置
     *         、帧的总数、当前需要显示的帧、帧与帧之间的时间间隔
     */
    private class SpanInfo {
        ArrayList<Bitmap> mapList;
        @SuppressWarnings("unused")
        int start, end, frameCount, currentFrameIndex, delay;

        public SpanInfo() {
            mapList = new ArrayList<Bitmap>();
            start = end = frameCount = currentFrameIndex = delay = 0;
        }
    }

    /**
     * spanInfoList 是一个SpanInfo的list ,用于处理一个TextView中出现多个要匹配的图片的情况
     */
    private ArrayList<SpanInfo> spanInfoList = null;
    private Handler handler; // 用于处理从子线程TextView传来的消息
    private String myText; // 存储textView应该显示的文本

    /**
     * 这三个构造方法一个也不要少，否则会产生CastException，注意在这三个构造函数中都为spanInfoList实例化，可能有些浪费
     * ，但保证不会有空指针异常
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    @SuppressLint("NewApi")
    public GifTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        // spanInfoList = new ArrayList<SpanInfo>();
        GifTextView.this.setFocusableInTouchMode(false);
    }

    @SuppressLint("NewApi")
    public GifTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        // spanInfoList = new ArrayList<SpanInfo>();
        GifTextView.this.setFocusableInTouchMode(false);
    }

    @SuppressLint("NewApi")
    public GifTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        // spanInfoList = new ArrayList<SpanInfo>();
        GifTextView.this.setFocusableInTouchMode(false);
    }

    /**
     * 对要显示在textView上的文本进行解析，看看是否文本中有需要与Gif或者静态图片匹配的文本 若有，那么调用parseGif
     * 对该文本对应的Gif图片进行解析 或者嗲用parseBmp解析静态图片
     *
     * @param inputStr
     */
    private boolean parseText(String inputStr) {
        myText = inputStr;
//		Pattern mPattern = Pattern.compile("\\[:..\\]|\\[:...\\]");
        Pattern mPattern = Pattern.compile("\\[[^\\]]+\\]");
        Matcher mMatcher = mPattern.matcher(inputStr);
        boolean hasGif = false;
        while (mMatcher.find()) {
            String faceName = mMatcher.group();
            Integer faceId = null;
            /**
             * 这里匹配时用到了图片库，即一个专门存放图片id和其匹配的名称的静态对象，这两个静态对象放在了FaceData.java
             * 中，并采用了静态块的方法进行了初始化，不会有空指针异常
             */
            if ((faceId = FaceData.gifFaceInfo.get(faceName)) != null) {
                if (isGif) {
                    parseGif(faceId, mMatcher.start(), mMatcher.end());
                } else {
                    parseBmp(faceId, mMatcher.start(), mMatcher.end());
                }
            }
            hasGif = true;
        }
        return hasGif;
    }

    /**
     * 对静态图片进行解析：
     * 创建一个SpanInfo对象，帧数设为1，按照下面的参数设置，最后不要忘记将SpanInfo对象添加进spanInfoList中， 否则不会显示
     *
     * @param resourceId
     * @param start
     * @param end
     */
    @SuppressWarnings("unused")
    private void parseBmp(int resourceId, int start, int end) {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext()
                .getResources(), resourceId);
        ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
        SpanInfo spanInfo = new SpanInfo();
        spanInfo.currentFrameIndex = 0;
        spanInfo.frameCount = 1;
        spanInfo.start = start;
        spanInfo.end = end;
        spanInfo.delay = 100;
        spanInfo.mapList.add(bitmap);
        spanInfoList.add(spanInfo);

    }

    /**
     * 解析Gif图片，与静态图片唯一的不同是这里需要调用GifOpenHelper类读取Gif返回一系一组bitmap（用for 循环把这一
     * 组的bitmap存储在SpanInfo.mapList中，此时的frameCount参数也大于1了）
     *
     * @param resourceId
     * @param start
     * @param end
     */
    private void parseGif(int resourceId, int start, int end) {

        GifOpenHelper helper = new GifOpenHelper();
        helper.read(getContext().getResources().openRawResource(resourceId));
        SpanInfo spanInfo = new SpanInfo();
        spanInfo.currentFrameIndex = 0;
        spanInfo.frameCount = helper.getFrameCount();
        spanInfo.start = start;
        spanInfo.end = end;
        spanInfo.mapList.add(helper.getImage());
        for (int i = 1; i < helper.getFrameCount(); i++) {
            spanInfo.mapList.add(helper.nextBitmap());
        }
        spanInfo.delay = helper.nextDelay(); // 获得每一帧之间的延迟
        spanInfoList.add(spanInfo);

    }

    private boolean isGif;

    /**
     * GifTextView 与外部对象的接口，以后设置文本内容时使用setSpanText() 而不再是setText();
     *
     * @param handler
     * @param text
     */
    public void setSpanText(Handler handler, final String text, boolean isGif) {
        this.handler = handler; // 获得UI的Handler
        this.isGif = isGif;
        spanInfoList = new ArrayList<SpanInfo>();
//        ThreadPoolUtils.execute(new Runnable() {
//
//            @Override
//            public void run() {
////				// TODO Auto-generated method stub
        if (parseText(text)) {// 对String对象进行解析
//                    mStartHandler.sendEmptyMessage(0);
            if (parseMessage(this)) {
                startPost();
            }
        } else {
//                    mStartHandler.sendEmptyMessage(1);
            setText(myText);
        }
//            }
//        });
    }

//    private StartHandler mStartHandler = new StartHandler(this);
//
//    public static class StartHandler extends Handler {
//        private final WeakReference<GifTextView> mGifWeakReference;
//
//        public StartHandler(GifTextView gifTextView) {
//            mGifWeakReference = new WeakReference<GifTextView>(gifTextView);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            GifTextView gifTextView = mGifWeakReference.get();
//            if (gifTextView != null) {
//                if (msg.what == 0) {
//                    gifTextView.startPost();
//                } else if (msg.what == 1) {
//                    gifTextView.setText(gifTextView.myText);
//                }
//            }
//        }
//    }

    public boolean parseMessage(GifTextView gifTextView) {
        if (gifTextView.myText != null && !gifTextView.myText.equals("")) {
            SpannableString sb = new SpannableString("" + gifTextView.myText); // 获得要显示的文本
            int gifCount = 0;
            SpanInfo info = null;
            for (int i = 0; i < gifTextView.spanInfoList.size(); i++) { // for循环，处理显示多个图片的问题
                info = gifTextView.spanInfoList.get(i);
                if (info.mapList.size() > 1) {
                            /*
                             * gifCount用来区分是Gif还是BMP，若是gif gifCount>0
							 * ,否则gifCount=0
							 */
                    gifCount++;

                }
                Bitmap bitmap = info.mapList
                        .get(info.currentFrameIndex);
                info.currentFrameIndex = (info.currentFrameIndex + 1)
                        % (info.frameCount);
                /**
                 * currentFrameIndex
                 * 用于控制当前应该显示的帧的序号，每次显示之后currentFrameIndex 应该加1
                 * ，加到frameCount后再变成0循环显示
                 */
                int size = ScreenUtil.dip2px(gifTextView.getContext(), 30);
                if (gifCount != 0) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, size,
                            size, true);

                } else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, size,
                            size, true);
                }
                ImageSpan imageSpan = new ImageSpan(gifTextView.getContext(),
                        bitmap);
                if (info.end <= sb.length()) {
                    sb.setSpan(imageSpan, info.start, info.end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    break;
                }

            }
            // 对所有的图片对应的ImageSpan完成设置后，调用TextView的setText方法设置文本
            gifTextView.setText(sb);
            if (gifCount != 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public TextRunnable rTextRunnable;

    public void startPost() {
        rTextRunnable = new TextRunnable(this); // 生成Runnable对象
        handler.post(rTextRunnable); // 利用UI线程的Handler 将r添加进消息队列中。
    }

    public static final class TextRunnable implements Runnable {
        private final WeakReference<GifTextView> mWeakReference;

        public TextRunnable(GifTextView f) {
            mWeakReference = new WeakReference<GifTextView>(f);
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            GifTextView gifTextView = mWeakReference.get();
            if (gifTextView != null) {
                /**
                 * 这一步是为了节省内存而是用，即如果文本中只有静态图片没有动态图片，那么该线程就此终止，不会重复执行
                 * 。而如果有动图，那么会一直执行
                 */
                if (gifTextView.parseMessage(gifTextView)) {
                    gifTextView.handler.postDelayed(this, DELAYED);
                }
            }
        }
    }

}

