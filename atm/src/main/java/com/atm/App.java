// package com.atm;

// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.Scanner;

// public class App {
//     public static void main(String[] args) throws Exception {
//         SQLQueries q = new SQLQueries();
//         q.importAccounts();

//         // Welcome Message
//         System.out.println("");
//         System.out.printf("%s%n", "-".repeat(32));
//         System.out.printf("|        Welcome to ATM!       |%n");
//         System.out.printf("%s%n%n", "-".repeat(32));

//         Scanner sc = new Scanner(System.in);
//         // Get client username and password
//         Account user = null;
//         Authenticate au = new Authenticate();
//         Boolean authenticated = false;

//         while (!authenticated) {
//             // Prompt user for account details
//             System.out.print("Enter username: ");
//             String username = sc.next();

//             System.out.print("Enter password: ");
//             String password = sc.next();

//             if (username.length() != 0 && password.length() != 0) {
//                 if (au.checkPassword(username, password)) {
//                     // Set user based on username input
//                     user = getCurrentUser(username);
//                     authenticated = true;
//                     break;
//                 }
//             }

//             // TODO: add 3 times timeout
//             System.out.println("Password incorrect, try again.");
//         }

//         selectionMenu(user); // main
//         sc.close();
//     }

//     // Create Account object based on username input
//     public static Account getCurrentUser(String username) {
//         SQLQueries q = new SQLQueries();
//         Account currentUser = q.getAccountfromUsername(username);
//         return currentUser;
//     }

//     // Create Account object based on accountNumber (For transfer)
//     public static Account getTransferAccount(Long accountNumber) {
//         SQLQueries q = new SQLQueries();
//         Account transferAccount = q.getAccountfromAccountNumber(accountNumber);
//         return transferAccount;
//     }

//     public static void selectionMenu(Account user) {
//         Scanner sc = new Scanner(System.in);

//         while (true) {
//             System.out.printf("%n---------- %s ----------%n", "Available Services");
//             System.out.printf("| %-36s |%n", "(0) Exit");
//             // For Admins Only
//             /* insert check for if user is admin here */
//             if (true) {
//                 System.out.printf("| %-36s |%n", "(1) View All Accounts");
//                 System.out.printf("| %-36s |%n", "(2) View all Transaction History");
//             }

//             // User options
//             System.out.printf("| %-36s |%n", "(1) Deposit");
//             System.out.printf("| %-36s |%n", "(2) Withdraw");
//             System.out.printf("| %-36s |%n", "(3) Transfer");
//             System.out.printf("| %-36s |%n", "(4) View Account Balance");
//             System.out.printf("| %-36s |%n", "(5) Settings");
//             System.out.printf("| %-36s |%n", "(6) Help");
//             System.out.printf("%s%n%n", "-".repeat(40));

//             System.out.print("Please enter an option: ");

//             try {
//                 Transaction transaction = new Transaction(user);
//                 int userinput = Integer.parseInt(sc.next());

//                 switch (userinput) {
//                     case 0:
//                         // Thank You Message
//                         System.out.println("Thank You and Have a Nice Day!");
//                         // Close and return
//                         sc.close();
//                         return;
//                     case 1:
//                         // Deposit
//                         System.out.print("Please enter an amount to deposit: $");
//                         double depositAmount = sc.nextDouble();
//                         transaction.deposit(user, depositAmount);
//                         System.out.println("Your Total Balance is after deposit is: $" + user.getTotalBalance());
//                         break;
//                     case 2:
//                         // Withdraw
//                         System.out.print("Please enter an amount to withdraw: $");
//                         double withdrawalAmount = sc.nextDouble();
//                         transaction.withdraw(user, withdrawalAmount);
//                         System.out.println("Your Total Balance is after withdrawal is: $" + user.getTotalBalance());
//                         break;
//                     case 3:
//                         // Transfer
//                         System.out.println("Please enter account number to transfer to: ");
//                         long transferAccountNumber = sc.nextLong();
//                         System.out.println("Please enter amount to be transferred: ");
//                         double amount = sc.nextDouble();
//                         Account a2 = getTransferAccount(transferAccountNumber);
//                         transaction.transferToAccount(user, a2, amount);
//                         break;
//                     case 4:
//                         System.out.println("Your Available Balance is: $" + user.getAvailableBalance());
//                         System.out.println("Your Total Balance is: $" + user.getTotalBalance());
//                         break;
//                     case 5:
//                         System.out.println("Please contact the customer service hotline for any assistance.");
//                         break;
//                     case 6:
//                         printAllAccounts();
//                         break;
//                     case 7:
//                         // print all transactions history
//                 }
//             } catch (NumberFormatException e) {
//                 System.out.println("Invalid choice! Please choose again!");
//                 e.printStackTrace();
//             }
//         }
//     }

