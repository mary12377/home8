package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


import static org.hamcrest.MatcherAssert.assertThat;


public class FilesParsingTet {
    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    void zipTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/files/zip/examples.zip"));
        ZipInputStream is = new ZipInputStream(Objects.requireNonNull(classLoader.getResourceAsStream("files/zip/examples.zip")));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {

            if (entry.isDirectory()) {
                System.out.println("dir  : " + entry.getName());
            } else {
                System.out.println("file : " + entry.getName());
            }

             if (entry.getName().equals("csv/CVS-example.csv")) {
                try (InputStream inputStream = zf.getInputStream(entry)) {
                    CSVReader csvReader = new CSVReader(
                            new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                    List<String[]> content = csvReader.readAll();

                    Assertions.assertThat(content).contains(
                            new String[]{"ANZSIC", "DataInfo"},
                            new String[]{"Energy", "Transport"},
                            new String[]{"Econmic", "Environment"});
                }

            } else if (entry.getName().equals("pdf/pdf_example.pdf")) {
                try (InputStream inputStream = zf.getInputStream(entry)) {
                    PDF pdf = new PDF(inputStream);

                    Assertions.assertThat(pdf.numberOfPages).isEqualTo(2);
                    assertThat(pdf, new ContainsExactText("PDF File"));
                }
            }

            else if (entry.getName().equals("xlsx/xls_example.xls")) {
                try (InputStream inputStream = zf.getInputStream(entry)) {
                    XLS xls = new XLS(inputStream);

                    String value = xls.excel.getSheetAt(0).getRow(2).getCell(4).getStringCellValue();
                    Assertions.assertThat(value).contains("Hashimoto");
                }

            }
        }
    }
}
