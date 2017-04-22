package ru.ag78.utils.cfgman;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class RegTest {

    public static void main(String[] args) {

        System.out.println("RegTest is here...");

        try {
            RegTest t = new RegTest();
            t.start(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * https://stackoverflow.com/questions/17462146/java-patternsyntaxexception-illegal-repetition-on-string-substitution/17462186#17462186?newreg=6ca8d1feb7f74c7e82f08c9bad6ef270
     * 
     * @param args
     * @throws Exception
     */
    private void start(String[] args) throws Exception {

        String filename = args[0];
        String regex = args[1];
        String file = FileUtils.readFileToString(new File(filename));

        System.out.println("filename=" + filename);
        System.out.println("regex=" + regex);
        System.out.println("file={" + file + "}");

        Map<String, String> replacements = new HashMap<String, String>() {

            /**
             * For serialization
             */
            private static final long serialVersionUID = -1523661106200914884L;

            {
                put("${blogic.host}", "cedr34");
                put("${blogic.port}", "5002");
                put("${blogic.login}", "blog3");
                put("${blogic.password}", "log");
            }
        };

        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(file);
        while (m.find()) {
            System.out.println(m.group());

            String repString = replacements.get(m.group());
            if (repString != null) {
                m.appendReplacement(sb, repString);
            }
        }
        m.appendTail(sb);
        System.out.println(sb.toString());
    }
}
