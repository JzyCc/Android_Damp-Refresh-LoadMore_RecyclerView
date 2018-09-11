# Android_DampRefreshAndLoadMoreLayout
## DampRefreshAndLoadMoreLayout介绍
1. recyclerview在它里面可以更灵活的转交事件。
2. 根据配置可以实现刷新和加载更多的功能。
3. 提供接口将头部和底部与容器分离，可以根据自己的需求完全自定义自己想要的刷新头部和加载底部。

## 使用
### 1. 在XML布局中加入如下代码

```
<com.jzycc.layout.dampscrollview.damprv.DampRefreshAndLoadMoreLayout
        android:id="@+id/dv_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#3131">
        <com.jzycc.layout.dampscrollview.damprv.DampRecyclerViewChild
            android:id="@+id/rv_content"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:overScrollMode="never">
        </com.jzycc.layout.dampscrollview.damprv.DampRecyclerViewChild>
    </com.jzycc.layout.dampscrollview.damprv.DampRefreshAndLoadMoreLayout>
```

```
DampRecyclerViewChild继承了RecyclerView，使用与RecyclerView一样。
```
此时配置好RecyclerView后，运行项目可以实现基本的阻尼回弹效果。
### 2.添加下拉刷新和下拉加载功能

- #### 设置下拉刷新功能 

##### 设置默认刷新头部

```
dampRefreshAndLoadMoreLayout.setTopView();
```
##### 添加刷新监听

```
dampRefreshAndLoadMoreLayout.addOnDampRefreshListener(new DampRefreshAndLoadMoreLayout.DampRefreshListener() {
            @Override
            public void getScrollChanged(int dy, int topViewPosition) {
                //此处返回当前滑动距离和topView顶部到容器顶部的距离
            }

            @Override
            public void startRefresh() {
                //当刷新触发时在此处写刷新相关逻辑
            }
        });
```
##### 结束刷新

```
//当刷新结束后调用此方法结束刷新动画
dampRefreshAndLoadMoreLayout.stopRefreshAnimation();
```
- #### 设置加载更多功能
##### 设置默认加载底部

```
dampRefreshAndLoadMoreLayout.setBottomView();
```
##### 添加加载更多监听

```
dampRefreshAndLoadMoreLayout.addOnDampLoadMoreListener(new DampRefreshAndLoadMoreLayout.DampLoadMoreListener() {
            @Override
            public void getScrollChanged(int dy, int bottomViewPosition) {
                //此处返回当前滑动距离和bottomView底部到容器底部的距离
            }

            @Override
            public void startLoadMore() {
                //当加载更多触发时在此处写加载相关逻辑
            }
        });
```
##### 结束加载

```
//当加载完成后调用此方法结束加载动画
dampRefreshAndLoadMoreLayout.stopLoadMoreAnimation();
```
##### 所有数据加载完毕

```
//当所有数据加载完成后调用此方法
dampRefreshAndLoadMoreLayout.loadOver();
```
### 3.就这样？自定义出自己的刷新和加载吧！
- #### 自定义刷新View
##### 新建一个Class文件,此处我继承FrameLayout，实现DampTopViewListener接口

```
public class TopViewChild extends FrameLayout implements DampTopViewListener {

    public TopViewChild(@NonNull Context context) {
        super(context);
    }

    @Override
    public void getScrollChanged(int dy, int topViewPosition) {
         //此处返回当前滑动距离和topView顶部到容器顶部的距离
    }

    @Override
    public void refreshComplete() {
        //此时刷新已经完成
    }

    @Override
    public void refreshing() {
        //此时正在刷新
    }

    @Override
    public void refreshReady() {
        //此时松手可以触发刷新
    }

    @Override
    public void shouldInitialize() {
        //需要初始化的步骤，此处在按下屏幕并下拉时触发
    }
}
```
提供了DampTopViewListener接口来返回当前容器的刷新状态，可以通过这些来实现刷新的动画。
##### 添加自定义topView
```
dampRefreshAndLoadMoreLayout.setTopView(new TopViewChild(context),topViewHeight);
```

```
此处应当传入自定以topView的高度（单位：dp）
```
- #### 自定义加载View
##### 新建一个Class文件,此处我继承FrameLayout，实现DampBottomViewListener接口

```
public class BottomViewChild extends FrameLayout implements DampBottomViewListener {
    public BottomViewChild(@NonNull Context context) {
        super(context);
    }

    @Override
    public void startLoadMore() {
        //此时加载被触发，初始化工作也可以在此处执行
    }

    @Override
    public void stopLoadMore() {
        //此时加载结束
    }

    @Override
    public void cannotLoadMore() {
        //此时所有数据已经加载完毕
    }

    @Override
    public void getScrollChanged(int dy, int topViewPosition) {
        //此处返回当前滑动距离和bottomView底部到容器底部的距离
    }
}
```
##### 添加自定义bottomView
```
dampRefreshAndLoadMoreLayout.setTopView(new BottomViewChild(context),topViewHeight);
```
```
此处应当传入自定以bottomView的高度（单位：dp）
```

