# FDroid
>优雅的Android MVP敏捷开发框架 ———— FDroid
-------
## 特性
- [x] `网络请求`：基于RxJava+Retrofit+Gson+FDroid的封装，支持对网络请求结果的统一处理、请求结果回调到UI线程、自动判断网络连接状态、自动管理生命周期、模拟后台返回数据、自定义请求头、请求响应日志打印、每个阶段都可扩展等。
- [x] `MVP模式`：利用泛型深度解耦，为项目省掉一半代码；您不需要知道MVP实现细节，轻松使用MVP进行项目开发；
- [x] `模板开发`：无需编写MVP的各层代码，利用Android Studio Template一键生成具有View+Contract+Presenter的Activity。模板下载：FDActivity
- [x] `强大的控制层`：进行网络请求时，自动显隐等待对话框；使用FastTask类轻松进行耗时操作，自动回调到UI线程；
- [x] `有用的FDFragment`：支持缓存rootView，防止重复加载布局；拥有是否对用户可见的操作；
- [x] `版本升级`：一行代码实现APP版本升级。框架自带版本升级功能，不用自己实现版本升级逻辑。支持自动安装、强制升级、免流量安装等。
- [x] `L/T/SP类`：对Log、Toast和SharedP进行的有趣封装；Log支持显示打印的位置；全局管理log和Toast的显隐；
- [x] `丰富的工具包`：RxBus、FilePicker等，不停追加中。

## 使用步骤
#### Step 1.在项目根build.gradle中添加maven仓库
```groovy
allprojects {
  repositories {
    //...
    maven { url 'https://jitpack.io' }
  }
}
```
#### Step 2.在项目app的build.gradle中添加依赖
```groovy
dependencies {
  //...
  api 'com.github.feimenggo:fdroid:3.1.20'
}
```
#### Step 3.自定义Application继承FDApp.java
```java
public class BaseApp extends FDApp {
    @Override
    protected void config() {
	// 此处可以进行您项目其它库的初始化
    }
}
```
#### Step 4.[可选]为了便于在父类做统一的操作，可以自定义BaseActivity/BaseFragment分别继承自FDActivity.java和FDFragment.java
```java
public abstract class BaseActivity<V extends FDView, P extends FDPresenter<V>> extends FDActivity<V, P> {

}
public abstract class BaseFragment<V extends FDView, P extends FDPresenter<V>> extends FDFragment<V, P> {

}
```
#### Step 5.编写MVP文件，可以使用FDActivity模板一键生成。下面展示MVP三层代码和一次操作的执行流程
##### Contract层代码
```java
public interface TestContract {
    interface View extends FDView {
        void getUserName(String username);
    }

    abstract class Presenter extends FDPresenter<View> {
        public abstract void getUserName();
    }
}
```
##### Presenter层代码
```java
public class TestPresenter extends TestContract.Presenter {
    @Override
    public void getUserName() {
        // 步骤二
        // 这里进行具体的业务
        // ...
        // 简单模拟获取到的用户名是“小飞”
        String username = "小飞";
        // 回调结果给View层
        mView.getUserName(username);
    }
}
```
##### View层代码
```java
public class TestActivity extends FDActivity<TestContract.View, TestContract.Presenter> implements TestContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // 步骤一
        // 调用Presenter层，获取用户名
        mPresenter.getUserName();		
    }

    @Override
    protected TestContract.Presenter initPresenter() {
        return new TestPresenter();
    }

    @Override
    public void getUserName(String username) {
        // 步骤三
        // 显示获取的用户名
        Toast.makeText(this, "用户名是" + username, Toast.LENGTH_SHORT).show();
    }
}
```
#### Step 5.恭喜，您完成了FDroid入门任务。
##### 接下来，你的任务有自定义FDApi编写网络请求、学习使用L/T/SP工具类、全局对话框的使用等等。教程正在制作。。。

## 一键生成MVP模板
### FDActivity模板：位于download目录
#### Android Studio Template的使用，请自行度娘哦。
## License

```  
Copyright 2017 feimeng

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
