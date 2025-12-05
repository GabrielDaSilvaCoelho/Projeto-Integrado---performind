package com.example.minhaparte.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ScoreGaugeView extends View {

    private int maxScore = 1000;
    private int score = 0;

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF arcRect = new RectF();

    public ScoreGaugeView(Context context) {
        super(context);
        init();
    }

    public ScoreGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScoreGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(30f);
        backgroundPaint.setColor(Color.parseColor("#333333"));
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(30f);
        progressPaint.setColor(Color.GREEN);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setColor(Color.WHITE);
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
        invalidate();
    }

    public void setScore(int score) {
        if (score < 0) score = 0;
        if (score > maxScore) score = maxScore;
        this.score = score;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Usamos a largura como base e criamos um círculo encaixado na parte de cima do view
        float padding = 40f;
        float left = padding;
        float right = width - padding;
        float size = right - left; // diâmetro do círculo

        // Centraliza verticalmente o semicírculo
        float centerY = height * 0.7f; // um pouco acima do centro
        float top = centerY - size / 2f;
        float bottom = centerY + size / 2f;

        arcRect.set(left, top, right, bottom);

        // Arco de fundo (meio círculo 180°)
        canvas.drawArc(arcRect, 180, 180, false, backgroundPaint);

        // Sweep do progresso (0–180 graus)
        float sweep = 180f * score / maxScore;

        // Cor varia do vermelho -> amarelo -> verde
        progressPaint.setColor(getColorForScore(score / (float) maxScore));

        canvas.drawArc(arcRect, 180, sweep, false, progressPaint);

        // Ponteiro na ponta do arco
        float angleRad = (float) Math.toRadians(180 + sweep);
        float radius = size / 2f;
        float cx = (left + right) / 2f + (float) (radius * Math.cos(angleRad));
        float cy = centerY + (float) (radius * Math.sin(angleRad));

        canvas.drawCircle(cx, cy, 10f, pointerPaint);
    }

    /**
     * Retorna uma cor entre vermelho -> amarelo -> verde conforme o score (0–1).
     */
    private int getColorForScore(float fraction) {
        if (fraction < 0f) fraction = 0f;
        if (fraction > 1f) fraction = 1f;

        // 0.0 -> vermelho (#FF4B3A)
        // 0.5 -> amarelo (#FFC300)
        // 1.0 -> verde (#2ECC71)
        int startColor;
        int endColor;
        float localFraction;

        if (fraction < 0.5f) {
            startColor = Color.parseColor("#FF4B3A");
            endColor = Color.parseColor("#FFC300");
            localFraction = fraction / 0.5f;
        } else {
            startColor = Color.parseColor("#FFC300");
            endColor = Color.parseColor("#2ECC71");
            localFraction = (fraction - 0.5f) / 0.5f;
        }

        int r = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * localFraction);
        int g = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * localFraction);
        int b = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * localFraction);

        return Color.rgb(r, g, b);
    }
}
