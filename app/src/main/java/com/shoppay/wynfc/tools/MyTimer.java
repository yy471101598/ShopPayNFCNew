package com.shoppay.wynfc.tools;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyTimer extends CountDownTimer {

	private TextView btn;
	private Activity mActivity;
	private RelativeLayout rl_click;

	public MyTimer(Activity mActivity, long millisInFuture,
			long countDownInterval, TextView btn, RelativeLayout rl_click) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
		this.btn = btn;
		this.rl_click = rl_click;
		this.mActivity = mActivity;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		rl_click.setClickable(false);// ���ò��ܵ��
		btn.setText(millisUntilFinished / 1000 + "");// ���õ���ʱʱ��

		// ���ð�ťΪ��ɫ����ʱ�ǲ��ܵ����
		Spannable span = new SpannableString(btn.getText().toString());// ��ȡ��ť������
		btn.setText(span);

	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		btn.setText("发送验证码");
		rl_click.setClickable(true);// ���»�õ��
	}

}
