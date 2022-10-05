package com.scandecode_example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.scandecode_example.etc.PlayService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import constants.Const;

public class MainActivity extends AppCompatActivity {
    private EditText mReception;
    private TextView tvcound;
    private Button btnSingleScan, btnClear, btnTouch;
    private ToggleButton toggleButtonRepeat,toggleButtonSound,toggleButtonVibrate;
    private boolean isFlag = false;
    private int scancount = 0;
//    private ScanInterface scanDecode;
    boolean order_no_flag = false;
    boolean serial_no_flag = false;
    TextView tv_order_no,tv_case_no,tv_date,tv_serial_no,tv_count;
    ArrayList order_array;
    ArrayList serial_array;
    Button button_undo,button_send;
    boolean scan_start = false;
    boolean order_check = false;
    boolean serial_check = false;
    private TimerTask mTask3;
    private Timer mTimer3;
    int mStatus3 = 0;
    boolean thread_check = false;
    String server_return_string;
    String const_ip = "www.npc-rental.com:7778"; //aws 서버
    //String const_ip = "119.201.111.73:7778"; //영천 서버

    //String const_ip = "124.194.93.51:7778";
    EditText container_no,seal_no;
    int thread_count = 0;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalyzer analyzer;
    Context mcontext;
    Activity mactivity;
    ProcessCameraProvider processCameraProvider;
    ArrayList arrayList = new ArrayList();


//    ZXingScannerView barcode_scanner;


    public class MyImageAnalyzer implements ImageAnalysis.Analyzer{
        private FragmentManager fragmentManager;
        private bottom_dialog bd;

        public MyImageAnalyzer(FragmentManager fragmentManager){
            this.fragmentManager = fragmentManager;
            bd = new bottom_dialog();
        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
            scanbarcode(image);
        }

