package com.indoor_navigation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.indoor_navigation.R;
import com.indoor_navigation.model.JsonPoint;
import com.indoor_navigation.model.Point;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ־             �� on 2015/7/23.
 */
public class MapActivity extends ActionBarActivity {
    private ArrayList<Point> chosePointList = null;
    private ArrayList<JsonPoint> jsonPointsList = null;
    private String  jsonString = null;
    private String  GeolocationUrl = null;
    private WebView mMapWebView ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //��ȡ�ص��б�
        Intent intent = getIntent();
        chosePointList = (ArrayList<Point>) intent.getSerializableExtra("chosepointlist");
        //ת���ص���������
        jsonPointsList = toJsonPoint(chosePointList);
        //���jsonString
        jsonString = toJsonString(jsonPointsList);
        Log.e("JSON",jsonString);
        Toast toast= Toast.makeText(getApplicationContext(), jsonString.toString(), Toast.LENGTH_SHORT);
        toast.show();

        GeolocationUrl = getURL();
        //WebView ������ʾ��ͼ
        mMapWebView = (WebView)findViewById(R.id.MapWebView);
        WebSettings webSettings = mMapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mMapWebView.loadUrl("file:///android_asset/WebMap/navigation-map/navigation-map.html");
        mMapWebView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mMapWebView.loadUrl("javascript: setGeolocationUrl('" + GeolocationUrl + "')");
                        mMapWebView.loadUrl("javascript: setEnableRerouting(true)");
                        mMapWebView.loadUrl("javascript: setDestJSON('"+ jsonString +"')");
                        mMapWebView.loadUrl("javascript: startRouting()");
                    }
                }
        );
    }


    public ArrayList<JsonPoint> toJsonPoint(ArrayList<Point> chosePointList){
        ArrayList<JsonPoint> jsonPointslist = new ArrayList<JsonPoint>();
        for (Iterator iterator = chosePointList.iterator(); iterator.hasNext();) {
            Point point = (Point) iterator.next();
            JsonPoint jsonPoint =  new JsonPoint();
            jsonPoint.setX(point.getX());
            jsonPoint.setY(point.getY());
            jsonPoint.setZ(point.getZ());
            jsonPointslist.add(jsonPoint);
        }
        return jsonPointslist;
    }

    public String toJsonString(ArrayList<JsonPoint> jsonPointsList){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<JsonPoint>>(){}.getType();
        String jsonString = gson.toJson(jsonPointsList, listType);
        return jsonString;
    }


    public String getURL(){
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
        return content.toString();
    }
}
