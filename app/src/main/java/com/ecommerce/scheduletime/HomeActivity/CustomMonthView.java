package com.ecommerce.scheduletime.HomeActivity;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.ecommerce.scheduletime.R;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarUtil;
import com.haibin.calendarview.MonthView;

/**
 * CustomMonthView with canvas
 */
public class CustomMonthView extends MonthView {

    public void onCustomise() {
        //DARK MODE
        SharedPreferences preferences_ = getContext().getSharedPreferences("dark_mode", MODE_PRIVATE);
        String state = preferences_.getString("state", "");
        if (state.equals("true")) {
            mSchemePaint.setColor(ContextCompat.getColor(getContext(), R.color.blue));
            mOtherMonthTextPaint.setColor(ContextCompat.getColor(getContext(), R.color._white));
            mSchemeTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            mSelectTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.blue));
            mCurDayTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        } else if (state.equals("false")) {
            mSchemePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            mOtherMonthTextPaint.setColor(ContextCompat.getColor(getContext(), R.color._black));
            mSchemeTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.blue));
            mSelectTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            mCurDayTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.blue));
        } else {
            mSchemePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            mOtherMonthTextPaint.setColor(ContextCompat.getColor(getContext(), R.color._black));
            mSchemeTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.blue));
            mSelectTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            mCurDayTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.blue));
        }
        mSelectedPaint.setColor(ContextCompat.getColor(getContext(), R.color.transparent));
        mCurMonthTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.purple_500));

//        mCurMonthLunarTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorViewOne));
//        mCurDayLunarTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorViewOne));
//        mSelectedLunarTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorViewOne));
//        mOtherMonthLunarTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorViewOne));
//        mSchemeLunarTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorViewOne));

    }

    public CustomMonthView(Context context) {
        super(context);
    }

    /**
     * draw select calendar
     *
     * @param canvas    canvas
     * @param calendar  select calendar
     * @param x         calendar item x start point
     * @param y         calendar item y start point
     * @param hasScheme is calendar has scheme?
     * @return if return true will call onDrawScheme again
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        canvas.drawRect(x, y, x + mItemWidth, y + mItemHeight, mSelectedPaint);
        return true;
    }

    /**
     * draw scheme if calendar has scheme
     *
     * @param canvas   canvas
     * @param calendar calendar has scheme
     * @param x        calendar item x start point
     * @param y        calendar item y start point
     */
    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        canvas.drawCircle(x + mItemWidth / 2, y + mItemHeight - 3 * 16, 24, mSelectedPaint);
    }

    /**
     * draw text
     *
     * @param canvas     canvas
     * @param calendar   calendar
     * @param x          calendar item x start point
     * @param y          calendar item y start point
     * @param hasScheme  is calendar has scheme?
     * @param isSelected is calendar selected?
     */
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        int size = (int) (Math.min(mItemWidth, mItemHeight) / 10 * 8.5 / 2);

        onCustomise();

        canvas.drawRoundRect(cx - size,
                cy - size,
                cx + size,
                cy + size,
                36,
                36, isSelected ? mCurDayTextPaint : mSchemePaint);

        canvas.drawText(String.valueOf(calendar.getDay()),
                cx,
                baselineY,
                isSelected ? mSelectTextPaint :
                        (calendar.isCurrentDay() ? mCurMonthTextPaint :
                                calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint));

        //Paint paint1 = calendar.isCurrentMonth() ? mOtherMonthTextPaint : paint;
    }
}