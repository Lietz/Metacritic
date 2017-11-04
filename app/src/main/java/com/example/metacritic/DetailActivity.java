package com.example.metacritic;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.metacritic.MainActivity.UPDATE_RESPONSE;
import static com.example.metacritic.MainActivity.itemList;


public class DetailActivity extends AppCompatActivity {

    public static final int UPDATE_RESPONSE=1;
    private String score;
    private String summary;
    private String moredetail;
    private TextView moredetailText;
    private TextView summaryText;
    private TextView metascoreView;
    private TextView basedview;
    private TextView seeallcriticTextview;
    private String detail;
    private String based;
    private String allcriticlink;
    private ListViewForScrollView criticlistview;
    private List<Critic> criticlist = new ArrayList<>();
    private String responseStr = new String();
    private static okhttp3.Callback okhttpcallback;

    private void setView(){
        if (seeallcriticTextview.getText().toString().contains("see all Critic Reviews >>")){
            parseHtml(responseStr);
        } else{
            parseAllcriticHtml(responseStr);
        }
        summaryText.setText(summary);
        moredetailText.setText(detail);
        metascoreView.setText(score);
        if (score.isEmpty()) {
            metascoreView.setBackgroundColor(Color.WHITE);
        }else if (score.contains("tbd")){   //==对字符串有待商榷~
            metascoreView.setBackgroundColor(Color.GRAY);
        } else if (new Integer(score) >60){
            metascoreView.setBackgroundColor(Color.GREEN);
        }else if (new Integer(score)<=40){
            metascoreView.setBackgroundColor(Color.RED);
        }else {
            metascoreView.setBackgroundColor(Color.parseColor("#fff700"));
        }

        if (!based.isEmpty()) {
            basedview.setText("Metascore based on " + based + ":");
        } else {
            basedview.setText("No reviews currently available");
            seeallcriticTextview.setVisibility(View.GONE);
        }
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.detail_progressbar);
        progressBar.setVisibility(View.GONE);
        CriticAdapter adapter = new CriticAdapter(DetailActivity.this, R.layout.list_critic_item, criticlist);
        criticlistview.setAdapter(adapter);
        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.critic_scrollview);
  //      setListViewHeightBasedOnChildren(criticlistview,criticlist);
   //     criticlistview.setMinimumHeight(criticlistview.getLayoutParams().height+200);
        Log.d("listviewheight", ""+criticlistview.getLayoutParams().height);
        //     criticlistview.getLayoutParams().height
//        criticlistview.setFocusable(false);
  //      LinearLayout detaillinearlayout = (LinearLayout) findViewById(R.id.critic_linearlayout);
    //    detaillinearlayout.setMinimumHeight(criticlistview.getLayoutParams().height+200);
   //     scrollView.setMinimumHeight(criticlistview.getLayoutParams().height+200);
    //    CardView criticcardview = (CardView) findViewById(R.id.critic_cardview);
   //     criticcardview.setMinimumHeight(criticlistview.getLayoutParams().height);
      //  criticcardview.set
     //   ScrollView innerscroll = (ScrollView) findViewById(R.id.inner_critic_scroview);
       // innerscroll.setMinimumHeight(criticcardview.getHeight());

    //    Log.d("cardviewheight", "" + criticcardview.getHeight());
//        criticcardview.setFocusable(false);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
               scrollView.scrollTo(0, 0);
            }
        });

    //    scrollView.scrollTo(0,0);
