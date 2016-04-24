package com.jihe.dialog.anim.Attention;

import android.view.View;

import com.jihe.dialog.anim.BaseAnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


public class Swing extends BaseAnimatorSet {
	public Swing() {
		duration = 1000;
	}

	@Override
	public void setAnimation(View view) {
		animatorSet.playTogether(//
				ObjectAnimator.ofFloat(view, "alpha", 1, 1, 1, 1, 1, 1, 1, 1),//
				ObjectAnimator.ofFloat(view, "rotation", 0, 10, -10, 6, -6, 3, -3, 0));
	}
}
