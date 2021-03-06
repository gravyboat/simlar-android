/**
 * Copyright (C) 2013 The Simlar Authors.
 *
 * This file is part of Simlar. (https://www.simlar.org)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.simlar.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class Util
{
	private static final int MAX_BUFFER_SIZE = 1024 * 1024;

	private Util()
	{
		throw new AssertionError("This class was not meant to be instantiated");
	}

	public static boolean isNullOrEmpty(final String string)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return string == null || string.isEmpty();
		}

		return string == null || string.length() == 0;
	}

	public static int compareString(final String lhs, final String rhs)
	{
		if (Util.isNullOrEmpty(lhs) && Util.isNullOrEmpty(rhs)) {
			return 0;
		}

		if (Util.isNullOrEmpty(lhs)) {
			return -1;
		}

		if (Util.isNullOrEmpty(rhs)) {
			return 1;
		}

		return lhs.compareToIgnoreCase(rhs);
	}

	public static boolean equalString(final String lhs, final String rhs)
	{
		return compareString(lhs, rhs) == 0;
	}

	public static boolean equals(final Object lhs, final Object rhs)
	{
		return lhs == rhs || lhs != null && lhs.equals(rhs);
	}

	public static void copyStream(final InputStream is, final OutputStream os) throws IOException
	{
		final byte[] buffer = new byte[MAX_BUFFER_SIZE];
		int length;
		while ((length = is.read(buffer)) != -1) {
			os.write(buffer, 0, length);
		}
	}

	public static void setBackgroundCompatible(final View view, final Drawable drawable)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(drawable);
		} else {
			//noinspection deprecation
			view.setBackgroundDrawable(drawable);
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static Drawable getDrawableCompatible(final Resources resources, final int id)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return resources.getDrawable(id, null);
		} else {
			//noinspection deprecation
			return resources.getDrawable(id);
		}
	}

	public static String formatMilliSeconds(final long milliSeconds)
	{
		if (milliSeconds >= 0) {
			return formatPositiveMilliSeconds(milliSeconds);
		}

		return "-" + formatPositiveMilliSeconds(-1 * milliSeconds);
	}

	private static String formatPositiveMilliSeconds(final long milliSeconds)
	{
		final SimpleDateFormat sdf = createSimpleDateFormat(milliSeconds);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		return sdf.format(new Date(milliSeconds));
	}

	private static SimpleDateFormat createSimpleDateFormat(final long milliSeconds)
	{
		if (milliSeconds >= 3600000) {
			return new SimpleDateFormat("HH:mm:ss", Locale.US);
		}

		return new SimpleDateFormat("mm:ss", Locale.US);
	}

	@SuppressWarnings("SameParameterValue")
	public static void setFinishOnTouchOutsideCompatible(final Activity activity, final boolean finish)
	{
		// versions before HONEYCOMB do not support FinishOnTouchOutsides
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			activity.setFinishOnTouchOutside(finish);
		}
	}
}
