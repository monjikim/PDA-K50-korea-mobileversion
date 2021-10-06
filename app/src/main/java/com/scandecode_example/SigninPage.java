package com.scandecode_example;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.scandecode_example.etc.SharedPreferenceManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constants.Const;

import static constants.Const.User_id;
import static constants.Const.User_pw;
import static constants.Const.all_pallet_types;

public class SigninPage extends Activity {

    EditText et_id,et_pw;
    Button bt_login;
    String token_value;
    String u_id,u_pw;
    String const_ip = "www.npc-iot.com:7778"; //aws 서버
    //String const_ip = "119.201.111.73:7778"; //영천 서버
    //String const_ip = "124.194.93.51:7778";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        //Camera permission
        checkDangerousPermissions();
        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);
        bt_login = (Button) findViewById(R.id.bt_login);

        et_id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return true;
                }
                return false;
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bt_login.isEnabled()) {
                    Toast.makeText(SigninPage.this, "Please wait, Log-in is processing", Toast.LENGTH_SHORT).show();
                }
                bt_login.setEnabled(false);
                check_log_in();
            }
        });

        u_id = User_id;
        u_id = SharedPreferenceManager.getString(getApplicationContext(),"USER_ID");
        if(u_id == ""){
            SharedPreferenceManager.setString(getApplicationContext(), "USER_ID", "");
            u_id = "";
        }
        else if(u_id == User_id){
            SharedPreferenceManager.setString(getApplicationContext(), "USER_ID", User_id);
        }
        u_pw = User_pw;
        u_pw = SharedPreferenceManager.getString(getApplicationContext(),"USER_PW");
        if(u_pw == ""){
            SharedPreferenceManager.setString(getApplicationContext(), "USER_PW", "");
            u_pw = "";
        }
        else if(u_pw == User_pw){
            SharedPreferenceManager.setString(getApplicationContext(), "USER_PW", User_pw);
        }
        et_id.setText(u_id);
        et_pw.setText(u_pw);

    }

    public void check_log_in(){
        String log_in_id,log_in_pw;
        log_in_id = et_id.getText().toString().toLowerCase();
        log_in_pw = et_pw.getText().toString();
        if(log_in_id == null || log_in_id.equals("") || log_in_pw == null || log_in_pw.equals("") ){
            Toast.makeText(this, "Please Check ID and Password", Toast.LENGTH_SHORT).show();
            bt_login.setEnabled(true);
        }else{
            AWSServerLogin id_check = new AWSServerLogin(log_in_id,log_in_pw);
            try {
                id_check.start();
            }catch (Exception e){
                Toast.makeText(this, "Device does not connected to Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ServerLogin extends Thread {
        String id;
        String pw;


        public ServerLogin(String user_id, String user_pw) {
            id = user_id;
            pw = user_pw;
        }

        public void run() {
            try {

                PrintStream ps = null;
                URL url = new URL("http://"+const_ip+"/npc_youngchun/from_korea_package/login_check.php");       // URL 설정
                Log.d("sw : ","http://"+const_ip+"/npc_youngchun/from_korea_package/login_check.php");
                URLConnection con = url.openConnection();   // 접속
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());
                String buffer = "";

                buffer += ("id") + ("=") + (id.toLowerCase()) + "&";            // 변수 구분은 '&' 사용
                buffer += ("pw") + ("=") + (pw.toLowerCase());           // 변수 구분은 '&' 사용
                Log.d("check", buffer);

                ps.print(buffer);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                InputStream in = con.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);

                String return_string = "";
                int data = isw.read();
                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    return_string += current;
                }
                Log.d("check", return_string);
                String final_return_string = return_string;
                Log.d("login_check ", "----------------------check Connectthread : " + final_return_string);
                if (!final_return_string.trim().equals("2")) {
                    Log.d("login_check", "----------------------check Connectthread - 1: YES!!!!!");
                    /*SharedPrefManager.getInstance(LoginPage.this).setUserID(id);
                    SharedPrefManager.getInstance(LoginPage.this).setUserPW(pw);
                    Const.ROLE = final_return_string;*/
                    Const.Country = final_return_string.trim();
                    startActivity(new Intent(SigninPage.this, SelectionPage.class));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bt_login.setEnabled(true);
                        }
                    });
                } else if(final_return_string.trim().equals("2")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SigninPage.this, "Please Check ID and Password", Toast.LENGTH_SHORT).show();
                            bt_login.setEnabled(true);
                        }
                    });
                }
                isw.close();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ps.flush();
                ps.close();
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SigninPage.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                        bt_login.setEnabled(true);
                    }
                });
                ex.printStackTrace();
            }
        }
    }
    class AWSServerLogin extends Thread {
        String id;
        String pw;


        public AWSServerLogin(String user_id, String user_pw) {
            id = user_id;
            pw = user_pw;
        }

        public void run() {
            try {

                PrintStream ps = null;
                URL url = new URL("http://www.npc-iot.com:1337/auth/local");       // URL 설정
                Log.d("sw : ","http://www.npc-iot.com:1337/auth/local");
                URLConnection con = url.openConnection();   // 접속
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());
                String buffer = "";

                /*buffer += ("id") + ("=") + (id) + "&";            // 변수 구분은 '&' 사용
                buffer += ("pw") + ("=") + (pw);           // 변수 구분은 '&' 사용*/
                buffer += ("identifier") + ("=") + (id) + "&";            // 변수 구분은 '&' 사용
                buffer += ("password") + ("=") + (pw);           // 변수 구분은 '&' 사용
                Log.d("check", buffer);

                ps.print(buffer);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                InputStream in = con.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);

                String return_string = "";
                int data = isw.read();
                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    return_string += current;
                }
                Log.d("check", return_string);
                String final_return_string = return_string;
                Log.d("login_check ", "----------------------check Connectthread : " + final_return_string.contains("jwt"));
