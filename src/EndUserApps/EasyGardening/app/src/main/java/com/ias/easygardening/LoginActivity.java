package com.ias.easygardening;

import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends ActionBarActivity {

    EditText username , password;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        submit = (Button) findViewById(R.id.submitbutton);
        submit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here

                String uname=username.getText().toString();
                String passwd=password.getText().toString();

                if(!isValidString(uname)){
                    username.setError("Invalid username (min length 6 characters)");
                    return; }

                if(!isValidString(uname)) {
                    password.setError("Invalid password (min length 6 characters)");
                    return;
                }



                String loginURL=StartActivity.IPaddr+"/authenticateUser";
                String postParameters="";

                HttpClient httpclient = new DefaultHttpClient();
               // httpclient = AndroidHttpClient.newInstance("Android");

                try {
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("userName", uname);
                    jsonobj.put("password", passwd);
                    jsonobj.put("lat",22.45);
                    jsonobj.put("lon",24.45);


                    HttpPost httppostreq = new HttpPost(loginURL);
                    StringEntity se = new StringEntity(jsonobj.toString());
                    se.setContentType("application/json;charset=UTF-8");
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
                    httppostreq.setEntity(se);
                    Log.d("request is" , "being sent");
                    HttpResponse httpresponse=null ;
                    httpresponse= httpclient.execute(httppostreq);
                    Log.d("request is" , "sent");
                    String responseText = "";

                    responseText="{\n" +
                            "\tisAuthenticated:true,\n" +
                            "\tTemperature : {\n" +
                            "\t\tvalue : 44.45,\n" +
                            "\t\tunit : 'Celsius'\n" +
                            "\t},\n" +
                            "\tHumidity : {\n" +
                            "\t\tvalue : 70.22,\n" +
                            "\t\tunit : 'Percentage'\n" +
                            "\t}\n" +
                            "}";

                    responseText = EntityUtils.toString(httpresponse.getEntity());
                    Log.d("Response received:" , responseText);

                    JSONObject resjsonobj = new JSONObject(responseText);




                    if(resjsonobj.getBoolean("isAuthenticated")) {
                        StartActivity.accesskey=resjsonobj.get("accessKey").toString();
                        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                        intent.putExtra("sensordata", responseText);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(LoginActivity.this, "Invalid username or Password", Toast.LENGTH_LONG).show();

                }
                catch(Exception e){
                    e.printStackTrace();
                }



            }
        });
    }

    private boolean isValidString(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
