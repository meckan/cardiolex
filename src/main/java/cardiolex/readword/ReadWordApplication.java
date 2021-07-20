package cardiolex.readword;

import cardiolex.readword.BO.ReqDocument;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ReadWordApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReadWordApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args){
        //readReqFiles();
        readElements();
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
            ReqDocument document = new ReqDocument();
            String fileName = "E:\\Storage\\Cardiolex\\Files\\RS-20025-Kravspecifikation-EC View-2.2.docx";

            document.checkAllElements(fileName);
        }catch (IOException e){
            System.out.println("Error: "+ e.getMessage());
        }
    }

    void readReqFiles(){
        ReqDocument readWord = new ReqDocument();
        String folderPath = "E:\\Storage\\Cardiolex\\Files\\readFiles";

        folderPath = "E:\\Storage\\Cardiolex\\Files\\TS-10019-TestSpec-CommonECSenseView.docx";

        folderPath = "E:\\Storage\\Cardiolex\\Files\\TS-20033-TestSpec EC View 2.2 tillägg.docx";
        folderPath = "E:\\Storage\\Cardiolex\\Files\\RS-20025-Kravspecifikation-EC View-2.2.docx";

        readWord.readFolder(folderPath);
        //readWord.readFolder("src/main/resources/testdoc.docx");
        //readWord.readFolder("src/main/resources/testinstruktion.docx");
        readWord.printIntoCSV();
    }
}


