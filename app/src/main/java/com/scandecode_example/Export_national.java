package com.scandecode_example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.scandecode_example.etc.SharedPrefManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import constants.Const;

public class Export_national extends Activity {

    //ui
    private TextView mTvResult = null;
    private EditText edSymNum = null;
    private EditText edValNum = null;

    //sw ui
    TextView menu_title;
    RadioGroup Radio_type = null;
    RadioGroup Radio_type2 = null;
    Button manual_input_data = null;
    Button manual_input_data2 = null;
    Button btnClear= null;
    Button btnSend = null;
    private EditText ipAddress_et1,ipAddress_et2,ipAddress_et3,ipAddress_et4;
    EditText Serial_no,container_no,seal_no;
    RelativeLayout rlPopupSetupPrice;
    RelativeLayout rlPopupManual;
    Button btn_setting_save,btn_setting_cancel;
    Button btn_add_serial = null;
    Button btn_add_cancel = null;
    TextView tv_scan_no = null;
    Button btn_go_to_menu,btn_start_scan,btn_stop_scan;

    //sw value
    String user_id = Const.User_id;
    //String user_pw = SharedPrefManager.getInstance(this).getUserPW();
    String const_ip = "www.npc-rental.com:7778";
    Context mContext;
    PDAExportThread thread = new PDAExportThread("");
    ArrayList<Object> arr = new ArrayList<Object>();
    ArrayList<Object> Bufarr = new ArrayList<Object>();
    String a;
    Spinner target_numb,target_numb2,target_numb3;
    int NumbOfScan;
    int NumbToServer;
    Boolean arr_check = false;
    Boolean send_checker = false;
    String destination = "";
    String sending_con_num = "";
    String sending_seal_num = "";
    LinearLayout llsame_country,lldiff_country,lldiff_country2;

    private final static String TAG = "export_international";
    //private IScannerServiceHoneywell mHService = null;
    private ScanInterface scanDecode;
    int currentApiVersion;

    String error_result = "";
    String export_company = "";
    boolean same_country = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_export_national);
        currentApiVersion = Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务
        mainScreen();

        btn_go_to_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Export_national.this);
                alert_confirm.setMessage("Go back to Main Menu").setCancelable(false).setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });
        try{
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(target_numb);
            // Set popupWindow height to 500px
            popupWindow.setHeight(600);
            popupWindow.setWidth(1500);
        }catch (NoClassDefFoundError e) {
        }catch (ClassCastException e ){
        }catch (NoSuchFieldException e ){
        }catch (IllegalAccessException e){
        }
        target_numb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        try{
            Field popup2 = Spinner.class.getDeclaredField("mPopup2");
            popup2.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow2 = (android.widget.ListPopupWindow) popup2.get(target_numb2);
            // Set popupWindow height to 500px
            //int width = mContext.getResources().getDimensionPixelSize(R.dimen.overflow_width);
            //popupWindow2.setWidth(200);
            popupWindow2.setHeight(600);
            popupWindow2.setWidth(2500);
        }catch (NoClassDefFoundError e) {
        }catch (ClassCastException e ){
        }catch (NoSuchFieldException e ){
        }catch (IllegalAccessException e){
        }
        menu_title.setText(getResources().getString(R.string.export_rsb));
        target_numb2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPrefManager.getInstance(Export_national.this).setNationalexport_spinner(String.valueOf(i));
                Const.ExportNationalSpinner = String.valueOf(i);
                target_numb2.setSelection(i);
                target_numb3.setSelection(i);
