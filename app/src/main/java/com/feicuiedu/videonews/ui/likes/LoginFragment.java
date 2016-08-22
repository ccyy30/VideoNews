package com.feicuiedu.videonews.ui.likes;

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
import com.feicuiedu.videonews.bombapi.UserApi;
import com.feicuiedu.videonews.bombapi.result.ErrorResult;
import com.feicuiedu.videonews.bombapi.result.UserResult;
import com.feicuiedu.videonews.commons.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录对话框
 * <p/>
 */
public class LoginFragment extends DialogFragment {

    private Unbinder unbinder;

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnLogin) Button btnLogin;

    private String username;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 无标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.dialog_login, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnLogin)
    public void login() {
        username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        // 用户名或密码不能为空
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtils.showShort(R.string.username_or_password_can_not_be_null);
            return;
        }
        // 隐藏按钮，避免按钮重复点击，此时显示的是按钮下方的进度条。
        btnLogin.setVisibility(View.GONE);
        // 注册API
        UserApi userApi = BombClient.getsInstance().getUserApi();

        Call<UserResult> call = userApi.login(username, password);

        call.enqueue(new Callback<UserResult>() {
            @Override public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                btnLogin.setVisibility(View.VISIBLE);
                // 登录未成功(不是200-300)
                if (!response.isSuccessful()) {
                    try {
                        String error = response.errorBody().string();
                        ErrorResult errorResult = new Gson().fromJson(error, ErrorResult.class);
                        ToastUtils.showShort(errorResult.getError());
                    } catch (IOException e) {
                        onFailure(call, e);
                    }
                    return;
                }
                // 登录成功
                UserResult result = response.body();
                listener.loginSuccess(username, result.getObjectId());
                ToastUtils.showShort(R.string.login_success);
            }

            @Override public void onFailure(Call<UserResult> call, Throwable t) {
                btnLogin.setVisibility(View.VISIBLE);
                ToastUtils.showShort(t.getMessage());
            }
        });
    }

    private OnLoginSuccessListener listener;

    public void setListener(@NonNull OnLoginSuccessListener listener) {
        this.listener = listener;
    }

    public interface OnLoginSuccessListener {
        /** 当登录成功时，将来调用*/
        void loginSuccess(String username, String objectId);
    }
}
