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

package org.simlar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public final class RingingActivity extends Activity
{
	private static final String LOGTAG = RingingActivity.class.getSimpleName();

	private final SimlarServiceCommunicator mCommunicator = new SimlarServiceCommunicatorRinging();

	private final class SimlarServiceCommunicatorRinging extends SimlarServiceCommunicator
	{
		public SimlarServiceCommunicatorRinging()
		{
			super(LOGTAG);
		}

		@Override
		void onBoundToSimlarService()
		{
			RingingActivity.this.onSimlarCallStateChanged();
		}

		@Override
		void onSimlarCallStateChanged()
		{
			RingingActivity.this.onSimlarCallStateChanged();
		}

		@Override
		void onServiceFinishes()
		{
			RingingActivity.this.finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Lg.i(LOGTAG, "onCreate");
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ringing);

		// make sure this activity is shown even if the phone is locked
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
				WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES);

		final AnimationDrawable animation = new AnimationDrawable();
		animation.addFrame(getResources().getDrawable(R.drawable.ringing_b), 250);
		animation.addFrame(getResources().getDrawable(R.drawable.ringing_c), 250);
		animation.addFrame(getResources().getDrawable(R.drawable.ringing_d), 250);
		animation.setOneShot(false);

		Util.setBackgroundCompatible((ImageView) findViewById(R.id.RingingAnimate), animation);

		animation.start();
	}

	@Override
	protected void onResume()
	{
		Lg.i(LOGTAG, "onResume");
		super.onResume();
		if (!mCommunicator.register(this, RingingActivity.class)) {
			Lg.w(LOGTAG, "SimlarService is not running, starting MainActivity");
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	@Override
	protected void onPause()
	{
		Lg.i(LOGTAG, "onPause");
		mCommunicator.unregister();
		super.onPause();
	}

	@Override
	public void onStop()
	{
		Lg.i(LOGTAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		Lg.i(LOGTAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		return true;
	}

	void onSimlarCallStateChanged()
	{
		if (mCommunicator.getService() == null) {
			Lg.e(LOGTAG, "ERROR: onSimlarCallStateChanged but not bound to service");
			return;
		}

		final SimlarCallState simlarCallState = mCommunicator.getService().getSimlarCallState();
		if (simlarCallState == null || simlarCallState.isEmpty()) {
			Lg.e(LOGTAG, "ERROR: onSimlarCallStateChanged simlarCallState null or empty");
			return;
		}

		Lg.i(LOGTAG, "onSimlarCallStateChanged ", simlarCallState);

		if (simlarCallState.isEndedCall()) {
			finish();
		}

		final ImageView contactImage = (ImageView) findViewById(R.id.contactImage);
		final TextView contactName = (TextView) findViewById(R.id.contactName);

		contactImage.setImageBitmap(simlarCallState.getContactPhotoBitmap(this, R.drawable.contact_picture));
		contactName.setText(simlarCallState.getContactName());
	}

	@SuppressWarnings("unused")
	public void pickUp(final View view)
	{
		mCommunicator.getService().pickUp();
		startActivity(new Intent(this, CallActivity.class));
		finish();
	}

	@SuppressWarnings("unused")
	public void terminateCall(final View view)
	{
		mCommunicator.getService().terminateCall();
		finish();
	}

	@Override
	public void onBackPressed()
	{
		// prevent switch to MainActivity
		moveTaskToBack(true);
	}
}
