package com.zijin.ibeacon.http;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.zijin.ibeacon.util.Utils;

public class HttpSession {
    private static final String CHARSET = "UTF-8";

    private DefaultHttpClient client;

    public HttpSession() {
        client = createHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
    }

    private DefaultHttpClient createHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);

            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, schReg);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public String get(AppRequest request) throws IOException {
        HttpGet get = new HttpGet(request.getmRequestURL() + request.toJson());
        return execute(get);
    }

    public String post(AppRequest request) throws IOException {
    	Log.i("HttpSession",request.getmRequestURL());
        HttpPost post = new HttpPost(request.getmRequestURL());
        if(AppRequest.UPIMG == request.getmRequestType()){
        	post= uploadSubmit(post,request);
        }else{
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if(request.getmParas()!=null && !request.getmParas().isEmpty()){
	            for(String key : request.getmParas().keySet())  
	            {  
	                params.add(new BasicNameValuePair(key , request.getmParas().get(key)));  
	            }  
	        	post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
            }
        }
            Header hedr = post.getEntity().getContentType();//Content-Type: application/x-www-form-urlencoded
        	String str = hedr.getName();//Content-Type
        	String str1 = hedr.getValue();//application/x-www-form-urlencoded
        	HttpEntity ent = post.getEntity();
        	byte[] buffer = new byte[1024]; 
//        	int context = ent.getContent().read(buffer);
//        	String strBuf = String.valueOf(buffer);
            return execute(post);
        	
    }
    public  HttpPost  uploadSubmit( HttpPost post,AppRequest request) throws IOException{  
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        Map<String,String> param = request.getmParas();
       ArrayList<File> files = request.getmUpFiles();
        if (param != null && !param.isEmpty()) {  
            for (Map.Entry<String, String> entry : param.entrySet()) {  
                if (entry.getValue() != null  && entry.getValue().trim().length() > 0) {
//                	String value = new String(entry.getValue().trim().getBytes(),"utf-8");
                    entity.addPart(entry.getKey(),new StringBody(entry.getValue().trim(),Charset.forName("UTF-8")));  
                }  
            }  
        }  
        //进行压缩
        File file=null;
        if(files!=null)
          file = Utils.ZipMultiFile(files);
        // 添加文件参数  
        if (file != null && file.exists()) {  
            if("android".equals(request.getPara("clientType"))){
            	entity.addPart("uploadimage",new FileBody(file));
            }else{
            	entity.addPart("image",new FileBody(file));
            }
        }
        post.setEntity(entity);
        return post;
    }
    
    private String execute(HttpUriRequest request) throws IOException {
        try {
        	/*RequestLine requestLine = request.getRequestLine();
        	String requestL = requestLine.toString();
        	String method = requestLine.getMethod();
        	String uriStr = requestLine.getUri();
        	ProtocolVersion version = requestLine.getProtocolVersion();
        	Log.i("info", "请求行的信息："+requestL);
        	Log.i("info", "获得请求方法："+method);
        	Log.i("info", "获得请求uri："+uriStr);
        	Log.i("info", "获得Http协议版本号："+version);
        	
        	HttpParams params = request.getParams();
        	String paramsStr = params.toString(); 
        	Log.i("info", paramsStr);*/
        	
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity, CHARSET);
            }
        }
        catch(SocketTimeoutException e){
        	throw new SocketTimeoutException("服务器连接超时");
        }
        
        catch (ConnectTimeoutException e){
        	e.printStackTrace();
        	throw new ConnectTimeoutException("网络不给力");
        } 
//        catch (NoHttpResponseException e){
//        	throw new NoHttpResponseException("服务器无响应");
//        }
        catch (IOException e) {
        	e.printStackTrace();
        	throw new IOException(e);
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        return "";
       
    }
}
