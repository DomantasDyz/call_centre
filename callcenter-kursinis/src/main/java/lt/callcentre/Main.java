package lt.callcentre;

import lt.callcentre.model.Call;
import lt.callcentre.repository.SQLiteCallRepository;
import lt.callcentre.service.CallQueueService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final String OPERATOR_A = "Domantas";
    private static final String OPERATOR_B = "Ernest";

    public static void main(String[] args) {
        SQLiteCallRepository repository = new SQLiteCallRepository("data/callcentre.db");
        CallQueueService service = new CallQueueService(repository);
        if (service.getCalls(OPERATOR_A).isEmpty() && service.getCalls(OPERATOR_B).isEmpty()) {
            addSampleData(service);
        }

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));


        Scanner scanner = new Scanner(System.in);
        String currentOperator = OPERATOR_A;
        boolean switchOperator = true;
        boolean running = true;

        while (running) {
            if(currentOperator.equals(OPERATOR_A)) {
                switchOperator = true;
            } else if(currentOperator.equals(OPERATOR_B)) {
                switchOperator = false;
            }

            printMenu(currentOperator, switchOperator);

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> showCalls(service, currentOperator);
                    case "2" -> addTestCall(service, scanner, currentOperator);
                    case "3" -> takeNextCall(service, currentOperator);
                    case "4" -> endActiveCall(service, currentOperator);
                    case "5" -> currentOperator = switchOperator(currentOperator);
                    case "6" -> setVip(service, scanner, currentOperator);
                    case "0" -> running = false;
                    default -> System.err.println("Tokios opcijos nėra...");
                }
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.err.println("KLAIDA: " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("Programa sėkmingai nutraukta.");
        scanner.close();
    }

    private static void printMenu(String currentOperator, boolean switchOperator) {
        System.out.println("=== TELIA SKAMBUČIŲ CENTRO PADALINYS ===");
        System.out.println("Prisijungęs operatorius: " + currentOperator);
        System.out.println("1. Rodyti aktyvius skambučius");
        System.out.println("2. Pridėti naują skambutį");
        System.out.println("3. Atsiliepti į sekantį skambutį");
        System.out.println("4. Nutraukti aktyvų skambutį");
        System.out.println("5. Prisijungti kaip operatorius " + (switchOperator ? "Ernest" : "Domantas"));
        System.out.println("6. Keisti skambučio prioritetą (VIP)");
        System.out.println("0. Baigti darbą");
        System.out.print("Pasirinkti: ");
    }

    private static void showCalls(CallQueueService service, String operatorId) {
        List<Call> calls = service.getCalls(operatorId);
        if (calls.isEmpty()) {
            System.err.println("Šis operatorius neturi jokių skambučių.");
            return;
        }
        for (Call call : calls) {
            System.out.println(call);
        }
    }

    private static void addTestCall(CallQueueService service, Scanner scanner, String operatorId) {
        System.out.print("Telefono numeris: ");
        String phone = scanner.nextLine();
        if(!checkPhoneNumber(phone)) {
            System.err.println("KLAIDA: Telefono numeris neatitinka skaitmenų kiekio.");
            return;
        }
        System.out.print("VIP? (y/n): ");
        boolean vip = scanner.nextLine().equalsIgnoreCase("y");
        int nextCallId = 1;
        for (Call call : service.getCalls(operatorId)) {
            if(call.getId() >= nextCallId) {
                nextCallId = call.getId() + 1;
            }
        }
        Call call = new Call(nextCallId, operatorId, phone, vip, LocalDateTime.now());
        service.addCall(call);
        System.out.println("Skambutis įtrauktas: " + call);
    }

    private static boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber != null
                && phoneNumber.startsWith("+370")
                && phoneNumber.length() == 12;
    }

    private static void takeNextCall(CallQueueService service, String operatorId) {
        Call selected = service.takeNextCall(operatorId);
        System.out.println("Pakeltas skambutis: " + selected);
    }

    private static void endActiveCall(CallQueueService service, String operatorId) {
        Call ended = service.endActiveCall(operatorId);
        System.out.println("Nutrauktas skambutis: " + ended);
    }

    private static String switchOperator(String currentOperator) {
        if (currentOperator.equals(OPERATOR_A)) {
            System.out.println("Prisijungta kaip operatorius Ernest.");
            return OPERATOR_B;
        }
        System.out.println("Prisijungta kaip operatorius Domantas.");
        return OPERATOR_A;
    }

    private static void setVip(CallQueueService service, Scanner scanner, String operatorId) {
        System.out.print("Skambučio ID: ");
        int callId = Integer.parseInt(scanner.nextLine());
        System.out.print("VIP? (y/n): ");
        boolean vip = scanner.nextLine().equalsIgnoreCase("y");
        service.setVip(operatorId, callId, vip);
        System.out.println("Skambučio prioritetas pakeistas.");
    }

    private static void addSampleData(CallQueueService service) {
        service.addCall(new Call(1, OPERATOR_A, "+37060000001", false, LocalDateTime.now().minusMinutes(5)));
        service.addCall(new Call(2, OPERATOR_A, "+37060000002", true, LocalDateTime.now().minusMinutes(1)));
        service.addCall(new Call(3, OPERATOR_B, "+37061111111", false, LocalDateTime.now().minusMinutes(3)));
        service.addCall(new Call(4, OPERATOR_B, "+37062222222", true, LocalDateTime.now().minusMinutes(2)));
    }
}
