package com.example.temiboardgame;

public class TileInfoProvider {

    // 칸 번호에 따른 제목 반환
    public static String getTitle(int position) {
        switch (position) {
            case 1: return "시작 - 눈싸움게임";
            case 2: return "압력 맞추기";
            case 3: return "2칸 뒤로";
            case 4: return "무인도(감옥)";
            case 5: return "인간 빼빼로 게임";
            case 6: return "황금 열쇠";
            case 7: return "시간 맞추기";
            case 8: return "올림픽";
            case 9: return "불빛 반응 속도";
            case 10: return "물 양 맞추기";
            case 11: return "황금 열쇠";
            case 12: return "손잡고 온도 올리기";
            default: return position + "번 칸";
        }
    }

    // 칸 번호에 따른 설명(미션) 반환
    public static String getDescription(int position) {
        switch (position) {
            case 1: return "눈싸움 게임! 눈을 먼저 깜빡이는 사람이 패배!";
            case 2: return "압력을 정확하게 맞춰보세요!";
            case 3: return "아쉽지만 2칸 뒤로 이동!";
            case 4: return "무인도(감옥)! 다음 턴은 쉽니다.";
            case 5: return "인간 빼빼로 게임에 도전해보세요!";
            case 6: return "황금 열쇠! 특별 미션을 수행하세요.";
            case 7: return "정해진 시간에 최대한 가깝게 맞춰보세요!";
            case 8: return "올림픽 종목 중 하나를 골라 도전!";
            case 9: return "불빛이 켜지는 순간, 누구보다 빠르게 반응하세요!";
            case 10: return "지정된 양에 최대한 가깝게 물을 맞춰보세요!";
            case 11: return "황금 열쇠! 좋은 일 또는 나쁜 일이 벌어질 수 있어요.";
            case 12: return "손을 잡고 체온을 올려보세요! 마지막 칸입니다.";
            default: return "새로운 모험이 기다리고 있는 칸입니다.";
        }
    }
}
