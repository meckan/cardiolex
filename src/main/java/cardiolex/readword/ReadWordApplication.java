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
        readReqFiles();
        //readElements();
//        for (int i = 0; i < args.getSourceArgs().length; i++) {
//            System.out.println(args.getSourceArgs()[i]);
//            if(args.getSourceArgs()[i].equals("initDB")) {
//                System.out.println(args.getSourceArgs()[i]);
//                System.out.println(args.getSourceArgs()[i+1]);
//            }
//        }
    }

    void readElements(){
        try {
            ReqDocument document = new ReqDocument();
            document.checkAllElements();
        }catch (IOException e){
            System.out.println("Error: "+ e.getMessage());
        }
    }

    //@Bean
    void readReqFiles(){
        ReqDocument readWord = new ReqDocument();
        readWord.readFolder("src/main/resources/testdoc.docx");
        readWord.printIntoCSV();
    }
}


