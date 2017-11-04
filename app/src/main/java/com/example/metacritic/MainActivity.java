package com.example.metacritic;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.ListPreloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.metacritic.R.id.cancel_action;
import static com.example.metacritic.R.id.nav_games;
import static com.example.metacritic.R.id.nav_history;
import static com.example.metacritic.R.id.nav_home;
import static com.example.metacritic.R.id.nav_movies;
import static com.example.metacritic.R.id.nav_music;
import static com.example.metacritic.R.id.nav_tv;
import static com.example.metacritic.R.id.text;

public class MainActivity extends AppCompatActivity {


  //  String filePath = MainActivity.getFilesDir().getPath().toString() + "/fileName.txt";
    private static String latestStr;
    private static String lastUrl;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private WebView webView;
    private ListView listView;
    private ProgressBar progressBar;
    private String responseStr;
    public static final int UPDATE_RESPONSE=1;
    private TextView textView;
    //    private SearchView searchView = (SearchView) findViewById(R.id.search);
    public static List<Listitem> itemList=new ArrayList<>();
    public static Set<Listitem> clickHistory = new HashSet<>();
    final okhttp3.Callback okhttpcallback=new okhttp3.Callback(){

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String responseData=response.body().string();
            Log.d("responseData",responseData);
            //     responseStr=responseData;
            Message msg = new Message();
            msg.what = UPDATE_RESPONSE;
            msg.obj = responseData;
            mhandler.sendMessage(msg);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("SYSTEM ERROR");
                    dialog.setMessage("System error.Check your network");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.show();
                }
            });

            return;
        }
    };

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_RESPONSE:
                    responseStr=(String)msg.obj;
                    parseHtmlToList(responseStr);
                    refreshListview(itemList);  //发送完请求回到主线程就会调用
                    swipeRefresh.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
            }
        }
    };

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        Log.d("searchview",searchView.toString());
    //    Toast.makeText(this,"search",Toast.LENGTH_SHORT).show();
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);    // 显示“开始搜索”的按钮
        searchView.setQueryRefinementEnabled(true); // 提示内容右边提供一个将提示内容放到搜索框的按钮
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
//                textView.setText(query);
                String queryStr="http://www.metacritic.com/search/all/"+query+"/results";
                Log.d("queryStr", queryStr);
                HttpUtil.sendOkHttpRequest(queryStr,okhttpcallback);
                progressBar.setVisibility(View.VISIBLE);
                lastUrl=queryStr;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }});
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("querystr", "query");
        switch (item.getItemId()){
  //          case R.id.search:
//                Log.d("querystr", "query");
//                searchView = (SearchView) findViewById(R.id.search);
//                Toast.makeText(this,"search",Toast.LENGTH_SHORT).show();
//                final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                final SearchView searchView = (SearchView) item.getActionView();
//
//                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//                searchView.setIconifiedByDefault(false);
//                searchView.setSubmitButtonEnabled(true);    // 显示“开始搜索”的按钮
//                searchView.setQueryRefinementEnabled(true); // 提示内容右边提供一个将提示内容放到搜索框的按钮
//                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                    // 当点击搜索按钮时触发该方法
//                    @Override
//                    public boolean onQueryTextSubmit(String query) {
//                        Toast.makeText(MainActivity.this,searchManager.QUERY,Toast.LENGTH_SHORT).show();
//                        Log.d("querystr", searchManager.QUERY);
//                        textView.setText(searchManager.QUERY);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String newText) {
//                        return false;
//                    }});
//            case R.id.leftmenu:
//                Toast.makeText(this,"leftmenu",Toast.LENGTH_SHORT).show();
//                break;
            case android.R.id.home:
          //      Toast.makeText(this,"leftmenu",Toast.LENGTH_SHORT).show();
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        SaveHistory();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                HttpUtil.sendOkHttpRequest(lastUrl,okhttpcallback);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LoadHistory();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        toolbar.setTitle("");
   //     toolbar.setTitleMarginStart(100) ;
        toolbar.setLogo(R.drawable.titlelogoshort);
       // toolbar.setNavigationIcon(R.drawable.leftmenu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
  //      textView = (TextView) findViewById(R.id.text_view);
        listView = (ListView) findViewById(R.id.list_view);
        //解决滑动冲突
        listView.setOnScrollListener(new ListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(listView != null && listView.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefresh.setEnabled(enable);
            }});


     //   sendRequest();
        HttpUtil.sendOkHttpRequest("http://www.metacritic.com/",okhttpcallback);
        lastUrl="http://www.metacritic.com/";
  //      refreshListview(responseStr);
    //    searchView=(SearchView)findViewById(R.id.search);
       // webView = (WebView) findViewById(R.id.web_view);
    //    webView.getSettings().setJavaScriptEnabled(true);
    //    webView.setWebViewClient(new WebViewClient());
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.leftmenu);
        }
        navView.setCheckedItem(nav_home);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                progressBar.setVisibility(View.VISIBLE);
                switch (item.getItemId()) {
                    case nav_home:
                        Log.d("homed", "homed");
                        HttpUtil.sendOkHttpRequest("http://www.metacritic.com/",okhttpcallback);
                        lastUrl="http://www.metacritic.com/";
                        break;
                    case nav_movies:
                        HttpUtil.sendOkHttpRequest("http://www.metacritic.com/browse/movies/release-date/theaters/date",okhttpcallback);
             //           HttpUtil.sendOkHttpRequest("http://www.metacritic.com/movie",okhttpcallback);
                        lastUrl="http://www.metacritic.com/movie";
                        break;
                    case nav_games:
                        HttpUtil.sendOkHttpRequest("http://www.metacritic.com/browse/games/release-date/new-releases/all/date",okhttpcallback);
                        lastUrl="http://www.metacritic.com/game";
                        break;
                    case nav_tv:
                        HttpUtil.sendOkHttpRequest("http://www.metacritic.com/browse/tv/release-date/new-series/date",okhttpcallback);
                        lastUrl="http://www.metacritic.com/tv";
                        break;
                    case nav_music:
                        HttpUtil.sendOkHttpRequest("http://www.metacritic.com/browse/albums/release-date/new-releases/date",okhttpcallback);
                        lastUrl="http://www.metacritic.com/music";
                        break;
                    case nav_history:
                        List history = new ArrayList();
                        history.addAll(clickHistory);
                        refreshListview(history);
                        progressBar.setVisibility(View.GONE);
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

    }
    private void refreshListview(final List itemList){
        if (itemList.size() == 0) {
            Toast.makeText(this,"No data for now!",Toast.LENGTH_SHORT).show();
            return;
        }

 //       HttpUtil.sendOkHttpRequest("http://www.metacritic.com/",okhttpcallback);
    //    Log.d("responsestr1",responseStr);
        //    textView.setText(responseStr);

        final ItemAdapter adapter = new ItemAdapter(MainActivity.this, R.layout.list_item, itemList);
     //   Log.d("adapter",""+adapter.isEmpty());
        listView.setAdapter(adapter);
    //    setListViewHeightBasedOnChildren(listView);
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height=listView.getMeasuredHeight();
//        params.height +=50;
//        //listView.getDividerHeight()获取子项间分隔符占用的高度
//        //params.height最后得到整个ListView完整显示需要的高度
//        listView.setLayoutParams(params);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listitem thisitem = (Listitem)itemList.get(position);
                clickHistory.add(thisitem);
           //     Toast.makeText(MainActivity.this,thisitem.getLinkUrl(),Toast.LENGTH_SHORT).show();
                if (thisitem.getLinkUrl().contains("person")) {
                    Toast.makeText(MainActivity.this,"Show this person's works",Toast.LENGTH_SHORT).show();
                    HttpUtil.sendOkHttpRequest(thisitem.getLinkUrl(),okhttpcallback);
                }else{
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("linkurl",thisitem.getLinkUrl());
                    intent.putExtra("title",thisitem.getTitle());
                    intent.putExtra("score", thisitem.getMetascore());
                    intent.putExtra("imgurl", thisitem.getImgUrl());
                    startActivity(intent);
                }
            }
        });

    }
    private void parseHtmlToList(String response){
        itemList.clear();
        Document doc = Jsoup.parse(responseStr);

        Elements items = doc.select("div.item");//选择器语法
        //       Log.d("doc", doc.text());
  //      Log.d("doclenth",""+responseStr.length());
        //       Elements items=doc.getElementsByClass("item"); //dom选择
   //     Log.d("itemsize",""+items.size());
//                    textView.setText(responseStr);
        for (Element item : items) {
            String itemTitle = item.getElementsByClass("title").text();
            String src="";
            String releaseDate="";
            String score="";
            String stats="";
            String linkurl = "";
            if (item.getElementsByTag("img").size()!=0) {
                src =item.getElementsByTag("img").get(0).attr("src");
            }
            if (item.getElementsByClass("release_date").size()!=0){
                releaseDate=item.getElementsByClass("release_date").get(0).getElementsByClass("data").text();
            }
            if (item.getElementsByClass("right").size() != 0) {
                score = item.getElementsByClass("right").text();
            }
            if (item.getElementsByClass("col_right").size() != 0) {
                score = item.getElementsByClass("col_right").text();
            }
            linkurl=item.select("a[href]").attr("href");
//            Log.d("linkurl", linkurl);


            if (item.getElementsByClass("stats").size() != 0) {
   //             Log.d("statssize", "" + item.getElementsByClass("stats").size());
                Element statselements=item.getElementsByClass("stats").get(0);


      //          Log.d("nodesize",""+statselements.child(0).child(0).child(0).siblingElements().size());
                StringBuilder statss=new StringBuilder();
                for(int i=0;i<statselements.child(0).child(0).child(0).siblingElements().size()+1;i++){
   //                 Log.d("nodei", "" + i);
                    String a=statselements.child(0).child(0).child(i).text();
                    statss.append(a+"\n");
                }
                stats=statss.toString();

 //               Log.d("stats",stats);
            }

            //      Log.d("statsleng", ""+firstItem.getElementsByClass("stats").size());
  //          Log.d("score",""+score);

            Listitem newitem=new Listitem(itemTitle);
            newitem.setImgUrl(src);
            newitem.setReleaseDate(releaseDate);
            newitem.setMetascore(score);
            newitem.setStats(stats);
            newitem.setLinkUrl("http://www.metacritic.com"+linkurl);
            Log.d("title",itemTitle);
            Log.d("linkurl", newitem.getLinkUrl());
            //  Log.d("iftbd",newitem.getMetascore());
            itemList.add(newitem);
            //
            if (itemList.size() > 50) {
                return;
            }
        }
//        if (doc.select("item first ").size()!=0){
//            Log.d("enter", "entered");
//            Element firstItem = doc.select("item first").get(0);
//            String firstitemTitle=firstItem.getElementsByClass("title").text();
//            //     String score = firstItem.getElementsByClass("right").text();
//            String stats=firstItem.getElementsByClass("stats").text();
//            Log.d("statsleng", ""+firstItem.getElementsByClass("stats").size());
//            Listitem newitem=new Listitem(firstitemTitle);
//            itemList.add(newitem);
//        }
//        Log.d("itemlistsize",""+itemList.size());

    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

//    private void sendRequest(){
//        try {
//            HttpUtil.sendOkHttpRequest("http://www.metacritic.com/movie",okhttpcallback);
//        }catch (Exception e){
//            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//            dialog.setTitle("SYSTEM ERROR");
//            dialog.setMessage("System error.Check you network");
//            dialog.setCancelable(false);
//            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//            dialog.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                }
//            });
//            dialog.show();
//        }
//    }
    private void SaveHistory(){
        try {
     //       String filePath = MainActivity.getFilesDir().getPath().toString() + "/fileName.txt";
            FileOutputStream fos = openFileOutput("Hislistdata", MODE_PRIVATE);
          //  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(clickHistory);
            out.flush();
            out.close();
        }catch (IOException e ){
            e.printStackTrace();
            Log.d("Savefailed", "savefailed");
        }
    }
    private void LoadHistory(){
        try {
            clickHistory.clear();
            FileInputStream inf = openFileInput("Hislistdata");
            ObjectInputStream in = new ObjectInputStream(inf);
//            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Hislistdata"));
            clickHistory = (HashSet<Listitem>) in.readObject();
            Log.d("loadhis", "OK");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("fileno", "filenofound");
      //      Toast.makeText(MainActivity.this, "No data now!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
