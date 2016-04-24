package com.gather.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gather.android.R;
import com.gather.android.utils.Checker;
import com.gather.android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import me.nereo.multi_image_selector.utils.Log;

public class DatePickDialog extends Dialog {

	private View mDialogView;
	private TextView tvTips, tvCancel, tvSure;
	private DatePicker datePicker;
	private LinearLayout mLinearLayoutView;
	private int mDuration;
	// private Calendar mCalendar;
	private OnDateClickListener listener;
	private int defYear = 1990, defMonth = 1, defDay = 1;

	private Effectstype type = null;
	private int[] date;

	public DatePickDialog(Context context) {
		super(context);
		init(context);
	}

	public DatePickDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public DatePickDialog(Context context, int theme, int[] date){
		super(context, theme);
		this.date = date;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((WindowManager.LayoutParams) params);

	}

	public void setDefDate(int year, int month, int day){
		defYear = year;
		defMonth = month;
		defDay = day;
		if (datePicker != null){
			datePicker.setDefDate(defYear, defMonth, defDay);
		}
	}

	private void init(Context context) {
		// mCalendar = Calendar.getInstance();
		mDialogView = View.inflate(context, R.layout.dialog_select_date, null);
		mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
		tvTips = (TextView) mDialogView.findViewById(R.id.tvTips);
		tvCancel = (TextView) mDialogView.findViewById(R.id.tvCancel);
		tvSure = (TextView) mDialogView.findViewById(R.id.tvSure);
		datePicker = (DatePicker) mDialogView.findViewById(R.id.datePicker);

		setContentView(mDialogView);
		this.setCanceledOnTouchOutside(true);
		this.withDuration(300);
		this.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {

				mLinearLayoutView.setVisibility(View.VISIBLE);
				if (type == null) {
					type = Effectstype.Fadein;
				}
				start(type);

			}
		});
		this.tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		this.tvSure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (listener != null) {
					if (datePicker.isUperToday()){
						Toast.makeText(getContext(), R.string.pick_right_birthday, Toast.LENGTH_LONG).show();
					}
					else {
						int year = datePicker.getYear();
						int month = datePicker.getMonth() + 1;
						int day = datePicker.getDay();
						int age = getAgeByBirthday(year, month, day);
						listener.onDateListener(year, month, day, formatBirthday(year, month, day), age);
						dismiss();
					}

				}
			}
		});

		datePicker.setDefDate(defYear, defMonth, defDay);

	}

	public DatePickDialog setTips(CharSequence title) {
		tvTips.setText(title);
		return this;
	}

	public DatePickDialog setCancel(CharSequence cancel) {
		tvCancel.setText(cancel);
		return this;
	}

	public DatePickDialog setSure(CharSequence sure) {
		tvSure.setText(sure);
		return this;
	}

	public DatePickDialog setOnCancelClick(View.OnClickListener cancelClick) {
		tvSure.setOnClickListener(cancelClick);
		return this;
	}

	public DatePickDialog setOnSureClick(OnDateClickListener sureClick) {
		this.listener = sureClick;
		return this;
	}

	public DatePickDialog withDuration(int duration) {
		this.mDuration = duration;
		return this;
	}

	public DatePickDialog withEffect(Effectstype type) {
		this.type = type;
		return this;
	}

	public DatePickDialog isCancelableOnTouchOutside(boolean cancelable) {
		this.setCanceledOnTouchOutside(cancelable);
		return this;
	}

	private void start(Effectstype type) {
		BaseEffects animator = type.getAnimator();
		if (mDuration != -1) {
			animator.setDuration(Math.abs(mDuration));
		}
		animator.start(mLinearLayoutView);
	}

	public static int[] getDate(String date){
		if (!Checker.isEmpty(date) && date.matches("^\\d{4}-\\d{2}-\\d{2}$")){
			String[] dateStrs = date.split("-");
			int[] dateInt = new int[3];
			for (int i = 0; i < dateStrs.length; i++){
				dateInt[i] = Integer.parseInt(dateStrs[i]);
			}
			return dateInt;
		}
		return null;
	}

	public interface OnDateClickListener {
		public void onDateListener(int year, int month, int day, String date, int age);
	}

	public void setDateListener(OnDateClickListener listener) {
		this.listener = listener;
	}

	public static String formatBirthday(int year, int month, int day) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(year);
//		sb.append("-");
//		if (month < 10) {
//			sb.append("0");
//		}
//		sb.append(month);
//		sb.append("-");
//		if (day < 10) {
//			sb.append("0");
//		}
//		sb.append(day);
//		return sb.toString();
		return String.format("%1$d-%2$02d-%3$02d", year, month, day);
	}

	public static Date getBirthday(int year, int month, int day){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return  new Date(calendar.getTimeInMillis());
	}

	public static int getAgeByBirthday(int year, int month, int day) {
		Date date = getBirthday(year,month,day);
		return  getAgeByBirthday(date);
	}

	public static int getAgeByBirthday(Date birthday) {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthday)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthday);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				// monthNow>monthBirth
				age--;
			}
		}
		return age;
	}

}
