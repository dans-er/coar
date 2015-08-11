package nl.knaw.dans.coar.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Reporter
{

    public static void report(String file, String line) {
        File reports = new File("reports");
        if (!reports.exists()) {
            reports.mkdir();
        }
        File report = new File(reports, file);
        try
        {
            FileUtils.writeStringToFile(report, line + "\n", "UTF-8", true);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
