package guru.qa.tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import guru.qa.domain.Fruit;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesTest {

    private final ClassLoader classLoader = FilesTest.class.getClassLoader();
    private final String file = "test-files.zip";

    @Test
    void pdfRead() throws Exception {
        String pdfFileName = "pdf-test.pdf";
        InputStream inputStream = getFile(file, pdfFileName);
        PDF pdf = new PDF(inputStream);
        assertThat(pdf.text.contains("PDF Test File"));
        inputStream.close();
    }

    @Test
    void xlsxRead() throws Exception {
        String xlsxFileName = "xlsx-test.xlsx";
        InputStream inputStream = getFile(file, xlsxFileName);
        XLS xls = new XLS(inputStream);
        assertThat(xls.excel
                .getSheetAt(0)
                .getRow(0)
                .getCell(0)
                .getStringCellValue()).contains("Test Excel File");
        inputStream.close();
    }

    @Test
    void csvRead() throws Exception {
        String csvFileName = "csv-test.csv";
        InputStream inputStream = getFile(file, csvFileName);
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, UTF_8));
        List<String[]> csv = csvReader.readAll();
        assertThat(csv).contains(
                new String[]{"10", "Xerox 198", "Dorothy Badders", "678", "-226.36",
                        "4.98", "8.33", "Nunavut", "Paper", "0.38"}
        );
        inputStream.close();
    }

    @Test
    void parseJsonTest() throws Exception {
        String jsonFileName = "json-test.json";
        InputStream inputStream = classLoader.getResourceAsStream(jsonFileName);
        ObjectMapper objectMapper = new ObjectMapper();
        Fruit fruit = objectMapper.readValue(inputStream, Fruit.class);
        assertThat(fruit.type).isEqualTo("Apple");
        assertThat(fruit.size).contains("Large", "Small");
        assertThat(fruit.color).isEqualTo("Red");
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private InputStream getFile(final String archiveName, final String fileName) throws Exception {
        URL zipUrl = classLoader.getResource(archiveName);
        File zipFile = new File(zipUrl.toURI());
        ZipFile zip = new ZipFile(zipFile);
        return zip.getInputStream(zip.getEntry(fileName));
    }
}