//                if (final_return_string.trim().equals("1")) {
                if (final_return_string.contains("jwt") && final_return_string.contains("user")) {
                    JSONObject obj = new JSONObject(final_return_string);
                    token_value = obj.getString("jwt");
                    Const.Tokeninfo = token_value;
                    //token_value = final_return_string.substring();
                    Log.d("login_check", "//////////////////////////////////////////////JWT : "+token_value);
                    Log.d("login_check", "----------------------check Connectthread - 1: YES!!!!!");
                    /*SharedPrefManager.getInstance(LoginPage.this).setUserID(id);
                    SharedPrefManager.getInstance(LoginPage.this).setUserPW(pw);
                    Const.ROLE = final_return_string;*/
                    User_id = id;
                    String query = "query {\n" +
                            "  users(where: { username: \""+ User_id+"\" }) {\n" +
                            "    company {\n" +
                            "      name\n" +
                            "      id\n" +
                            "    }\n" +
                            "   id\n" +
                            "  }\n" +
                            "}\n";
                    LoginThread thread = new LoginThread(query);
                    try {
                        thread.start();
                    } catch (Exception e) {
                        Log.d("abc", "thread error ///////////////////////////// ");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bt_login.setEnabled(true);
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SigninPage.this, "Please Check ID and Password", Toast.LENGTH_SHORT).show();
                            bt_login.setEnabled(true);
                        }
                    });
                }
                isw.close();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ps.flush();
                ps.close();
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SigninPage.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                        bt_login.setEnabled(true);
                    }
                });
                ex.printStackTrace();
            }
        }
    }
    class LoginThread extends Thread {
        String rfid_tag;
        public LoginThread(String rfid) {
            rfid_tag = rfid;
        }
        public void run() {
            try {
                PrintStream ps = null;
                URL url = new URL("http://www.npc-iot.com:1337/graphql");       // URL 설정
                URLConnection con = url.openConnection();   // 접속
                con.setRequestProperty("Authorization","Bearer "+token_value);
                con.setRequestProperty("Method","POST");
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());
                String buffer = "";
                buffer += ("query") + ("=") + (rfid_tag);           // 변수 구분은 '&' 사용
                Log.d("check",buffer);

                ps.print(buffer);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                InputStream in = con.getInputStream();

                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String return_string = "";
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                return_string = total.toString();
                Log.d("check",return_string);

                JSONObject obj = new JSONObject(return_string);
                Const.company_id= obj.getJSONObject("data").getJSONArray("users").getJSONObject(0).getJSONObject("company").getString("id");
                Const.company_name= obj.getJSONObject("data").getJSONArray("users").getJSONObject(0).getJSONObject("company").getString("name");
                Const.User_id_no= obj.getJSONObject("data").getJSONArray("users").getJSONObject(0).getString("id");


                SharedPreferenceManager.setString(getApplicationContext(), "USER_ID", et_id.getText().toString());
                SharedPreferenceManager.setString(getApplicationContext(), "USER_PW", et_pw.getText().toString());

                String query2 = "query {\n" +
                        "   companies(where: {use_yn:1 type:35}) {\n" +
                        "    id\n" +
                        "    name\n" +
                        "    eng_name\n" +
                        "    country{\n" +
                        "      id\n" +
                        "    }\n" +
                        "  }\n" +
                        "}\n";
                LoginThread2 thread2 = new LoginThread2(query2);
                try {
                    thread2.start();
                } catch (Exception e) {
                    Log.d("abc", "thread error ///////////////////////////// ");
                }
//                startActivity(new Intent(SigninPage.this, SelectionPage.class));
                ps.flush();
                ps.close();

            } catch (Exception ex) {
                Toast.makeText(SigninPage.this, "Don't have permission to use this app", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        }
    }

    class LoginThread2 extends Thread {
        String rfid_tag;
        public LoginThread2(String rfid) {
            rfid_tag = rfid;
        }
        public void run() {
            try {
                PrintStream ps = null;
                URL url = new URL("http://www.npc-iot.com:1337/graphql");       // URL 설정
                URLConnection con = url.openConnection();   // 접속
                con.setRequestProperty("Authorization","Bearer "+token_value);
                con.setRequestProperty("Method","POST");
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());
                String buffer = "";
                buffer += ("query") + ("=") + (rfid_tag);           // 변수 구분은 '&' 사용
                Log.d("check2",buffer);

                ps.print(buffer);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                InputStream in = con.getInputStream();

                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String return_string = "";
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                return_string = total.toString();
                Log.d("check",return_string);

                JSONObject obj = new JSONObject(return_string);

                int export_company_count = 0;
                export_company_count = obj.getJSONObject("data").getJSONArray("companies").length();
                for(int i=0;i<export_company_count;i++){
                    try{
                        if(!obj.getJSONObject("data").getJSONArray("companies").getJSONObject(i).equals("null")){
                            Const.export_companies.put(obj.getJSONObject("data").getJSONArray("companies").getJSONObject(i).getString("name"),
                                    i+"@"+
                                            obj.getJSONObject("data").getJSONArray("companies").getJSONObject(i).getJSONObject("country").getString("id")+"@"+
                                            obj.getJSONObject("data").getJSONArray("companies").getJSONObject(i).getString("id")
                            );
                        }
                    }catch (Exception e){

                    }
                }
                Log.e("CHECK:3","export_companies : "+Const.export_companies);
                String query3 = "query {\n" +
                        "  rsbtypes(where: { use_yn: 1 }) {\n" +
                        "    code\n" +
                        "    id\n" +
                        "    unit\n" +
                        "    cunit\n" +
                        "  }"  +
                        "}\n";
                LoginThread3 thread = new LoginThread3(query3);
                try {
                    thread.start();
                } catch (Exception e) {
                    Log.d("abc", "thread error ///////////////////////////// ");
                }
                ps.flush();
                ps.close();

            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SigninPage.this, "Failed to load data, Please Log in again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    class LoginThread3 extends Thread {
        String rfid_tag;
        public LoginThread3(String rfid) {
            rfid_tag = rfid;
        }
        public void run() {
            try {
                PrintStream ps = null;
                URL url = new URL("http://www.npc-iot.com:1337/graphql");       // URL 설정
                URLConnection con = url.openConnection();   // 접속
                con.setRequestProperty("Authorization","Bearer "+token_value);
                con.setRequestProperty("Method","POST");
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());
                String buffer = "";
                buffer += ("query") + ("=") + (rfid_tag);           // 변수 구분은 '&' 사용
                Log.d("check3",buffer);

                ps.print(buffer);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                InputStream in = con.getInputStream();

                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String return_string = "";
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                return_string = total.toString();
                Log.d("check",return_string);

                JSONObject obj = new JSONObject(return_string);

                int type_count = 0;
                type_count = obj.getJSONObject("data").getJSONArray("rsbtypes").length();
                all_pallet_types.clear();
                for(int i=0;i<type_count;i++){
                    Const.pallet_types.put(obj.getJSONObject("data").getJSONArray("rsbtypes").getJSONObject(i).getString("code"),obj.getJSONObject("data").getJSONArray("rsbtypes").getJSONObject(i).getString("id"));
                    Const.all_pallet_types.add(obj.getJSONObject("data").getJSONArray("rsbtypes").getJSONObject(i).getString("code"));
                    Const.all_pallet_types_id.add(obj.getJSONObject("data").getJSONArray("rsbtypes").getJSONObject(i).getString("id"));
                    Const.all_pallet_types_unit.add(obj.getJSONObject("data").getJSONArray("rsbtypes").getJSONObject(i).getString("unit"));
                    Const.all_pallet_types_cunit.add(obj.getJSONObject("data").getJSONArray("rsbtypes").getJSONObject(i).getString("cunit"));
                }
                Log.e("TAG3333","///////"+Const.all_pallet_types.toString());
                startActivity(new Intent(SigninPage.this, SelectionPage.class));
                ps.flush();
                ps.close();

            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SigninPage.this, "Failed to load data, Please Log in again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
        };
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }
}