//     public static void printAllAccounts() {
//         System.out.printf("%s%n", "-".repeat(116));
//         System.out.println(
//                 String.format("| %-20s | %-20s | %-20s | %-20s | %-20s |", "Account Number", "First Name", "Last Name",
//                         "Total Balance", "Available Balance"));
//         System.out.printf("%s%n", "-".repeat(116));

//         try {
//             BufferedReader br = new BufferedReader(new FileReader("atm/res/accounts.csv"));
//             br.readLine(); // skip headers

//             while (true) {
//                 String row = br.readLine();
//                 if (row == null)
//                     break;
//                 String[] data = row.split(",");
//                 System.out.println(
//                         String.format("| %-20s | %-20s | %-20s | %-20.2f | %-20.2f |", data[0], data[3], data[4],
//                                 Float.parseFloat(data[6]), Float.parseFloat(data[7])));
//             }
//             br.close();
//             System.out.printf("%s%n", "-".repeat(116));
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

// }

// /// CODES USED TO GENERATE transactions.csv AND accounts.csv

// // public static void generateTransactions() {
// // try {
// // XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new
// // File("src/bank.xlsx")));
// // XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve
// // object
// // Iterator<Row> rowItr = sheet.iterator(); // iterating over excel file
// // FileWriter transWriter = new FileWriter("transactions.csv");
// // Row headers = rowItr.next();
// // transWriter.append(headers.getCell(0).getStringCellValue() + "," +
// // headers.getCell(1).getStringCellValue()
// // + "," + headers.getCell(2).getStringCellValue() + "," +
// // headers.getCell(3).getStringCellValue()
// // + "," + headers.getCell(4).getStringCellValue() + "," +
// // headers.getCell(5).getStringCellValue()
// // + "," + headers.getCell(6).getStringCellValue() + "," +
// // headers.getCell(7).getStringCellValue()
// // + "\n");

// // // Create transactions.csv from Bank.xlsx
// // while (rowItr.hasNext()) {
// // Row row = rowItr.next();
// // String accountNumber = row.getCell(0).getStringCellValue().replaceAll("[\']",
// // "").trim();
// // transWriter.append(accountNumber + ",");
// // for (int i = 1; i <= 7; i++) {
// // switch (i) {
// // case 1:
// // case 4:
// // if (row.getCell(i) != null)
// // transWriter.append(row.getCell(i).getDateCellValue().toString());
// // break;
// // case 2: // Notes: Some cells are numeric
// // if (row.getCell(i) != null) {
// // try {
// // transWriter.append(row.getCell(i).getStringCellValue());
// // } catch (Exception e) {
// // transWriter.append(row.getCell(i).getNumericCellValue() + "");
// // }
// // }
// // break;
// // case 3:
// // case 5:
// // case 6:
// // case 7:
// // if (row.getCell(i) != null)
// // transWriter.append(row.getCell(i).getNumericCellValue() + "");
// // break;
// // }
// // transWriter.append(",");
// // }
// // transWriter.append("\n");
// // transWriter.flush();
// // }
// // transWriter.close();
// // } catch (Exception e) {
// // e.printStackTrace();
// // }
// // }

// // public static void generateAccounts() {

// // // Create accounts.csv from transactions.csv
// // // NOTE: GENERATES HEADERS OF transactions AT THE BOTTOM.
// // //IF THIS CODE IS RUN, DELETE LAST ROW OR EDIT CODE
// // ArrayList<String> accounts = new ArrayList<String>();
// // String[] words = { "Hu", "Tao", "Jun", "Kai", "Yue", "Hao", "Ti", "Xuan" };
// // Random rand = new Random();
// // try {
// // FileWriter accountWriter = new FileWriter("accounts.csv");
// // ReversedLinesFileReader fr = new ReversedLinesFileReader(new
// // File("transactions.csv"));
// // String row = "";
// // accountWriter.append(
// // "Account
// // Number,Username,Password,FirstName,LastName,PinNumber,AvailableBalance,TotalBalance,TransferLimit,IsAdmin\n");

// // while (true) {
// // row = fr.readLine();
// // if (row == null) break;
// // String[] data = row.split(",");
// // if (!accounts.contains(data[0])) {
// // accounts.add(data[0]);
// // String firstName = words[rand.nextInt(7)];
// // String lastName = words[rand.nextInt(7)];
// // accountWriter.append(data[0] + "," + data[0] + ",," + firstName + "," +
// // lastName + ",," + data[7]
// // + "," + data[7] + ",,False\n");
// // }
// // }
// // accountWriter.flush();
// // accountWriter.close();
// // } catch (IOException e) {
// // e.printStackTrace();
// // }
// // }
