package com.feicuiedu.videonews.ui.likes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.ui.UserManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的收藏页面
 * 上: 个人信息,登录注册
 * 下: 收藏列表
 */
public class LikesFragment extends Fragment implements
        RegisterFragment.OnRegisterSuccessListener,
        LoginFragment.OnLoginSuccessListener{

    private View view;

    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnRegister) Button btnRegister;
    @BindView(R.id.btnLogout) Button btnLogout;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.divider) View divider;
    @BindView(R.id.likesListView) LikesListView likesListView;

    private RegisterFragment registerFragment; // 注册
    private LoginFragment loginFragment; // 登录

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_likes, container, false);
            ButterKnife.bind(this, view);
            // 进入我的收藏页面
            final UserManager userManager = UserManager.getInstance();
            if(!userManager.isOffline()){
                view.post(new Runnable() {
                    @Override public void run() {
                        userOnLine(userManager.getUsername(), userManager.getObjectId());
                    }
                });
            }
        }
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }

    // 显示登陆对话框Fragment
    @OnClick(R.id.btnLogin)
    public void showLoginDialog(){
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
            loginFragment.setListener(this);
        }
        loginFragment.show(getChildFragmentManager(), "Login Dialog");

    }

    // 显示注册对话框Fragment
    @OnClick(R.id.btnRegister)
    public void showRegisterDialog() {
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
            registerFragment.setListener(this);
        }
        registerFragment.show(getChildFragmentManager(),"Register Dialog");
    }

    // 登出
    @OnClick(R.id.btnLogout)
    public void logout(){
        userOffline();
    }

    @Override public void loginSuccess(String username, String objectId) {
        loginFragment.dismiss();
        // 登录成功,用户上线
        userOnLine(username, objectId);
    }

    @Override public void registerSuccess(String username, String objectId) {
        registerFragment.dismiss();
        // 注册成功,用户上线
        userOnLine(username, objectId);
    }

    private void userOffline() {
        // 清除用户信息
        UserManager.getInstance().clear();
        // 更改UI状态
        btnLogout.setVisibility(View.INVISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
        tvUsername.setText(R.string.tourist);
        // 清空收藏列表(adapter上的内容clear了)
        likesListView.clear();
    }

    private void userOnLine(String username, String objectId) {
        // 存储用户信息
        UserManager.getInstance().setUsername(username);
        UserManager.getInstance().setObjectId(objectId);
        // 更改UI状态
        btnLogout.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.INVISIBLE);
        btnRegister.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);
        tvUsername.setText(username);
        // 刷新收藏列表
        likesListView.autoRefresh();
    }
}
