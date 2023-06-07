package de.seanbri.aquaplane.controller;

import de.seanbri.aquaplane.model.ArgumentPair;
import de.seanbri.aquaplane.service.IArgumentPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/argumentpair")
public class ArgumentPairController {

    @Autowired
    private IArgumentPairService IArgumentPairService;

    @PostMapping("/add")
    public int add(@RequestBody ArgumentPair argumentPair) {
        IArgumentPairService.saveArgumentPair(argumentPair);
        return argumentPair.getId();
    }

    @PostMapping("/aquaplaneify")
    public String aquaplaneify(@RequestBody ArgumentPair argumentPair) {
        return IArgumentPairService.compareArgumentQuality(argumentPair);
    }
}