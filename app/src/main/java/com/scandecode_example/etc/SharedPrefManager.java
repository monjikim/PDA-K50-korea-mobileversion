package com.scandecode_example.etc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;


public class SharedPrefManager {
    private static final String TAG = SharedPrefManager.class.getSimpleName();
    private static SharedPrefManager _instance = null;
    private SharedPreferences mShreadPref;
    private Editor mEditor;
    private Map<String, String> mConfirmKeySet;
    public String cupSize_1 ;
    public String cupSize_2 ;
    public String cupSize_3 ;
    public static final String SHARED_PREFERENCES_ID = "glovis_qr";


    public static SharedPrefManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new SharedPrefManager(context);
        }

        return _instance;
    }

    public SharedPrefManager(Context context) {
        mShreadPref = context.getSharedPreferences(SHARED_PREFERENCES_ID, Activity.MODE_PRIVATE);

        if (mShreadPref != null) {
            mEditor = mShreadPref.edit();
            mConfirmKeySet = new HashMap<String, String>();
        } else {
            //Xlog.e(TAG,"SharedPreferences fail");
        }
    }
    public String getAndroidDeviceID() {
        String deviceId = mConfirmKeySet.get(SharedPrefApi.DEVICE_ID);
        if(deviceId != null){
            return deviceId;
        }else{
            deviceId = mShreadPref.getString(SharedPrefApi.DEVICE_ID, null);
            mConfirmKeySet.put(SharedPrefApi.DEVICE_ID, deviceId);
        }
        return deviceId;
    }
    public String getPushID(){
        String pushId = mConfirmKeySet.get(SharedPrefApi.PUSH_ID);
        if(pushId != null){
            return pushId;
        }else{
            pushId = mShreadPref.getString(SharedPrefApi.PUSH_ID, null);
            mConfirmKeySet.put(SharedPrefApi.PUSH_ID, pushId);
        }
//		pushId="f7f8a33b-66ad-3b3a-9d66-a337e695ecc3";	//conoz
        return pushId;
    }
    public void setAndroidDeviceID(String deviceId){
//		mConfirmKeySet.put(SharedPrefApi.DEVICE_ID, deviceId);

        mEditor.putString(SharedPrefApi.DEVICE_ID, deviceId);
        mEditor.commit();
    }

    public void setPushID(String pushId){
//		mConfirmKeySet.put(SharedPrefApi.PUSH_ID, pushId);

        mEditor.putString(SharedPrefApi.PUSH_ID, pushId);
        mEditor.commit();
    }
    public void setIndiaExport_spinner(String IndiaExport_spinner){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.IndiaExport_spinner, IndiaExport_spinner);
        mEditor.commit();
    }
    public String getIndiaExport_spinner(){
        String IndiaExport_spinner = mConfirmKeySet.get(SharedPrefApi.IndiaExport_spinner);
        if(IndiaExport_spinner != null){
            return IndiaExport_spinner;
        }else{
            IndiaExport_spinner = mShreadPref.getString(SharedPrefApi.IndiaExport_spinner, null);
            mConfirmKeySet.put(SharedPrefApi.IndiaExport_spinner, IndiaExport_spinner);
        }
        return IndiaExport_spinner;
    }
    public void setIndiaImport_spinner(String IndiaImport_spinner){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.IndiaImport_spinner, IndiaImport_spinner);
        mEditor.commit();
    }
    public String getIndiaImport_spinner(){
        String IndiaImport_spinner = mConfirmKeySet.get(SharedPrefApi.IndiaImport_spinner);
        if(IndiaImport_spinner != null){
            return IndiaImport_spinner;
        }else{
            IndiaImport_spinner = mShreadPref.getString(SharedPrefApi.IndiaImport_spinner, null);
            mConfirmKeySet.put(SharedPrefApi.IndiaImport_spinner, IndiaImport_spinner);
        }
        return IndiaImport_spinner;
    }
    public void setexport_spinner(String export_spinner){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.EXPORT_SPINNER, export_spinner);
        mEditor.commit();
    }
    public String getexport_spinner(){
        String export_spinner = mConfirmKeySet.get(SharedPrefApi.EXPORT_SPINNER);
        if(export_spinner != null){
            return export_spinner;
        }else{
            export_spinner = mShreadPref.getString(SharedPrefApi.EXPORT_SPINNER, null);
            mConfirmKeySet.put(SharedPrefApi.EXPORT_SPINNER, export_spinner);
        }
        return export_spinner;
    }
    public void setNationalexport_spinner(String export_spinner){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.NATIONALEXPORT_SPINNER, export_spinner);
        mEditor.commit();
    }
    public String getNationalexport_spinner(){
        String export_spinner = mConfirmKeySet.get(SharedPrefApi.NATIONALEXPORT_SPINNER);
        if(export_spinner != null){
            return export_spinner;
        }else{
            export_spinner = mShreadPref.getString(SharedPrefApi.NATIONALEXPORT_SPINNER, null);
            mConfirmKeySet.put(SharedPrefApi.NATIONALEXPORT_SPINNER, export_spinner);
        }
        return export_spinner;
    }
    public void setImport_spinner(String import_spinner){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.IMPORT_SPINNER, import_spinner);
        mEditor.commit();
    }
    public String getImport_spinner(){
        String import_spinner = mConfirmKeySet.get(SharedPrefApi.IMPORT_SPINNER);
        if(import_spinner != null){
            return import_spinner;
        }else{
            import_spinner = mShreadPref.getString(SharedPrefApi.IMPORT_SPINNER, null);
            mConfirmKeySet.put(SharedPrefApi.IMPORT_SPINNER, import_spinner);
        }
        return import_spinner;
    }
    public void setNationalImport_spinner(String NationalImport_spinner){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.NATIONALIMPORT_SPINNER, NationalImport_spinner);
        mEditor.commit();
    }
    public String getNationalImport_spinner(){
        String NATIONALIMPORT_SPINNER = mConfirmKeySet.get(SharedPrefApi.NATIONALIMPORT_SPINNER);
        if(NATIONALIMPORT_SPINNER != null){
            return NATIONALIMPORT_SPINNER;
        }else{
            NATIONALIMPORT_SPINNER = mShreadPref.getString(SharedPrefApi.NATIONALIMPORT_SPINNER, null);
            mConfirmKeySet.put(SharedPrefApi.NATIONALIMPORT_SPINNER, NATIONALIMPORT_SPINNER);
        }
        return NATIONALIMPORT_SPINNER;
    }
    public void setRepair_spinner(String repair_spinner){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.REPAIR_SPINNER, repair_spinner);
        mEditor.commit();
    }
    public String getRepair_spinner(){
        String repair_spinner = mConfirmKeySet.get(SharedPrefApi.REPAIR_SPINNER);
        if(repair_spinner != null){
            return repair_spinner;
        }else{
            repair_spinner = mShreadPref.getString(SharedPrefApi.REPAIR_SPINNER, null);
            mConfirmKeySet.put(SharedPrefApi.REPAIR_SPINNER, repair_spinner);
        }
        return repair_spinner;
    }
    public void setUserID(String userID){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.USER_ID, userID);
        mEditor.commit();
    }
    public String getUserID(){
        String userID = mConfirmKeySet.get(SharedPrefApi.USER_ID);
        if(userID != null){
            return userID;
        }else{
            userID = mShreadPref.getString(SharedPrefApi.USER_ID, null);
            mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        }
        return userID;
    }
    public void setUserPW(String userPW){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.USER_PW, userPW);
        mEditor.commit();
    }
    public String getUserPW(){
        String userPW = mConfirmKeySet.get(SharedPrefApi.USER_PW);
        if(userPW != null){
            return userPW;
        }else{
            userPW = mShreadPref.getString(SharedPrefApi.USER_PW, null);
            mConfirmKeySet.put(SharedPrefApi.USER_PW, userPW);
        }
        return userPW;
    }

    public void setDeviceName(String deviceName){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.DEVICE_NAME, deviceName);
        mEditor.commit();
    }
    public String getDeviceName(){
        String deviceName = mConfirmKeySet.get(SharedPrefApi.DEVICE_NAME);
        if(deviceName != null){
            return deviceName;
        }else{
            deviceName = mShreadPref.getString(SharedPrefApi.DEVICE_NAME, null);
            mConfirmKeySet.put(SharedPrefApi.DEVICE_NAME, deviceName);
        }
        return deviceName;
    }

    public void setPourAmount(String pourAmount){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.DEVICE_POUR_AMOUNT, pourAmount);
        mEditor.commit();
    }
    public String getPourAmount(){
        String pourAmount = mConfirmKeySet.get(SharedPrefApi.DEVICE_POUR_AMOUNT);
        if(pourAmount != null){
            return pourAmount;
        }else{
            pourAmount = mShreadPref.getString(SharedPrefApi.DEVICE_POUR_AMOUNT, null);
            mConfirmKeySet.put(SharedPrefApi.DEVICE_POUR_AMOUNT, pourAmount);
        }
        return pourAmount;
    }
    public void setPourPrice(String pourPrice){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.DEVICE_POUR_PRICE, pourPrice);
        mEditor.commit();
    }
    public String getPourPrice(){
        String pourPrice = mConfirmKeySet.get(SharedPrefApi.DEVICE_POUR_PRICE);
        if(pourPrice != null){
            return pourPrice;
        }else{
            pourPrice = mShreadPref.getString(SharedPrefApi.DEVICE_POUR_PRICE, null);
            mConfirmKeySet.put(SharedPrefApi.DEVICE_POUR_PRICE, pourPrice);
        }
        return pourPrice;
    }
    public void setPourTotal(String pourTotal){
//		mConfirmKeySet.put(SharedPrefApi.USER_ID, userID);
        mEditor.putString(SharedPrefApi.DEVICE_POUR_TOTAL, pourTotal);
        mEditor.commit();
    }
    public String getPourTotal(){
        String pourTotal = mConfirmKeySet.get(SharedPrefApi.DEVICE_POUR_TOTAL);
        if(pourTotal != null){
            return pourTotal;
        }else{
            pourTotal = mShreadPref.getString(SharedPrefApi.DEVICE_POUR_TOTAL, null);
            mConfirmKeySet.put(SharedPrefApi.DEVICE_POUR_TOTAL, pourTotal);
        }
        return pourTotal;
    }

    public void setPhoneNumber(String phoneNumber){
//		mConfirmKeySet.put(SharedPrefApi.USER_PHONE_NUMBER, phoneNumber);

        mEditor.putString(SharedPrefApi.USER_PHONE_NUMBER, phoneNumber);
        mEditor.commit();
    }
    public String getPhoneNumber(){
        String phoneNumber = mConfirmKeySet.get(SharedPrefApi.USER_PHONE_NUMBER);
        if(phoneNumber != null){
            return phoneNumber;
        }else{
            phoneNumber = mShreadPref.getString(SharedPrefApi.USER_PHONE_NUMBER, null);
            mConfirmKeySet.put(SharedPrefApi.USER_PHONE_NUMBER, phoneNumber);
        }
        return phoneNumber;
    }
    public void setUserType(String userType){
//		mConfirmKeySet.put(SharedPrefApi.USER_TYPE, userType);

        mEditor.putString(SharedPrefApi.USER_TYPE, userType);
        mEditor.commit();
    }
    public String getUserType(){
        String userType = mConfirmKeySet.get(SharedPrefApi.USER_TYPE);
        if(userType != null){
            return userType;
        }else{
            userType = mShreadPref.getString(SharedPrefApi.USER_TYPE, null);
            mConfirmKeySet.put(SharedPrefApi.USER_TYPE, userType);
        }
        return userType;
    }

    public String getUserMode(){
        String userMode = 	mConfirmKeySet.get(SharedPrefApi.USER_MODE);
        if(userMode != null){
            return userMode;
        }else{
            userMode = mShreadPref.getString(SharedPrefApi.USER_MODE, null);
            mConfirmKeySet.put(SharedPrefApi.USER_MODE, userMode);
        }
        return userMode;
    }

    public void setUserMode(String mode){
//		mConfirmKeySet.put(SharedPrefApi.USER_MODE, mode);
        mEditor.putString(SharedPrefApi.USER_MODE, mode);
        mEditor.commit();
    }

    public String getIpAddress(){
        String ipAddress = 	mConfirmKeySet.get(SharedPrefApi.IP_ADDRESS);
        if(ipAddress != null){
            return ipAddress;
        }else{
            ipAddress = mShreadPref.getString(SharedPrefApi.IP_ADDRESS, null);
            mConfirmKeySet.put(SharedPrefApi.IP_ADDRESS, ipAddress);
        }
        return ipAddress;
    }

    public void setIpAddress(String ipAddress){
        mEditor.putString(SharedPrefApi.IP_ADDRESS, ipAddress);
        mEditor.commit();
    }

    public String getAdminPassword(){
        String pwd = 	mConfirmKeySet.get(SharedPrefApi.ADMIN_PWD);
        if(pwd != null){
            return pwd;
        }else{
            pwd = mShreadPref.getString(SharedPrefApi.ADMIN_PWD, null);
            mConfirmKeySet.put(SharedPrefApi.ADMIN_PWD, pwd);
        }
        return pwd;
    }

    public void setAdminPassword(String pwd){
//		mConfirmKeySet.put(SharedPrefApi.USER_MODE, mode);
        mEditor.putString(SharedPrefApi.ADMIN_PWD, pwd);
        mEditor.commit();
    }


    public void setBrandName(String brandName){
//		mConfirmKeySet.put(SharedPrefApi.USER_MODE, mode);
        mEditor.putString(SharedPrefApi.BRAND_NAME, brandName);
        mEditor.commit();

    }
    public void setKegDate(String kegDate){
//		mConfirmKeySet.put(SharedPrefApi.USER_MODE, mode);
        mEditor.putString(SharedPrefApi.KEG_DATE, kegDate);
        mEditor.commit();

    }

    public String getCupSize1(){
        String cupSize1 = 	mConfirmKeySet.get(SharedPrefApi.CUP_SIZE1);
        if(cupSize1 != null){
            return cupSize1;
        }else{
            cupSize1 = mShreadPref.getString(SharedPrefApi.CUP_SIZE1, null);
            mConfirmKeySet.put(SharedPrefApi.CUP_SIZE1, cupSize1);
        }
        return cupSize1;
    }

    public void setCupSize1(String cupSize1){
        mEditor.putString(SharedPrefApi.CUP_SIZE1, cupSize1);
        mEditor.commit();
    }

    public String getCupSize2(){
        String cupSize2 = 	mConfirmKeySet.get(SharedPrefApi.CUP_SIZE2);
        if(cupSize2 != null){
            return cupSize2;
        }else{
            cupSize2 = mShreadPref.getString(SharedPrefApi.CUP_SIZE2, null);
            mConfirmKeySet.put(SharedPrefApi.CUP_SIZE2, cupSize2);
        }
        return cupSize2;
    }

    public void setCupSize2(String cupSize2){
        mEditor.putString(SharedPrefApi.CUP_SIZE2, cupSize2);
        mEditor.commit();
    }

    public String getCupSize3(){
        String cupSize3 = 	mConfirmKeySet.get(SharedPrefApi.CUP_SIZE3);
        if(cupSize3 != null){
            return cupSize3;
        }else{
            cupSize3 = mShreadPref.getString(SharedPrefApi.CUP_SIZE3, null);
            mConfirmKeySet.put(SharedPrefApi.CUP_SIZE3, cupSize3);
        }
        return cupSize3;
    }

    public void setCupSize3(String cupSize3){
        mEditor.putString(SharedPrefApi.CUP_SIZE3, cupSize3);
        mEditor.commit();
    }


    public String getCupSize_1(){
        return cupSize_1;
    }

    public void setCupSize_1(String cupSize1){
        cupSize_1 = cupSize1;
    }
    public String getCupSize_2(){
        return cupSize_2;
    }

    public void setCupSize_2(String cupSize2){
        cupSize_2 = cupSize2;
    }
    public String getCupSize_3(){
        return cupSize_3;
    }

    public void setCupSize_3(String cupSize3){
        cupSize_3 = cupSize3;
    }

    public class SharedPrefApi{
        public static final String DEVICE_ID = "device_id";
        public static final String PUSH_ID = "push_id";
        public static final String IndiaExport_spinner = "IndiaExport_spinner";
        public static final String IndiaImport_spinner = "IndiaImport_spinner";
        public static final String EXPORT_SPINNER = "export_spinner";
        public static final String NATIONALEXPORT_SPINNER = "NATIONALEXPORT_SPINNER";
        public static final String IMPORT_SPINNER = "import_spinner";
        public static final String NATIONALIMPORT_SPINNER = "NATIONALIMPORT_SPINNER";
        public static final String REPAIR_SPINNER = "repair_spinner";
        public static final String USER_ID = "user_id";
        public static final String USER_PW = "user_pw";
        public static final String USER_PHONE_NUMBER = "user_phone";
        public static final String USER_TYPE = "user_type";

        public static final String USER_MODE = "user_mode";
        public static final String IP_ADDRESS = "ip_address";


        public static final String ADMIN_PWD = "admin_password";

        public static final String BRAND_NAME = "brand_name";
        public static final String KEG_DATE = "keg_date";

        public static final String CUP_SIZE1 = "330";
        public static final String CUP_SIZE2 = "520";
        public static final String CUP_SIZE3 = "700";

        public String cup_size_2 = "";
        public String cup_size_3 = "";

        public static final String DEVICE_NAME = "device_name";
        public static final String DEVICE_POUR_AMOUNT = "pour_amount";
        public static final String DEVICE_POUR_PRICE = "pour_price";
        public static final String DEVICE_POUR_TOTAL = "pour_total";


    }
}
