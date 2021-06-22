package cardiolex.readword.BO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TestInstruction {

    private String id;
    private List<String> tests;


    private String worksItem;
    private String path;


    public String getCSVHeader() {
        return "id,testStep,test";
    }

    public String getCSV() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
//        if(id != null)
//            builder.append(checkSpecialChars(id));
        for (String s : tests) {
            builder.append(id).append(",").append(i++).append(",").append(checkSpecialChars(s)).append(System.lineSeparator());
        }
        return builder.toString();
    }

    public String getComboCSV(String[] cvsHeader) {
        if (worksItem == null)
            worksItem = "Test Case";
        if (path == null)
            path = "ECProjects\\Legacy";

        StringBuilder builder = new StringBuilder();

        for (String s : cvsHeader) {
            if (s.equals("title 2"))
                if (id != null)
                    builder.append(checkSpecialChars(id));

            if (s.equals("Work Item Type"))
                if (worksItem != null)
                    builder.append(checkSpecialChars(worksItem));

            if (s.contains("path"))
                if (path != null)
                    builder.append(checkSpecialChars(path));

            if (s.equals("description"))
                if (!tests.isEmpty())
                    if (tests.get(0) != null)
                        builder.append(checkSpecialChars(tests.get(0)));


            if (s.equals("state"))
                builder.append("Design");

            builder.append(";");
        }
        return builder.toString();
    }

    private String checkSpecialChars(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;


    }
}
