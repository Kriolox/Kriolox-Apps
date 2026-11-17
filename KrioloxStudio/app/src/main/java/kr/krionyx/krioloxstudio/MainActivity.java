package kr.krionyx.krioloxstudio;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.webkit.*;
import android.content.Intent;
import android.net.Uri;
public class MainActivity extends Activity {
private WebView webView; private ValueCallback<Uri[]> fileCallback; private static final int FILE_CHOOSER_REQUEST=4102;
@Override public void onCreate(Bundle state){super.onCreate(state);webView=new WebView(this);if(Build.VERSION.SDK_INT>=26)webView.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);WebSettings s=webView.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setSaveFormData(true);s.setAllowFileAccess(true);s.setAllowContentAccess(true);webView.addJavascriptInterface(new AutofillBridge(),"KrioloxAutofill");webView.setWebViewClient(new WebViewClient());webView.setWebChromeClient(new WebChromeClient(){@Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams p){if(fileCallback!=null)fileCallback.onReceiveValue(null);fileCallback=cb;try{startActivityForResult(p.createIntent(),FILE_CHOOSER_REQUEST);}catch(Exception e){fileCallback=null;cb.onReceiveValue(null);return false;}return true;}});setContentView(webView);webView.loadUrl("file:///android_asset/index.html");}
private class AutofillBridge {@JavascriptInterface public void commit(){runOnUiThread(()->{if(Build.VERSION.SDK_INT>=26){AutofillManager manager=getSystemService(AutofillManager.class);if(manager!=null)manager.commit();}});}}
@Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(req==FILE_CHOOSER_REQUEST&&fileCallback!=null){fileCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res,data));fileCallback=null;}}
@Override public void onBackPressed(){if(webView!=null&&webView.canGoBack())webView.goBack();else super.onBackPressed();}
}