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
	public static final String f1 = "[:f1]";
	public static final String f2 = "[:f2]";
	public static final String f3 = "[:f3]";
	public static final String f4 = "[:f4]";
	public static final String f5 = "[:f5]";
	public static final String f6 = "[:f6]";
	public static final String f7 = "[:f7]";
	public static final String f8 = "[:f8]";
	public static final String f9 = "[:f9]";
	public static final String f10 = "[:f10]";
	public static final String f11 = "[:f11]";
	public static final String f12 = "[:f12]";
	public static final String f13 = "[:f13]";
	public static final String f14 = "[:f14]";
	public static final String f15 = "[:f15]";
	public static final String f16 = "[:f16]";
	public static final String f17 = "[:f17]";
	public static final String f18 = "[:f18]";
	public static final String f19 = "[:f19]";
	public static final String f20 = "[:f20]";
	public static final String f21 = "[:f21]";
	public static final String f22 = "[:f22]";
	public static final String f23 = "[:f23]";
	public static final String f24 = "[:f24]";
	public static final String f25 = "[:f25]";
	public static final String f26 = "[:f26]";
	public static final String f27 = "[:f27]";
	public static final String f28 = "[:f28]";
	public static final String f29 = "[:f29]";
	public static final String f30 = "[:f30]";
	public static final String f31 = "[:f31]";
	public static final String f32 = "[:f32]";
	public static final String f33 = "[:f33]";
	public static final String f34 = "[:f34]";
	public static final String f35 = "[:f35]";
	public static final String f36 = "[:f36]";
	public static final String f37 = "[:f37]";
	public static final String f38 = "[:f38]";
	public static final String f39 = "[:f39]";
	public static final String f40 = "[:f40]";

	private static final Factory spannableFactory = Factory
			.getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {

		addPattern(emoticons, f1, R.mipmap.f1);
		addPattern(emoticons, f2, R.mipmap.f2);
		addPattern(emoticons, f3, R.mipmap.f3);
		addPattern(emoticons, f4, R.mipmap.f4);
		addPattern(emoticons, f5, R.mipmap.f5);
		addPattern(emoticons, f6, R.mipmap.f6);
		addPattern(emoticons, f7, R.mipmap.f7);
		addPattern(emoticons, f8, R.mipmap.f8);
		addPattern(emoticons, f9, R.mipmap.f9);
		addPattern(emoticons, f10, R.mipmap.f10);
		addPattern(emoticons, f11, R.mipmap.f11);
		addPattern(emoticons, f12, R.mipmap.f12);
		addPattern(emoticons, f13, R.mipmap.f13);
		addPattern(emoticons, f14, R.mipmap.f14);
		addPattern(emoticons, f15, R.mipmap.f15);
		addPattern(emoticons, f16, R.mipmap.f16);
		addPattern(emoticons, f17, R.mipmap.f17);
		addPattern(emoticons, f18, R.mipmap.f18);
		addPattern(emoticons, f19, R.mipmap.f19);
		addPattern(emoticons, f20, R.mipmap.f20);
		addPattern(emoticons, f21, R.mipmap.f21);
		addPattern(emoticons, f22, R.mipmap.f22);
		addPattern(emoticons, f23, R.mipmap.f23);
		addPattern(emoticons, f24, R.mipmap.f24);
		addPattern(emoticons, f25, R.mipmap.f25);
		addPattern(emoticons, f26, R.mipmap.f26);
		addPattern(emoticons, f27, R.mipmap.f27);
		addPattern(emoticons, f28, R.mipmap.f28);
		addPattern(emoticons, f29, R.mipmap.f29);
		addPattern(emoticons, f30, R.mipmap.f30);
		addPattern(emoticons, f31, R.mipmap.f31);
		addPattern(emoticons, f32, R.mipmap.f32);
		addPattern(emoticons, f33, R.mipmap.f33);
		addPattern(emoticons, f34, R.mipmap.f34);
		addPattern(emoticons, f35, R.mipmap.f35);
		addPattern(emoticons, f36, R.mipmap.f36);
		addPattern(emoticons, f37, R.mipmap.f37);
		addPattern(emoticons, f38, R.mipmap.f38);
		addPattern(emoticons, f39, R.mipmap.f39);
		addPattern(emoticons, f40, R.mipmap.f40);
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