        private void scanbarcode(final ImageProxy image) {
            @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
            assert image1 != null;
            InputImage inputImage = InputImage.fromMediaImage(image1,image.getImageInfo().getRotationDegrees());
            BarcodeScannerOptions options =
                    new BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(
                                    Barcode.FORMAT_QR_CODE,
                                    Barcode.FORMAT_CODE_128,
                                    Barcode.FORMAT_CODE_39,
                                    Barcode.FORMAT_CODE_93,
//                                    Barcode.FORMAT_CODABAR,
                                    Barcode.FORMAT_EAN_13,
                                    Barcode.FORMAT_EAN_8,
//                                    Barcode.FORMAT_ITF,
                                    Barcode.FORMAT_UPC_E,
                                    Barcode.FORMAT_UPC_A,
                                    Barcode.FORMAT_PDF417,
                                    Barcode.FORMAT_AZTEC,
                                    Barcode.FORMAT_DATA_MATRIX

                            )
                            .build();
            //        BarcodeScanner scanner = BarcodeScanning.getClient();
            // Or, to specify the formats to recognize:
            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            Task<List<Barcode>> result = scanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            readerBarcodeData(barcodes);
                            // Task completed successfully
                            // ...
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) {
                            image.close();
                        }
                    });
        }

        private void readerBarcodeData(List<Barcode> barcodes) {
            for (Barcode barcode : barcodes) {
                Rect bounds = barcode.getBoundingBox();
                Point[] corners = barcode.getCornerPoints();
                String rawValue = barcode.getRawValue();
                int valueType = barcode.getValueType();
//                Log.e("TAG","valuetype : "+barcode.getValueType()+" rawvalue : "+barcode.getRawValue());
                // See API reference for complete list of supported types
                switch (valueType) {
                    case Barcode.TYPE_WIFI:
                        String ssid = barcode.getWifi().getSsid();
                        String password = barcode.getWifi().getPassword();
                        int type = barcode.getWifi().getEncryptionType();
                        break;
                    case Barcode.TYPE_URL:
                        if(!bd.isAdded()){
                            bd.show(fragmentManager,"");
                        }
                        bd.fetchurl(barcode.getUrl().getUrl());
                        String title = barcode.getUrl().getTitle();
                        String url = barcode.getUrl().getUrl();
                        break;
                    case Barcode.TYPE_TEXT:
                        try{
//                            if(!car_list.contains(rawValue)){
//                                C_Export cthread = new C_Export(rawValue);
//                                cthread.start();
//                            }
                            String data = rawValue;
                            if(!arrayList.contains(data)){
                                if(data.trim().length()> 10){
                                    if(serial_no_flag){
                                        if(data.trim().substring(0,3).equals("CP-")){
                                            Toast.makeText(MainActivity.this, "오더 번호를 읽어주세요", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            if(scancount == 0){
                                                arrayList.add(data);
                                                scancount+=1;
                                                tv_count.setText(scancount+"");
                                                serial_no_flag = false;
                                                tv_order_no.setText(data.trim().substring(0,10));
                                                tv_case_no.setText(data.substring(10,data.trim().length()));

                                                order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
                                                serial_array.add(tv_serial_no.getText().toString().trim());

                                                mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+"\n"+tv_serial_no.getText()+"\n");

                                                tv_order_no.setText("");
                                                tv_case_no.setText("");
                                                tv_serial_no.setText("");
                                                Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
                                            }else{
                                                for(int i=0; i<order_array.size();i++){
                                                    if(order_array.get(i).equals(data.trim())){
                                                        order_check = true;
                                                    }
                                                }
                                                if(order_check){
                                                    order_check = false;
                                                    Toast.makeText(MainActivity.this, "이미 등록된 오더 번호 입니다.", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    arrayList.add(data);
                                                    scancount+=1;
                                                    tv_count.setText(scancount+"");
                                                    serial_no_flag = false;
                                                    tv_order_no.setText(data.trim().substring(0,10));
                                                    tv_case_no.setText(data.substring(10,data.trim().length()));

                                                    order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
                                                    serial_array.add(tv_serial_no.getText().toString().trim());
                                                    mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+"\n"+tv_serial_no.getText()+"\n");

                                                    tv_order_no.setText("");
                                                    tv_case_no.setText("");
                                                    tv_serial_no.setText("");
                                                    Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
                                                }
                                            }
                                        }
                                    }
                                    else if (order_no_flag){
                                        if(data.substring(0,3).equals("CP-")){
                                            if(scancount == 0){
                                                arrayList.add(data);
                                                scancount+=1;
                                                tv_count.setText(scancount+"");
                                                order_no_flag = false;
                                                tv_serial_no.setText(data.trim());

                                                order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
                                                serial_array.add(tv_serial_no.getText().toString().trim());

                                                mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+" "+tv_serial_no.getText()+"\n");

                                                tv_order_no.setText("");
                                                tv_case_no.setText("");
                                                tv_serial_no.setText("");
                                                Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
                                            }else{
                                                for(int i=0; i<serial_array.size();i++){
                                                    if(serial_array.get(i).equals(data.trim())){
                                                        serial_check = true;
                                                    }
                                                }
                                                if(serial_check){
                                                    serial_check = false;
                                                    Toast.makeText(MainActivity.this, "이미 등록된 시리얼 번호 입니다.", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    arrayList.add(data);
                                                    scancount+=1;
                                                    tv_count.setText(scancount+"");
                                                    order_no_flag = false;
                                                    tv_serial_no.setText(data.trim());

                                                    order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
                                                    serial_array.add(tv_serial_no.getText().toString().trim());

                                                    mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+" "+tv_serial_no.getText()+"\n");

                                                    tv_order_no.setText("");
                                                    tv_case_no.setText("");
                                                    tv_serial_no.setText("");
                                                    Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
                                                }
                                            }
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this, "시리얼 번호를 읽어주세요", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        if(data.substring(0,3).equals("CP-")){
                                            if(scancount == 0){
                                                arrayList.add(data);
                                                serial_no_flag = true;
                                                tv_serial_no.setText(data.trim());
                                            }else{
                                                for(int i=0; i<serial_array.size();i++){
                                                    if(serial_array.get(i).equals(data.trim())){
                                                        serial_check = true;
                                                    }
                                                }
                                                if(serial_check){
                                                    serial_check = false;
                                                    Toast.makeText(MainActivity.this, "이미 등록된 시리얼 번호 입니다.", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    arrayList.add(data);
                                                    serial_no_flag = true;
                                                    tv_serial_no.setText(data.trim());
                                                }
                                            }
                                        }
                                        else{
                                            if(scancount == 0){
                                                arrayList.add(data);
                                                order_no_flag= true;
                                                tv_order_no.setText(data.trim().substring(0,10));
                                                tv_case_no.setText(data.substring(10,data.trim().length()));
                                            }else{
                                                for(int i=0; i<order_array.size();i++){
                                                    if(order_array.get(i).equals(data.trim())){
                                                        order_check = true;
                                                    }
                                                }
                                                if(order_check){
                                                    order_check = false;
                                                    Toast.makeText(MainActivity.this, "이미 등록된 오더 번호 입니다.", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    arrayList.add(data);
                                                    order_no_flag= true;
                                                    tv_order_no.setText(data.trim().substring(0,10));
                                                    tv_case_no.setText(data.substring(10,data.trim().length()));
                                                }
                                            }
                                        }
                                    }
//                                    scancount+=1;
                                    //tvcound.setText(getString(R.string.scan_time)+scancount+"");
//                                    mReception.append(data+"\n");
                                }else{
                                    Toast.makeText(MainActivity.this, "Please check barcode No.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        scanDecode = new ScanDecode(this);
//        scanDecode.initService("true");//初始化扫描服务
        //btnSingleScan = (Button) findViewById(R.id.buttonscan);


        mcontext = this;
        mactivity = this;

        previewView = findViewById(R.id.previewview);
        this.getWindow().setFlags(1024,1024);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(mcontext);

        analyzer = new MyImageAnalyzer(getSupportFragmentManager());


        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try{
                    if(ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.CAMERA) != (PackageManager.PERMISSION_GRANTED)){
                        ActivityCompat.requestPermissions(mactivity,new String[] {Manifest.permission.CAMERA},101);
                    }else{
                        processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                        bindpreview(processCameraProvider);
                    }
                }catch (ExecutionException e){
                    e.printStackTrace();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));


        btnClear = (Button) findViewById(R.id.buttonclear);
        toggleButtonRepeat = (ToggleButton) findViewById(R.id.button_repeat);
        mReception = (EditText) findViewById(R.id.EditTextReception);
//        btnStop = (Button) findViewById(R.id.buttonstop);
//        btnStop.setOnClickListener(this);
        tvcound = (TextView) findViewById(R.id.tv_cound);
        //btnClear.setOnClickListener(this);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scancount == 0){

                }else{
                    Toast.makeText(MainActivity.this, "초기화 하였습니다", Toast.LENGTH_SHORT).show();
                }
                arrayList.clear();
                container_no.setText("");
                seal_no.setText("");
                mReception.setText(""); //清屏
                order_array.clear();
                serial_array.clear();
                tv_order_no.setText("");
                tv_case_no.setText("");
                tv_serial_no.setText("");
                order_no_flag = false;
                serial_no_flag= false;
                scancount=0;
                tv_count.setText(scancount+"");
            }
        });
        //btnTouch = (Button) findViewById(R.id.buttonscan);
        //toggleButtonSound =(ToggleButton) findViewById(R.id.butSound);
        //toggleButtonVibrate = (ToggleButton) findViewById(R.id.butVibrate);

        tv_order_no = (TextView)findViewById(R.id.tv_order_no);
        tv_case_no = (TextView)findViewById(R.id.tv_case_no);
        tv_date = (TextView)findViewById(R.id.tv_date);
        tv_serial_no = (TextView)findViewById(R.id.tv_serial_no);

        button_send = (Button)findViewById(R.id.button_send);
        button_undo = (Button)findViewById(R.id.button_undo);

        container_no = (EditText)findViewById(R.id.container_no);
        seal_no = (EditText)findViewById(R.id.seal_no);
        tv_count = (TextView)findViewById(R.id.tv_count);
//        tv_title = (TextView)findViewById(R.id.tv_title);

        //zxing
//        barcode_scanner = (ZXingScannerView)findViewById(R.id.barcode_scanner);

        order_array = new ArrayList();
        serial_array = new ArrayList();

        Date today;
        String result;
        SimpleDateFormat formatter;

        formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        today = new Date();
        result = formatter.format(today);

//        tv_title.setText(""+Const.company_name);
        tv_date.setText(result);

        container_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(" ")){
                    container_no.setText(container_no.getText().subSequence(0,container_no.getText().length()-1));
                    container_no.setSelection(container_no.getText().length());
                }else if(s.toString().length()>0){
                    if(s.toString().substring(s.toString().length()-1).equals(" ")){
                        container_no.setText(container_no.getText().subSequence(0,container_no.getText().length()-1));
                        container_no.setSelection(container_no.getText().length());
                    }
                }
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
                        container_no.clearFocus();
                        seal_no.requestFocus();
                    }else{
                        String text = container_no.getText().toString();
                        container_no.setText(text.substring(0, text.length() - 1));
                        container_no.setSelection(container_no.getText().length());
                        Toast.makeText(MainActivity.this, "잘못된 컨테이너 번호입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() < 4){
                    container_no.setInputType(InputType.TYPE_CLASS_TEXT);
                    container_no.setSelection(container_no.getText().length());
                }
                // 입력하기 전에

            }
        });

        button_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order_array.size() <= 0){
                    Toast.makeText(MainActivity.this, "작업 이후에 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    if(order_no_flag){
                        arrayList.remove(arrayList.size()-2);
                        arrayList.remove(arrayList.size()-2);
                    }else if(serial_no_flag){
                        arrayList.remove(arrayList.size()-2);
                        arrayList.remove(arrayList.size()-2);
                    }else{
                        arrayList.remove(arrayList.size()-1);
                        arrayList.remove(arrayList.size()-1);
                    }
                    order_array.remove(order_array.size()-1);
                    serial_array.remove(serial_array.size()-1);
                    tv_order_no.setText("");
                    tv_case_no.setText("");
                    tv_serial_no.setText("");
                    order_no_flag = false;
                    serial_no_flag= false;
                    Toast.makeText(MainActivity.this, "마지막 작업이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    mReception.setText(mReception.getText().subSequence(0,(mReception.length()-30)-(scancount+"").length()));
                    scancount-=1;
                    tv_count.setText(scancount+"");
                }
            }
        });
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_send.setEnabled(false);
                thread_check = false;
                thread_count = 0;
                mTimer3 = new Timer();
                mTask3 = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (mStatus3){
                                    case 0:
                                        if(!thread_check){
                                            if(container_no.getText().length() != 11){
                                                Toast.makeText(MainActivity.this, "컨테이너 번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                                                mStatus3 = 3;
                                            }else if(order_array.size()<1){
                                                Toast.makeText(MainActivity.this, "스캔된 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                                                mStatus3 = 3;
                                            }else if(Const.company_id.equals("null")){
                                                Toast.makeText(MainActivity.this, "업체 정보가 잘못되었습니다.\n재로그인을 진행하세요.", Toast.LENGTH_SHORT).show();
                                                mStatus3 = 3;
                                            }else{
                                                thread_check = true;
                                                mStatus3 = 1;
                                                SendThread thread = new SendThread();
                                                try {
                                                    thread.start();
                                                } catch (Exception e) {
                                                    Log.d("sw", "thread error ///////////////////////////// ");
                                                }
                                            }
                                        }else{
                                            if(mTimer3!=null) {
                                                button_send.setEnabled(true);
                                                mStatus3 = 0;
                                                mTimer3.cancel();
                                                mTimer3=null;
                                                mTask3.cancel();
                                                mTask3 = null;
                                            }
                                            //Toast.makeText(MainActivity.this, "전송이 잘 되지 않았습니다. 다시 시도 부탁드립니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 1:
                                        thread_count++;
                                        if(thread_count >100){
                                            button_send.setEnabled(true);
                                            Toast.makeText(MainActivity.this, "전송이 잘 되지 않았습니다. 다시 시도 부탁드립니다.", Toast.LENGTH_SHORT).show();
                                            thread_count = 0;
                                            if(mTimer3!=null) {
                                                mStatus3 = 0;
                                                mTimer3.cancel();
                                                mTimer3=null;
                                                mTask3.cancel();
                                                mTask3 = null;
                                            }
                                        }
                                        break;
                                    case 2:
                                        if(server_return_string.equals("success")){
                                            button_send.setEnabled(true);
                                            Toast.makeText(MainActivity.this, "정상 처리 되었습니다.", Toast.LENGTH_SHORT).show();
                                            mReception.setText(""); //清屏
                                            order_array.clear();
                                            serial_array.clear();
                                            tv_order_no.setText("");
                                            tv_case_no.setText("");
                                            tv_serial_no.setText("");
                                            order_no_flag = false;
                                            serial_no_flag= false;
                                            scancount=0;
                                            tv_count.setText(scancount+"");
                                            container_no.clearFocus();
                                            seal_no.clearFocus();
                                        }else{
                                            button_send.setEnabled(true);
                                            Toast.makeText(MainActivity.this, "표기된 시리얼 번호가 정상 처리 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                            String error_serial_no = "";
                                            String[] err_tmp = server_return_string.split("/");
                                            for(int j=0;j<err_tmp.length;j++){
                                                error_serial_no += err_tmp[j]+"\n";
                                            }
                                            mReception.setText(error_serial_no); //清屏
                                            order_array.clear();
                                            serial_array.clear();
                                            tv_order_no.setText("");
                                            tv_case_no.setText("");
                                            tv_serial_no.setText("");
                                            order_no_flag = false;
                                            serial_no_flag= false;
                                            scancount=0;
                                            tv_count.setText(scancount+"");
                                            container_no.clearFocus();
                                            seal_no.clearFocus();
                                        }
                                        thread_count = 0;
                                        if(mTimer3!=null) {
                                            button_send.setEnabled(true);
                                            mStatus3 = 0;
                                            mTimer3.cancel();
                                            mTimer3=null;
                                            mTask3.cancel();
                                            mTask3 = null;
                                        }
                                    case 3 :
                                        if(mTimer3!=null) {
                                            button_send.setEnabled(true);
                                            mStatus3 = 0;
                                            mTimer3.cancel();
                                            mTimer3=null;
                                            mTask3.cancel();
                                            mTask3 = null;
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                    }
                };
                mTimer3.schedule(mTask3, 100, 100);
            }
        });

        /*btnTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startScan();
                *//*switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:{
                        scanDecode.stopScan();//停止扫描
                        handler.removeCallbacks(startTask);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:{
                        scanDecode.starScan();//启动扫描
                        break;
                    }

                    default:
                        break;
                }*//*
                return false;
            }
        });*/
        toggleButtonRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
//                    if(!scan_start){
//                        scan_start = true;
//                        startScan();
//                    }
                    /*scancount = 0;
                    handler.removeCallbacks(startTask);
                    handler.postDelayed(startTask, 0);*/
                    ////////////////////////
//                    barcode_scanner.setResultHandler(MainActivity.this);
//                    barcode_scanner.startCamera();
//                    barcode_scanner.setVisibility(View.VISIBLE);
                    if(cameraProviderFuture != null){
                        cameraProviderFuture = null;
                    }
                    if(previewView.getVisibility() == View.GONE)
                    {
                        previewView.setVisibility(View.VISIBLE);
                    }
                    cameraProviderFuture = ProcessCameraProvider.getInstance(mcontext);
                    cameraProviderFuture.addListener(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.CAMERA) != (PackageManager.PERMISSION_GRANTED)){
                                    ActivityCompat.requestPermissions(mactivity,new String[] {Manifest.permission.CAMERA},101);
                                }else{
                                    processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                                    bindpreview(processCameraProvider);
                                }
                            }catch (ExecutionException e){
                                e.printStackTrace();
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }, ContextCompat.getMainExecutor(mcontext));
                    ////////////////////////
                } else {
//                    scan_start = false;
//                    handler.removeCallbacks(startTask);
//                    scanDecode.stopScan();


                    ////////////////////////
//                    barcode_scanner.stopCamera();
//                    barcode_scanner.setVisibility(View.INVISIBLE);

                    processCameraProvider.unbindAll();
                    previewView.setVisibility(View.GONE);
                    ////////////////////////
                }
            }
        });

        registerReceiver(reciever, new IntentFilter());

        Intent intent=new Intent(this, PlayService.class);
        intent.putExtra("msgs", mReception.getText());
        Log.e("MainActivity","before startservice");
        Log.e("MainActivity",mReception.getText()+"");
        startService(intent);
        Log.e("MainActivity","after startservice");
        Log.e("MainActivity",mReception.getText()+"");

        /*toggleButtonSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean isChecked) {
                if (isChecked) {
                    SystemProperties.set("persist.sys.playscanmusic","true");
                }
                else {
                    SystemProperties.set("persist.sys.playscanmusic","false");
                }
            }
        });*/

        /*toggleButtonVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean isChecked) {
                if (isChecked) {
                    SystemProperties.set("persist.sys.scanvibrate","true");
                }
                else {
                    SystemProperties.set("persist.sys.scanvibrate","false");
                }

            }
        });*/
