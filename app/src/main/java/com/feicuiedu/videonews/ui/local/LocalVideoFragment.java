package com.feicuiedu.videonews.ui.local;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.feicuiedu.videonews.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 本地视频页面
 * <P/>
 * 视图上主要有一个GridView来展示所有视图
 * <P/>
 * 业务上将使用Loader进行数据加载
 * 1. LoaderManager - initLoader
 * 2. Callback
 * 3. CallBack - onCreateLoader (用来创建出Loader - 一般就可直接使用CurseLoader)
 * 4. CallBack - onLoadFinished (完成后来调用)
 * 5. CallBack - onLoaderReset
 * <P/>
 */
public class LocalVideoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private Unbinder unbinder;

    @BindView(R.id.gridView) GridView gridView;

    private LocalVideoAdapter localVideoAdapter;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localVideoAdapter = new LocalVideoAdapter(getContext());
        // 初始当前页面的Loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_video,container,false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        gridView.setAdapter(localVideoAdapter);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        localVideoAdapter.release();
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Video.Media._ID, // 视频Id
                MediaStore.Video.Media.DATA, // 视频文件路径
                MediaStore.Video.Media.DISPLAY_NAME, // 视频名称
        };
        Log.i("loader","ok");
        return new CursorLoader(getContext(),
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,null,null,null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("onLoadFinished",data.getCount()+"");
        //相当于addData函数，将数据添加到适配器中
        localVideoAdapter.swapCursor(data);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        localVideoAdapter.swapCursor(null);
    }
}
