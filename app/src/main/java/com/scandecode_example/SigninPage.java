package com.scandecode_example;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;

import constants.Const;

public class SigninPage extends Activity {

    EditText et_id,et_pw;
    Button bt_login;
    String const_ip = "119.201.111.73:7778";
    //String const_ip = "124.194.93.51:7778";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

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

    }

    public void check_log_in(){
        String log_in_id,log_in_pw;
        log_in_id = et_id.getText().toString().toLowerCase();
        log_in_pw = et_pw.getText().toString();
        if(log_in_id == null || log_in_id.equals("") || log_in_pw == null || log_in_pw.equals("") ){
            Toast.makeText(this, "Please Check ID and Password", Toast.LENGTH_SHORT).show();
            bt_login.setEnabled(true);
        }else{
            ServerLogin id_check = new ServerLogin(log_in_id,log_in_pw);
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
                    startActivity(new Intent(SigninPage.this, MainActivity.class));
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
}
