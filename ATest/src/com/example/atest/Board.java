package com.example.atest;

import java.lang.Thread.State;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Board extends SurfaceView implements Runnable,
		SurfaceHolder.Callback {
	private static final int THREAD_DRAW = 1;
	private static final int THREAD_STOP = 2;

	private Thread thread;
	private Timer timer;
	private BlockingQueue<Integer> queue = null;
	private Star[] stars;
	private Vector offset = new Vector(0, 0);
	private Paint white = new Paint();

	public Board(Context context) {
		super(context);
		getHolder().addCallback(this);
		requestFocus();
		setFocusableInTouchMode(true);
		queue = new LinkedBlockingQueue<Integer>(1);
		stars = new Star[100];
		white.setColor(Color.WHITE);
		 stars[0] = new Star(250, 400, 2000, new Vector(0, 0));
		/*
		 * stars[1] = new Star(500, 50, 800, new Vector(4.2f,1)); stars[2] = new
		 * Star(605, 398, 12, new Vector(-0.6f,0.9f)); stars[3] = new Star(450,
		 * 139, 19, new Vector(0.48f,0.37f)); stars[4] = new Star(30, 139, 19,
		 * new Vector(0.78f,0f)); stars[5] = new Star(500, 20, 10, new
		 * Vector(8.9f, 0.05f));
		 */

		for (int i = 1; i < stars.length; i++) {
			stars[i] = new Star(Math.random() * 500, Math.random() * 800,
					Math.pow(Math.random() * 5, 3), new Vector(
							Math.random() * 2 - 1, Math.random() * 2 - 1));
		}
		
		setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN) {
					switch(keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						offset.x += 50;
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						offset.x -= 50;
						break;
					case KeyEvent.KEYCODE_DPAD_UP:
						offset.y += 50;
						break;
					case KeyEvent.KEYCODE_DPAD_DOWN:
						offset.y -= 50;
						break;
					}
				}
				return false;
			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (thread.getState() == State.NEW)
			thread.start();

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				redraw();
			}
		}, 10, 10);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new Thread(this);
		queue.offer(THREAD_DRAW);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		while (true) {
			try {
				queue.put(THREAD_STOP);
				thread.join();
				timer.cancel();
				return;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (queue.take() == THREAD_STOP)
					break;

				synchronized (this) {
					SurfaceHolder holder = getHolder();
					Canvas canvas = holder.lockCanvas();
					draw(canvas);
					holder.unlockCanvasAndPost(canvas);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		for (int i = 0; i < stars.length; i++) {
			stars[i].draw(canvas, offset);
			for (int j = 0; j < stars.length; j++)
				if (i != j)
					stars[i].updateSpeedVector(stars[j]);
		}

		for (int i = 0; i < stars.length; i++)
			stars[i].updatePosition();
		
		int c = 0;
		for (int i = 0; i < stars.length; i++)
			if (stars[i].active)
				c++;
		
		canvas.drawText("" + c, 10, 10, white);
	}

	public void redraw() {
		queue.offer(THREAD_DRAW); // insert if queue is free
	}
}