/*
                if(Const.Language.equals("ko")){
                    if(Const.export_companies.get(target_numb2.getSelectedItem().toString()).split("@")[0].equals(Const.User_country_id)){
                        same_country = true;
                        llsame_country.setVisibility(View.VISIBLE);
                        lldiff_country.setVisibility(View.GONE);
                        lldiff_country2.setVisibility(View.GONE);
                    }else{
                        same_country = false;
                        llsame_country.setVisibility(View.GONE);
                        lldiff_country.setVisibility(View.VISIBLE);
                        lldiff_country2.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(Const.export_companies_eng.get(target_numb2.getSelectedItem().toString()).split("@")[0].equals(Const.User_country_id)){
                        same_country = true;
                        llsame_country.setVisibility(View.VISIBLE);
                        lldiff_country.setVisibility(View.GONE);
                        lldiff_country2.setVisibility(View.GONE);
                    }else{
                        same_country = false;
                        llsame_country.setVisibility(View.GONE);
                        lldiff_country.setVisibility(View.VISIBLE);
                        lldiff_country2.setVisibility(View.VISIBLE);
                    }
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        target_numb3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPrefManager.getInstance(Export_national.this).setNationalexport_spinner(String.valueOf(i));
                Const.ExportNationalSpinner = String.valueOf(i);
                target_numb2.setSelection(i);
                target_numb3.setSelection(i);
/*
                if(Const.Language.equals("ko")){
                    if(Const.export_companies.get(target_numb3.getSelectedItem().toString()).split("@")[0].equals(Const.User_country_id)){
                        same_country = true;
                        llsame_country.setVisibility(View.VISIBLE);
                        lldiff_country.setVisibility(View.GONE);
                        lldiff_country2.setVisibility(View.GONE);
                    }else{
                        same_country = false;
                        llsame_country.setVisibility(View.GONE);
                        lldiff_country.setVisibility(View.VISIBLE);
                        lldiff_country2.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(Const.export_companies_eng.get(target_numb3.getSelectedItem().toString()).split("@")[0].equals(Const.User_country_id)){
                        same_country = true;
                        llsame_country.setVisibility(View.VISIBLE);
                        lldiff_country.setVisibility(View.GONE);
                        lldiff_country2.setVisibility(View.GONE);
                    }else{
                        same_country = false;
                        llsame_country.setVisibility(View.GONE);
                        lldiff_country.setVisibility(View.VISIBLE);
                        lldiff_country2.setVisibility(View.VISIBLE);
                    }
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Log.i("ScannerTest","onCreate");

        btn_start_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seal_no.clearFocus();
                container_no.clearFocus();
                btn_start_scan.setEnabled(false);
                startScan();
            }
        });
        btn_stop_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start_scan.setEnabled(true);
                handler.removeCallbacks(startTask);
                scanDecode.stopScan();
            }
        });

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {
                data = data.trim();
                if (data != null) {
                    if(arr.size()==0){
                        mTvResult.setText("");
                    }
                    NumbOfScan = 999;
                    a = data.trim();
                    if(a.split(" ")[1].length() != 6){
                        arr_check = true;
                        Toast.makeText(mContext, "Please Check Serial No.", Toast.LENGTH_SHORT).show();
                    }else if(a.split(" ")[0].equals("KD-02")){
                        String tmp_serial = a.split(" ")[1];
                        a = "KD-L02 "+tmp_serial;
                    }
                    for (int i = 0; i < arr.size(); i++) {
                        if (arr.get(i).equals(a)) {
                            arr_check = true;
                        }
                    }
                    if (arr.size() < NumbOfScan) {
                        int textadding = 0;
                        if (!arr_check) {
                            arr.add(a);
                            textadding = arr.size() - 1;
                            if (textadding < NumbOfScan - 1) {
                                mTvResult.append("" + arr.get(textadding).toString() + "   >>   [ ");
                                mTvResult.append(String.format("%3d", textadding + 1));
//								mTvResult.append("/");
//								mTvResult.append(String.format("%2d", NumbOfScan));
                                mTvResult.append(" ] Scanned\n");
                            } else {
                                mTvResult.append("" + arr.get(arr.size() - 1).toString() + "   >>   [ ");
                                mTvResult.append(String.format("%3d", textadding + 1));
                                //mTvResult.append("/");
                                //mTvResult.append(String.format("%2d", NumbOfScan));
                                mTvResult.append(" ] Done\n");
                            }
                            tv_scan_no.setText(""+(textadding+1));
                        } else {

                        }
                    } else {
                        //Intent intent = new Intent(ConstantValues.SCANNER_ACTION_START, null);
                        //mContext.sendOrderedBroadcast(intent, null);
                    }

                    final ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            scroll.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    };
                    scroll.post(runnable);
                    arr_check = false;
                    //mTvResult.append(barcode+"\n");

                } else {
                    Toast.makeText(getApplicationContext(),"Error code - 0010", Toast.LENGTH_SHORT).show();
                }
                startScan();
            }

            @Override
            public void getBarcodeByte(byte[] bytes) {
                //返回原始解码数据
//                scancount+=1;
//                tvcound.setText(getString(R.string.scan_time)+scancount+"");
//                mReception.append(DataConversionUtils.byteArrayToString(bytes) +"\n");
            }
        });

        container_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 4){
                    container_no.setInputType(InputType.TYPE_CLASS_TEXT);
                    container_no.setSelection(container_no.getText().length());
                }
                if(s.length() == 4){
                    container_no.setInputType(InputType.TYPE_CLASS_NUMBER);
                    container_no.setSelection(container_no.getText().length());
                }
                if(s.length() == 11){
                    int number = 0;
                    int checking_number = 0;
                    int last_number = -1;
                    String disecting_character = "";
                    String calculating = container_no.getText().toString();
                    for(int i = 0; i<10;i++){
                        disecting_character = ""+calculating.charAt(i);
                        switch (disecting_character){
                            case "a":
                            case "A": number += Math.pow(2,i)*10;break;
                            case "b":
                            case "B": number += Math.pow(2,i)*12;break;
                            case "c":
                            case "C": number += Math.pow(2,i)*13;break;
                            case "d":
                            case "D": number += Math.pow(2,i)*14;break;
                            case "e":
                            case "E": number += Math.pow(2,i)*15;break;
                            case "f":
                            case "F": number += Math.pow(2,i)*16;break;
                            case "g":
                            case "G": number += Math.pow(2,i)*17;break;
                            case "h":
                            case "H": number += Math.pow(2,i)*18;break;
                            case "i":
                            case "I": number += Math.pow(2,i)*19;break;
                            case "j":
                            case "J": number += Math.pow(2,i)*20;break;
                            case "k":
                            case "K": number += Math.pow(2,i)*21;break;
                            case "l":
                            case "L": number += Math.pow(2,i)*23;break;
                            case "m":
                            case "M": number += Math.pow(2,i)*24;break;
                            case "n":
                            case "N": number += Math.pow(2,i)*25;break;
                            case "o":
                            case "O": number += Math.pow(2,i)*26;break;
                            case "p":
                            case "P": number += Math.pow(2,i)*27;break;
                            case "q":
                            case "Q": number += Math.pow(2,i)*28;break;
                            case "r":
                            case "R": number += Math.pow(2,i)*29;break;
                            case "s":
                            case "S": number += Math.pow(2,i)*30;break;
                            case "t":
                            case "T": number += Math.pow(2,i)*31;break;
                            case "u":
                            case "U": number += Math.pow(2,i)*32;break;
                            case "v":
                            case "V": number += Math.pow(2,i)*34;break;
                            case "w":
                            case "W": number += Math.pow(2,i)*35;break;
                            case "x":
                            case "X": number += Math.pow(2,i)*36;break;
                            case "y":
                            case "Y": number += Math.pow(2,i)*37;break;
                            case "z":
                            case "Z": number += Math.pow(2,i)*38;break;
                            default: number += Math.pow(2,i)* Integer.parseInt(disecting_character);break;
                        }
                    }
                    last_number = Integer.parseInt(""+calculating.charAt(10));
                    checking_number = number/11;
                    if(number-(checking_number*11) == last_number || (number-(checking_number*11) == 10 && last_number == 0)){

                    }else{
                        String text = container_no.getText().toString();
                        container_no.setText(text.substring(0, text.length() - 1));
                        container_no.setSelection(container_no.getText().length());
                        Toast.makeText(mContext, "Wrong Container No. Please check.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable arg0) {

                // 입력이 끝났을 때

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // 입력하기 전에

            }
        });
        seal_no.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    seal_no.clearFocus();
                    hideKeyboard(v);
                    return true; //this is required to stop sending key event to parent
                }
                return false;
            }
        });

        btn_add_serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "ok";
                //adding serial no to list.
                int id = Radio_type.getCheckedRadioButtonId();
                int id2 = Radio_type2.getCheckedRadioButtonId();
                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                RadioButton rb = (RadioButton) findViewById(id);
                RadioButton rb2 = (RadioButton) findViewById(id2);
                int check_rb = id == -1 ? id2 : id;

                String check_serial = Serial_no.getText().toString().trim();
                if(check_serial.length() != 6){
                    Toast.makeText(mContext, "Serial 번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(arr.size()==0){
                        mTvResult.setText("");
                    }
                    String numb = target_numb.getSelectedItem().toString().trim();
                    if(numb.equals("")){
                        NumbOfScan = 0;
                    }else{
                        NumbOfScan = Integer.parseInt(target_numb.getSelectedItem().toString().trim());
                    }

                    if(check_rb == id){
                        a = rb.getText().toString().trim()+" "+check_serial;
                    }else if(check_rb == id2){
                        a = rb2.getText().toString().trim()+" "+check_serial;
                    }

                    for (int i = 0; i < arr.size(); i++) {
                        if (arr.get(i).equals(a)) {
                            arr_check = true;
                        }
                    }

                    if (arr.size() < NumbOfScan) {
                        int textadding = 0;
                        if (!arr_check) {

                            arr.add(a);

                            //Vibrator vibrated = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            //vibrated.vibrate(50);

                            textadding = arr.size() - 1;
                            if (textadding < NumbOfScan - 1) {
                                mTvResult.append("" + arr.get(textadding).toString() + "   >>   [ ");
                                mTvResult.append(String.format("%3d", textadding + 1));
                                //mTvResult.append("/");
                                //mTvResult.append(String.format("%2d", NumbOfScan));
                                mTvResult.append(" ] Scanned\n");
                            } else {
                                mTvResult.append("" + arr.get(arr.size() - 1).toString() + "   >>   [ ");
                                mTvResult.append(String.format("%3d", textadding + 1));
                                //mTvResult.append("/");
                                //mTvResult.append(String.format("%2d", NumbOfScan));
                                mTvResult.append(" ] Done\n");
                            }
                            tv_scan_no.setText(""+(textadding+1));
                            viewManualPopup();
                        } else {
                            Toast.makeText(mContext, "이미 입력된 Serial 번호입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "스캔 갯수를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(ConstantValues.SCANNER_ACTION_START, null);
                        //mContext.sendOrderedBroadcast(intent, null);
                    }

                    final ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            scroll.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    };
                    scroll.post(runnable);
                    arr_check = false;
                    //mTvResult.append(barcode+"\n");
                }//serial 번호 체크

            }
        });
        btn_add_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewManualPopup();
            }
        });

        manual_input_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewManualPopup();
/*
                int id = Radio_type.getCheckedRadioButtonId();
                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                RadioButton rb = (RadioButton) findViewById(id);
                tv.setText("결과: " + rb.getText().toString());
                */
            }
        });
        manual_input_data2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewManualPopup();
