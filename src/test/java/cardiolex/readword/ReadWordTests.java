package cardiolex.readword;

import cardiolex.readword.BO.Requirement;
import cardiolex.readword.BO.TestInstruction;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@SpringBootTest
public class ReadWordTests {

    @Test
    void kravTest(){
        try {
            File file = new File("src/main/resources/testdoc.docx");
            XWPFDocument document = new XWPFDocument(new FileInputStream(file));
            List<XWPFTable> tables = document.getTables();

            Scanner scanner = new Scanner(System.in);
            List<String> answers = Arrays.asList("y","yes");

            for (XWPFTable t : tables) {

                System.out.println(t.getText());
                System.out.println("Is table requirements? (yes (y), no (n))");
                if(answers.contains(scanner.nextLine().toLowerCase(Locale.ROOT)))
                    readTable(t);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void AK121() {
        try {
            File file = new File("src/main/resources/ak121.docx");
            XWPFDocument document = new XWPFDocument(new FileInputStream(file));
            List<XWPFTable> tables = document.getTables();
            for (XWPFTable t : tables) {
                readTable(t);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Requirement readTable(XWPFTable table) {
        Requirement req = new Requirement();
        for (XWPFTableRow row : table.getRows()) {

            req.setReqId(row.getCell(0).getText());
            req.setDescription(row.getCell(1).getText());
            req.setTests(new ArrayList<>());

            for (XWPFTableCell cell : row.getTableCells()) {
                if(!cell.getText().isEmpty())
                    //System.out.println("Cell text: " + cell.getText());
                if(!cell.getTables().isEmpty()) {
                    for (XWPFTable inner : cell.getTables()) {
                        req.setDescription(readInnerTable(inner,new StringBuilder(req.getDescription()),"\t"));
                    }
                }
            }
        }
        //System.out.println(req.toString());
        return req;
    }

    private String readInnerTable(XWPFTable table,StringBuilder des,String tab){
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                if(!cell.getText().isEmpty())
                    des.append(System.lineSeparator()).append(tab).append(cell.getText());
                if(!cell.getTables().isEmpty()) {
                    for (XWPFTable inner : cell.getTables()) {
                        des.append(readInnerTable(inner, des,tab+"\t"));
                        //req.setDescription(readInnerTable(inner,req.getDescription()));
                    }
                }
            }
        }
        return des.toString();
    }

    @Test
    void testGuide(){
        try{
            File file = new File("src/main/resources/testinstruktion.docx");
            XWPFDocument document = new XWPFDocument(new FileInputStream(file));
            TestInstruction testInstruction = null;
            boolean add = false;
            for (XWPFParagraph p: document.getParagraphs()) {
                if(p.getText().contains("Testinstruktion")) {
                    add = true;
                    testInstruction = new TestInstruction();
                    testInstruction.setTests(new ArrayList<>());
                }
                else if(p.getText().contains("Avslutande åtgärder"))
                    add = false;

                else if(add){
                    if(!p.getText().isEmpty())
                        testInstruction.getTests().add(p.getText());
                }
            }
            if(testInstruction != null) {
                System.out.println(testInstruction.toString());
                System.out.println(testInstruction.getTests().size());
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }


    @Test
    void test(){
        List<String> list = Arrays.asList("A","B","C","D");

        String[] array = {"1","2","3"};

        System.out.println(list.indexOf("A"));
        try {
            System.out.println(list.indexOf("D"));
            System.out.println(array[list.indexOf("D")]);
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Nope");
        }
        System.out.println(list.indexOf("B"));
    }

}
