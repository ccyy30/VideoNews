package com.feicuiedu.videoplayer.list;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.ImageView;

/**
 * 扩展{@link TextureView}，用来实现类似{@link ImageView}的{@link ImageView.ScaleType}
 * 功能。
 * <p/>
 * 比如想获得类似{@link ImageView.ScaleType#CENTER_CROP}的效果，只需要这样做：
 * <ol>
 * <li/> 设置缩放类型：{@link #setScaleType(ScaleType)}，设置为{@link ScaleType#CENTER_CROP}
 * <li/> 设置视频内容的宽和高：{@link #setContentWidth(int)} 和 {@link #setContentHeight(int)}
 * <li/> 更新TextureView尺寸：{@link #updateTextureViewSize()}
 * </ol>
 */
@SuppressWarnings("unused")
public class ScalableTextureView extends TextureView {

    private Integer contentWidth;
    private Integer contentHeight;

    // 中心点(pivot)坐标，中心点是在矩阵变换前后不变的点
    private float pivotPointX = 0f;
    private float pivotPointY = 0f;

    private float contentScaleX = 1f;
    private float contentScaleY = 1f;

    private float contentRotation = 0f;

    // 自定义缩放比例
    private float contentScaleMultiplier = 1f;

    private int contentX = 0;
    private int contentY = 0;

    private final Matrix mTransformMatrix = new Matrix();

    private ScaleType mScaleType;

    public enum ScaleType {
        CENTER_CROP, TOP, BOTTOM, FILL
    }

    public ScalableTextureView(Context context) {
        super(context);
    }

    public ScalableTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScalableTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (contentWidth != null && contentHeight != null) {
            updateTextureViewSize();
        }
    }

    public void updateTextureViewSize() {
        if (contentWidth == null || contentHeight == null) {
            throw new RuntimeException("null content size");
        }

        float viewWidth = getMeasuredWidth();
        float viewHeight = getMeasuredHeight();

        float contentWidth = this.contentWidth;
        float contentHeight = this.contentHeight;


        float scaleX = 1.0f;
        float scaleY = 1.0f;

        switch (mScaleType) {
            case FILL:
                if (viewWidth > viewHeight) {   // device in landscape
                    scaleX = (viewHeight * contentWidth) / (viewWidth * contentHeight);
                } else {
                    scaleY = (viewWidth * contentHeight) / (viewHeight * contentWidth);
                }
                break;
            case BOTTOM:
            case CENTER_CROP:
            case TOP:
                if (contentWidth > viewWidth && contentHeight > viewHeight) {
                    scaleX = contentWidth / viewWidth;
                    scaleY = contentHeight / viewHeight;
                } else if (contentWidth < viewWidth && contentHeight < viewHeight) {
                    scaleY = viewWidth / contentWidth;
                    scaleX = viewHeight / contentHeight;
                } else if (viewWidth > contentWidth) {
                    scaleY = (viewWidth / contentWidth) / (viewHeight / contentHeight);
                } else if (viewHeight > contentHeight) {
                    scaleX = (viewHeight / contentHeight) / (viewWidth / contentWidth);
                }
                break;
        }


        // Calculate pivot points, in our case crop from center
        // 计算中心点
        float pivotPointX;
        float pivotPointY;

        switch (mScaleType) {
            case TOP:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case BOTTOM:
                pivotPointX = viewWidth;
                pivotPointY = viewHeight;
                break;
            case CENTER_CROP:
                pivotPointX = viewWidth / 2;
                pivotPointY = viewHeight / 2;
                break;
            case FILL:
                pivotPointX = this.pivotPointX;
                pivotPointY = this.pivotPointY;
                break;
            default:
                throw new IllegalStateException("pivotPointX, pivotPointY for ScaleType " + mScaleType + " are not defined");
        }


        float fitCoef = 1;
        switch (mScaleType) {
            case FILL:
                break;
            case BOTTOM:
            case CENTER_CROP:
            case TOP:
                if (this.contentHeight > this.contentWidth) { //Portrait video
                    fitCoef = viewWidth / (viewWidth * scaleX);
                } else { //Landscape video
                    fitCoef = viewHeight / (viewHeight * scaleY);
                }
                break;
        }

        contentScaleX = fitCoef * scaleX;
        contentScaleY = fitCoef * scaleY;

        this.pivotPointX = pivotPointX;
        this.pivotPointY = pivotPointY;

        updateMatrixScaleRotate();
    }

    private void updateMatrixScaleRotate() {

        mTransformMatrix.reset();
        mTransformMatrix.setScale(contentScaleX * contentScaleMultiplier, contentScaleY * contentScaleMultiplier, pivotPointX, pivotPointY);
        mTransformMatrix.postRotate(contentRotation, pivotPointX, pivotPointY);
        // 注意：此方法不会改变TextureView本身的位置或大小，只会改变TextureView上的内容
        setTransform(mTransformMatrix);
    }

    private void updateMatrixTranslate() {

        float scaleX = contentScaleX * contentScaleMultiplier;
        float scaleY = contentScaleY * contentScaleMultiplier;

        mTransformMatrix.reset();
        mTransformMatrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);
        mTransformMatrix.postTranslate(contentX, contentY);
        setTransform(mTransformMatrix);
    }

    @Override
    public void setRotation(float degrees) {

        contentRotation = degrees;

        updateMatrixScaleRotate();
    }

    @Override
    public float getRotation() {
        return contentRotation;
    }

    @Override
    public void setPivotX(float pivotX) {

        pivotPointX = pivotX;
    }

    @Override
    public void setPivotY(float pivotY) {

        pivotPointY = pivotY;
    }

    @Override
    public float getPivotX() {
        return pivotPointX;
    }

    @Override
    public float getPivotY() {
        return pivotPointY;
    }

    public float getContentAspectRatio() {
        return contentWidth != null && contentHeight != null
                ? (float) contentWidth / (float) contentHeight
                : 0;
    }

    /**
     * Use it to animate TextureView content x position
     */
    public final void setContentX(float x) {
        contentX = (int) x - (getMeasuredWidth() - getScaledContentWidth()) / 2;
        updateMatrixTranslate();
    }

    /**
     * Use it to animate TextureView content x position
     */
    public final void setContentY(float y) {
        contentY = (int) y - (getMeasuredHeight() - getScaledContentHeight()) / 2;
        updateMatrixTranslate();
    }

    protected final float getContentX() {
        return contentX;
    }

    protected final float getContentY() {
        return contentY;
    }

    /**
     * 让TextureView的内容居中
     */
    public void centralizeContent() {
        contentX = 0;
        contentY = 0;
        updateMatrixScaleRotate();
    }

    public Integer getScaledContentWidth() {
        return (int) (contentScaleX * contentScaleMultiplier * getMeasuredWidth());
    }

    public Integer getScaledContentHeight() {
        return (int) (contentScaleY * contentScaleMultiplier * getMeasuredHeight());
    }

    public float getContentScale() {
        return contentScaleMultiplier;
    }

    public void setContentScale(float contentScale) {

        contentScaleMultiplier = contentScale;
        updateMatrixScaleRotate();
    }

    public final void setContentHeight(int height) {
        contentHeight = height;
    }

    protected final Integer getContentHeight() {
        return contentHeight;
    }

    public final void setContentWidth(int width) {
        contentWidth = width;
    }

    protected final Integer getContentWidth() {
        return contentWidth;
    }

}
