package com.feicuiedu.videonews.ui.local;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地视频适配器
 * 做了线程池的管理以及图片的简单缓存和线程池的释放
 */
public class LocalVideoAdapter extends CursorAdapter {
    // 用来加载生成视频预览图的线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    // 用来缓存视频预览图
    private LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(5 * 1024 * 1024) {
        @Override protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    public LocalVideoAdapter(Context context) {
        super(context, null, true);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new LocalVidioView(context);
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        final LocalVidioView localVidioView = (LocalVidioView) view;
        // 将当前数据,设置到视图上
        localVidioView.bind(cursor);
        // 以下的代码最终就是为了设置这个视图的预览图
        final String filePath = localVidioView.getFilePath();// 当前视频路径
        // 从缓存中获取预览图
        Bitmap bitmap = lruCache.get(filePath);
        if (bitmap != null) {
            // 设置当前视图预览图
            localVidioView.setPreview(bitmap);
        }
        // 缓存中没有预览图,后台线程进行预览图加载
        else {
            executorService.submit(new Runnable() {
                @Override public void run() {
                    // 获取设置视频预览图(此方法很费时)
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    lruCache.put(filePath, bitmap);
                    // 设置当前视图预览图
                    localVidioView.setPreview(filePath, bitmap);
                }
            });
        }
    }

    public void release(){
        executorService.shutdown();
    }
}
