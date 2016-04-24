package com.jihe.dialog.anim.FlipEnter;

import android.view.View;

import com.jihe.dialog.anim.BaseAnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


public class FlipVerticalEnter extends BaseAnimatorSet {
	@Override
	public void setAnimation(View view) {
		animatorSet.playTogether(//
				// ObjectAnimator.ofFloat(view, "rotationX", -90, 0));
				ObjectAnimator.ofFloat(view, "rotationX", 90, 0));
	}
}
