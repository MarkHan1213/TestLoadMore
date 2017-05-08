package com.loadmore.mark.testloadmore;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.loadmore.mark.testloadmore.a.LoadMoreView;
import com.loadmore.mark.testloadmore.bean.Page;
import com.loadmore.mark.testloadmore.bean.Results;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "--Main--";

    private LoadMoreView list;
    private SwipeRefreshLayout refresh;
    private MyAdapter mMyAdapter;

    private List<Results> mResultsList;
    private int pageno = 34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (LoadMoreView) findViewById(R.id.list);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageno = 1;
                getData();
            }
        });

        list.setOnLoadMoreListener(new LoadMoreView.OnLoadMoreListener() {
            @Override
            public void onLoadingMore() {
                getData();
            }
        });

//        if (list.getIsSetNoLoad()) {
//            list.setOkToLoad();
//        }
//
//        list.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                getData();
//            }
//        });
        initData();
        getData();
    }

    private void initData() {
        mResultsList = new ArrayList<>();
        mMyAdapter = new MyAdapter(this, mResultsList);
        list.setAdapter(mMyAdapter);
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.put("method", "album.item.get");
        params.put("appKey", "myKey");
        params.put("format", "json");
        params.put("albumId", "Lqfme5hSolM");
        params.put("pageNo", pageno);
        params.put("pageSize", 20);
        loadData(params);
    }

    private void loadData(RequestParams params) {

        HttpClient.get(APi.loadUrl, params, new JsonResponse() {
            @Override
            public void onSuccess(JSONObject object) {

                refresh.setRefreshing(false);

                Page page = object.getObject("page", Page.class);
                if (page.getPageNo() * page.getPageSize() >= page.getTotalCount()) {
//                    list.setOkToLoad();
//                    list.setNoMoreToLoad();
                    list.onNoMoreData();
                } else {
                    pageno++;
//                    list.onLoadMoreComplete();
                }
                list.onFinishLoading();
                List<Results> resultses = object.getJSONArray("results").toJavaList(Results.class);
                mResultsList.addAll(resultses);
                mMyAdapter.notifyDataSetChanged();
//                Log.e(TAG, "onSuccess: " + object.toJSONString());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }


}
