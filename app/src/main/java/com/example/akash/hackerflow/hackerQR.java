package com.example.akash.hackerflow;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class hackerQR extends Activity {


    public final String twilioAPI = "https://api.twilio.com/2010-04-01/Accounts";
    public static final String ACCOUNT_SID = "ACCID";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public String hackerName;
    public String hackerPhone;
    public String hackerID;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hacker_qr);
        String hackerID = getIntent().getStringExtra("hackerID");
        ImageView imageView = (ImageView) findViewById(R.id.qr_image);
        int qrCodeDimention = 500;
        hackerName = getIntent().getStringExtra("hackerName");
        hackerPhone = getIntent().getStringExtra("hackerPhone");
        hackerID = getIntent().getStringExtra("hackerID");

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(hackerID, null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            imageView.setImageBitmap(bitmap);
            String tempAPI = "https://"+ACCOUNT_SID+":"+AUTH_TOKEN+"@api.twilio.com/2010-04-01/Accounts/"+ACCOUNT_SID+"/Messages";
            new AsyncTaskTwilio().execute(tempAPI);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void POST(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
            //String url = "https://AC163d176ec478d027313402a74a2f3902:1425bc2d5bf3c265062ba4157348926f@api.twilio.com/2010-04-01/Accounts/Messages";
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(ACCOUNT_SID, AUTH_TOKEN.toCharArray());
                }
            });

            Log.d("API", urlString);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("To", hackerPhone.trim()));
            params.add(new BasicNameValuePair("From", "+12175744238"));
            params.add(new BasicNameValuePair("Body", "Hey " + hackerName+"! You're all set! Thank you for registering!"));
            Log.d("Print", hackerPhone);
            Log.d("Print", hackerName);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            logConnection(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            if(pair.getValue().charAt(0)!='+')
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            else
                result.append(pair.getValue());
        }
        Log.d("Print", result.toString());
        return result.toString();

    }

    private static void logConnection(HttpURLConnection httpConnection) throws IOException {
        int status = httpConnection.getResponseCode();
        Log.d("logConnection", "status: " + status);
        for (Map.Entry<String, List<String>> header : httpConnection.getHeaderFields().entrySet()) {
            Log.d("logConnection", header.getKey() + "=" + header.getValue());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hacker_qr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncTaskTwilio extends AsyncTask<String, Void, String> {
        public AsyncTaskTwilio() {
        }

        @Override
        protected String doInBackground(String... params) {
            POST(params[0]);
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("API", "Done");
        }
    }
}

