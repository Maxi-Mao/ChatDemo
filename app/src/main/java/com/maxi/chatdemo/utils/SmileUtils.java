/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agrtbd to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Stb the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maxi.chatdemo.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.maxi.chatdemo.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmileUtils {
	public static final String tb_1 = "[:困惑]";
	public static final String tb_2 = "[:流泪]";
	public static final String tb_3 = "[:鬼脸]";
	public static final String tb_4 = "[:郁闷]";
	public static final String tb_5 = "[:傲慢]";
	public static final String tb_6 = "[:闭嘴]";
	public static final String tb_7 = "[:惊讶]";
	public static final String tb_8 = "[:流汗]";
	public static final String tb_9 = "[:疑问]";
	public static final String tb_10 = "[:心动]";
	public static final String tb_11 = "[:耍酷]";
	public static final String tb_12 = "[:发怒]";
	public static final String tb_13 = "[:难过]";
	public static final String tb_14 = "[:高兴]";
	public static final String tb_15 = "[:委屈]";
	public static final String tb_16 = "[:鄙视]";
	public static final String tb_17 = "[:瞌睡]";
	public static final String tb_18 = "[:偷笑]";
	public static final String tb_19 = "[:胜利]";
	public static final String tb_20 = "[:再见]";
	public static final String tb_21 = "[:好样的]";
	public static final String tb_22 = "[:开心]";
	public static final String tb_23 = "[:鼓掌]";
	public static final String tb_24 = "[:我晕]";
	public static final String tb_25 = "[:握手]";
	public static final String tb_26 = "[:找打]";
	public static final String tb_27 = "[:吃饭]";
	public static final String tb_28 = "[:得意]";
	public static final String tb_29 = "[:ok]";
	public static final String tb_30 = "[:有礼]";
	public static final String tb_31 = "[:惊吓]";
	public static final String tb_32 = "[:发财]";
	public static final String tb_33 = "[:奋斗]";
	public static final String tb_34 = "[:蛋糕]";
	public static final String tb_35 = "[:鲜花]";
	public static final String tb_36 = "[:干杯]";
	public static final String tb_37 = "[:成交]";
	public static final String tb_38 = "[:欢迎]";
	public static final String tb_39 = "[:再会]";
	public static final String tb_40 = "[:通话中]";

	private static final Factory spannableFactory = Factory
			.getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {

		addPattern(emoticons, tb_1, R.mipmap.tb_1);
		addPattern(emoticons, tb_2, R.mipmap.tb_2);
		addPattern(emoticons, tb_3, R.mipmap.tb_3);
		addPattern(emoticons, tb_4, R.mipmap.tb_4);
		addPattern(emoticons, tb_5, R.mipmap.tb_5);
		addPattern(emoticons, tb_6, R.mipmap.tb_6);
		addPattern(emoticons, tb_7, R.mipmap.tb_7);
		addPattern(emoticons, tb_8, R.mipmap.tb_8);
		addPattern(emoticons, tb_9, R.mipmap.tb_9);
		addPattern(emoticons, tb_10, R.mipmap.tb_10);
		addPattern(emoticons, tb_11, R.mipmap.tb_11);
		addPattern(emoticons, tb_12, R.mipmap.tb_12);
		addPattern(emoticons, tb_13, R.mipmap.tb_13);
		addPattern(emoticons, tb_14, R.mipmap.tb_14);
		addPattern(emoticons, tb_15, R.mipmap.tb_15);
		addPattern(emoticons, tb_16, R.mipmap.tb_16);
		addPattern(emoticons, tb_17, R.mipmap.tb_17);
		addPattern(emoticons, tb_18, R.mipmap.tb_18);
		addPattern(emoticons, tb_19, R.mipmap.tb_19);
		addPattern(emoticons, tb_20, R.mipmap.tb_20);
		addPattern(emoticons, tb_21, R.mipmap.tb_21);
		addPattern(emoticons, tb_22, R.mipmap.tb_22);
		addPattern(emoticons, tb_23, R.mipmap.tb_23);
		addPattern(emoticons, tb_24, R.mipmap.tb_24);
		addPattern(emoticons, tb_25, R.mipmap.tb_25);
		addPattern(emoticons, tb_26, R.mipmap.tb_26);
		addPattern(emoticons, tb_27, R.mipmap.tb_27);
		addPattern(emoticons, tb_28, R.mipmap.tb_28);
		addPattern(emoticons, tb_29, R.mipmap.tb_29);
		addPattern(emoticons, tb_30, R.mipmap.tb_30);
		addPattern(emoticons, tb_31, R.mipmap.tb_31);
		addPattern(emoticons, tb_32, R.mipmap.tb_32);
		addPattern(emoticons, tb_33, R.mipmap.tb_33);
		addPattern(emoticons, tb_34, R.mipmap.tb_34);
		addPattern(emoticons, tb_35, R.mipmap.tb_35);
		addPattern(emoticons, tb_36, R.mipmap.tb_36);
		addPattern(emoticons, tb_37, R.mipmap.tb_37);
		addPattern(emoticons, tb_38, R.mipmap.tb_38);
		addPattern(emoticons, tb_39, R.mipmap.tb_39);
		addPattern(emoticons, tb_40, R.mipmap.tb_40);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
			int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * 
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
		boolean hasChanges = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(),
						matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start()
							&& spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					hasChanges = true;
					spannable.setSpan(new ImageSpan(context, entry.getValue()),
							matcher.start(), matcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
		Spannable spannable = spannableFactory.newSpannable(text);
		addSmiles(context, spannable);
		return spannable;
	}

	public static boolean containsKey(String key) {
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(key);
			if (matcher.find()) {
				b = true;
				break;
			}
		}

		return b;
	}

}