/*
                int id = Radio_type.getCheckedRadioButtonId();
                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                RadioButton rb = (RadioButton) findViewById(id);
                tv.setText("결과: " + rb.getText().toString());
                */
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Export_national.this);
                alert_confirm.setMessage("RESET?").setCancelable(false).setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = arr.size() - 1; i >= 0; i--) {
                                    arr.remove(i);
                                }
                                //Toast.makeText(getApplicationContext(), ".", Toast.LENGTH_SHORT).show();
                                mTvResult.setText("");
                                tv_scan_no.setText("0");
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });
        /*Log.e("LANG","//111/"+Const.export_companies.get(target_numb2.getSelectedItem().toString()).split("@")[0].equals(Const.User_country_id)+"////"+Const.User_country_id+"///"+Const.export_companies.get(target_numb2.getSelectedItem().toString()).split("@")[0]);
        Log.e("LANG","//222/"+Const.export_companies_eng.get(target_numb2.getSelectedItem().toString()).split("@")[0].equals(Const.User_country_id)+"////"+Const.User_country_id+"///"+Const.export_companies_eng.get(target_numb2.getSelectedItem().toString()).split("@")[0]);
        Log.e("LANG","//333/"+Const.export_companies.get(target_numb3.getSelectedItem().toString()).split("@")[0].equals(Const.User_country_id)+"////"+Const.User_country_id+"///"+Const.export_companies.get(target_numb3.getSelectedItem().toString()).split("@")[0]);
        Log.e("LANG","//444/"+Const.export_companies_eng.get(target_numb3.getSelectedItem().toString()).split("@")[0].equals(Const.User_country_id)+"////"+Const.User_country_id+"///"+Const.export_companies_eng.get(target_numb3.getSelectedItem().toString()).split("@")[0]);*/
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Export_national.this);
                alert_confirm.setMessage("Send to Server?").setCancelable(false).setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                NumbToServer = arr.size();
                                for (int i = 0; i < arr.size(); i++) {
                                    Bufarr.add(arr.get(i));
                                }
                                if (NumbToServer != 0) {
                                    try {
                                        thread.start();
                                    } catch (Exception e) {
                                        thread = null;
                                        thread = new PDAExportThread("");
                                        Log.d("sw : ", "http://" + String.valueOf(const_ip) + ":7778/test.php");
                                        thread.start();
                                    }
                                }
                            /*if (btnBeepCheck) {
                                tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 50);
                            }*/
                            /*
                            if(send_checker){
                            }else{
                                Toast.makeText(getApplicationContext(), "이미 재고로 잡힌 번호가 있습니다. 체크 해주세요.", Toast.LENGTH_LONG).show();
                            }*/

                                for (int i = arr.size() - 1; i >= 0; i--) {
                                    arr.remove(i);
                                }
                            }

                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });
    }
    Handler handler = new Handler();
    private Runnable startTask = new Runnable() {
        @Override
        public void run() {
            scanDecode.starScan();
            handler.postDelayed(startTask, 1000);
        }
    };
    private String START_SCAN_ACTION = "com.geomobile.se4500barcode";
    private void startScan() {
        Intent intent = new Intent();
        intent.setAction(START_SCAN_ACTION);
        sendBroadcast(intent, null);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        scanDecode.onDestroy();//回复初始状态
    }


    protected void mainScreen()
    {
        mContext = this;
        //btnPressed1 = (Button)findViewById(R.id.btnPressed1);
        menu_title = (TextView)findViewById(R.id.menu_title);
        btn_add_serial = (Button)findViewById(R.id.btn_add_serial);
        btn_add_cancel = (Button)findViewById(R.id.btn_add_cancel);
        btn_setting_save = (Button)findViewById(R.id.btn_setting_save);
        btn_setting_cancel = (Button)findViewById(R.id.btn_setting_cancel);
        mTvResult = (TextView)findViewById(R.id.scanresult);
        btnClear = (Button) findViewById(R.id.clear_button);
        manual_input_data = (Button)findViewById(R.id.manual_input_data);
        manual_input_data2 = (Button)findViewById(R.id.manual_input_data2);
        btnSend = (Button) findViewById(R.id.send_to_server);
        btn_start_scan = (Button)findViewById(R.id.btn_start_scan);
        btn_stop_scan = (Button)findViewById(R.id.btn_stop_scan);
        Serial_no = (EditText)findViewById(R.id.Serial_no);
        container_no = (EditText)findViewById(R.id.container_no);
        seal_no = (EditText)findViewById(R.id.et_seal_no);
        /*ipAddress_et1 = (EditText)findViewById(R.id.ipAddress_et1);
        ipAddress_et2 = (EditText)findViewById(R.id.ipAddress_et2);
        ipAddress_et3 = (EditText)findViewById(R.id.ipAddress_et3);
        ipAddress_et4 = (EditText)findViewById(R.id.ipAddress_et4);*/
        tv_scan_no = (TextView)findViewById(R.id.tv_scan_no);
        btn_go_to_menu = (Button)findViewById(R.id.btn_go_to_menu);
        rlPopupSetupPrice = (RelativeLayout) findViewById(R.id.rlPopupSetupPrice);
        rlPopupManual = (RelativeLayout) findViewById(R.id.rlPopupManual);
        llsame_country = (LinearLayout) findViewById(R.id.llsame_country);
        lldiff_country = (LinearLayout) findViewById(R.id.lldiff_country);
        lldiff_country2 = (LinearLayout) findViewById(R.id.lldiff_country2);
        //const_ip = SharedPrefManager.getInstance(mContext).getIpAddress();
        String[] numbers = new String[]{
                "999","1","2","3","4","5","6","7","8","9",
                "10","11","12","13","14","15","16","17","18","19",
                "20","21","22","23","24","25","26","27","28","29","30"
        };
        target_numb = (Spinner) findViewById(R.id.target_numb);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,numbers);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        target_numb.setAdapter(spinnerArrayAdapter);

        /*String[] destinations = new String[]{
                "anatapur"
        };*/

        ArrayList<String> temp_dest = new ArrayList<String>();
        Iterator it;
        if(Const.Language.equals("ko")){
            it = Const.export_companies.entrySet().iterator();
        }else{
            it = Const.export_companies_eng.entrySet().iterator();
        }
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue().toString().split("@")[1].equals(Const.User_country_id)){
                temp_dest.add(pair.getKey().toString());
            }
        }
        String[] destinations = new String[temp_dest.size()];
        temp_dest.toArray(destinations);

        String compareSpinner;
        if(Const.ExportNationalSpinner == null){
            //compareSpinner = SharedPrefManager.getInstance(Export_national.this).getNationalexport_spinner();
            compareSpinner = "0";
        }else{
            compareSpinner = "0";
            //compareSpinner = Const.ExportNationalSpinner;
        }
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item2,destinations);
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.spinner_item2);
        target_numb2 = (Spinner) findViewById(R.id.target_numb2);
        target_numb2.setAdapter(spinnerArrayAdapter2);
        if (compareSpinner != null) {
            //int spinnerPosition = spinnerArrayAdapter2.getPosition(compareSpinner);
            //target_numb2.setSelection(spinnerPosition);
            target_numb2.setSelection(Integer.parseInt(compareSpinner));
        }
        target_numb3 = (Spinner) findViewById(R.id.target_numb3);
        target_numb3.setAdapter(spinnerArrayAdapter2);
        if (compareSpinner != null) {
            target_numb3.setSelection(Integer.parseInt(compareSpinner));
        }

        Radio_type = (RadioGroup)findViewById(R.id.Radio_type);
        Radio_type.removeAllViews();
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {

                        Color.BLACK //disabled
                        , Color.BLACK //enabled

                }
        );
        for(int i = 0; i < Const.all_pallet_types.size(); i++){
            RadioButton rb = new RadioButton(mContext);
            rb.setTextSize(30);
            rb.setText(Const.all_pallet_types.get(i)+"");
            rb.setTag(""+(i+1));
            rb.setTextColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rb.setButtonTintList(colorStateList);
            }
            Log.e(TAG,">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> add Brand Name");
            Radio_type.addView(rb);
        }
        Radio_type2 = (RadioGroup)findViewById(R.id.Radio_type2);
        //Radio_type.clearCheck();
        Radio_type2.clearCheck();
        Radio_type.setOnCheckedChangeListener(listener1);
        Radio_type2.setOnCheckedChangeListener(listener2);
        tv_scan_no.setText("0");
    }
    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                Radio_type2.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                Radio_type2.clearCheck(); // clear the second RadioGroup!
                Radio_type2.setOnCheckedChangeListener(listener2); //reset the listener
                //Log.e("XXX11111111", "do the work");
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                Radio_type.setOnCheckedChangeListener(null);
                Radio_type.clearCheck();
                Radio_type.setOnCheckedChangeListener(listener1);
                //Log.e("XXX2", "do the work");
            }
        }
    };

    private void viewManualPopup(){
        if(rlPopupManual!=null) {
            if (rlPopupManual.getVisibility() == View.VISIBLE) {
                rlPopupManual.setVisibility(View.GONE);
            } else {
                rlPopupManual.setVisibility(View.VISIBLE);
                //ipAddress_et1.setText(const_ip);
            }
        }
    }
    class PDAExportThread extends Thread {
        String server_query;
        public PDAExportThread(String query) {
            server_query = query;
        }
        public void run() {
            try {

                /////////////////////////////

                if(NumbToServer!= 0){
                    sending_con_num = container_no.getText().toString().trim();
                    sending_seal_num = seal_no.getText().toString().trim();
                    String serial_temp = "";
                    String codes_temp = "";
                    String sending_query = "";
                    String return_string = "";
                    int array_size = 0;
                    final ArrayList<String> rsb_list = new ArrayList<>();
                    JSONObject obj;
                    String return_serial = "";
                    String error_message;
                    //BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));


                    PrintStream ps = null;
                    URL url = new URL("http://www.npc-rental.com:1337/graphql");       // URL 설정
                    URLConnection con = url.openConnection();   // 접속
                    con.setRequestProperty("Authorization","Bearer "+ Const.Tokeninfo);
                    con.setRequestProperty("Method","POST");
                    con.setDoOutput(true);


                    ps = new PrintStream(con.getOutputStream());



                    for(int i = 0;i<NumbToServer;i++){
                        if(i == 0 && i != (NumbToServer-1)){
                            serial_temp = Bufarr.get(i).toString()+",";
                            codes_temp= Const.pallet_types.get(Bufarr.get(i).toString().split(" ")[0])+",";
                        }else if(i == (NumbToServer-1)){
                            serial_temp = serial_temp+Bufarr.get(i).toString();
                            codes_temp = codes_temp+ Const.pallet_types.get(Bufarr.get(i).toString().split(" ")[0]);
                        }else{
                            serial_temp = serial_temp+Bufarr.get(i).toString()+",";
                            codes_temp = codes_temp+ Const.pallet_types.get(Bufarr.get(i).toString().split(" ")[0])+",";
                        }
                    }
                    if(Const.Language.equals("ko")){
                        export_company = Const.export_companies.get(target_numb2.getSelectedItem().toString()).split("@")[2];
                    }else{
                        export_company = Const.export_companies_eng.get(target_numb2.getSelectedItem().toString()).split("@")[2];
                    }
                    ///////////////////////////////
                    sending_con_num = "";
                    sending_seal_num = "";
                    /////////////////////////////
                    /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now = new Date();
                    sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String strDate = sdf.format(now);
                    String strDate1 = sdf1.format(now);
                    String strDate2 = sdf2.format(now);*/


                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    Date now = new Date();
                    sdf.setTimeZone(TimeZone.getTimeZone("Greenwich"));
                    sdf1.setTimeZone(TimeZone.getTimeZone(Const.server_time_zone));
                    sdf2.setTimeZone(TimeZone.getTimeZone(Const.server_time_zone));
                    String strDate = sdf.format(now.getTime() + Const.server_time_difference);
                    String strDate1 = sdf1.format(now.getTime() + Const.server_time_difference);
                    String strDate2 = sdf2.format(now.getTime() + Const.server_time_difference);
                    String date_iso = strDate2.substring(0,strDate2.length()-2)+":"+strDate2.substring(strDate2.length()-2,strDate2.length());


                    sending_query = "mutation{\n" +
                            "  createPdaissueinsert(\n" +
                            "    input:{\n" +
                            "     data:{\n" +
                            "      menu:1379\n" + //pda export
                            "      prv_company: "+ Const.Warehouse_id+"\n" +
                            "      cur_company: "+ Const.Warehouse_id+"\n" +
                            "      nxt_company: "+export_company+"\n" +
                            "      rsb_no : \""+serial_temp+"\"\n" +
                            "      container_no : \""+sending_con_num+"\"\n" +
                            "      seal_no : \""+sending_seal_num+"\"\n" +
                            "      sender : "+ Const.User_id_no+"\n" +
                            "      creator : "+ Const.User_id_no+"\n" +
                            "      date : \""+strDate+"\"\n" +
                            "      use_yn : 1\n" +
                            "      type : 72\n" +  //national export
                            "      date_utc : \""+strDate+"\"\n" +
                            "      date_iso : \""+date_iso+"\"\n" +
                            "      date_ymd : \""+strDate1.substring(0,10).trim()+"\"\n" +
                            "      date_ymdhms : \""+strDate1+"\"\n" +
                            "      pdaerror : 2\n" +
                            "    } \n" +
                            "    }\n" +
                            "  ){\n" +
                            "    pdaissueinsert{\n" +
                            "      pdaerror{\n" +
                            "        error_list\n" +
                            "      }\n" +
                            "    }\n" +
                            "  }\n" +
                            "}";


                    String buffer = "";
                    buffer += ("query") + ("=") + (sending_query);           // 변수 구분은 '&' 사용
                    Log.d("check",buffer);
                    buffer = replace_character(buffer);
                    ps.print(buffer);

                    InputStream in = con.getInputStream();

                    BufferedReader r = new BufferedReader(new InputStreamReader(in));

                    StringBuilder total = new StringBuilder();
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    return_string = total.toString();
                    Log.d("RSB CHECK!!!",return_string);

                    error_message = "";
                    obj = new JSONObject(return_string);
                    try{
                        //error_message = obj.getJSONObject("data").getJSONObject("createPdainsert").getJSONObject("pdainsert").getJSONObject("pdaerror").getString("error_list");
                        error_message = obj.getJSONObject("data").getJSONObject("createPdaissueinsert").getJSONObject("pdaissueinsert").getJSONObject("pdaerror").getString("error_list");
                    }catch (Exception e){
                        error_message = "none";
                    }
                    Log.e("ERROR","/msg ----------//////////////////////////////////////////"+error_message);

                    total = null;

                    in.close();
                    r.close();

                    ps.flush();
                    ps.close();

                    error_result = "";
                    if(error_message.equals("none")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvResult.setText("Do not have authorization.\nPlease contact Manager");
                                //rsb_list.clear();
                            }
                        });
                    }else if(!error_message.equals("")){
                        String[] split_list = error_message.split("/");

                        for(int i=0; i<split_list.length;i++){
                            error_result += split_list[i]+"\n";
                        }
                        if(split_list.length == 1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvResult.setText("Among "+NumbToServer+"of items\n"+error_result+" has an error! Please Check");
                                    //rsb_list.clear();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvResult.setText("Among "+NumbToServer+"of items\n"+error_result+" have an error! Please Check");
                                    //rsb_list.clear();
                                }
                            });
                        }
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "data has been sent", Toast.LENGTH_LONG).show();
                                mTvResult.setText("");
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_scan_no.setText("0");
                        }
                    });

                    for (int i = arr.size() - 1; i >= 0; i--) {
                        arr.remove(i);
                    }

                }
                //--------------------------
                //   서버에서 전송받기
                //--------------------------
                for(int i = Bufarr.size()-1;i>=0;i--){
                    Bufarr.remove(i);
                }



                //////////////////



            } catch (Exception ex) {
                //Toast.makeText(SigninPage.this, "Don't have permission to use this app", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        }
    }
    class ImportLogin extends Thread {
        String id;
        String pw;


        public ImportLogin(String user_id, String user_pw) {
            id = user_id;
            pw = user_pw;
        }
        public void run() {
            try {

                PrintStream ps = null;
                URL url = new URL("http://"+ String.valueOf(const_ip)+"/npc_youngchun/pda/import_start.php");       // URL 설정
                Log.d("sw : ","http://"+ String.valueOf(const_ip)+"/npc_youngchun/pda/import_start.php");       // URL 설정
                destination = target_numb2.getSelectedItem().toString().trim();
                URLConnection con = url.openConnection();   // 접속
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());
                String buffer = "";

                buffer += ("id") + ("=") +  (user_id)+"&";            // 변수 구분은 '&' 사용
                buffer += ("destination") + ("=") +  (destination)+"&";            // 변수 구분은 '&' 사용
                //buffer += ("pw") + ("=") +  (user_pw);           // 변수 구분은 '&' 사용
                Log.d("check",buffer);

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
                Log.d("check",return_string);
                String[] tmp_string = return_string.split("\\/");
                String return_tmp_string = "";
                for(int i=0;i<tmp_string.length;i++){
                    return_tmp_string += tmp_string[i]+"\n";
                }
                final String final_return_string = return_tmp_string;
                if(this == null){
                    return;
                }
                Log.d("login_check ","----------------------check Connectthread : "+final_return_string);
                //mStatus3=2;
                /*mWaitCnt=0;
                mTimeoutCnt=0;*/
                if(final_return_string.trim().toString().equals("1")){
                    Log.d("login_check","----------------------check Connectthread - 1: YES!!!!!");
                    ConnectThread cthread = new ConnectThread("YEAH");
                    cthread.start();
                    /*mStatus3=2;
                    mWaitCnt=0;
                    mTimeoutCnt=0;*/
                    //Toast.makeText(StartActivity.this, "1", Toast.LENGTH_SHORT).show();
                }else if(final_return_string.equals("2")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(StartActivity.this, "2", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(final_return_string.equals("3")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(StartActivity.this, "3", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Export_national.this, "Please Check ID and Password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                isw.close();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //con.getInputStream();
                ps.flush();
                ps.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    class ConnectThread extends Thread {
        String hostname;
        Socket sock;
        public ConnectThread(String addr) {
            hostname = addr;
        }

        public void run() {
            try {

                //--------------------------
                //   URL 설정하고 접속하기
                //--------------------------
                PrintStream ps = null;
                //URL url = new URL("http://124.194.93.51:7777/front1/insert.php");       // URL 설정
                //URL url = new URL("http://192.168.0.162:7778/test.php");       // URL 설정
                URL url = new URL("http://"+ String.valueOf(const_ip)+"/npc_youngchun/pda/pda_qr_damage_from_oversea.php");       // URL 설정
                Log.d("sw : ","http://"+ String.valueOf(const_ip)+"/npc_youngchun/pda/pda_qr_damage_from_oversea.php");
                URLConnection con = url.openConnection();   // 접속


                //--------------------------
                //   전송 모드 설정 - 기본적인 설정이다
                //--------------------------


                con.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                //ps = new PrintStream(con.getOutputStream());


                //http.setRequestMethod("POST");         // 전송 방식은 POST

                // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
                //http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                //--------------------------
                //   서버로 값 전송
                //--------------------------

                if(NumbToServer!= 0){
                    String string_temp = "";
                    sending_con_num = container_no.getText().toString().trim();
                    sending_seal_num = seal_no.getText().toString().trim();
                    destination = target_numb2.getSelectedItem().toString().trim();
                    for(int i = 0;i<NumbToServer;i++){
                        string_temp += Bufarr.get(i).toString()+"/";
                    }
                    string_temp = string_temp.substring(0,string_temp.length()-1);
                    con = url.openConnection();
                    con.setDoOutput(true);
                    ps = new PrintStream(con.getOutputStream());

                    String buffer = "";
                    buffer += ("QR_d") + ("=") + (string_temp)+"&";           // 변수 구분은 '&' 사용
                    buffer += ("destination") + ("=") + (destination)+"&";           // 변수 구분은 '&' 사용
                    buffer += ("container_no") + ("=") + (sending_con_num)+"&";           // 변수 구분은 '&' 사용
                    buffer += ("seal_no") + ("=") + (sending_seal_num)+"&";           // 변수 구분은 '&' 사용
                    buffer += ("id") + ("=") + (user_id);           // 변수 구분은 '&' 사용
                    Log.d("check",buffer);

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
                        //System.out.print(current);
                    }
                    Log.d("check",return_string);
                    String[] tmp_string = return_string.split("\\/");
                    String return_tmp_string = "";
                    for(int i=0;i<tmp_string.length;i++){
                        return_tmp_string += tmp_string[i]+"\n";
                    }
                    final String final_return_string = return_tmp_string;
                    if(return_string.equals("1")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvResult.setText("");
                                Toast.makeText(getApplicationContext(), "data has been sent", Toast.LENGTH_LONG).show();
                                tv_scan_no.setText("0");
                            }
                        });
                        send_checker = true;
                    }else if(return_string.equals("2")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvResult.setText("'START'버튼을 눌러주세요.");
                            }
                        });
                    }else if(return_string.equals("3")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvResult.setText("정상 입고 'START'버튼을 눌러주세요");
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mTvResult.setText(NumbToServer+"개의 데이터 중\n"+final_return_string+"가 등록되지 않은 번호입니다. 확인 해주세요.\n");
                                mTvResult.setText("Among "+NumbToServer+" of data\n"+final_return_string+"is not in correct place.\n Please Check No.\n");
                            }
                        });
                        send_checker = false;
                    }
                    WorkDoneThread work_thread = new WorkDoneThread("work_done");
                    work_thread.start();
                    isw.close();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //con.getInputStream();
                    ps.flush();
                    ps.close();
                }
                //--------------------------
                //   서버에서 전송받기
                //--------------------------
                for(int i = Bufarr.size()-1;i>=0;i--){
                    Bufarr.remove(i);
                }
