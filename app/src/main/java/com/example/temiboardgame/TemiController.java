package com.example.temiboardgame;

import android.util.Log;
import com.robotemi.sdk.Robot; // Temi SDK import (활성화)

public class TemiController {

    // 보드 칸(1~12) → Temi 위치 이름 매핑
    public static String getLocationNameForPosition(int position) {
        switch (position) {
            case 1:
                return "1"; // 사용자가 등록한 이름 "1"로 변경
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "10";
            case 11:
                return "11";
            case 12:
                return "12";
            default:
                return null;
        }
    }

    // 해당 위치가 Temi에 저장되어 있는지 확인
    public static boolean isLocationSaved(String locationName) {
        try {
            Robot robot = Robot.getInstance();
            if (robot != null && locationName != null) {
                return robot.getLocations().contains(locationName);
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void moveToPosition(int position) {
        String locationName = getLocationNameForPosition(position);
        if (locationName == null) {
            Log.w("TemiController", "알 수 없는 칸: " + position);
            return;
        }

        Log.d("TemiController", "테미 이동 시도: " + locationName);

        try {
            Robot robot = Robot.getInstance();
            if (robot != null) {
                // 위치 목록 체크 없이 강제 이동 시도 (이름 불일치 문제 배제)
                robot.goTo(locationName);
            }
        } catch (Exception e) {
            Log.e("TemiController", "Robot SDK Error", e);
        }
    }
}
