package com.scandecode_example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.Image;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import constants.Const;

public class Export_national extends AppCompatActivity {


    //sw value
    String user_id = Const.User_id;
    //String user_pw = SharedPrefManager.getInstance(this).getUserPW();
    PDAExportThread thread = new PDAExportThread("");
    Context mContext;
    ArrayList<Object> arr = new ArrayList<Object>();
    ArrayList<Object> Bufarr = new ArrayList<Object>();

    private final static String TAG = "export_national";
    //private IScannerServiceHoneywell mHService = null;
//    private ScanInterface scanDecode;
    int currentApiVersion;


    private int scancount = 0;
    boolean serial_check = false;
    String error_result = "";
    String export_company = "";

    TextView tv_date,tv_count;
    Spinner target_numb2;
    ToggleButton toggleButtonRepeat;
//    ZXingScannerView barcode_scanner;
    Button btnClear,button_undo,button_send;
    EditText mReception;

    //pop up
    String add_serial;
    RelativeLayout rlPopupManual;
    RadioGroup Radio_type = null;
    Button btn_add_serial,btn_add_cancel,manual_input_data;
    EditText Serial_no;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalyzer analyzer;
    Context mcontext;
    Activity mactivity;
    ProcessCameraProvider processCameraProvider;
    ArrayList arrayList = new ArrayList();

    Vibrator vib;
    ToneGenerator tg;
    Switch flash_light;

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
                            String data = rawValue.trim();
                            if(!arrayList.contains(data)){
                                if(data.trim().length()> 10){
                                    if(data.startsWith("CP-")){
                                        arrayList.add(data);
                                        arr.add(data);
                                        mReception.append(""+data+"\n");
                                        scancount+=1;
                                        vib.vibrate(1000);
                                        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                                        tv_count.setText(""+(scancount));
                                    }
//                                    scancount+=1;
                                    //tvcound.setText(getString(R.string.scan_time)+scancount+"");
//                                    mReception.append(data+"\n");
                                }else{
                                    Toast.makeText(mContext, "QR코드를 확인해주세요", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_export_national);
        mainScreen();

        flash_light = findViewById(R.id.switchFlashLight);
        mcontext = this;
        mactivity = this;

        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
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
//                    barcode_scanner.setResultHandler(Export_national.this);
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
                }
                else {
                    ////////////////////////
//                    barcode_scanner.stopCamera();
//                    barcode_scanner.setVisibility(View.INVISIBLE);
                    processCameraProvider.unbindAll();
                    previewView.setVisibility(View.GONE);
                    ////////////////////////
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scancount == 0){

                }
                else{
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Export_national.this);
                    alert_confirm.setMessage("초기화 합니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(mContext, "초기화 하였습니다", Toast.LENGTH_SHORT).show();
                                    arrayList.clear();
                                    mReception.setText(""); //清屏
                                    arr.clear();
                                    scancount=0;
                                    tv_count.setText(scancount+"");
                                }
                            }).setNegativeButton("취소",
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
            }
        });

        button_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arr.size() <= 0){
                    Toast.makeText(mContext, "작업 이후에 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    int delete_length = 0;
                    delete_length = 0;
                    delete_length += (arr.get(arr.size()-1).toString().length())+1;
                    arrayList.remove(arrayList.size()-1);
                    arr.remove(arr.size()-1);
                    Toast.makeText(mContext, "마지막 작업이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    mReception.setText(mReception.getText().subSequence(0,(mReception.length()-delete_length)));
                    scancount-=1;
                    tv_count.setText(scancount+"");
                }
            }
        });
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //array count check
                if(arr.size() < 1){
                    Toast.makeText(mContext, "스캔을 먼저 진행해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Export_national.this);
                    alert_confirm.setMessage("서버로 전송합니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    button_send.setEnabled(false);
                                    for (int i = 0; i < arr.size(); i++) {
                                        Bufarr.add(arr.get(i));
                                    }
                                    try {
                                        thread.start();
                                    } catch (Exception e) {
                                        thread = null;
                                        thread = new PDAExportThread("");
                                        thread.start();
                                    }
                                }

                            }).setNegativeButton("취소",
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
            }
        });

