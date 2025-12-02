package com.example.temiboardgame;

import android.util.Log;
import com.robotemi.sdk.Robot;   // Temi SDK import

public class TemiController {

    // 1~12 → 위치 이름 매핑 (이건 이미 해둔 거 그대로 쓰면 됨)
    public static String getLocationNameForPosition(int position) {
        switch (position) {
            case 1:  return "1";
            case 2:  return "2";
            case 3:  return "3";
            case 4:  return "4";
            case 5:  return "5";
            case 6:  return "6";
            case 7:  return "7";
            case 8:  return "8";
            case 9:  return "9";
            case 10: return "10";
            case 11: return "finish";
            case 12: return "start";
            default: return null;
        }
    }

    public static void moveToPosition(int position) {
        String locationName = getLocationNameForPosition(position);
        if (locationName == null) {
            Log.w("TemiController", "알 수 없는 칸: " + position);
            return;
        }

        Log.d("TemiController", "테미 이동: " + locationName);

        // 🔥 여기서 실제 로봇 이동
        Robot robot = Robot.getInstance();
        if (robot != null) {
            robot.goTo(locationName);   // 위치 이름은 Temi에 저장한 것과 100% 같아야 함
        }
    }
}
