/*
 * Created by Jay Khatri on 4/8/21 12:29 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 4/8/21 12:29 PM
 */

package com.digitalpathology.digi_report.api;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.NetworkOnMainThreadException;
import android.preference.Preference;
import android.util.Base64;
import android.util.Log;

import com.android.internal.http.multipart.ByteArrayPartSource;
import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String multipartRequest(String urlTo, Map<String, String> parmas, String filepath, String filefield, String fileMimeType) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            Log.d(TAG, "try");
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            Log.d(TAG, "try4");

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            Log.d(TAG, "try 1");

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            Log.d(TAG, "try2" + connection.getOutputStream());

            outputStream = new DataOutputStream(connection.getOutputStream());
            Log.d(TAG, "try5");
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            Log.d(TAG, "try6");
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            Log.d(TAG, "try7");
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
//            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
            Log.d(TAG, "try8");

            outputStream.writeBytes(lineEnd);

            Log.d(TAG, "try6");
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            Log.d(TAG, "all set");

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                Log.d(TAG, "started writing");
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
//            Iterator<String> keys = parmas.keySet().iterator();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                String value = parmas.get(key);
//
//                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
//                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
//                outputStream.writeBytes(lineEnd);
//                outputStream.writeBytes(value);
//                outputStream.writeBytes(lineEnd);
//                Log.d(TAG, "key: " + key);
//            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
                Log.d(TAG, "Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            Log.d(TAG, result);

            return result;
        } catch (NetworkOnMainThreadException e){
            Log.d(TAG, "here jay here");
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return null;
    }

//    public void uploadToServer(byte[] data) {
//        Bitmap bitmapOrg = BitmapFactory.decodeByteArray(data, 0, data.length);
//        ByteArrayOutputStream bao = new ByteArrayOutputStream();
//        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
//        byte[] ba = bao.toByteArray();
//        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
//        final ArrayList<NameValuePair> nameValuePairs = new
//                ArrayList<NameValuePair>();
//        nameValuePairs.add(new BasicNameValuePair("image", ba1));
//        Log.d(TAG, "here: " + ba1);
//        Thread t = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Log.d(TAG, "in try trying started");
//                    HttpClient httpclient = new DefaultHttpClient();
//                    HttpPost httppost = new
//                            HttpPost("http://100.26.239.59:8080/upload");
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//                    HttpResponse response = httpclient.execute(httppost);
//                    // HttpEntity entity = response.getEntity();
//                    String res = new BasicResponseHandler().handleResponse(response);
//                    Log.d(TAG, "response: " + res);
//                    // is = entity.getContent();
//                    // String the_string_response =
//                    // convertResponseToString(response);
//                    // Log.e("log_tag", "Image Uploaded  "+the_string_response);
//                } catch (Exception e) {
//                    Log.d(TAG, "Error in http connection " + e.getMessage());
//                }
//            }
//        };
//
//        t.start();
//    }

