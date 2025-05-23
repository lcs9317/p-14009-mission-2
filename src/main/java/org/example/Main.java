package org.example;

import java.util.Scanner;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {
    public static void main(String[] args) {
        int WiseCount = 0;
        WiseSpeak[] wisespeaks = new WiseSpeak[100];
        System.out.println("===명언===");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("메뉴를 입력하세요: ");
            String input = sc.nextLine();
            if (input.equals("종료")) {
                break;
            } else if (input.equals("등록")) {
                System.out.print("명언을 입력하세요: ");
                String wisespeak = sc.nextLine();


                System.out.print("작가를 입력하세요: ");
                String wiseman = sc.nextLine();
                wisespeaks[WiseCount] = new WiseSpeak(wisespeak, wiseman, WiseCount);
                WiseCount++;

                System.out.println(WiseCount + "번 명언이 등록되었습니다.");
            } else if (input.equals("조회")) {
                printAllWiseSpeaks(wisespeaks, WiseCount);
            } else if (input.startsWith("삭제")) {
                String[] parts = input.split("\\?id=");

                if (parts.length == 2) {
                    try {
                        int deleteIndex = Integer.parseInt(parts[1]) - 1;
                        if (deleteIndex >= 0 && deleteIndex < WiseCount) {
                            deleteWiseSpeak(wisespeaks, deleteIndex);

                        }
                        else {
                            System.out.println("존재하지 않는 번호입니다.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 번호 형식입니다.");
                    }

                } else {
                    System.out.println("명령어 형식이 올바르지 않습니다.");
                }


            } else if (input.startsWith("수정")) {
                String[] parts = input.split("\\?id=");
                if (parts.length == 2) {
                    try {
                        int updateIndex = Integer.parseInt(parts[1]) - 1;
                        if (wisespeaks[updateIndex] == null || wisespeaks[updateIndex].getId() == -1) {
                            System.out.println(++updateIndex + "번 명언은 존재하지 않습니다.");

                        }
                        else if (updateIndex >= 0 && updateIndex < WiseCount) {
                            updateWiseSpeak(wisespeaks, updateIndex, sc);
                        }
                        else {
                            System.out.println("잘못된 번호입니다.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 번호 형식입니다.");
                    }

                } else {
                    System.out.println("명령어 형식이 올바르지 않습니다.");
                }
            }
            else {
                System.out.println("잘못된 입력입니다.");
            }


        }
        sc.close();

    }

    public static class WiseSpeak {
        private final String wisespeak;
        private final String wiseman;
        private int id;
        public WiseSpeak(String wisespeak, String wiseman, int id) {
            this.id = id;
            this.wisespeak = wisespeak;
            this.wiseman = wiseman;
        }

        public String getWisespeak() {
            return wisespeak;
        }

        public String getWiseman() {
            return wiseman;
        }
        public int getId() {
            return id;
        }



    }
    private static void deleteWiseSpeak(WiseSpeak[] wisespeaks, int id) {
        if(wisespeaks[id].id == -1 || wisespeaks[id] == null) {
            id += 1;
            System.out.println(id + "번 명언은 존재하지 않습니다.");

        }
        else {
            wisespeaks[id].id = -1;
            System.out.println((id + 1) + "번 명언이 삭제되었습니다.");
        }

    }
    private static void updateWiseSpeak(WiseSpeak[] wisespeaks, int updateIndex, Scanner sc) {
        System.out.println("명언(기존): " + wisespeaks[updateIndex].getWisespeak());
        System.out.print("명언: ");
        String wisespeak = sc.nextLine();
        System.out.println("작가(기존): " + wisespeaks[updateIndex].getWiseman());
        System.out.print("작가: ");
        String wiseman = sc.nextLine();
        wisespeaks[updateIndex] = new WiseSpeak(wisespeak, wiseman, updateIndex);
        System.out.println((updateIndex + 1) + "번 명언이 수정되었습니다.");
    }

    private static void printWiseSpeak(WiseSpeak wisespeak) {
        System.out.print(" / " + wisespeak.getWisespeak());
        System.out.println(" / " + wisespeak.getWiseman());
    }
    private static void printAllWiseSpeaks(WiseSpeak[] wisespeaks, int WiseCount) {
        System.out.println("번호 / 작가 / 명언");
        System.out.println("-----------------------------------------");
        for (int i = 0; i < WiseCount; i++) {
            if (wisespeaks[i] != null && wisespeaks[i].getId() >= 0) {
                System.out.print((i + 1));
                printWiseSpeak(wisespeaks[i]);
            }
        }
    }

}