package com.wscodelabs.callLogs;

import android.provider.CallLog;
import android.provider.CallLog.Calls;
import java.lang.StringBuffer;
import android.database.Cursor;
import java.util.Date;
import android.content.Context;
import org.json.*;
import android.net.Uri;
import android.os.Build;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.Date;

import java.util.Map;

public class CallLogModule extends ReactContextBaseJavaModule {

    private Context context;

    public CallLogModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context= reactContext;
    }

    @Override
    public String getName() {
        return "CallLogs";
    }

    @ReactMethod
    public void show( Callback callBack) {
        boolean hasSlotId = false;
        Uri uri;
        StringBuffer stringBuffer = new StringBuffer();
        String deviceMan = android.os.Build.MANUFACTURER;
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT && "samsung".equals(deviceMan)){
            hasSlotId = true;
            uri = Uri.parse("content://logs/call");
        }else {
            uri = CallLog.Calls.CONTENT_URI;
        }
        Cursor cursor = this.context.getContentResolver().query(uri,
                null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor == null) {
            callBack.invoke("[]");
            return;
        }
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int subIndex = cursor.getColumnIndex("subscription_id");
        int simIndex = cursor.getColumnIndex(hasSlotId ? "sim_id" : "simid");

        JSONArray callArray = new JSONArray();
        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(duration);
            String cachedName = cursor.getString(name);
            String sub = subIndex == -1 ? null : cursor.getString(subIndex);
            String simId = simIndex == -1 ? null : cursor.getString(simIndex);
            String subscription = (sub == null || "".equals(sub))? simId: sub;
            subscription = subscription == null ? "" : subscription;
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }

            JSONObject callObj = new JSONObject();
            try{
                callObj.put("phoneNumber",phNumber);
                callObj.put("callType", dir);
                callObj.put("callDate", callDate);
                callObj.put("callDuration", callDuration);
                callObj.put("callDayTime", callDayTime);
                callObj.put("cachedName", cachedName);
                callObj.put("subscription", subscription);
                if (hasSlotId) {
                    callObj.put("slotId", simId);
                }
                callArray.put(callObj);
            }
            catch(JSONException e){
                e.printStackTrace();
            }


        }
        cursor.close();
        callBack.invoke(callArray.toString());
    }

    @ReactMethod
    public void getLog(String type, String phone, String startTime, Callback callBack) {
        boolean hasSlotId = false;
        Uri uri;
        StringBuffer stringBuffer = new StringBuffer();
        String deviceMan = android.os.Build.MANUFACTURER;
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT && "samsung".equals(deviceMan)){
            hasSlotId = true;
            uri = Uri.parse("content://logs/call");
        }else {
            uri = CallLog.Calls.CONTENT_URI;
        }
        Cursor cursor = this.context.getContentResolver().query(uri,
                null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor == null) {
            callBack.invoke("[]");
            return;
        }
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int subIndex = cursor.getColumnIndex("subscription_id");
        int simIndex = cursor.getColumnIndex(hasSlotId ? "sim_id" : "simid");

        JSONArray callArray = new JSONArray();
        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(duration);
            String cachedName = cursor.getString(name);
            String sub = subIndex == -1 ? null : cursor.getString(subIndex);
            String simId = simIndex == -1 ? null : cursor.getString(simIndex);
            String subscription = (sub == null || "".equals(sub))? simId: sub;
            subscription = subscription == null ? "" : subscription;
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }

            if (phNumber.equals(phone)) continue;
            if (dir.equals(type)) continue;

            long logTime = Long.parseLong(callDate);
            long start = Long.parseLong(startTime);
            Date startDate = new Date(start);
            Date logDate = new Date(logTime);
            if (startDate.compareTo(logDate) > 0) continue;

            JSONObject callObj = new JSONObject();
            try{
                callObj.put("phoneNumber",phNumber);
                callObj.put("callType", dir);
                callObj.put("callDate", callDate);
                callObj.put("callDuration", callDuration);
                callObj.put("callDayTime", callDayTime);
                callObj.put("cachedName", cachedName);
                callObj.put("subscription", subscription);
                if (hasSlotId) {
                    callObj.put("slotId", simId);
                }
                callArray.put(callObj);
            }
            catch(JSONException e){
                e.printStackTrace();
            }


        }
        cursor.close();
        callBack.invoke(callArray.toString());
    }
}