import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        // Account user = authenticateUser(); //figure out which user is logged in
        // selectionMenu(user); //main 

        //mysql testing
        Account testAccount = new Account("111111", 0, 20, 100, false);
        Transaction testDeposit = new Transaction(testAccount, 50);
        testDeposit.deposit(testAccount);
    }

    // public static Account authenticateUser() {
    //     Account account = new Account(null, null, null, null, false);
    //     return account;
    // }

    public static void selectionMenu(Account user) {
        Scanner sc = new Scanner(System.in);
		while (true) {
			//TODO: Add whatever options yall want
			System.out.println("Welcome to ATM!\nEnter 1 for x\nEnter 2 for y\n");
			if (true /*insert check for if user is admin here*/) {
				System.out.println("\n---Admin Options---\nEnter 8 for View All Accounts\n");
			}
			System.out.println("\nEnter 0 to exit");
			try {
				int userinput = Integer.parseInt(sc.next());
				switch (userinput) {
				case 0:
					return;
				case 1:
					// some function
					break;
				case 2:
					// some other function
					break;
				case 8:
					printAllAccounts();
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid choice! Please choose again!");
                e.printStackTrace();
			}
			System.out.println("");
		}
    }

    public static void printAllAccounts() {
        System.out.println(String.format("%20s %20s %20s %20s %20s", "Account Number", "First Name", "Last Name", "Total Balance", "Available Balance"));
        try {
            BufferedReader br = new BufferedReader(new FileReader("res/accounts.csv"));
            br.readLine(); //skip headers
            while (true) {
                String row = br.readLine();
                if (row == null) break;
                String[] data = row.split(",");
                System.out.println(String.format("%20s %20s %20s %20.2f %20.2f", data[0], data[3], data[4], Float.parseFloat(data[6]), Float.parseFloat(data[7])));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



///CODES USED TO GENERATE transactions.csv AND accounts.csv

// public static void generateTransactions() {
//     try {
//         XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("src/bank.xlsx")));
//         XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
//         Iterator<Row> rowItr = sheet.iterator(); // iterating over excel file
//         FileWriter transWriter = new FileWriter("transactions.csv");
//         Row headers = rowItr.next();
//         transWriter.append(headers.getCell(0).getStringCellValue() + "," + headers.getCell(1).getStringCellValue()
//                 + "," + headers.getCell(2).getStringCellValue() + "," + headers.getCell(3).getStringCellValue()
//                 + "," + headers.getCell(4).getStringCellValue() + "," + headers.getCell(5).getStringCellValue()
//                 + "," + headers.getCell(6).getStringCellValue() + "," + headers.getCell(7).getStringCellValue()
//                 + "\n");

//         // Create transactions.csv from Bank.xlsx
//         while (rowItr.hasNext()) {
//             Row row = rowItr.next();
//             String accountNumber = row.getCell(0).getStringCellValue().replaceAll("[\']", "").trim();
//             transWriter.append(accountNumber + ",");
//             for (int i = 1; i <= 7; i++) {
//                 switch (i) {
//                 case 1:
//                 case 4:
//                     if (row.getCell(i) != null)
//                         transWriter.append(row.getCell(i).getDateCellValue().toString());
//                     break;
//                 case 2: // Notes: Some cells are numeric
//                     if (row.getCell(i) != null) {
//                         try {
//                             transWriter.append(row.getCell(i).getStringCellValue());
//                         } catch (Exception e) {
//                             transWriter.append(row.getCell(i).getNumericCellValue() + "");
//                         }
//                     }
//                     break;
//                 case 3:
//                 case 5:
//                 case 6:
//                 case 7:
//                     if (row.getCell(i) != null)
//                         transWriter.append(row.getCell(i).getNumericCellValue() + "");
//                     break;
//                 }
//                 transWriter.append(",");
//             }
//             transWriter.append("\n");
//             transWriter.flush();
//         }
//         transWriter.close();
//     } catch (Exception e) {
//         e.printStackTrace();
//     }
// }

// public static void generateAccounts() {

//     // Create accounts.csv from transactions.csv
//     // NOTE: GENERATES HEADERS OF transactions AT THE BOTTOM. 
//     //IF THIS CODE IS RUN, DELETE LAST ROW OR EDIT CODE
//     ArrayList<String> accounts = new ArrayList<String>();
//     String[] words = { "Hu", "Tao", "Jun", "Kai", "Yue", "Hao", "Ti", "Xuan" };
//     Random rand = new Random();
//     try {
//         FileWriter accountWriter = new FileWriter("accounts.csv");
//         ReversedLinesFileReader fr = new ReversedLinesFileReader(new File("transactions.csv"));
//         String row = "";
//         accountWriter.append(
//                 "Account Number,Username,Password,FirstName,LastName,PinNumber,AvailableBalance,TotalBalance,TransferLimit,IsAdmin\n");

//         while (true) {
//             row = fr.readLine();
//             if (row == null) break;
//             String[] data = row.split(",");
//             if (!accounts.contains(data[0])) {
//                 accounts.add(data[0]);
//                 String firstName = words[rand.nextInt(7)];
//                 String lastName = words[rand.nextInt(7)];
//                 accountWriter.append(data[0] + "," + data[0] + ",," + firstName + "," + lastName + ",," + data[7]
//                         + "," + data[7] + ",,False\n");
//             }
//         }
//         accountWriter.flush();
//         accountWriter.close();
//     } catch (IOException e) {
//         e.printStackTrace();
//     }
// }