package com.feimeng.fdroid.utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.feimeng.fdroid.R;

/**
 * 增强用户体验效果工具
 * Created by feimeng on 2017/3/4.
 */
public class UE {
    private Animation mShakeAnim;
    private static UE mInstance;

    private UE(Context context) {
        mShakeAnim = AnimationUtils.loadAnimation(context, R.anim.shake);
    }

    public static void init(Context context) {
        mInstance = new UE(context);
    }

    public static UE get() {
        return mInstance;
    }

    public void shake(EditText view) {
        shake(view, null);
    }

    public void shake(EditText view, String error) {
        view.requestFocus();
        view.startAnimation(mShakeAnim);
        if (error != null)
            view.setError(error);
    }

    public boolean input(EditText view, OnJudge judge) {
        JudgeResult judgeResult = judge.condition(view.getText().toString());
        if (!judgeResult.status) {
            view.requestFocus();
            view.startAnimation(mShakeAnim);
            view.setError(judgeResult.message);
        }
        return judgeResult.status;
    }

    public interface OnJudge {
        JudgeResult condition(String content);
    }

    public static class JudgeResult {
        private boolean status;
        private String message;

        private JudgeResult(boolean status, String message) {
            this.status = status;
            this.message = message;
        }

        public static JudgeResult get(boolean status, String message) {
            return new JudgeResult(status, message);
        }
    }
}
