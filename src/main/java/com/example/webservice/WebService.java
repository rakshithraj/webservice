package com.example.webservice;

/**
 * Created by Rakshith on 8/6/2015.
 */
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;




public class WebService {

    public static int DEFAULT_TIMEOUT = 10000;
    public static String CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT";
    /**
     * set false if you want repose to server or true
     */
    boolean send = false;

    public WebService() {

    }

    /**
     * set false if you want repose to server or true
     *
     * @param send
     */
    public WebService(boolean send) {
        this.send = send;
    }

    public String executeRequest(HttpUriRequest request, int _timeout,
                                 String url) {

        String response = null;
        DefaultHttpClient client = getNewHttpClient();
        request.setHeader("User-Agent", "USER_AGENT");
        HttpResponse httpResponse;
        HttpEntity entity;
        try {

            httpResponse = client.execute(request);
            // String message = httpResponse.getStatusLine().getReasonPhrase();
            entity = httpResponse.getEntity();
            if (entity != null) {
                response = EntityUtils.toString(entity);
                return response;
            }
        } catch (SocketTimeoutException e) {

            client.getConnectionManager().shutdown();
            e.printStackTrace();
            response = CONNECTION_TIMEOUT;
        } catch (Exception e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
            response = null;
        } catch (OutOfMemoryError e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
            response = null;
        } finally {
            client.getConnectionManager().shutdown();
            client = null;
            request = null;
            httpResponse = null;
            entity = null;
        }
        return response;
    }

    private String execute(String url, String methodName, int _timeout,
                           RequestMethod methodType, String json) throws Exception {
        switch (methodType) {

            case PUT: {

                try {
                    HttpPut request = new HttpPut(url);

                    StringEntity se = new StringEntity(json);
                    request.setEntity(se);

                    request.setHeader("Accept", "application/json");
                    request.setHeader("Content-type", "application/json");

                    return executeRequest(request, _timeout, url);

                } catch (Exception e) {

                }
            }

            case GET: {

                HttpGet request = new HttpGet(url);
                return executeRequest(request, _timeout, url);
            }
            case POST: {

                try {
                    HttpPost request = new HttpPost(url);

                    StringEntity se = new StringEntity(json);
                    request.setEntity(se);

                    request.setHeader("Accept", "application/json");
                    request.setHeader("Content-type", "application/json");

                    return executeRequest(request, _timeout, url);

                } catch (Exception e) {

                }

            }
            default:
                return null;
        }
    }

    public String executePost(String url, String json) throws Exception {
        return execute(url, "", DEFAULT_TIMEOUT, RequestMethod.POST, json);
    }

    public String executeget(String url, String json) throws Exception {
        return execute(url, "", DEFAULT_TIMEOUT, RequestMethod.GET, json);
    }

    public enum RequestMethod {
        GET, POST, PUT
    }

    public DefaultHttpClient getNewHttpClient() {
        try {
            System.setProperty("http.keepAlive", "false");
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);
            org.apache.http.conn.ssl.SSLSocketFactory sf = new WebSSLSocketFactory(
                    trustStore);
            sf.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();

            // HttpConnectionParams.setConnectionTimeout(params, 3000);
            if (send)
                HttpConnectionParams.setSoTimeout(params, 1);
            // else
            // HttpConnectionParams.setSoTimeout(params,60000);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public String executePostReport(String url,
                                    Activity activity) {
        // TODO Auto-generated method stub
        MultipartEntity reqEntity;
        reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        FormBodyPart bodyPart;


        if(url!=null){
            File file;

            file = new File(url);
            try {
                file = Utilities.convertInToReqiuredOrientation(activity, file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bodyPart = new FormBodyPart("photo", new FileBody(file));
            reqEntity.addPart(bodyPart);
        }

        HttpPost postRequest = new HttpPost(url);
        postRequest.setEntity(reqEntity);
        String resp = null;
        resp = executeRequest(postRequest, 2000, url);

        return resp;
    }


    public String executePostReport(MultipartEntity reqEntity,String url,
                                    Activity activity) {
        // TODO Auto-generated method stub


        HttpPost postRequest = new HttpPost(url);
        postRequest.setEntity(reqEntity);
        String resp = null;
        resp = executeRequest(postRequest, 2000, url);

        return resp;
    }


    /**
     * execute post request
     * @param url
     * @param nameValuePairs
     * @return response from server as String
     */
    public String executePost(String url,List<NameValuePair> nameValuePairs) {
        // TODO Auto-generated method stub


        HttpPost request = new HttpPost(url);
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String resp=null;
        resp= executeRequest(request, DEFAULT_TIMEOUT, url);

        return resp;
    }



}
