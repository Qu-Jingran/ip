import java.util.Scanner;

public class Eli {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
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
            System.out.println(s);
        }
        System.out.println( "Bye. 记得来找我\n"+
                "____________________________________________________________");

    }
}
