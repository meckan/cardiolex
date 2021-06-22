package cardiolex.readword.BO;

import lombok.*;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Component
public class ReqDocument {

    private List<Requirement> requirementList;
    private List<TestInstruction> testInstructionList;

    public ReqDocument() {
        requirementList = new ArrayList<>();
        testInstructionList = new ArrayList<>();
    }


    public void checkAllElements() throws IOException {
        File file = new File("src/main/resources/testdoc.docx");
        XWPFDocument doc = new XWPFDocument(new FileInputStream(file));

        Iterator<IBodyElement> iter = doc.getBodyElementsIterator();
        while (iter.hasNext()) {
            IBodyElement elem = iter.next();
            if (elem instanceof XWPFParagraph) {
                System.out.println("paragrap");
            } else if (elem instanceof XWPFTable) {
                System.out.println("table");
            }
        }
    }

    /**
     * Method to print al the objects in to CVS files
     * @return idk returns true if the CSV files was created and saved.
     */
    public boolean printIntoCSV() {
        try {
            //BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/requirementsCSV.csv"));
            PrintWriter writer;

            if (!requirementList.isEmpty()) {
                writer = new PrintWriter("src/main/resources/requirementsCSV.csv");
                writer.write(requirementList.get(0).getCSVHeader() + "\n");
                //writer.println();
                for (Requirement r : requirementList) {
                    writer.write(r.getCSV() + "\n");
                }
                writer.close();
            }
            if (!testInstructionList.isEmpty()) {
                writer = new PrintWriter("src/main/resources/reqTestCSV.csv");
                writer.write(testInstructionList.get(0).getCSVHeader() + "\n");

                for (TestInstruction test : testInstructionList) {
                    //System.out.println(test.toString());
                    writer.write(test.getCSV() + "\n");
                }
                writer.close();
            }

            if(!testInstructionList.isEmpty() && !requirementList.isEmpty()){
                writer = new PrintWriter("src/main/resources/comboFile.csv");
                writer.write(requirementList.get(0).getComboHeader() + "\n");

                writer.write(requirementList.get(0).getCSVCombo(requirementList.get(0).getComboHeaderArray()) + "\n");
                writer.write(testInstructionList.get(0).getComboCSV(requirementList.get(0).getComboHeaderArray())+ "\n");
                writer.write(testInstructionList.get(1).getComboCSV(requirementList.get(0).getComboHeaderArray())+ "\n");
                writer.write(testInstructionList.get(2).getComboCSV(requirementList.get(0).getComboHeaderArray())+ "\n");


                writer.close();
            }


        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    /**
     * Main method that takes in a folderPath and reads all the files in the folder,
     * also works with just one file.
     * TODO denna kan behöva kolla så att den endast kollar docx filen.
     * @param folderPath path to the folder / file to look at.
     */
    public void readFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            for (File f : Objects.requireNonNull(folder.listFiles())) {
                readWordFile(f);
            }
        } else if (folder.isFile()) {
            readWordFile(folder);
        } else
            System.out.println("No file / folder found");
    }