//
//        btn_go_to_menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Export_national.this);
//                alert_confirm.setMessage("Go back to Main Menu").setCancelable(false).setPositiveButton("Confirm",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        }).setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // 'No'
//                                return;
//                            }
//                        });
//                AlertDialog alert = alert_confirm.create();
//                alert.show();
//            }
//        });

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
        target_numb2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPrefManager.getInstance(Export_national.this).setNationalexport_spinner(String.valueOf(i));
                Const.ExportSpinner = String.valueOf(i);
                target_numb2.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_add_serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "ok";
                //adding serial no to list.
                int id = Radio_type.getCheckedRadioButtonId();
                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                RadioButton rb = (RadioButton) findViewById(id);

                //rb check
                if(rb != null){
                    String check_serial = Serial_no.getText().toString().trim();
                    if(check_serial.length() != 6){
                        Toast.makeText(mContext, "Serial 번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(arr.size()==0){
                            mReception.setText("");
                        }
                        add_serial = rb.getText().toString().trim()+" "+check_serial;
                        for (int i = 0; i < arr.size(); i++) {
                            if (arr.get(i).equals(add_serial)) {
                                serial_check = true;
                            }
                        }
                        if (!serial_check) {
                            arr.add(add_serial);
                            scancount+=1;
                            vib.vibrate(1000);
                            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                            mReception.append(""+add_serial+"\n");
//                        mReception.append("" + arr.get(textadding).toString() + "   >>   [ ");
//                        mReception.append(String.format("%3d", textadding + 1));
//                        mReception.append(" ] Scanned\n");
                            tv_count.setText(""+(scancount));
                            viewManualPopup();
                        } else {
                            Toast.makeText(mContext, "이미 입력된 Serial 번호입니다.", Toast.LENGTH_SHORT).show();
                        }
                        serial_check = false;
                    }//serial 번호 체크
                }
                else{
                    Toast.makeText(mContext, "타입을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
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
            }
        });
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

        Camera camera = processCameraProvider1.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
        CameraControl cameracontroler = camera.getCameraControl();
        if(flash_light.isChecked()){
            cameracontroler.enableTorch(true);
        }
        else{
            cameracontroler.enableTorch(false);
        }
        flash_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    cameracontroler.enableTorch(true);
                    flash_light.setText("플래쉬 ON");
                }
                else {
                    cameracontroler.enableTorch(false);
                    flash_light.setText("플래쉬 OFF");
                }
            }
        });