//        basedview.requestFocus();
       // basedview.getFoc
      //  criticcardview.setMinimumHeight();
        if (allcriticlink.isEmpty()) {
            seeallcriticTextview.setVisibility(View.INVISIBLE);
            seeallcriticTextview.setClickable(false);
        }
        if (seeallcriticTextview.getText().toString().contains("see all Critic Reviews >>")){
            seeallcriticTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seeallcriticTextview.setText("Loading...");
                    //       seeallcriticTextview.setVisibility(View.INVISIBLE);
                    HttpUtil.sendOkHttpRequest(allcriticlink,okhttpcallback);
                }
            });
        }else {
            seeallcriticTextview.setText("Back to top");
            seeallcriticTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollView.scrollTo(0,0);
                }
            });

        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_RESPONSE:
                    responseStr=(String)msg.obj;
 //                   Log.d("handlerresponse", responseStr);
                    setView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
        score=intent.getStringExtra("score");
        String imgUrl=intent.getStringExtra("imgurl");
        String linkUrl=intent.getStringExtra("linkurl");
        basedview = (TextView) findViewById(R.id.basedon);
        seeallcriticTextview= (TextView) findViewById(R.id.see_all_reviews);
        seeallcriticTextview.setVisibility(View.VISIBLE);
        criticlistview = (ListViewForScrollView) findViewById(R.id.listview_critic);


        okhttpcallback=new okhttp3.Callback(){

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
//                Log.d("responseData",responseData);
                //     responseStr=responseData;
                Message msg = new Message();
                msg.what = UPDATE_RESPONSE;
                msg.obj = responseData;
                mhandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                seeallcriticTextview.setVisibility(View.VISIBLE);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
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
        HttpUtil.sendOkHttpRequest(linkUrl,okhttpcallback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView detailimageView = (ImageView) findViewById(R.id.detail_image_view);
        summaryText = (TextView) findViewById(R.id.summary_text);
        metascoreView = (TextView) findViewById(R.id.detail_metascore);
        moredetailText = (TextView) findViewById(R.id.more_detail_text);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
       // metascoreView.requestFocus();


        collapsingToolbar.setTitle(title);
       // Glide.with(this).load(imgUrl).into(detailimageView);
        Glide.with(this)
                .load(imgUrl)
                .dontAnimate()
                .bitmapTransform(new BlurTransformation(this, 2, 1))
                .into(detailimageView);


    }
    private void parseHtml(String response){
        Document doc = Jsoup.parse(response);

        Elements items = doc.select("div.product_details");
        if (doc.getElementsByClass("based").size() != 0) {
            based = doc.getElementsByClass("based").get(0).child(1).text();
        } else {
            based = "";
        }
        Log.d("basedon",based);
        summary=items.get(0).child(0).text();
        //moredetail

        StringBuilder details=new StringBuilder();
   //     String a1=items.get(0).child(1).child(1).text();
  //      Log.d("Astr",a1);
       // details.append(a+"\n");
        for(int i=0;i<items.get(0).child(1).child(0).siblingElements().size()+1;i++){
            Log.d("nodei", "" + i);
            String a=items.get(0).child(1).child(i).text();
            Log.d("Astr",a);
           details.append(a+"\n");
        }
        detail=details.toString();
        detail = detail.substring(0,detail.length()-1);
        //       Log.d("doc", doc.text());
 //       Log.d("doclenth",""+response.length());
        //       Elements items=doc.getElementsByClass("item"); //dom选择
        Log.d("itemsize",""+items.size());
        //分类
        Elements critics;
        if (doc.getElementsByClass("review critic two_source").size()!=0){    //电影
            critics = doc.getElementsByClass("review critic two_source");
        } else{
            critics = doc.getElementsByClass("review critic");
        }
        Log.d("criticssize", "" + critics.size());
        if (doc.getElementsByClass("critic reviews").get(0).
                getElementsByClass("section_footer std_blue").size() != 0) {
            allcriticlink = doc.getElementsByClass("critic reviews").get(0).
                    getElementsByClass("section_footer std_blue").get(0).child(0).attr("href");
            allcriticlink = "http://www.metacritic.com" + allcriticlink;
            Log.d("criticlink", allcriticlink);
        } else {
            allcriticlink="";
        }
        for (Element item : critics) {
            Critic critic=new Critic();
            String itsscore=item.getElementsByClass("review_grade left").get(0).text();
            String source=item.getElementsByClass("source").text();
            String author=item.getElementsByClass("label").text();
            String date = item.getElementsByClass("date").text();
            String summary=item.getElementsByClass("clr summary").text();
            //         Log.d("summary",summary);
            critic.setAuthor(author);
            critic.setDate(date);
            critic.setScore(itsscore);
            critic.setSource(source);
            critic.setSummary(summary);
            criticlist.add(critic);
        }

//                    textView.setText(responseStr);
//        for (Element item : items) {
//            String itemTitle = item.getElementsByClass("title").text();
//            String src="";
//            String releaseDate="";
//            String score="";
//            String stats="";
//            String linkurl = "";
//            if (item.getElementsByTag("img").size()!=0) {
//                src =item.getElementsByTag("img").get(0).attr("src");
//            }
//            if (item.getElementsByClass("release_date").size()!=0){
//                releaseDate=item.getElementsByClass("release_date").get(0).getElementsByClass("data").text();
//            }
//            if (item.getElementsByClass("right").size() != 0) {
//                score = item.getElementsByClass("right").text();
//            }
//            if (item.getElementsByClass("col_right").size() != 0) {
//                score = item.getElementsByClass("col_right").text();
//            }
//            linkurl=item.select("a[href]").attr("href");
////            Log.d("linkurl", linkurl);
//
//
//            if (item.getElementsByClass("stats").size() != 0) {
//                Log.d("statssize", "" + item.getElementsByClass("stats").size());
//                Element statselements=item.getElementsByClass("stats").get(0);
//
//
//                Log.d("nodesize",""+statselements.child(0).child(0).child(0).siblingElements().size());
//                StringBuilder statss=new StringBuilder();
//                for(int i=0;i<statselements.child(0).child(0).child(0).siblingElements().size()+1;i++){
//                    Log.d("nodei", "" + i);
//                    String a=statselements.child(0).child(0).child(i).text();
//                    statss.append(a+"\n");
//                }
//                stats=statss.toString();
//
//                Log.d("stats",stats);
//            }
//
//            //      Log.d("statsleng", ""+firstItem.getElementsByClass("stats").size());
//            Log.d("score",""+score);
//
//            Listitem newitem=new Listitem(itemTitle);
//            newitem.setImgUrl(src);
//            newitem.setReleaseDate(releaseDate);
//            newitem.setMetascore(score);
//            newitem.setStats(stats);
//            newitem.setLinkUrl("http://www.metacritic.com"+linkurl);
//            Log.d("title",itemTitle);
//            Log.d("linkurl", newitem.getLinkUrl());
//            //  Log.d("iftbd",newitem.getMetascore());
//            itemList.add(newitem);
//        }
////        if (doc.select("item first ").size()!=0){
////            Log.d("enter", "entered");
////            Element firstItem = doc.select("item first").get(0);
////            String firstitemTitle=firstItem.getElementsByClass("title").text();
////            //     String score = firstItem.getElementsByClass("right").text();
////            String stats=firstItem.getElementsByClass("stats").text();
////            Log.d("statsleng", ""+firstItem.getElementsByClass("stats").size());
////            Listitem newitem=new Listitem(firstitemTitle);
////            itemList.add(newitem);
////        }
//        Log.d("itemlistsize",""+itemList.size());

    }
    private void parseAllcriticHtml(String response){
        Document doc = Jsoup.parse(response);
        Elements critics;
        if (doc.getElementsByClass("review critic two_source").size()!=0){    //电影
            critics = doc.getElementsByClass("review critic two_source");
        } else{
            critics = doc.getElementsByClass("review critic");
        }
        Log.d("criticssize", "" + critics.size());
//        allcriticlink = doc.getElementsByClass("critic reviews").get(0).
//                getElementsByClass("section_footer std_blue").get(0).child(0).attr("href");
//        allcriticlink="http://www.metacritic.com"+allcriticlink;
//        Log.d("criticlink", allcriticlink);
        for (Element item : critics) {
            Critic critic=new Critic();
            String itsscore=item.getElementsByClass("review_grade left").get(0).text();
            String source=item.getElementsByClass("source").text();
            String author=item.getElementsByClass("label").text();
            String date = item.getElementsByClass("date").text();
            String summary=item.getElementsByClass("clr summary").text();
            //         Log.d("summary",summary);
            critic.setAuthor(author);
            critic.setDate(date);
            critic.setScore(itsscore);
            critic.setSource(source);
            critic.setSummary(summary);
            criticlist.add(critic);

    }}
    public static void setListViewHeightBasedOnChildren(ListView listView,List itemlist) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            Critic critic=(Critic)itemlist.get(i);
            int summarylines=critic.getSummary().length()/50;
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight +=(listItem.getMeasuredHeight()+(summarylines)*30); // 统计所有子项的总高度
            Log.d("itemheight", ""+(listItem.getMeasuredHeight()+(summarylines)*30));
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 200 ;
        Log.d("height", ""+params.height);
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
 // 动态改变listView的高度

}