    /**
     * Simple UI metod to just ask the user if the current file contains requirements or tests.
     * @param file current file to check.
     */
    public void readWordFile(File file) {
        try {
            XWPFDocument document = new XWPFDocument(new FileInputStream(file));
            Scanner scanner = new Scanner(System.in);
            List<String> answers = Arrays.asList("y", "yes");

            System.out.println("Dose " + file.getName() + " contain requirements?: (yes (y), no (n))");
            if (answers.contains(scanner.nextLine().toLowerCase(Locale.ROOT)))
                readRequirements(document);

            System.out.println("Dose " + file.getName() + " contain tests?: (yes (y), no (n))");
            if (answers.contains(scanner.nextLine().toLowerCase(Locale.ROOT)))
                readReqTest(document.getParagraphs());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method to read all the requirements from a word docx file.
     * Looks at all the tables in the docx file and find the tabels and checks if its fits.
     * @param doc the current docx file
     */
    private void readRequirements(XWPFDocument doc) {
//        Scanner scanner = new Scanner(System.in);
//        List<String> answers = Arrays.asList("y", "yes");
        Iterator<IBodyElement> iter = doc.getBodyElementsIterator();
        String tableName = null;

        while (iter.hasNext()) {
            IBodyElement elem = iter.next();
            if (elem instanceof XWPFParagraph) {
                tableName = ((XWPFParagraph) elem).getText();
            } else if (elem instanceof XWPFTable) {
                if (!((XWPFTable) elem).getText().equals("OK\t\tFelrapport #\t\n")) {
//                    StringBuilder builder = new StringBuilder();
//                    for (XWPFTableCell cell : ((XWPFTable) elem).getRow(0).getTableCells()) {
//                        builder.append(cell.getText()).append("\t");
//                    }
//                    builder.append(System.lineSeparator()).append("Is table requirements? (yes (y), no (n))");
//                    System.out.println(builder.toString());
//                    if (answers.contains(scanner.nextLine().toLowerCase(Locale.ROOT)))
//                        readTable((XWPFTable) elem,tableName);
                    //System.out.println(((XWPFTable) elem).getRow(0).getCell(0).getText().toCharArray());
                    if(checkIfNewTest(((XWPFTable) elem).getRow(0).getCell(0).getText().toCharArray())) {
                        //System.out.println("Was true: " + Arrays.toString(((XWPFTable) elem).getRow(0).getCell(0).getText().toCharArray()));
                        readTable((XWPFTable) elem, tableName);
                    }
                }
            }
        }
//        for (XWPFTable t : tables) {
//            if (!t.getText().equals("OK\t\tFelrapport #\t\n")) {
//                StringBuilder builder = new StringBuilder();
//                for (XWPFTableCell cell : t.getRow(0).getTableCells()) {
//                    builder.append(cell.getText()).append("\t");
//                }
//                builder.append(System.lineSeparator()).append("Is table requirements? (yes (y), no (n))");
//                System.out.println(builder.toString());
//                if (answers.contains(scanner.nextLine().toLowerCase(Locale.ROOT)))
//                    readTable(t);
//            }
//        }
    }

    /**
     * Reads a tables that's shown to fit the model of a requirement.
     * @param table the table object from Apatche poi
     * @param tableName the label of the current table
     */
    private void readTable(XWPFTable table, String tableName) {
        Requirement req;
        for (XWPFTableRow row : table.getRows()) {
            req = new Requirement();
            req.setReqId(row.getCell(0).getText());
            req.setDescription(row.getCell(1).getText());
            req.setTitle(tableName);


            req.setTests(new ArrayList<>());
            if(row.getTableCells().size() == 3)
                req.setTests(Arrays.asList(row.getCell(2).getText().split(" ")));

            for (XWPFTableCell cell : row.getTableCells()) {
                if (!cell.getText().isEmpty())
                    if (!cell.getTables().isEmpty()) {
                        for (XWPFTable inner : cell.getTables())
                            req.setDescription(readInnerTable(inner,
                                    new StringBuilder(req.getDescription()), "\t"));
                    }
            }
            requirementList.add(req);
        }
    }

    /**
     * Method to read a table that's inside a table cell in the requirements to be able to read inner tables
     * @param table the current inner table
     * @param des Stringbuilder object that contains all the parts of the description
     * @param tab just to get the right nr of tabs to be able to format the description
     * @return returns the
     */
    private String readInnerTable(XWPFTable table, StringBuilder des, String tab) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                if (!cell.getText().isEmpty())
                    des.append(System.lineSeparator()).append(tab).append(cell.getText());
                if (!cell.getTables().isEmpty()) {
                    for (XWPFTable inner : cell.getTables())
                        des.append(readInnerTable(inner, des, tab + "\t"));
                }
            }
        }
        return des.toString();
    }

    /**
     * Method to read the test instructions, using the standard model of the paragraphs that starts with
     * "Testinstruktion" and ends with "Avslutande åtgärder"
     * @param paragraphs list of all the paragraphs in a docx file
     */
    private void readReqTest(List<XWPFParagraph> paragraphs) {
        TestInstruction testInstruction = null;
        boolean add = false;
        for (XWPFParagraph p : paragraphs) {
            if (p.getText().contains("Testinstruktion")) {
                add = true;
                testInstruction = new TestInstruction();
                testInstruction.setTests(new ArrayList<>());
            } else if (p.getText().contains("Avslutande åtgärder")) {
                testInstructionList.add(testInstruction);
                add = false;
            } else if (add) {
                if(p.getText().length() < 6){
                    if(checkIfNewTest(p.getText().toCharArray())){
                        if(!testInstruction.getTests().isEmpty()){
                            testInstructionList.add(testInstruction);
                        }
                        testInstruction = new TestInstruction();
                        testInstruction.setId(p.getText());
                        testInstruction.setTests(new ArrayList<>());
                    }
                }
                else if (!p.getText().isEmpty() && !p.getText().contains("Mötesprotokoll:") &&
                        !p.getText().contains("Användarintyg:__"))
                    testInstruction.getTests().add(p.getText());
            }
        }
    }


    private boolean checkIfNewTest(char[] chars){
        for (int i = 0; i < chars.length; i++) {
            if(i < 2){
                if(!Character.isAlphabetic(chars[i]))
                    return false;
            }else {
                if(!Character.isDigit(chars[i]))
                    return false;
            }
        }
        return true;
    }
}
