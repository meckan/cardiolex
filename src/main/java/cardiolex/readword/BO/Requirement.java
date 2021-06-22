package cardiolex.readword.BO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Requirement {

    private String ID;

    private String worksItem;
    private String title;
    private String description;

    private String state;
    private String ReqId;

    private String effort;
    private String path;

    private List<String> tests;


    public String getCSVHeader() {
        return "id,Work Item Type,title,description,state,reqId,effort,path,tags";
    }

    public String getComboHeader() {
        return "id;Work Item Type;title 1;title 2;description;state;Req ID;effort;area path;iteration path;tags";
    }
    public String[] getComboHeaderArray() {
        return new String[]{"id", "Work Item Type", "title 1", "title 2",
                "description", "state", "Req ID", "effort","area path" ,"iteration path", "tags"};
    }

    public String getCSV() {

        if (worksItem == null)
            worksItem = "User Story";
        if (path == null)
            path = "ECProjects\\Legacy";

        StringBuilder builder = new StringBuilder();
        checkString(builder, ID, worksItem, title + ": " + ReqId, description);
        checkString(builder, state, ReqId, effort, path);

        if (tests.isEmpty())
            return builder.toString();
        for (String s : tests) {
            if (!s.isEmpty())
                builder.append(checkSpecialChars(s)).append(" ; ");
        }
        return builder.toString();
    }

    public String getCSVCombo(String[] cvsHeaders) {

        if (worksItem.isEmpty())
            worksItem = "User Story";
        if (path.isEmpty())
            path = "ECProjects\\Legacy";

        StringBuilder builder = new StringBuilder();
        for (String s : cvsHeaders) {
            if (s.equals("id"))
                if (ID != null)
                    builder.append(checkSpecialChars(ID));
            if (s.equals("Work Item Type"))
                if (worksItem != null)
                    builder.append(checkSpecialChars(worksItem));
            if (s.equals("title 1"))
                if (title != null)
                    builder.append(checkSpecialChars(title + ": " +ReqId));
            if (s.equals("description"))
                if (description != null)
                    builder.append(checkSpecialChars(description));
            if (s.equals("state"))
                if (state != null)
                    builder.append(checkSpecialChars(state));
                else
                    builder.append("New");
            if (s.equals("Req ID"))
                if (ReqId != null)
                    builder.append(checkSpecialChars(ReqId));
            if (s.equals("effort"))
                if (effort != null)
                    builder.append(checkSpecialChars(effort));
            if (s.contains("path"))
                if (path != null)
                    builder.append(checkSpecialChars(path));

            if (s.equals("tags")) {
                if (tests.isEmpty())
                    return builder.toString();
                for (String test : tests) {
                    if (!test.isEmpty())
                        builder.append(checkSpecialChars(test)).append(" , ");
                }
            }
            builder.append(";");
        }
        return builder.toString();
    }

    private void checkString(StringBuilder builder, String state, String reqId, String effort, String path) {
        if (state != null)
            builder.append(checkSpecialChars(state));
        builder.append(",");
        if (reqId != null)
            builder.append(checkSpecialChars(reqId));
        builder.append(",");
        if (effort != null)
            builder.append(checkSpecialChars(effort));
        builder.append(",");
        if (path != null)
            builder.append(checkSpecialChars(path));
        builder.append(",");
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