/*
				int port = 7777;
				sock = new Socket(hostname, port);

				ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());
				outstream.writeObject(NumbToServer);
				outstream.flush();
				sock.close();
				if(NumbToServer!= 0){
					for(int i = 0;i<NumbToServer;i++){
						sock = new Socket(hostname, port);
						outstream = new ObjectOutputStream(sock.getOutputStream());
						outstream.writeObject(Bufarr.get(i).toString().trim());
						outstream.flush();
						sock.close();
					}
				}*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    class WorkDoneThread extends Thread {
        String hostname;
        Socket sock;
        public WorkDoneThread(String addr) {
            hostname = addr;
        }

        public void run() {
            try {

                //--------------------------
                //   URL 설정하고 접속하기
                //--------------------------
                PrintStream ps = null;
                //URL url = new URL("http://124.194.93.51:7777/front1/insert.php");       // URL 설정
                //URL url = new URL("http://192.168.0.162:7778/test.php");       // URL 설정
                URL url = new URL("http://"+ String.valueOf(const_ip)+"/npc_youngchun/pda/import_work_done.php");       // URL 설정
                Log.d("sw : ","http://"+ String.valueOf(const_ip)+"/npc_youngchun/pda/import_work_done.php");
                URLConnection con = url.openConnection();   // 접속


                //--------------------------
                //   전송 모드 설정 - 기본적인 설정이다
                //--------------------------


                con.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                //ps = new PrintStream(con.getOutputStream());


                //http.setRequestMethod("POST");         // 전송 방식은 POST

                // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
                //http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                //--------------------------
                //   서버로 값 전송
                //--------------------------
                destination = target_numb2.getSelectedItem().toString().trim();
                con = url.openConnection();
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());

                String coopertaion_no = "";
                switch (destination){
                    case "suyung" :
                        coopertaion_no = "1";
                        break;
                    case "korea" :
                        coopertaion_no = "2";
                        break;
                    case "etc" :
                        coopertaion_no = "3";
                        break;
                    case "chennai" :
                        coopertaion_no = "4";
                        break;
                    case "anatapur" :
                        coopertaion_no = "5";
                        break;
                    case "taiwan" :
                        coopertaion_no = "6";
                        break;
                    case "vietnam" :
                        coopertaion_no = "7";
                        break;
                    case "dongwooSNJ" :
                        coopertaion_no = "8";
                        break;
                    case "koreapackage" :
                        coopertaion_no = "9";
                        break;
                    case "glovis" :
                        coopertaion_no = "10";
                        break;
                    default:
                        coopertaion_no = "0";
                        break;
                }

                String buffer = "";
                buffer += ("destination") + ("=") + (destination)+"&";           // 변수 구분은 '&' 사용
                buffer += ("coopertaion_no") + ("=") + (coopertaion_no)+"&";           // 변수 구분은 '&' 사용
                buffer += ("id") + ("=") + (user_id);           // 변수 구분은 '&' 사용
                Log.d("check",buffer);

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
                    //System.out.print(current);
                }
                Log.d("check",return_string);
                String[] tmp_string = return_string.split("\\/");
                String return_tmp_string = "";
                for(int i=0;i<tmp_string.length;i++){
                    return_tmp_string += tmp_string[i]+"\n";
                }
                final String final_return_string = return_tmp_string.trim();
                if(final_return_string.equals("1")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mTvResult.setText("");
                        }
                    });
                    send_checker = true;
                }else if(final_return_string.equals("2")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvResult.setText("'START'버튼을 눌러주세요.");
                        }
                    });
                }else if(final_return_string.equals("3")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvResult.setText("정상 입고 'START'버튼을 눌러주세요");
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mTvResult.setText("정상적으로 Done되지 않았습니다."+final_return_string);
                        }
                    });
                    send_checker = false;
                }
                isw.close();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //con.getInputStream();
                ps.flush();
                ps.close();

                //--------------------------
                //   서버에서 전송받기
                //--------------------------
                for(int i = Bufarr.size()-1;i>=0;i--){
                    Bufarr.remove(i);
                }
/*
				int port = 7777;
				sock = new Socket(hostname, port);

				ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());
				outstream.writeObject(NumbToServer);
				outstream.flush();
				sock.close();
				if(NumbToServer!= 0){
					for(int i = 0;i<NumbToServer;i++){
						sock = new Socket(hostname, port);
						outstream = new ObjectOutputStream(sock.getOutputStream());
						outstream.writeObject(Bufarr.get(i).toString().trim());
						outstream.flush();
						sock.close();
					}
				}*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String replace_character(String url) {
        url= url.replace("+","%2B");
        return url;
    }
}