//        processCameraProvider1.bindToLifecycle(this,cameraSelector,preview,imageCapture,imageAnalysis);
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
//        scanDecode.onDestroy();//回复初始状态
    }

    protected void mainScreen()
    {
        mContext = this;

        tv_count = (TextView)findViewById(R.id.tv_count); 
        tv_date = (TextView)findViewById(R.id.tv_date);
        Date today;
        String date_result;
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        today = new Date();
        date_result = formatter.format(today);
        tv_date.setText(date_result);
        toggleButtonRepeat = (ToggleButton) findViewById(R.id.button_repeat);
        //zxing
//        barcode_scanner = (ZXingScannerView)findViewById(R.id.barcode_scanner);
        btnClear = (Button)findViewById(R.id.btnClear);
        button_undo = (Button)findViewById(R.id.button_undo);
        button_send = (Button)findViewById(R.id.button_send);
        btn_add_serial = (Button)findViewById(R.id.btn_add_serial);
        btn_add_cancel = (Button)findViewById(R.id.btn_add_cancel);
        manual_input_data = (Button)findViewById(R.id.manual_input_data);
        mReception = (EditText)findViewById(R.id.mReception);
        Serial_no = (EditText)findViewById(R.id.Serial_no);
        rlPopupManual = (RelativeLayout)findViewById(R.id.rlPopupManual);

        ArrayList<String> temp_dest = new ArrayList<String>();
        Iterator it;
        it = Const.export_companies.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue().toString().split("@")[1].equals("1") && !pair.getValue().toString().split("@")[2].equals(Const.company_id)){
                temp_dest.add(pair.getKey().toString());
            }
        }
        String[] destinations = new String[temp_dest.size()];
        temp_dest.toArray(destinations);

        String compareSpinner;
        if(Const.ExportSpinner == null){
            compareSpinner = "0";
        }else{
            compareSpinner = Const.ExportSpinner;
        }
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item2,destinations);
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.spinner_item2);
        target_numb2 = (Spinner) findViewById(R.id.target_numb2);
        target_numb2.setAdapter(spinnerArrayAdapter2);
        if (compareSpinner != null) {
            target_numb2.setSelection(Integer.parseInt(compareSpinner));
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
            if(Const.all_pallet_types.get(i).toString().substring(0,3).equals("CP-")){
                RadioButton rb = new RadioButton(mContext);
                rb.setTextSize(30);
                rb.setText(Const.all_pallet_types.get(i)+"");
                rb.setTag(""+(i+1));
                rb.setTextColor(Color.BLACK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    rb.setButtonTintList(colorStateList);
                }
                Radio_type.addView(rb);
            }
        }
        tv_count.setText("0");
    }

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

    /*@Override
    public void handleResult(Result result) {

        String data = result.getText().trim();
        if(data.length() == 13){
            for(int i=0; i<arr.size();i++){
                if(arr.get(i).equals(data.trim())){
                    serial_check = true;
                }
            }
            if(serial_check){
                serial_check = false;
                Toast.makeText(mContext, "이미 등록된 시리얼 번호 입니다.", Toast.LENGTH_SHORT).show();
            }else{
                scancount+=1;
                tv_count.setText(scancount+"");
                arr.add(data);

                mReception.append(data+"\n");
            }
        }
        else{
            Toast.makeText(mContext, "잘못된 시리얼 번호 입니다.", Toast.LENGTH_SHORT).show();
        }
        barcode_scanner.setResultHandler(Export_national.this);
        barcode_scanner.startCamera();
    }*/

    class PDAExportThread extends Thread {
        String server_query;
        public PDAExportThread(String query) {
            server_query = query;
        }
        public void run() {
            try {
                /////////////////////////////
                String serial_temp = "";
                String codes_temp = "";
                String sending_query = "";
                String return_string = "";
                JSONObject obj;
                String error_message;

                PrintStream ps = null;
                URL url = new URL("http://www.npc-rental.com:1337/graphql");       // URL 설정
                URLConnection con = url.openConnection();   // 접속
                con.setRequestProperty("Authorization","Bearer "+ Const.Tokeninfo);
                con.setRequestProperty("Method","POST");
                con.setDoOutput(true);
                ps = new PrintStream(con.getOutputStream());

                for(int i = 0;i<Bufarr.size();i++){
                    if(i == 0 && i != (Bufarr.size()-1)){
                        serial_temp = Bufarr.get(i).toString()+",";
                        codes_temp= Const.pallet_types.get(Bufarr.get(i).toString().split(" ")[0])+",";
                    }else if(i == (Bufarr.size()-1)){
                        serial_temp = serial_temp+Bufarr.get(i).toString();
                        codes_temp = codes_temp+ Const.pallet_types.get(Bufarr.get(i).toString().split(" ")[0]);
                    }else{
                        serial_temp = serial_temp+Bufarr.get(i).toString()+",";
                        codes_temp = codes_temp+ Const.pallet_types.get(Bufarr.get(i).toString().split(" ")[0])+",";
                    }
                }
                export_company = Const.export_companies.get(target_numb2.getSelectedItem().toString()).split("@")[2];

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
                        "      prv_company: "+ Const.company_id+"\n" +
                        "      cur_company: "+ Const.company_id+"\n" +
                        "      nxt_company: "+export_company+"\n" +
                        "      rsb_no : \""+serial_temp+"\"\n" +
                        "      container_no : \"\"\n" +
                        "      seal_no : \"\"\n" +
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
                            mReception.setText("권한이 없습니다.");
                        }
                    });
                }
                else if(!error_message.equals("")){
                    String[] split_list = error_message.split("/");

                    for(int i=0; i<split_list.length;i++){
                        error_result += split_list[i]+"\n";
                    }
                    if(split_list.length == 1){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mReception.setText(error_result+" 번호에 에러가 있습니다 확인해주세요.");
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mReception.setText(error_result+" 번호에 에러가 있습니다 확인해주세요.");
                            }
                        });
                    }
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "전송 되었습니다.", Toast.LENGTH_LONG).show();
                            mReception.setText("");
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scancount = 0;
                        tv_count.setText("0");
                        button_send.setEnabled(true);
                    }
                });

                for (int i = arr.size() - 1; i >= 0; i--) {
                    arr.remove(i);
                }
                //--------------------------
                //   서버에서 전송받기
                //--------------------------
                for(int i = Bufarr.size()-1;i>=0;i--){
                    Bufarr.remove(i);
                }
                //////////////////
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public static String replace_character(String url) {
        url= url.replace("+","%2B");
        return url;
    }
}