//    public static boolean uploadImage(final byte[] imageData, String filename ,String message) throws Exception{
//
//        String responseString = null;
//
//        PostMethod method;
//
//        String auth_token = Preference.getAuthToken(mContext);
//
//
//        method = new PostMethod("http://10.0.2.20/"+ "upload_image/" + Bitmap.Config.getApiVersion()
//                + "/"     +auth_token);
//
//        HttpClient client = new
//                org.apache.commons.httpclient.HttpClient();
//        client.getHttpConnectionManager().getParams().setConnectionTimeout(
//                100000);
//
//        FilePart photo = new FilePart("icon",
//                new ByteArrayPartSource( filename, imageData));
//
//        photo.setContentType("image/png");
//        photo.setCharSet(null);
//        String s    =   new String(imageData);
//        Part[] parts = {
//                new StringPart("message_text", message),
//                new StringPart("template_id","1"),
//                photo
//        };
//
//        method.setRequestEntity(new
//                MultipartRequestEntity(parts,     method.getParams()));
//        client.executeMethod(method);
//        responseString = method.getResponseBodyAsString();
//        method.releaseConnection();
//
//        Log.e("httpPost", "Response status: " + responseString);
//
//        if (responseString.equals("SUCCESS")) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    public static void postFile(String filePath, String postUrl,
//                                String pictureTitleStr, String pseudoTextStr)
//            throws Exception {
//
//        String url = postUrl;
//        HttpClient mHttpClient = new HttpClient() {
//            @Override
//            public HttpParams getParams() {
//                return null;
//            }
//
//            @Override
//            public ClientConnectionManager getConnectionManager() {
//                return null;
//            }
//
//            @Override
//            public HttpResponse execute(HttpUriRequest httpUriRequest) throws ClientProtocolException, IOException {
//                return null;
//            }
//
//            @Override
//            public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws ClientProtocolException, IOException {
//                return null;
//            }
//
//            @Override
//            public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws ClientProtocolException, IOException {
//                return null;
//            }
//
//            @Override
//            public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws ClientProtocolException, IOException {
//                return null;
//            }
//
//            @Override
//            public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws ClientProtocolException, IOException {
//                return null;
//            }
//
//            @Override
//            public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws ClientProtocolException, IOException {
//                return null;
//            }
//
//            @Override
//            public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws ClientProtocolException, IOException {
//                return null;
//            }
//
//            @Override
//            public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws ClientProtocolException, IOException {
//                return null;
//            }
//        };
//        HttpURLConnection conn = null;
//        final String CrLf = "\r\n";
//        JSONObject json = new JSONObject();
//        int bytesRead = 0;
//
//
//        String lineEnd = "\r\n";
//        String threeHyphens = "---";
//        String boundary = "WebKitFormBoundaryE19zNvXGzXaLvS5C";
//        String EndBoundary = "";
//        int maxBufferSize = 1 * 1024 * 1024;
//
//        HttpResponse response = null;
//
//        // Having HttpClient to respond to both HTTP and HTTPS url connection by accepting the urls along with keystore / trust certificates
//
//        try {
////            KeyStore trustStore = KeyStore.getInstance(KeyStore
////                    .getDefaultType());
////            trustStore.load(null, null);
////
////            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
////            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//            HttpParams params = new BasicHttpParams();
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
////            HttpProtocolParams.setUserAgent(params, "YourAppName/1.1");
//            HttpConnectionParams.setStaleCheckingEnabled(params, false);
//            HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
//            HttpConnectionParams.setSoTimeout(params, 20 * 1000);
//            HttpConnectionParams.setSocketBufferSize(params, 8192);
//            HttpClientParams.setRedirecting(params, false);
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("http", PlainSocketFactory
//                    .getSocketFactory(), 8080));
////            registry.register(new Scheme("https", sf, 443));
//
//            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
//                    params, registry);
//
//            mHttpClient = new DefaultHttpClient(ccm, params);
//
//        } catch (Exception e) {
//            Log.d(TAG, e.getMessage());
//        }
//
////        String base64EncodedCredentials = Base64.encodeToString((userName + ":" + password).getBytes("US-ASCII"),
////                Base64.DEFAULT);
////        System.out.println("Encoded Credit " + base64EncodedCredentials);
//
//        json.put("pseudo", pseudoTextStr);
//        json.put("title", pictureTitleStr);
//
//        String jsonStr = json.toString();
//        Log.d(TAG, "JSON VALUE  " + jsonStr);
//
//        URL url2 = new URL(postUrl);
//
//
//
//        Bitmap bm = BitmapFactory.decodeFile(filePath);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 25, baos); // bm is the bitmap object
//        byte[] b = baos.toByteArray();
//
//        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//
//
//        String str = threeHyphens + boundary + EndBoundary;
////        String str2 = "Content-Disposition: form-data; name=\"image\"";
////        String str3 = "Content-Type: application/json";
//        String str4 = "Content-Disposition: form-data; name=\"image\"; filename=\"" + filePath +"\"";
//        String str5 = "Content-Type: image/jpeg";
//        String str6 = threeHyphens + boundary + EndBoundary;
//
//
//
//        String StrTotal =str + "\r\n" + str4 + "\r\n" + str5 + "\r\n"+"\r\n" + "\r\n" + str6;
//
//        Log.d(TAG, "Multipart request string is:  \n"+StrTotal);
//
//        HttpPost post = new HttpPost(postUrl);
//
//
////        post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(
////                userName, password), "UTF-8", false));
//        post.addHeader("Content-Type","multipart/form-data;boundary="+boundary);
//        Log.d(TAG, "Sending Post proxy request: " + post.getMethod());
//
//        StringEntity se = new StringEntity(StrTotal);
//        se.setContentEncoding("UTF-8");
//        post.setEntity(se);
//        response = mHttpClient.execute(post);
//
//        /* Checking response */
//        int statusCode = response.getStatusLine().getStatusCode();
//        Log.d(TAG, "Http Execute finish " + statusCode);
//
//        HttpEntity entity = response.getEntity();
//        String getResponseText = entity.toString(); // EntityUtils.toString(entity);
//        Log.d(TAG, " Post Response Text from Server : "
//                + getResponseText);
//
//    }

}