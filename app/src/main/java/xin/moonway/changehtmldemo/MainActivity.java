package xin.moonway.changehtmldemo;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    Button vip;
    Button showHtml;
    Button changeIntegral;
    public static final String LOGIN_URL = "https://login.m.taobao.com/login.htm?redirectURL=http%3A%2F%2Fmember1.taobao.com%2Fmember%2Ffresh%2Faccount_security.htm";
    public static final String VIP_URL = "https://h5.m.taobao.com/vip/home.html";
    public String htmlContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        vip = findViewById(R.id.vip);
        showHtml = findViewById(R.id.showHtml);
        changeIntegral = findViewById(R.id.changeIntegral);
        init();
    }

    public void init() {
        webView.getSettings().setDefaultTextEncodingName("UTF-8") ;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }


        });
        webView.loadUrl(LOGIN_URL);

        vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(VIP_URL);
            }
        });
        showHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                        + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });
        changeIntegral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(networkTask).start();
            }
        });
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            htmlContent = html;
            System.out.println("====>html=" + htmlContent);
        }
    }


    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Document document = Jsoup.parse(htmlContent);

            try {
                Elements elements = document.getElementsByClass("vip_growth");
                elements.get(0).text("99999");
                Log.d("html",  elements.get(0).text());

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", document.toString());
                msg.setData(data);
                handler.sendMessage(msg);

            }catch (Exception e){
                Log.e("elementError=",e.toString());
            }



        }
    };



        Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            webView.loadDataWithBaseURL("https://h5.m.taobao.com", val, "text/html", "utf-8", null);
//            webView.loadData(val, "text/html","UTF-8");
        }
    };



}
