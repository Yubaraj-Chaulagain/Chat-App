package np.com.fulkumari.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_CHOOSER = 1001;
    private static final String HOME_URL = "https://fulkumari.com.np/sheettoweb/App/Chat";

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);

        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){
                Uri u=r.getUrl(); String h=u.getHost();
                if(h!=null && (h.equals("fulkumari.com.np") || h.endsWith(".fulkumari.com.np"))) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW,u)); } catch(Exception ignored){}
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p){
                if(fileCallback!=null) fileCallback.onReceiveValue(null);
                fileCallback=cb;
                try { startActivityForResult(p.createIntent(),FILE_CHOOSER); return true; }
                catch(Exception e){ fileCallback=null; return false; }
            }
        });
        webView.loadUrl(HOME_URL);
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==FILE_CHOOSER){
            Uri[] r=null;
            if(resultCode==Activity.RESULT_OK && data!=null && data.getData()!=null) r=new Uri[]{data.getData()};
            if(fileCallback!=null) fileCallback.onReceiveValue(r);
            fileCallback=null;
        }
    }
    @Override public void onBackPressed(){
        if(webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
    @Override protected void onDestroy(){ if(webView!=null) webView.destroy(); super.onDestroy(); }
}
