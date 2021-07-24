package cardiolex.readword;

import cardiolex.readword.BO.ReqDocument;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

@SpringBootApplication
public class ReadWordApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReadWordApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args){
        readReqFiles();
        //readElements();
        //readArgs(args);
    }

    void readArgs(ApplicationArguments args){
        ReqDocument document = new ReqDocument();
        for (int i = 0; i < args.getSourceArgs().length; i++) {
            if(args.getSourceArgs()[i].equals("folder")) {
                document.readFolder(args.getSourceArgs()[i+1]);
                //System.out.println(args.getSourceArgs()[i]);
                //System.out.println(args.getSourceArgs()[i+1]);
            }
        }
        document.printIntoCSV();
    }

    void readElements(){
        try {
            //ReqDocument document = new ReqDocument();
            String fileName = "E:\\Storage\\Cardiolex\\Files\\RS-20025-Kravspecifikation-EC View-2.2.docx";

            fileName = "E:\\Storage\\Cardiolex\\Files\\TS-20031-TestSpec Regression_ECView_1.x.docx";

            //Klar
            //fileName = "E:\\Storage\\Cardiolex\\Files\\TS-20033-TestSpec EC View 2.2 tillägg.docx";

            checkAllElements(fileName);
        }catch (IOException e){
            System.out.println("Error: "+ e.getMessage());
        }
    }

    void readReqFiles(){
        ReqDocument readWord = new ReqDocument();
        String folderPath = "E:\\Storage\\Cardiolex\\Files\\readFiles";

        //folderPath = "E:\\Storage\\Cardiolex\\Files\\readFiles\\RS-20025-Kravspecifikation-EC View-2.2-omgjord.docx";
        //folderPath = "E:\\Storage\\Cardiolex\\Files\\TS-10019-TestSpec-CommonECSenseView.docx";

        //folderPath = "E:\\Storage\\Cardiolex\\Files\\TS-20033-TestSpec EC View 2.2 tillägg.docx";
        //folderPath = "E:\\Storage\\Cardiolex\\Files\\RS-20025-Kravspecifikation-EC View-2.2.docx";

        //folderPath = "E:\\Storage\\Cardiolex\\Files\\TS-20031-TestSpec Regression_ECView_1.x.docx";

        readWord.readFolder(folderPath);
        //readWord.readFolder("src/main/resources/testdoc.docx");
        //readWord.readFolder("src/main/resources/testinstruktion.docx");
        readWord.printIntoCSV();
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
                System.out.println("Rows: " + ((XWPFTable) elem).getRows().size());
                if(((XWPFTable) elem).getText().isEmpty()){
                    System.out.println(((XWPFTable) elem).getRows().size());
                    for (XWPFTableRow row: ((XWPFTable) elem).getRows()) {
                        for (XWPFTableCell cell: row.getTableCells()) {
                            System.out.println(cell.getText());
                        }
                    }
                }
            }else if(elem instanceof XWPFSDT){
                System.out.println(elem.getClass());
                XWPFSDT xwpfsdt = (XWPFSDT)elem;

                System.out.println(xwpfsdt.getContent().getText());
            }
            else {
                System.out.println(elem.getClass());
            }
        }
    }
}