//
//        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
//            @Override
//            public void getBarcode(String data) {
//                data = data.trim();
//                if(data.trim().length()> 10){
//                    if(serial_no_flag){
//                        if(data.trim().substring(0,3).equals("CP-")){
//                            Toast.makeText(MainActivity.this, "오더 번호를 읽어주세요", Toast.LENGTH_SHORT).show();
//                        }else{
//                            if(scancount == 0){
//                                scancount+=1;
//                                tv_count.setText(scancount+"");
//                                serial_no_flag = false;
//                                tv_order_no.setText(data.trim().substring(0,10));
//                                tv_case_no.setText(data.substring(10,data.trim().length()));
//
//                                order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
//                                serial_array.add(tv_serial_no.getText().toString().trim());
//
//                                mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+" "+tv_serial_no.getText()+"\n");
//
//                                tv_order_no.setText("");
//                                tv_case_no.setText("");
//                                tv_serial_no.setText("");
//                                Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
//                            }else{
//                                for(int i=0; i<order_array.size();i++){
//                                    if(order_array.get(i).equals(data.trim())){
//                                        order_check = true;
//                                    }
//                                }
//                                if(order_check){
//                                    order_check = false;
//                                    Toast.makeText(MainActivity.this, "이미 등록된 오더 번호 입니다.", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    scancount+=1;
//                                    tv_count.setText(scancount+"");
//                                    serial_no_flag = false;
//                                    tv_order_no.setText(data.trim().substring(0,10));
//                                    tv_case_no.setText(data.substring(10,data.trim().length()));
//
//                                    order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
//                                    serial_array.add(tv_serial_no.getText().toString().trim());
//                                    mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+" "+tv_serial_no.getText()+"\n");
//
//                                    tv_order_no.setText("");
//                                    tv_case_no.setText("");
//                                    tv_serial_no.setText("");
//                                    Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
//                                }
//                            }
//                        }
//                    }else if (order_no_flag){
//                        if(data.substring(0,3).equals("CP-")){
//                            if(scancount == 0){
//                                scancount+=1;
//                                tv_count.setText(scancount+"");
//                                order_no_flag = false;
//                                tv_serial_no.setText(data.trim());
//
//                                order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
//                                serial_array.add(tv_serial_no.getText().toString().trim());
//
//                                mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+" "+tv_serial_no.getText()+"\n");
//
//                                tv_order_no.setText("");
//                                tv_case_no.setText("");
//                                tv_serial_no.setText("");
//                                Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
//                            }else{
//                                for(int i=0; i<serial_array.size();i++){
//                                    if(serial_array.get(i).equals(data.trim())){
//                                       serial_check = true;
//                                    }
//                                }
//                                if(serial_check){
//                                    serial_check = false;
//                                    Toast.makeText(MainActivity.this, "이미 등록된 시리얼 번호 입니다.", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    scancount+=1;
//                                    tv_count.setText(scancount+"");
//                                    order_no_flag = false;
//                                    tv_serial_no.setText(data.trim());
//
//                                    order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
//                                    serial_array.add(tv_serial_no.getText().toString().trim());
//
//                                    mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+" "+tv_serial_no.getText()+"\n");
//
//                                    tv_order_no.setText("");
//                                    tv_case_no.setText("");
//                                    tv_serial_no.setText("");
//                                    Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
//                                }
//                            }
//                        }else{
//                            Toast.makeText(MainActivity.this, "시리얼 번호를 읽어주세요", Toast.LENGTH_SHORT).show();
//                        }
//                    }else{
//                        if(data.substring(0,3).equals("CP-")){
//                            if(scancount == 0){
//                                serial_no_flag = true;
//                                tv_serial_no.setText(data.trim());
//                            }else{
//                                for(int i=0; i<serial_array.size();i++){
//                                    if(serial_array.get(i).equals(data.trim())){
//                                        serial_check = true;
//                                    }
//                                }
//                                if(serial_check){
//                                    serial_check = false;
//                                    Toast.makeText(MainActivity.this, "이미 등록된 시리얼 번호 입니다.", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    serial_no_flag = true;
//                                    tv_serial_no.setText(data.trim());
//                                }
//                            }
//                        }else{
//                            if(scancount == 0){
//                                order_no_flag= true;
//                                tv_order_no.setText(data.trim().substring(0,10));
//                                tv_case_no.setText(data.substring(10,data.trim().length()));
//                            }else{
//                                for(int i=0; i<order_array.size();i++){
//                                    if(order_array.get(i).equals(data.trim())){
//                                        order_check = true;
//                                    }
//                                }
//                                if(order_check){
//                                    order_check = false;
//                                    Toast.makeText(MainActivity.this, "이미 등록된 오더 번호 입니다.", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    order_no_flag= true;
//                                    tv_order_no.setText(data.trim().substring(0,10));
//                                    tv_case_no.setText(data.substring(10,data.trim().length()));
//                                }
//                            }
//                        }
//                    }
//                    /*scancount+=1;
//                    //tvcound.setText(getString(R.string.scan_time)+scancount+"");
//                    mReception.append(data+"\n");*/
//
//                }else{
//                    Toast.makeText(MainActivity.this, "Please check barcode No.", Toast.LENGTH_SHORT).show();
//                }
//
//                startScan();
//            }
//
//            @Override
//            public void getBarcodeByte(byte[] bytes) {
//                //返回原始解码数据
////                scancount+=1;
////                tvcound.setText(getString(R.string.scan_time)+scancount+"");
////                mReception.append(DataConversionUtils.byteArrayToString(bytes) +"\n");
//            }
//        });

