package de.seanbri.aquaplane.evaluation_service;

import de.seanbri.aquaplane.model.ArgumentPair;
import de.seanbri.aquaplane.service.IArgumentPairService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EvaluationService {

    @Autowired
    private IArgumentPairService IArgumentPairService;

    public void computeForAllInstances(Path datasetPath) {
        try (Reader in = new FileReader(datasetPath.toFile())) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setDelimiter("\t")
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(in);

            int index = 0;
            for (CSVRecord record : records) {
                String id = record.get(0);
                String a1 = record.get(1);
                String a2 = record.get(2);
                String claim = record.get(3);

                // Check if results are already saved in json file
                String fileName = id + ".json";
                Path jsonFilePath = Paths.get(
                        "src", "main", "resources", "evaluation", "json-results", fileName
                );
                if (Files.exists(jsonFilePath)) {
                    continue;
                }

                // Compute results for record
                ArgumentPair argumentPair = new ArgumentPair(
                        index, a1, a2, claim
                );

                System.out.println("Aquaplaneify instance with id " + id);
                String jsonResult = IArgumentPairService.compareArgumentQuality(argumentPair);

                // Write result to json file
                try (PrintWriter out = new PrintWriter(new FileWriter(jsonFilePath.toFile()))) {
                    out.write(jsonResult);
                }

                index++;
            }

            System.out.println("Compared argument quality of all instances!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
