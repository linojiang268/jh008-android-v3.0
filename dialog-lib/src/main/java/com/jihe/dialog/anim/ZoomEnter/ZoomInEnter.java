package com.jihe.dialog.anim.ZoomEnter;

import android.view.View;

import com.jihe.dialog.anim.BaseAnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


public class ZoomInEnter extends BaseAnimatorSet {
	@Override
	public void setAnimation(View view) {
		animatorSet.playTogether(//
				ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1),//
				ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1),//
				ObjectAnimator.ofFloat(view, "alpha", 0, 1));//
	}
}
