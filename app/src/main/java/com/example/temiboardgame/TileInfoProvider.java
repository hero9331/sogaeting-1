package com.example.temiboardgame;

public class TileInfoProvider {

    // 칸 번호에 따른 제목 반환 (업데이트된 배치 반영)
    public static String getTitle(int position) {
        switch (position) {
            case 1:
                return "시작";
            case 2:
                return "눈싸움 심박수 올리기";
            case 3:
                return "불빛 반응 속도";
            case 4:
                return "무인도(감옥)";
            case 5:
                return "앞으로 한 칸!";
            case 6:
                return "압력 맞추기";
            case 7:
                return "시간 맞추기";
            case 8:
                return "앞으로 한 칸!";
            case 9:
                return "인간 빼빼로 게임";
            case 10:
                return "시간 맞추기";
            case 11:
                return "앞으로 한 칸!";
            case 12:
                return "불빛 반응 속도";
            default:
                return position + "번 칸";
        }
    }

    // 칸 번호에 따른 설명(미션) 반환
    public static String getDescription(int position) {
        switch (position) {
            case 1:
                return "게임의 시작점입니다! 주사위를 굴려보세요.";
            case 2:
                return "서로 눈을 마주치며 심박수를 올려보세요! (눈싸움)";
            case 3:
                return "불빛이 켜지는 순간, 누구보다 빠르게 반응하세요!";
            case 4:
                return "무인도(감옥)! 다음 턴은 쉽니다.";
            case 5:
                return "앞으로 한 칸 이동!";
            case 6:
                return "두 사람이 손을 잡고 일정 압력을 맞춰보세요 >.<";
            case 7:
                return "3초를 최대한 맞춰보세요! (2.5초~3.5초까지 인정)";
            case 8:
                return "앞으로 한 칸 이동!";
            case 9:
                return "눈을 감고 상대방에게 가까워져보세요 >.<";
            case 10:
                return "3초를 최대한 맞춰보세요! (2.5초~3.5초까지 인정)";
            case 11:
                return "앞으로 한 칸 이동!";
            case 12:
                return "불빛이 켜지는 순간, 누구보다 빠르게 반응하세요!";
            default:
                return "새로운 모험이 기다리고 있는 칸입니다.";
        }
    }

}