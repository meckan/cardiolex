package cardiolex.readword.BO;

import lombok.*;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.zip.ZipException;

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


    public void checkAllElements(String fileUrl) throws IOException {
        //File file = new File("src/main/resources/testdoc.docx");
        File file = new File(fileUrl);
        XWPFDocument doc = new XWPFDocument(new FileInputStream(file));

        Iterator<IBodyElement> iter = doc.getBodyElementsIterator();
        while (iter.hasNext()) {
            IBodyElement elem = iter.next();
            if (elem instanceof XWPFParagraph) {
                System.out.println("paragrap");
                System.out.println(((XWPFParagraph) elem).getText());
            } else if (elem instanceof XWPFTable) {
                System.out.println("table");
                System.out.println(((XWPFTable) elem).getText());

                if(((XWPFTable) elem).getText().isEmpty()){
                    System.out.println(((XWPFTable) elem).getRows().size());
                    for (XWPFTableRow row: ((XWPFTable) elem).getRows()) {
                        for (XWPFTableCell cell: row.getTableCells()) {
                            System.out.println(cell.getText());
                        }
                    }


                }


            }
        }
    }

    /**
     * Method to print al the objects in to CVS files
     * @return idk returns true if the CSV files was created and saved.
     */
    //TODO se hur man får med alla test steps i CSV filen för att kunna ladda upp dem
    public boolean printIntoCSV() {
        try {
            //BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/requirementsCSV.csv"));
            PrintWriter writer;

            if (!requirementList.isEmpty()) {
                writer = new PrintWriter("src/main/resources/requirementsCSV.csv","UTF-8");
                writer.write(requirementList.get(0).getCSVHeader() + "\n");
                //writer.println();
                for (Requirement r : requirementList) {
                    writer.write(r.getCSV() + "\n");
                }
                writer.close();
            }
            if (!testInstructionList.isEmpty()) {
                writer = new PrintWriter("src/main/resources/reqTestCSV.csv","UTF-8");
                writer.write(testInstructionList.get(0).getCSVHeader() + "\n");

                for (TestInstruction test : testInstructionList) {
                    //System.out.println(test.toString());
                    writer.write(test.getCSV() + "\n");
                }
                writer.close();
            }

            if(!testInstructionList.isEmpty() && !requirementList.isEmpty()){
                writer = new PrintWriter("src/main/resources/comboFile.csv","UTF-8");
                writer.write(requirementList.get(0).getComboHeader() + "\n");

//                for (TestInstruction testInstruction: testInstructionList) {
//                    writer.write(testInstruction.getComboCSV(requirementList.get(0).getComboHeaderArray()));
//                        for (Requirement req: requirementList) {
//                            if(testInstruction.getRequirementIds() != null && testInstruction.getRequirementIds().contains(req.getReqId())) {
//                                writer.write(req.getCSVCombo(requirementList.get(0).getComboHeaderArray()) + "\n");
//
//                            }
//
//                    }
//                }

                for (Requirement req: requirementList) {
                    writer.write(req.getCSVCombo(requirementList.get(0).getComboHeaderArray()) + "\n");
                    for (TestInstruction test: testInstructionList) {
                        if(test.getRequirementIds() != null && test.getRequirementIds().contains(req.getReqId())){


                            writer.write(test.getComboCSV(requirementList.get(0).getComboHeaderArray()));
                        }
                    }
                }

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
            System.out.println(file.getName());

            XWPFDocument document = new XWPFDocument(new FileInputStream(file));
            Scanner scanner = new Scanner(System.in);
            List<String> answers = Arrays.asList("y", "yes");

            System.out.println("Dose " + file.getName() + " contain requirements?: (yes (y), no (n))");
            if (answers.contains(scanner.nextLine().toLowerCase(Locale.ROOT)))
                readRequirements(document,getArea(file.getName()));

            System.out.println("Dose " + file.getName() + " contain tests?: (yes (y), no (n))");
            if (answers.contains(scanner.nextLine().toLowerCase(Locale.ROOT)))
                readReqTest(document.getParagraphs(),getArea(file.getName()));

            System.out.println("Req: " + requirementList.size());
            System.out.println("Tests: " + testInstructionList.size());

        }catch (NotOfficeXmlFileException | IllegalStateException | ZipException e){
            System.out.println("Skipping: " + file.getName() +"\nWas not Word document");
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method to read all the requirements from a word docx file.
     * Looks at all the tables in the docx file and find the tabels and checks if its fits.
     * @param doc the current docx file
     */
    private void readRequirements(XWPFDocument doc,String area) {
//        Scanner scanner = new Scanner(System.in);
//        List<String> answers = Arrays.asList("y", "yes");
        Iterator<IBodyElement> iter = doc.getBodyElementsIterator();
        String tableName = null;

        while (iter.hasNext()) {
            IBodyElement elem = iter.next();
            if (elem instanceof XWPFParagraph) {


                System.out.println(((XWPFParagraph) elem).getText());

                String temp  = ((XWPFParagraph) elem).getText();
                if(!temp.isEmpty())
                    tableName = temp;
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
                        readTable((XWPFTable) elem, tableName,area);
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
    }
//        }

    /**
     * Reads a tables that's shown to fit the model of a requirement.
     * @param table the table object from Apatche poi
     * @param tableName the label of the current table
     */
    private void readTable(XWPFTable table, String tableName, String area) {
        Requirement req;
        for (XWPFTableRow row : table.getRows()) {
            req = new Requirement();
            req.setReqId(row.getCell(0).getText());
            req.setDescription(row.getCell(1).getText());
            req.setTitle(tableName);
            req.setArea(area);

            req.setTests(new ArrayList<>());
            if(row.getTableCells().size() == 3)
                req.setTests(Arrays.asList(row.getCell(2).getText().split(" |,")));

            for (XWPFTableCell cell : row.getTableCells()) {
                if (!cell.getText().isEmpty())
                    if (!cell.getTables().isEmpty()) {
                        for (XWPFTable inner : cell.getTables())
                            req.setDescription(readInnerTable(inner,
                                    new StringBuilder(req.getDescription()), "\t"));
                    }
            }
            //System.out.println(req.toString());
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

            //TODO försöker hitta hur man kan upptäcka om den är osynlig eller ej.

            for (XWPFTableCell cell : row.getTableCells()) {
                if (!cell.getText().isEmpty())
                    des.append(System.lineSeparator()).append(tab).append(cell.getText());
                if (!cell.getTables().isEmpty()) {
                    for (XWPFTable inner : cell.getTables()) {
                        des.append(readInnerTable(inner, des, tab + "\t"));
                    }
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
    private void readReqTest(List<XWPFParagraph> paragraphs,String area) {
        TestInstruction testInstruction = null;
        boolean add = false;

        Iterator<XWPFParagraph> paragraphIterator = paragraphs.iterator();
        XWPFParagraph currentParagraph;

        List<String> reqIds = null;
        while (paragraphIterator.hasNext()){
            currentParagraph = paragraphIterator.next();
        //for (XWPFParagraph p : paragraphs) {

            if(currentParagraph.getText().contains("Kommentar")){
                reqIds = new ArrayList<>();
                while (paragraphIterator.hasNext() &&
                        !(currentParagraph = paragraphIterator.next()).getText().contains("Beskrivning")){
                    //testInstruction.getRequirementIds().add(currentParagraph.getText().split("\t")[0]);

                    String[] dividedParagraph = currentParagraph.getText().split("\t");
                    if(dividedParagraph.length > 0) {
                        String reqId = dividedParagraph[0];
                        if (checkIfNewTest(reqId.toCharArray())) {
                            //testInstruction.getRequirementIds().add(currentParagraph.getText().split("\t")[0]);
                            reqIds.add(reqId);
                        }
                    }

                }
            }
            else if (currentParagraph.getText().contains("Testinstruktion")) {
                add = true;
                testInstruction = new TestInstruction();
                testInstruction.setTests(new ArrayList<>());
                testInstruction.setRequirementIds(reqIds);
                testInstruction.setArea(area);

            } else if (currentParagraph.getText().contains("Avslutande åtgärder")) {
                //System.out.println(testInstruction.toString());
                System.out.println(testInstruction.toString());

                testInstructionList.add(testInstruction);
                add = false;
            } else if (add) {
                if(currentParagraph.getText().length() < 6){
                    if(checkIfNewTest(currentParagraph.getText().toCharArray())){
//                        if(!testInstruction.getTests().isEmpty()){
//                            testInstructionList.add(testInstruction);
//                        }
                        //testInstruction = new TestInstruction();
                        testInstruction.setId(currentParagraph.getText());
                        //testInstruction.setTests(new ArrayList<>());
                    }
                }
                else if (!currentParagraph.getText().isEmpty() && !currentParagraph.getText().contains("Mötesprotokoll:") &&
                        !currentParagraph.getText().contains("Användarintyg:__")) {
                    testInstruction.getTests().add(currentParagraph.getText());
                }
            }
        }
    }

    private boolean checkIfNewTest(char[] chars){
        if(chars.length == 0)
            return false;

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

    private String getArea(String fileName){
        if(fileName.contains("ECView") || fileName.contains("EC-View"))
            return "ECProjects\\Legacy\\ECView Legacy";
        return null;
    }

}
