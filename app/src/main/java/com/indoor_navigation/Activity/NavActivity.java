package com.indoor_navigation.Activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.ListView;

import com.indoor_navigation.R;
import com.indoor_navigation.adapter.PointAdapter;
import com.indoor_navigation.model.Point;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 志豪 on 2015/7/21.
 */
public class NavActivity extends ActionBarActivity{
    private Button mBackBtn;
    private Button mNavigateBtn;
    private Button mCamaraBtn;
    private Button mMicBtn;
    private Button mLocateBtn;
    private Button mListBtn;
    private ListView mListView;
    private List<Point>  chosePointList = new ArrayList<Point>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_nav);

        PointAdapter pointadapter = new PointAdapter(this,
                R.layout.point_item,chosePointList);
        mListView = (ListView) findViewById(R.id.chosepoint_list);
        mListView.setAdapter(pointadapter);
    }


    private void GetLocationWithHttpURLConnection(Point point){

        //子线程获取定位接口的定位信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                String UrlString;
                //读取URL地址
                try {
                    FileInputStream in = null;
                    BufferedReader reader = null;
                    StringBuilder content = new StringBuilder();
                    try{
                        in = openFileInput("LocationServerURL");
                        reader = new BufferedReader(new InputStreamReader(in));
                        String line = "";
                        while((line = reader.readLine()) !=null){
                            content.append(line);
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try{
                            if(reader != null){
                                reader.close();
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                    UrlString = content.toString();
                    //使用HttpClient方式得到接口数据
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpGet httpGet = new HttpGet(UrlString);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode() == 200){
                        //请求和相应都成功了
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        //处理json格式数据
                        GetLocationInfo(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

    private void GetLocationInfo(String jsonData){
        //读取从定位接口得到的json格式数据，并修改起点信息
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            double x = jsonArray.getDouble(0);
            double y = jsonArray.getDouble(1);
            int z = jsonArray.getInt(2);
            chosePointList.get(0).setName("我的位置");
            chosePointList.get(0).setX(x);
            chosePointList.get(0).setY(y);
            chosePointList.get(0).setZ(z);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
