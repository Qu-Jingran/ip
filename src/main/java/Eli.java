import java.util.Scanner;

public class Eli {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Task[] arr = new Task[100];
        int count = 0;
        String banner = " _______     _           _____ \n"
                + "|  _____|   | |         |_   _|\n"
                + "| |___      | |           | |  \n"
                + "|  ___|     | |           | |  \n"
                + "| |_____    | |_____     _| |_ \n"
                + "|_______|   |_______|   |_____|\n";
        System.out.println(banner);
        System.out.println("____________________________________________________________\n" +
                        "Hello! I'm Eli.\n" +
                        "你需要什么帮助？\n" +
                        "\n____________________________________________________________\n");

        for (String s = input.nextLine().trim();
             !s.equals("bye") && !s.equals("再见");
             s = input.nextLine().trim()) {
            String list = "";

            if(s.equals("list")) {
                for (int i = 0; arr[i] != null; i++) {
                    list += i + 1 + ". " + arr[i].toString() + "\n";
                }
                System.out.println(list);

            } else if(s.startsWith("mark")) {
                int taskNumber = Integer.parseInt(s.substring(5).trim());
                if (taskNumber > count) {
                    System.out.println("Sorry, 我们没有那么多任务");
                } else {
                    arr[taskNumber - 1].markAsDone();
                    System.out.println("Nice! 已经标记为完成了");
                }
            } else if(s.startsWith("unmark")) {
                int taskNumber = Integer.parseInt(s.substring(7).trim());
                if (taskNumber > count) {
                    System.out.println("Sorry, 我们没有那么多任务");
                System.out.println("Nice! 已经标记为完成了");
                } else {
                    arr[taskNumber - 1].unMark();
                    System.out.println("ok!已经标记为未完成");
                }

            }
            else {
                System.out.printf("added: %s%n", s);
                arr[count] = new Task(s);
                count++;
            }
        }
        System.out.println( "Bye. 记得来找我\n"+
                "____________________________________________________________");

    }
}