//        barcode_scanner.setResultHandler(MainActivity.this);
//        barcode_scanner.startCamera();
//        barcode_scanner.setVisibility(View.VISIBLE);
    }
//    Handler handler = new Handler();

    //连续扫描
//    private Runnable startTask = new Runnable() {
//        @Override
//        public void run() {
//            scanDecode.starScan();
//            handler.postDelayed(startTask, 1000);
//        }
//    };
    //@Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.buttonclear:
//                if(scancount == 0){
//
//                }else{
//                    Toast.makeText(this, "초기화 하였습니다", Toast.LENGTH_SHORT).show();
//                }
//                container_no.setText("");
//                seal_no.setText("");
//                mReception.setText(""); //清屏
//                order_array.clear();
//                serial_array.clear();
//                tv_order_no.setText("");
//                tv_case_no.setText("");
//                tv_serial_no.setText("");
//                order_no_flag = false;
//                serial_no_flag= false;
//                scancount=0;
//                tv_count.setText(scancount+"");
//                //tvcound.setText(getString(R.string.scan_time)+scancount+"");
//                break;
//            case R.id.buttonscan:
//                scanDecode.starScan();//启动扫描
//                break;
////            case R.id.buttonstop:
////                scanDecode.stopScan();//停止扫描
////                handler.removeCallbacks(startTask);
////                break;
//            default:
//                break;
//        }
//    }

    BroadcastReceiver reciever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msgs=intent.getStringExtra("msgs");
            Log.e("MainActivity","BroadcastReceiver");
            Log.e("MainActivity",msgs+"");
            if(msgs != null){
                //mReception.setText(msgs);
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimer3!=null) {
            mTimer3.cancel();
            mTimer3=null;
            mTask3.cancel();
            mTask3 = null;
        }
        Log.e("MainActivity","ondestroy");
        unregisterReceiver(reciever);
