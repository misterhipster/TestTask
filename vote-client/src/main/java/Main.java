import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Connect connect = new Connect((argss)->{
            System.out.println((String)argss[0]);
        });
        Thread.sleep(600);
        Scanner scanner = new Scanner(System.in);

        String msg = new String();
        while (true) {
            msg = scanner.nextLine();
            connect.sendMessage(msg);
            Thread.sleep(600);
        }
    }

}
