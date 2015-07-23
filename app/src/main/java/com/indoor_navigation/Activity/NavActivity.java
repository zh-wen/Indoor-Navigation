package com.indoor_navigation.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ??? on 2015/7/21.
 */
public class NavActivity extends ActionBarActivity{
    private Button mBackBtn;
    private Button mNavigateBtn;
    private Button mCamaraBtn;
    private Button mMicBtn;
    private Button mLocateBtn;
    private Button mAddPointBtn;
    private ListView mListView;
    private PointAdapter pointadapter;
    private ArrayList<Point>  chosePointList = new ArrayList<Point>();
    private int item_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_nav);

        //地点列表
        pointadapter = new PointAdapter(this,
                R.layout.point_item,chosePointList);
        mListView = (ListView) findViewById(R.id.chosepoint_list);
        mListView.setAdapter(pointadapter);

        //设置listview单击修改地点
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NavActivity.this,FloorlistActivity.class);
                item_position = position;
                startActivityForResult(intent,2);
            }
        });

        //设置listview长按删除
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(NavActivity.this).setTitle("是否删除该地点")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        chosePointList.remove(position);
                                        pointadapter.notifyDataSetChanged();
                                    }
                                }

                        )
                        .

                                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }

                                ).

                        show();
                return true;
            }
        });

        //导航地点添加按钮
        mAddPointBtn = (Button) findViewById(R.id.addpoint_btn);
        mAddPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavActivity.this,FloorlistActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        //自动定位按钮
        mLocateBtn = (Button) findViewById(R.id.locate_btn);
        mLocateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetLocationWithHttpURLConnection();
            }
        });

        //设置返回按钮
        mBackBtn = (Button)findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置导航按钮
        mNavigateBtn = (Button)findViewById(R.id.navigation_btn);
        mNavigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavActivity.this,MapActivity.class);
                intent.putExtra("chosepointlist",chosePointList);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                //将选择的点返回给上一个activity
                if(resultCode == RESULT_OK){
                    Point point = (Point)data.getSerializableExtra("point");
                    chosePointList.add(point);
                    pointadapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Point point = (Point)data.getSerializableExtra("point");
                    chosePointList.set(item_position,point);
                    pointadapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void GetLocationWithHttpURLConnection(){
        //读取从定位接口得到的json格式数据，并修改起点信息
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