//        scanDecode.onDestroy();//回复初始状态
    }


    private void bindpreview(ProcessCameraProvider processCameraProvider1) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageCapture imageCapture = new ImageCapture.Builder().build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                .setTargetResolution(new Size(1280,720))
                .setTargetResolution(new Size(1920,1080))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(cameraExecutor,analyzer);

//        {
//            ImageAnalysis analysisUseCase;
//            int lensFacing = CameraSelector.LENS_FACING_BACK;
//
//            ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
//            Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
//            if (targetResolution != null) {
//                builder.setTargetResolution(targetResolution);
//            }
//            analysisUseCase = builder.build();
//
//            needUpdateGraphicOverlayImageSourceInfo = true;
//            analysisUseCase.setAnalyzer(
//                    // imageProcessor.processImageProxy will use another thread to run the detection underneath,
//                    // thus we can just runs the analyzer itself on main thread.
//                    ContextCompat.getMainExecutor(this),
//                    imageProxy -> {
//                        if (needUpdateGraphicOverlayImageSourceInfo) {
//                            boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
//                            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
//                            if (rotationDegrees == 0 || rotationDegrees == 180) {
//                                graphicOverlay.setImageSourceInfo(
//                                        imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
//                            } else {
//                                graphicOverlay.setImageSourceInfo(
//                                        imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
//                            }
//                            needUpdateGraphicOverlayImageSourceInfo = false;
//                        }
//                        try {
//                            VisionImageProcessor imageProcessor;
//                            imageProcessor = new BarcodeScannerProcessor(this);
//                            imageProcessor.processImageProxy(imageProxy, graphicOverlay);
//                        } catch (MlKitException e) {
//                            Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
//                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    });
//
//            processCameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, analysisUseCase);
//        }
        processCameraProvider1.unbindAll();
        processCameraProvider1.bindToLifecycle(this,cameraSelector,preview,imageCapture,imageAnalysis);
    }

    //Receiving broadcast
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    //Call Barcode scan
    private String START_SCAN_ACTION = "com.geomobile.se4500barcode";
    //Stop Barcode scan
    private String STOP_SCAN="com.geomobile.se4500barcode.poweroff";
    //The registration system of radio reception, barcode scanning data


