package de.seanbri.aquaplane.service;

import de.seanbri.aquaplane.argumentQuality.ArgumentQualityUtility;
import de.seanbri.aquaplane.argumentQuality.AssessmentApproachProcessor;
import de.seanbri.aquaplane.assessment_approaches.AARecords;
import de.seanbri.aquaplane.model.ArgumentPair;

import de.seanbri.aquaplane.repository.ArgumentPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ArgumentPairService implements IArgumentPairService {

    @Autowired
    private ArgumentPairRepository argumentPairRepository;

    @Override
    public ArgumentPair saveArgumentPair(ArgumentPair argumentPair) {
        return argumentPairRepository.save(argumentPair);
    }

    @Override
    public String compareArgumentQuality(ArgumentPair argumentPair) {
        AARecords records1 = AssessmentApproachProcessor.compute(argumentPair.getArg1(), argumentPair.getClaim());
        AARecords records2 = AssessmentApproachProcessor.compute(argumentPair.getArg2(), argumentPair.getClaim());
        return ArgumentQualityUtility.compare(argumentPair, records1, records2);
    }

}
