package com.feicuiedu.videonews.ui.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.bombapi.BombClient;
import com.feicuiedu.videonews.bombapi.NewsApi;
import com.feicuiedu.videonews.bombapi.entity.CommentsEntity;
import com.feicuiedu.videonews.bombapi.result.CreateResult;
import com.feicuiedu.videonews.commons.ToastUtils;
import com.feicuiedu.videonews.ui.UserManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 评论对话框fragment
 */
public class EditCommentFragment extends DialogFragment{

    private static final String KEY_NEWS_ID = "KEY_NEWS_ID";

    public static EditCommentFragment getInstance(String newsId){
        EditCommentFragment fragment = new EditCommentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_NEWS_ID, newsId);
        fragment.setArguments(args);
        return fragment;
    }

    private Unbinder unbinder;

    @BindView(R.id.etComment) EditText etComment;
    @BindView(R.id.btnOK) Button btnOk;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 无标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.dialog_edit_comment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnOK)
    public void postComment(){
        String comment = etComment.getText().toString();
        // 评论内容不能为空
        if(TextUtils.isEmpty(comment)){
            ToastUtils.showShort(R.string.please_edit_comment);
            return;
        }
        btnOk.setVisibility(View.INVISIBLE); // 评论按钮不可见
        // 评论处理
        String userId = UserManager.getInstance().getObjectId();
        String newsId = getArguments().getString(KEY_NEWS_ID);

        NewsApi newsApi = BombClient.getsInstance().getNewsApi();
        CommentsEntity commentsEntity = new CommentsEntity(comment,userId,newsId);
        Call<CreateResult> commentCall = newsApi.postComments(commentsEntity);
        commentCall.enqueue(commentCallback);
    }

    private Callback<CreateResult> commentCallback = new Callback<CreateResult>() {
        @Override public void onResponse(Call<CreateResult> call, Response<CreateResult> response) {
            btnOk.setVisibility(View.VISIBLE);
            if(response.isSuccessful()){
                listener.onCommentSuccess();
                return;
            }
            ToastUtils.showShort("评论异常");
        }

        @Override public void onFailure(Call<CreateResult> call, Throwable t) {
            btnOk.setVisibility(View.VISIBLE);
            ToastUtils.showShort(t.getMessage());
        }
    };

    private OnCommentSuccessListener listener;

    public void setListener(@NonNull OnCommentSuccessListener listener) {
        this.listener = listener;
    }

    /** 评论成功监听器*/
    public interface OnCommentSuccessListener{
        void onCommentSuccess();
    }
}
