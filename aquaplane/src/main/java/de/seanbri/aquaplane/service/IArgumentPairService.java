package de.seanbri.aquaplane.service;

import de.seanbri.aquaplane.model.ArgumentPair;

public interface IArgumentPairService {

    public ArgumentPair saveArgumentPair(ArgumentPair argumentPair);
    public String compareArgumentQuality(ArgumentPair argumentPair);

}