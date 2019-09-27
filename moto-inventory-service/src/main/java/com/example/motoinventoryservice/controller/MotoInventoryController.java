package com.example.motoinventoryservice.controller;

import com.example.motoinventoryservice.dao.MotoInventoryDao;
import com.example.motoinventoryservice.model.Motorcycle;
import com.example.motoinventoryservice.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RefreshScope
public class MotoInventoryController {

    @Autowired
    private MotoInventoryDao motoInventoryDao;

    @RequestMapping(value = "/motorcycles", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Motorcycle createMotorcycle(@RequestBody @Valid Motorcycle motorcycle) {
        motorcycle = motoInventoryDao.addMotorcycle(motorcycle);

        return motorcycle;
    }

    @RequestMapping(value = "/motorcycles/{motoId}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public Motorcycle getMotorcycle(@PathVariable int motoId) {
        if (motoId < 1) {
           throw new IllegalArgumentException("MotoId must be greater than 0.");
        }

        Motorcycle moto = motoInventoryDao.getMotorcycle(motoId);

        return moto;
    }

    @RequestMapping(value = "/motorcycles/{motoId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMotorcycle(@PathVariable("motoId") int motoId) {
        // do nothing here - in a real application we would delete the entry from
        // the backing data store.
    }

    @RequestMapping(value = "/motorcycles/{motoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMotorcycle(@RequestBody @Valid Motorcycle motorcycle, @PathVariable int motoId) {
        // make sure the motoId on the path matches the id of the motorcycle object
        if (motoId != motorcycle.getId()) {
            throw new IllegalArgumentException("Motorcycle ID on path must match the ID in the Motorcycle object.");
        }

        // do nothing here - in a real application we would update the entry in the backing data store

    }

    @RequestMapping(value = "/motorcycles/make/{make}")
    @ResponseStatus(HttpStatus.FOUND)
    public List<Motorcycle> getMotorcycleByMake(@PathVariable String make){
        List<Motorcycle> motoList = motoInventoryDao.getMotorcyclesByMake(make);
        return motoList;
    }

    @Autowired
    private DiscoveryClient discoveryClient;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${vinLookupName}")
    private String vinLookupName;

    @Value("${serviceProtocol}")
    private String serviceProtocol;

    @Value("${servicePath}")
    private String servicePath;

    @RequestMapping(value="/vehicle/{vin}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> getVinLookup(@PathVariable("vin") int vin) {

        List<ServiceInstance> instances = discoveryClient.getInstances(vinLookupName);

        String vinLookupUri = serviceProtocol + instances.get(0).getHost() + ":" + instances.get(0).getPort() + servicePath + vin;

        Vehicle vehicle = restTemplate.getForObject(vinLookupUri, Vehicle.class);

        Map<String, String> vinMap= new HashMap<>();
        vinMap.put("Vehicle Type", vehicle.getType());
        vinMap.put("Vehicle Make", vehicle.getMake());
        vinMap.put("Vehicle Model", vehicle.getModel());
        vinMap.put("Vehicle Year", vehicle.getYear());
        vinMap.put("Vehicle Color", vehicle.getColor());

        return vinMap;
    }
}
