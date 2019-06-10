package com.feimeng.fdroid.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.feimeng.fdroid.mvp.base.FDPresenter;
import com.feimeng.fdroid.mvp.base.FDView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 懒加载Fragment
 *
 * @author wangshijia
 * @date 2018/2/2
 * Fragment 第一次可见状态应该在哪里通知用户 在 onResume 以后？
 * https://blog.csdn.net/learningcoding/article/details/80044942
 */
public abstract class FDLazyFragment<V extends FDView, P extends FDPresenter<V>> extends FDFragment<V, P> {
    protected WeakReference<View> mRootView;
    private boolean mIsFirstVisible = true;
    private boolean mIsViewCreated = false;
    private boolean mCurrentVisibleState = false;

    public View getRootView() {
        return mRootView.get();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mRootView == null || mRootView.get() == null) {
            mRootView = new WeakReference<>(inflater.inflate(getLayoutRes(), container, false));
        } else {
            // 缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) getRootView().getParent();
            if (parent != null) parent.removeView(mRootView.get());
        }
        return mRootView.get();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!mIsViewCreated) {
            mIsViewCreated = true;
            // 判断是否已经初始化
            if (view.getTag() == null) {
                initView(view, savedInstanceState);
                view.setTag(true); // 已经初始化
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 对于默认 tab 和 间隔 checked tab 需要等到 mIsViewCreated = true 后才可以通过此通知用户可见
        // 这种情况下第一次可见不是在这里通知 因为 mIsViewCreated = false 成立,等从别的界面回到这里后会使用 onFragmentResume 通知可见
        // 对于非默认 tab mIsFirstVisible = true 会一直保持到选择则这个 tab 的时候，因为在 onActivityCreated 会返回 false
        if (mIsViewCreated) {
            if (isVisibleToUser && !mCurrentVisibleState) {
                dispatchUserVisibleHint(true);
            } else if (!isVisibleToUser && mCurrentVisibleState) {
                dispatchUserVisibleHint(false);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // !isHidden() 默认为 true  在调用 hide show 的时候可以使用
        if (!isHidden() && getUserVisibleHint()) {
            // 这里的限制只能限制 A - > B 两层嵌套
            dispatchUserVisibleHint(true);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        L.e(getClass().getSimpleName() + "  onHiddenChanged dispatchChildVisibleState  hidden " + hidden);
        if (hidden) {
            dispatchUserVisibleHint(false);
        } else {
            dispatchUserVisibleHint(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsFirstVisible) {
            if (!isHidden() && !mCurrentVisibleState && getUserVisibleHint()) {
                dispatchUserVisibleHint(true);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 当前 Fragment 包含子 Fragment 的时候 dispatchUserVisibleHint 内部本身就会通知子 Fragment 不可见
        // 子 fragment 走到这里的时候自身又会调用一遍 ？
        if (mCurrentVisibleState && getUserVisibleHint()) {
            dispatchUserVisibleHint(false);
        }
    }

//    private boolean isFragmentVisible(Fragment fragment) {
//        return !fragment.isHidden() && fragment.getUserVisibleHint();
//    }


    /**
     * 统一处理 显示隐藏
     *
     * @param visible 是否可见
     */
    private void dispatchUserVisibleHint(boolean visible) {
        //当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment getUserVisibleHint = true
        //但当父 fragment 不可见所以 mCurrentVisibleState = false 直接 return 掉
//        L.e(getClass().getSimpleName() + "  dispatchUserVisibleHint isParentInvisible() " + isParentInvisible());
        // 这里限制则可以限制多层嵌套的时候子 Fragment 的分发
        if (visible && isParentInvisible()) return;
//        //此处是对子 Fragment 不可见的限制，因为 子 Fragment 先于父 Fragment回调本方法 mCurrentVisibleState 置位 false
//        // 当父 dispatchChildVisibleState 的时候第二次回调本方法 visible = false 所以此处 visible 将直接返回
        if (!mIsViewCreated) return;
        if (mCurrentVisibleState == visible) return;
        mCurrentVisibleState = visible;
        if (visible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                onVisible(true);
            } else {
                onVisible(false);
            }
            dispatchChildVisibleState(true);
        } else {
            dispatchChildVisibleState(false);
            onInvisible();
        }
    }

    /**
     * 用于分发可见时间的时候父获取 fragment 是否隐藏
     *
     * @return true fragment 不可见， false 父 fragment 可见
     */
    private boolean isParentInvisible() {
        FDLazyFragment fragment = (FDLazyFragment) getParentFragment();
        return fragment != null && !fragment.isSupportVisible();
    }

    private boolean isSupportVisible() {
        return mCurrentVisibleState;
    }

    /**
     * 当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment 的唯一或者嵌套 VP 的第一 fragment 时 getUserVisibleHint = true
     * 但是由于父 Fragment 还进入可见状态所以自身也是不可见的， 这个方法可以存在是因为庆幸的是 父 fragment 的生命周期回调总是先于子 Fragment
     * 所以在父 fragment 设置完成当前不可见状态后，需要通知子 Fragment 我不可见，你也不可见，
     * <p>
     * 因为 dispatchUserVisibleHint 中判断了 isParentInvisible 所以当 子 fragment 走到了 onActivityCreated 的时候直接 return 掉了
     * <p>
     * 当真正的外部 Fragment 可见的时候，走 setVisibleHint (VP 中)或者 onActivityCreated (hide show) 的时候
     * 从对应的生命周期入口调用 dispatchChildVisibleState 通知子 Fragment 可见状态
     *
     * @param visible 是否可见
     */
    @SuppressLint("RestrictedApi")
    private void dispatchChildVisibleState(boolean visible) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        List<Fragment> fragments = childFragmentManager.getFragments();
        if (!fragments.isEmpty()) {
            for (Fragment child : fragments) {
                if (child instanceof FDLazyFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    ((FDLazyFragment) child).dispatchUserVisibleHint(visible);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getView() != null) {
            ViewGroup parent = (ViewGroup) getView().getParent();
            if (parent != null) {
                parent.removeView(getView());
            }
        }
        mIsViewCreated = false;
    }

    /**
     * 返回布局 resId
     *
     * @return layoutId
     */
    @LayoutRes
    protected abstract int getLayoutRes();

    /**
     * 初始化view
     *
     * @param rootView 根布局
     */
    protected abstract void initView(View rootView, @Nullable Bundle savedInstanceState);

    /**
     * 对用户可见
     *
     * @param isFirstVisible 是否是首次可见
     */
    public void onVisible(boolean isFirstVisible) {
//        L.e(getClass().getSimpleName() + "  对用户可见");
    }

    /**
     * 对用户不可见
     */
    public void onInvisible() {
//        L.e(getClass().getSimpleName() + "  对用户不可见");
    }
}
