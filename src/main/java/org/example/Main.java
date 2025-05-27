package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

//지금 wisecount 수정중이고 이거 수정하면 지금 int 하나도 안됨 개선방향을 찾아야함
//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {
    public static void main(String[] args) {
        int WiseCount = 0;

        // 명언 저장 배열 초기화 및 불러오기
        ArrayList<WiseSpeak> wisespeaks = FileManager.loadWiseSpeaks();
        WiseCount = load_lastId();

        // 명언 메뉴 시작
        System.out.println("===명언===");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("메뉴를 입력하세요: ");
            String input = sc.nextLine();
            if (input.equals("종료")) {
                break;
            }
            else if (input.equals("등록")) {
                System.out.print("명언을 입력하세요: ");
                String wisespeak = sc.nextLine();


                System.out.print("작가를 입력하세요: ");
                String wiseman = sc.nextLine();

                wisespeaks.add(new WiseSpeak(wisespeak, wiseman, WiseCount));
                WiseCount++;
                FileManager.saveWiseSpeaks(wisespeaks, WiseCount);


                System.out.println(WiseCount + "번 명언이 등록되었습니다.");
            } else if (input.equals("조회")) {
                printAllWiseSpeaks(wisespeaks, WiseCount);
            } else if (input.startsWith("삭제")) {
                String[] parts = input.split("\\?id=");

                if (parts.length == 2) {
                    try {
                        int deleteIndex = Integer.parseInt(parts[1]) - 1;
                        if (deleteIndex >= 0 && deleteIndex < WiseCount) {
                            FileManager.deleteWiseSpeak(wisespeaks, deleteIndex);

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
                        if (wisespeaks.get(updateIndex) == null || wisespeaks.get(updateIndex).getId() == -1) {
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
            else if (input.startsWith("빌드")) {
                FileManager.buildWiseSpeak(wisespeaks, WiseCount);

            }
            else {
                System.out.println("잘못된 입력입니다.");
            }


        }
        sc.close();

    }

    public static class WiseSpeak {
        private String wisespeak;
        private String wiseman;
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

    public static class FileManager {
        public static void saveWiseSpeaks(ArrayList<WiseSpeak> wisespeaks, int WiseCount)  {
            File dir = new File("db/wiseSaying/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

                WiseSpeak ws = wisespeaks.get(WiseCount - 1);
                if (ws != null && ws.getId() >= 0) {
                    File file = new File(dir, (WiseCount) + ".json");

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        // JSON 형식으로 저장
                        writer.write("{");
                        writer.newLine();
                        writer.write("\t\"id\": " + (WiseCount) + ",");
                        writer.newLine();
                        writer.write("\t\"wisespeak\": \"" + ws.getWisespeak() + "\",");
                        writer.newLine();
                        writer.write("\t\"wiseman\": \"" + ws.getWiseman() + "\"");
                        writer.newLine();
                        writer.write("}");
                        writer.newLine();
                        System.out.println((WiseCount) + "번 명언 저장 완료");
                        File file_last = new File(dir, "lastId.txt");
                        try (BufferedWriter lastWriter = new BufferedWriter(new FileWriter(file_last))) {
                            lastWriter.write(String.valueOf(WiseCount));
                        } catch (IOException e) {
                            System.out.println("마지막 ID 저장 실패: " + e.getMessage());
                        }

                    } catch (IOException e) {
                        System.out.println((WiseCount) + "번 명언 저장 실패: " + e.getMessage());
                    }
                }

        }
        public static ArrayList<WiseSpeak> loadWiseSpeaks() {
            File dir = new File("db/wiseSaying/");
            ArrayList<WiseSpeak> wisespeaks = new ArrayList<>();
            int idx = 0;
            if (dir.exists()) {
                File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
                if(files != null) {
                    for (File file : files) {
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            String wisespeak = "";
                            String wiseman = "";
                            while ((line = br.readLine()) != null) {
                                line = line.trim();
                                if (line.startsWith("\"id\":")) {
                                    String idStr = line.split(":")[1].trim().replace(",", "");
                                    idx = Integer.parseInt(idStr);
                                    while (wisespeaks.size() < idx) {
                                        wisespeaks.add(null);
                                    }
                                    wisespeaks.set(idx - 1, new WiseSpeak("", "", idx - 1));

                                } else if (line.startsWith("\"wisespeak\":")) {
                                    wisespeak = line.split(":")[1].trim().replace(",", "").replace("\"", "");
                                    wisespeaks.get(idx - 1).wisespeak = wisespeak;
                                } else if (line.startsWith("\"wiseman\":")) {
                                    wiseman = line.split(":")[1].trim().replace(",", "").replace("\"", "");
                                    wisespeaks.get(idx - 1).wiseman = wiseman;
                                }
                            }




                        } catch (IOException e) {
                            System.out.println("파일 로드 실패: " + e.getMessage());
                        } catch (NumberFormatException e) {
                            System.out.println("잘못된 파일 이름 형식: " + file.getName());
                        } catch (NullPointerException e) {
                            System.out.println("파일 내용이 비어있습니다: " + file.getName());
                        }
                    }
                }

            }
            return wisespeaks;
        }

        public static void deleteWiseSpeak(ArrayList<WiseSpeak> wisespeaks, int id) {
            File file = new File("db/wiseSaying/" + (id + 1) + ".json");
            if (file.exists()) {

                file.delete();
                delete_Wise(wisespeaks, id);
            }
        }

        public static void buildWiseSpeak(ArrayList<WiseSpeak> wisespeaks, int lastId) {
            File dir = new File("db/wiseSaying/");
            if (!dir.exists()) {
                dir.mkdirs();

            }

            try (BufferedWriter writer=new BufferedWriter(new FileWriter(new File(dir, "data.json")))) {
                writer.write("[");
                writer.newLine();
                IntStream.range(0, lastId)
                        .forEach(i -> {
                            if (wisespeaks.get(i) != null && wisespeaks.get(i).getId() >= 0) {
                                try {
                                    writer.write("\t{");
                                    writer.newLine();
                                    writer.write("\t\t\"id\": " + (i + 1) + ",");
                                    writer.newLine();
                                    writer.write("\t\t\"wisespeak\": \"" + wisespeaks.get(i).getWisespeak() + "\",");
                                    writer.newLine();
                                    writer.write("\t\t\"wiseman\": \"" + wisespeaks.get(i).getWiseman() + "\"");
                                    writer.newLine();
                                    writer.write("\t}");
                                    if (i < lastId - 1) {
                                        writer.write(",");
                                        writer.newLine();
                                    }
                                } catch (IOException e) {
                                    System.out.println("명언 빌드 실패: " + e.getMessage());
                                }
                            }
                        });
                writer.newLine();
                writer.write("]");
                System.out.println("명언 빌드 완료");
            }
            catch (IOException e) {
                    System.out.println("명언 빌드 실패: " + e.getMessage());

            }
        }
        }


    private static void delete_Wise(ArrayList<WiseSpeak> wisespeaks, int id) {
        if(wisespeaks.get(id).id == -1 || wisespeaks.get(id) == null) {

            System.out.println((id + 1) + "번 명언은 존재하지 않습니다.");

        }
        else {
            wisespeaks.set(id, null);

            System.out.println((id + 1) + "번 명언이 삭제되었습니다.");
        }

    }
    private static void updateWiseSpeak(ArrayList<WiseSpeak> wisespeaks, int updateIndex, Scanner sc) {
        System.out.println("명언(기존): " + wisespeaks.get(updateIndex).getWisespeak());
        System.out.print("명언: ");
        String wisespeak = sc.nextLine();
        System.out.println("작가(기존): " + wisespeaks.get(updateIndex).getWiseman());
        System.out.print("작가: ");
        String wiseman = sc.nextLine();
        wisespeaks.set(updateIndex, new WiseSpeak(wisespeak, wiseman, updateIndex));
        System.out.println((updateIndex + 1) + "번 명언이 수정되었습니다.");
    }

    private static void printWiseSpeak(WiseSpeak wisespeak) {
        System.out.print(" / " + wisespeak.getWisespeak());
        System.out.println(" / " + wisespeak.getWiseman());
    }
    private static void printAllWiseSpeaks(ArrayList<WiseSpeak> wisespeaks, int WiseCount) {
        System.out.println("번호 / 작가 / 명언");
        System.out.println("-----------------------------------------");
        for (int i = WiseCount - 1; i >= 0; i--) {
            if (wisespeaks.get(i) != null && wisespeaks.get(i).getId() >= 0) {
                System.out.print((i + 1));
                printWiseSpeak(wisespeaks.get(i));
            }
        }
    }
    private static int load_lastId() {
        File file = new File("db/wiseSaying/lastId.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    int lastId = Integer.parseInt(line);

                    return lastId;
                }
            } catch (IOException e) {
                System.out.println("마지막 ID 로드 실패: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("잘못된 마지막 ID 형식: " + e.getMessage());
            }
        }
        return 0;
    }

}