//    iFilter.addAction(RECE_DATA_ACTION)
//    registerReceiver(receiver, iFilter)




    /*private void judgePropert() {
        String result = SystemProperties.get("persist.sys.keyreport", "true");
        if (result.equals("false")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.key_test_back_title)
                    .setMessage(R.string.action_dialog_setting_config)
                    .setPositiveButton(R.string.action_dialog_setting_config_sure_go, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    startActivityForResult(intent, 1);
                                }
                            })
                    .setNegativeButton(R.string.action_exit_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    finish();
                                }
                            }
                    ).show();
        }
    }*/

    boolean isRepeat = false;
    private void startScan() {
        Intent intent = new Intent();
        intent.setAction(START_SCAN_ACTION);
        sendBroadcast(intent, null);
    }

    /*@Override
    public void handleResult(Result result) {

        String data = result.getText().trim();
        if(data.trim().length()> 10){
            if(serial_no_flag){
                if(data.trim().substring(0,3).equals("CP-")){
                    Toast.makeText(MainActivity.this, "오더 번호를 읽어주세요", Toast.LENGTH_SHORT).show();
                }else{
                    if(scancount == 0){
                        scancount+=1;
                        tv_count.setText(scancount+"");
                        serial_no_flag = false;
                        tv_order_no.setText(data.trim().substring(0,10));
                        tv_case_no.setText(data.substring(10,data.trim().length()));

                        order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
                        serial_array.add(tv_serial_no.getText().toString().trim());

                        mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+"\n"+tv_serial_no.getText()+"\n");

                        tv_order_no.setText("");
                        tv_case_no.setText("");
                        tv_serial_no.setText("");
                        Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
                    }else{
                        for(int i=0; i<order_array.size();i++){
                            if(order_array.get(i).equals(data.trim())){
                                order_check = true;
                            }
                        }
                        if(order_check){
                            order_check = false;
                            Toast.makeText(MainActivity.this, "이미 등록된 오더 번호 입니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            scancount+=1;
                            tv_count.setText(scancount+"");
                            serial_no_flag = false;
                            tv_order_no.setText(data.trim().substring(0,10));
                            tv_case_no.setText(data.substring(10,data.trim().length()));

                            order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
                            serial_array.add(tv_serial_no.getText().toString().trim());
                            mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+"\n"+tv_serial_no.getText()+"\n");

                            tv_order_no.setText("");
                            tv_case_no.setText("");
                            tv_serial_no.setText("");
                            Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
                        }
                    }
                }
            }else if (order_no_flag){
                if(data.substring(0,3).equals("CP-")){
                    if(scancount == 0){
                        scancount+=1;
                        tv_count.setText(scancount+"");
                        order_no_flag = false;
                        tv_serial_no.setText(data.trim());

                        order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
                        serial_array.add(tv_serial_no.getText().toString().trim());

                        mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+" "+tv_serial_no.getText()+"\n");

                        tv_order_no.setText("");
                        tv_case_no.setText("");
                        tv_serial_no.setText("");
                        Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
                    }else{
                        for(int i=0; i<serial_array.size();i++){
                            if(serial_array.get(i).equals(data.trim())){
                                serial_check = true;
                            }
                        }
                        if(serial_check){
                            serial_check = false;
                            Toast.makeText(MainActivity.this, "이미 등록된 시리얼 번호 입니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            scancount+=1;
                            tv_count.setText(scancount+"");
                            order_no_flag = false;
                            tv_serial_no.setText(data.trim());

                            order_array.add(tv_order_no.getText().toString().trim()+tv_case_no.getText().toString().trim());
                            serial_array.add(tv_serial_no.getText().toString().trim());

                            mReception.append(scancount+" "+tv_order_no.getText()+" "+tv_case_no.getText()+" "+tv_serial_no.getText()+"\n");

                            tv_order_no.setText("");
                            tv_case_no.setText("");
                            tv_serial_no.setText("");
                            Log.d("sw","LOGGGGGGGGGGGGGGGGGGGGGG/////"+order_array.toString()+"/////////"+serial_array.toString());
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "시리얼 번호를 읽어주세요", Toast.LENGTH_SHORT).show();
                }
            }else{
                if(data.substring(0,3).equals("CP-")){
                    if(scancount == 0){
                        serial_no_flag = true;
                        tv_serial_no.setText(data.trim());
                    }else{
                        for(int i=0; i<serial_array.size();i++){
                            if(serial_array.get(i).equals(data.trim())){
                                serial_check = true;
                            }
                        }
                        if(serial_check){
                            serial_check = false;
                            Toast.makeText(MainActivity.this, "이미 등록된 시리얼 번호 입니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            serial_no_flag = true;
                            tv_serial_no.setText(data.trim());
                        }
                    }
                }else{
                    if(scancount == 0){
                        order_no_flag= true;
                        tv_order_no.setText(data.trim().substring(0,10));
                        tv_case_no.setText(data.substring(10,data.trim().length()));
                    }else{
                        for(int i=0; i<order_array.size();i++){
                            if(order_array.get(i).equals(data.trim())){
                                order_check = true;
                            }
                        }
                        if(order_check){
                            order_check = false;
                            Toast.makeText(MainActivity.this, "이미 등록된 오더 번호 입니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            order_no_flag= true;
                            tv_order_no.setText(data.trim().substring(0,10));
                            tv_case_no.setText(data.substring(10,data.trim().length()));
                        }
                    }
                }
            }
                    scancount+=1;
                    //tvcound.setText(getString(R.string.scan_time)+scancount+"");
                    mReception.append(data+"\n");

        }else{
            Toast.makeText(MainActivity.this, "Please check barcode No.", Toast.LENGTH_SHORT).show();
        }

        barcode_scanner.setResultHandler(MainActivity.this);
        barcode_scanner.startCamera();
    }*/
