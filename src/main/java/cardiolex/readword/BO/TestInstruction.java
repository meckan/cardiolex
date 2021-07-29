package cardiolex.readword.BO;

import lombok.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TestInstruction {

    private String id;
    private List<String> tests;

    private List<String> requirementIds;

    private String worksItem;
    private String path;
    private String area;


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
        Iterator<String> iterator = Arrays.stream(cvsHeader).iterator();
        String s;
        while (iterator.hasNext()) {
            s = iterator.next();
            //for (String s : cvsHeader) {


            if (s.equals("title 2"))
                if (id != null && !id.isEmpty())
                    builder.append(checkSpecialChars(id));
                    // TODO remove och kom på annan metod
                else
                    builder.append(checkSpecialChars("Temp name"));

            if (s.equals("Work Item Type"))
                if (worksItem != null)
                    builder.append(checkSpecialChars(worksItem));

            if (s.equals("area path"))
                if (area != null)
                    builder.append(checkSpecialChars(area));
            if (s.equals("iteration path"))
                if (path != null)
                    builder.append(checkSpecialChars(path));


            if (s.equals("Steps")) {
                //builder.append("\"<steps id=\"0\" last=\"").append(tests.size()+1)
                builder.append(("\"<steps "))
                        .append(checkSpecialCharsForTest( "id=\"0\" last=\"")).append(tests.size()+1)
                        .append(checkSpecialCharsForTest("\""))
                        .append(">");
                builder.append(checkSpecialCharsForTest(getTestSteps()));
                builder.append(("</steps>\""));
            }

            if (s.equals("state"))
                builder.append("Design");

            if (iterator.hasNext())
                builder.append(",");
        }

        builder.append(System.lineSeparator());
        //builder.append(System.lineSeparator()).append(getTestSteps(cvsHeader));

        return builder.toString();
    }

    public String getTestSteps(){
        StringBuilder builder = new StringBuilder();

        int i = 2;
        for (String test : tests) {
            if (!test.isEmpty()) {
                //builder.append("<step id=\"")
                builder.append("<step id=\"").append(i++)
            .append("\" type=\"ActionStep\"><parameterizedString isformatted=\"true\">&lt;DIV&gt;&lt;P&gt;")
                        .append(checkSpecialChars(test))
                        .append(checkSpecialChars( "&lt;/P&gt;&lt;/DIV&gt;</parameterizedString><parameterizedString isformatted="))
                        .append( "\"true\"" )
                               .append(checkSpecialChars(">&lt;DIV&gt;&lt;P&gt;&lt;BR/&gt;&lt;/P&gt;&lt;/DIV&gt;</parameterizedString><description/></step>"));
            }
        }
        return builder.toString();
    }

    private String checkSpecialChars(String data) {
         data = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replaceAll("\"", "\"\"");
            //escapedData = "\"" + data + "\"";
        }

        data = data.replaceAll("\n","");
        data = data.replaceAll("\"","");
        return data.replaceAll(",", " ");
        //return escapedData;
    }

    private String checkSpecialCharsForTest(String data) {
        data = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replaceAll("\"", "\"\"");
            //escapedData = "\"" + data + "\"";
        }

        data = data.replaceAll("\n","");
        //data = data.replaceAll("\"","");
        return data.replaceAll(",", " ");
        //return escapedData;
    }
}
