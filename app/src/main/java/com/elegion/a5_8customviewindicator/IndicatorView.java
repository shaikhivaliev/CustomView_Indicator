package com.elegion.a5_8customviewindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class IndicatorView extends View {

    private Paint mTextPaint;
    private Paint mEmptyPaint;

    private RectF mStandartBounds;
    private Rect mTextSize;

    private float mIndicatorTextWidth;

    private List<Sector> mSectors;
    private int mEmptyIndicators;
    private int mIndicators;
    private int mIndicatorsColor;
    private int mViewSize;


    public IndicatorView(Context context) {
        super(context);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet set) {

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.IndicatorView);
        mViewSize = ta.getDimensionPixelSize(R.styleable.IndicatorView_viewSize, 24);
        mEmptyIndicators = ta.getInt(R.styleable.IndicatorView_maxCount, 4);
        mIndicators = ta.getInt(R.styleable.IndicatorView_indicateCount, 0);
        mIndicatorsColor = ta.getColor(R.styleable.IndicatorView_indicatorsColor, Color.BLUE);
        ta.recycle();

        mTextPaint = new Paint();
        mTextPaint.setColor(mIndicatorsColor);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(mViewSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeWidth(10);
        mTextPaint.setAntiAlias(true);

        mEmptyPaint = new Paint();
        mEmptyPaint.setColor(ContextCompat.getColor(context, R.color.colorEmptyIndicators));
        mEmptyPaint.setStyle(Paint.Style.STROKE);
        mEmptyPaint.setTextAlign(Paint.Align.CENTER);
        mEmptyPaint.setStrokeWidth(10);
        mEmptyPaint.setAntiAlias(true);

        mSectors = new ArrayList<>();

        createSectors(context, mEmptyIndicators, ContextCompat.getColor(context, R.color.colorEmptyIndicators));

        mStandartBounds = new RectF();
        mTextSize = new Rect();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mIndicatorTextWidth = mTextPaint.measureText("100.0 %");
        mTextPaint.getTextBounds("1", 0, 1, mTextSize);
        int desiredDiameter = (int) (mIndicatorTextWidth * 2);
        int measureWidth = resolveSize(desiredDiameter, widthMeasureSpec);
        int measureHeight = resolveSize(desiredDiameter, heightMeasureSpec);

        mStandartBounds.set(+10, +10, measureWidth - 10, measureHeight - 10);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cx = canvas.getWidth() / 2;
        int cy = canvas.getHeight() / 2;

        canvas.drawText(String.valueOf(mIndicators), cx, cy + (mTextSize.height()) / 2, mTextPaint);


        canvas.rotate(-90, cx, cy);


        if (mIndicators == 0) {
            canvas.drawCircle(cx, cy, mIndicatorTextWidth - 10, mEmptyPaint);

        } else if (mEmptyIndicators <= mIndicators) {
            canvas.drawCircle(cx, cy, mIndicatorTextWidth - 10, mTextPaint);

        } else {
            float gapAngle = ((360 / mEmptyIndicators) * 0.15f) / 2;
            float startAngle = gapAngle;

            for (Sector sector : mSectors) {
                startAngle = sector.draw(canvas, mStandartBounds, startAngle, mEmptyPaint);
            }

            for (int i = 0; i < mIndicators; i++) {
                startAngle = mSectors.get(i).draw(canvas, mStandartBounds, startAngle, mTextPaint);
            }
        }
    }

    private class Sector {
        private int mMaxInd;
        private float mEndAngle;
        private float mStartAngle;
        private Paint mPaint;


        private Sector(int max, int color) {
            mMaxInd = max;
            mPaint = new Paint();
            mPaint.setColor(color);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(10);
            mPaint.setAntiAlias(true);
        }

        private float draw(Canvas canvas, RectF bounds, float startAngle, Paint paint) {
            float sweepAngle = (360 / mMaxInd) * 0.85f;
            float gapAngle = (360 / mMaxInd) * 0.15f;
            mStartAngle = startAngle;
            mEndAngle = mStartAngle + sweepAngle + gapAngle;
            canvas.drawArc(bounds, startAngle, sweepAngle, false, paint);
            return mEndAngle;
        }
    }

    public void createSectors(Context context, int count, int color) {
        for (int i = 0; i < count; i++) {
            mSectors.add(new Sector(count, color));
        }
    }

}