//
//    class C_Import extends Thread {
//        String s_n;
//
//
//        public C_Import(String u_s_n) {
//            s_n = u_s_n;
//        }
//        public void run() {
//            try {
//                //////////////////////////////////////////////////////////////////////////////////////////
//                PrintStream ps = null;
////                URL url = new URL("http://"+ const_ip+"/EvDemo/save_data.php");       // URL 설정
////                Log.d("sw : ","http://"+ const_ip+"/EvDemo/save_data.php");       // URL 설정
//                URL url = new URL("http://"+ const_ip+"/EvDemo/import_check.php");       // URL 설정
//                Log.d("sw : ","http://"+ const_ip+"/EvDemo/import_check.php");       // URL 설정
//                //destination = target_numb2.getSelectedItem().toString().trim();
//                URLConnection con = url.openConnection();   // 접속
//                con.setDoOutput(true);
//                ps = new PrintStream(con.getOutputStream());
//                String buffer = "";
//
//                String data_format = "yyyy-MM-dd HH:mm:ss";
//                SimpleDateFormat df = new SimpleDateFormat(data_format);
//                Date today = Calendar.getInstance().getTime();
//
//                buffer += ("s_n") + ("=") +  (s_n)+"&";            // 변수 구분은 '&' 사용
//                buffer += ("u_c") + ("=") +  (Const.User_company_idx)+"&";            // 변수 구분은 '&' 사용
////                buffer += ("u_id") + ("=") +  (Const.User_id_idx)+"&";            // 변수 구분은 '&' 사용
//                buffer += ("c_d") + ("=") +  (df.format(today));            // 변수 구분은 '&' 사용
//                Log.d("check",buffer);
//
//                ps.print(buffer);
//                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                InputStream in = con.getInputStream();
//                InputStreamReader isw = new InputStreamReader(in);
//
//                check_return_string = "";
//
//                BufferedReader r = new BufferedReader(new InputStreamReader(in));
//
//                StringBuilder total = new StringBuilder();
//                for (String line; (line = r.readLine()) != null; ) {
//                    total.append(line).append('\n');
//                }
//                check_return_string = total.toString().trim();
//                Log.d("RSB CHECK!!!",check_return_string);
//
//                if(check_return_string.trim().equals("1")){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Edit_popup.setVisibility(View.VISIBLE);
//                            tv_error_s_n.setText(""+s_n);
//                            tv_error_detail.setText("등록되지 않은 RSB번호입니다.");
//                            vib.vibrate(new long[]{500,1000,500,1000,500,1000},-1);
//                            tg.startTone(ToneGenerator.TONE_PROP_NACK);
//                            if(isSwitchChecked){
//                                disable_nfc = false;
//                            }
//                            else{
//                                barcode_scanner.setResultHandler(Pallet_Import_NPC.this);
//                                barcode_scanner.startCamera();
//                            }
//
//                        }
//                    });
//                }
//                else if(check_return_string.trim().equals("2")){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Edit_popup.setVisibility(View.VISIBLE);
//                            tv_error_s_n.setText(""+s_n);
//                            tv_error_detail.setText("해당 RSB번호의 올바른 입고지가 아닙니다.");
//                            vib.vibrate(new long[]{500,1000,500,1000,500,1000},-1);
//                            tg.startTone(ToneGenerator.TONE_PROP_NACK);
//                            if(isSwitchChecked){
//                                disable_nfc = false;
//                            }
//                            else{
//                                barcode_scanner.setResultHandler(Pallet_Import_NPC.this);
//                                barcode_scanner.startCamera();
//                            }
//
//                        }
//                    });
//                }
//                else{
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String[] tmp_check_string = check_return_string.split("/");
//                            if(user_previous_company.equals("")){
//                                user_previous_company = tmp_check_string[0];
//                                user_previous_company_name = tmp_check_string[2];
////                                Btn_select_company.setText(user_previous_company_name);
//                            }
//                            if(user_previous_company.equals(tmp_check_string[0])){
//                                pallet_count ++;
//                                tv_pallet_count.setText(pallet_count+"");
//                                add_row(check_return_string,s_n);
//                                car_list.add(s_n);
//
//                                vib.vibrate(1000);
//                                tg.startTone(ToneGenerator.TONE_PROP_BEEP);
//                                if(isSwitchChecked){
//                                    disable_nfc = false;
//                                }
//                                else{
//                                    barcode_scanner.setResultHandler(Pallet_Import_NPC.this);
//                                    barcode_scanner.startCamera();
//                                }
//
//                            }
//                            else{
//                                Edit_popup.setVisibility(View.VISIBLE);
//                                tv_error_s_n.setText(""+s_n);
//                                tv_error_detail.setText("상차지가 다릅니다.");
//                            }
//                        }
//                    });
//                }
//                isw.close();
//                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                //con.getInputStream();
//                ps.flush();
//                ps.close();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

    class SendThread extends Thread {
        String table_no;
        public SendThread() {
        }
        public void run() {
            try {
                PrintStream ps = null;
//                URL url = new URL("http://"+const_ip+"/npc_youngchun/from_korea_package/receive_data.php");       // URL 설정
//                Log.d("sw : ","http://"+const_ip+"/npc_youngchun/from_korea_package/receive_data.php");
                URL url = new URL("http://"+const_ip+"/npc_youngchun/from_package/receive_data.php");       // URL 설정
                Log.d("sw : ","http://"+const_ip+"/npc_youngchun/from_package/receive_data.php");
                URLConnection con = url.openConnection();   // 접속
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());

                String order_list = "";
                String serial_list = "";
                if(order_array.get(0).toString().toUpperCase().substring(0,1).equals("P")){
                    Const.Country = "VNM";
                }else{
                    Const.Country = "IND";
                }
                for(int i =0;i<order_array.size();i++){
                    order_list = order_list + order_array.get(i) + "/";
                    serial_list = serial_list + serial_array.get(i) + "/";
                }
                if(order_array.size()>0){
                    order_list = order_list.substring(0,order_list.length()-1);
                    serial_list = serial_list.substring(0,serial_list.length()-1);
                }

                String buffer = "";
                //buffer += ("case_no") + ("=") + (table_no)+"&";           // 변수 구분은 '&' 사용
                buffer += ("seal_no") + ("=") + (seal_no.getText())+"&";           // 변수 구분은 '&' 사용
                buffer += ("container_no") + ("=") + (container_no.getText())+"&";           // 변수 구분은 '&' 사용
                buffer += ("order_no") + ("=") + (order_list)+"&";           // 변수 구분은 '&' 사용
                buffer += ("country") + ("=") + (Const.Country)+"&";           // 변수 구분은 '&' 사용
                buffer += ("company_id") + ("=") + (Const.company_id)+"&";           // 변수 구분은 '&' 사용
                buffer += ("User_id_no") + ("=") + (Const.User_id_no)+"&";           // 변수 구분은 '&' 사용
                buffer += ("serial_no") + ("=") + serial_list;
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

                mStatus3=2;

                server_return_string = return_string;

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

    @Override
    protected void onResume() {
        super.onResume();

        if(toggleButtonRepeat.isChecked()){
//            barcode_scanner.resumeCameraPreview(MainActivity.this);
        }
    }


}
