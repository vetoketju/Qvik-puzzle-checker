package fi.qvik.android.qvikpuzzlechecker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by villevalta on 20/01/16.
 */
public class BoardImageView extends ImageView {

    Bitmap bitmap;

    public BoardImageView(Context context) {
        super(context);
    }

    public BoardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        trySetImage();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        trySetImage();
    }

    private void trySetImage(){
        if(bitmap != null && getMeasuredHeight() > 0 && getMeasuredWidth() > 0){
            setImageBitmap(Bitmap.createScaledBitmap(bitmap,getMeasuredWidth(), getMeasuredHeight(), false));
        }
    }
}
