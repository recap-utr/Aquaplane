package de.seanbri.aquaplane;

import de.seanbri.aquaplane.evaluation_service.EvaluationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.nio.file.Paths;

@SpringBootApplication
public class AquaplaneApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(AquaplaneApplication.class, args);

		// Execute for evaluation on a dataset
		//EvaluationService evaluationService = applicationContext.getBean(EvaluationService.class);
		//evaluationService.computeForAllInstances(
		//		Paths.get(
		//				"src", "main", "resources", "evaluation",
		//				"ukp-convarg-dagstuhl-argquality-combined.csv"
		//		)
		//);
	}

}