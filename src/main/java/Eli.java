import java.util.Scanner;

public class Eli {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String[] arr = new String[100];
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

        for(String s = input.next(); !s.equals("bye") && !s.equals("再见"); s = input.next()){
            String list = "";

            if(s.equals("list")) {
                for(int i = 0; arr[i] != null; i++) {
                    list += i+1 + ". " + arr[i] + "\n";
                }
                System.out.println(list);

            }
            else {
                System.out.printf("added: %s%n", s);

                arr[count] = s;
                count++;
            }
        }
        System.out.println( "Bye. 记得来找我\n"+
                "____________________________________________________________");

    }
}
