package com.shenl.xmpplibrary.activiity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.shenl.xmpplibrary.R;

public class SeeImgActivity extends Activity {

    private ImageView iv_seeImg;
    // 縮放控制
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // 定义第一个按下的点，两只接触点的重点，以及出事的两指按下的距离：
    private PointF startPoint = new PointF();
    private PointF midPoint = new PointF();
    private float oriDis = 1f;
    // 不同状态的表示：
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_img);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        iv_seeImg = findViewById(R.id.iv_SeeImg);
    }

    private void initData() {
        Intent intent = getIntent();
        String imgPath = intent.getStringExtra("imgPath");
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        iv_seeImg.setImageBitmap(bitmap);
    }

    private void initEvent() {
        //图片点击
        iv_seeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //图片触摸事件
        iv_seeImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 单指
                    case MotionEvent.ACTION_DOWN:
                        matrix.set(view.getImageMatrix());
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    // 双指
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oriDis = distance(event);
                        if (oriDis > 10f) {
                            savedMatrix.set(matrix);
                            midPoint = middle(event);
                            mode = ZOOM;
                        }
                        break;
                    // 手指放开
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    // 单指滑动事件
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            // 是一个手指拖动
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            // 两个手指滑动
                            float newDist = distance(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oriDis;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }
                // 设置ImageView的Matrix
                view.setImageMatrix(matrix);
                return true;

            }
        });
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Float.valueOf(String.valueOf(Math.sqrt(x * x + y * y))) ;
    }

    // 计算两个触摸点的中点
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

}
