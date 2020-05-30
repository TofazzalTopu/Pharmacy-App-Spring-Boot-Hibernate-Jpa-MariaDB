package com.asl.pms.service;




import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.transaction.Transactional;

import com.asl.pms.viewmodels.DrugStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.asl.pms.exception.NotFoundException;
import com.asl.pms.model.Company;
import com.asl.pms.model.DosageForm;
import com.asl.pms.model.Drug;
import com.asl.pms.model.Generic;
import com.asl.pms.model.Location;
import com.asl.pms.model.Rack;
import com.asl.pms.model.Search;
import com.asl.pms.repository.DrugRepository;
import com.asl.pms.repository.LocationRepository;
import com.asl.pms.repository.RackRepository;
import com.asl.pms.viewmodels.IDrug;

//import lombok.extern.slf4j.Slf4j;

@Service
//@Slf4j
public class DrugService {
	
	@Autowired
    private DrugRepository drugRepo;
	@Autowired
    private GenericService genericService;
	@Autowired
    private DosageFormService dosageFormService;
	@Autowired
    private CompanyService companyService;
	@Autowired
    private LocationRepository locationRepo;
	
	@Autowired
    private RackRepository rackRepo;

    @Autowired
    public DrugService(DrugRepository drugRepo,
                       GenericService genericService,
                       DosageFormService dosageFormService,
                       CompanyService companyService,
                       LocationRepository locationRepo) {
        this.drugRepo = drugRepo;
        this.genericService = genericService;
        this.dosageFormService = dosageFormService;
        this.companyService = companyService;
        this.locationRepo = locationRepo;
    }

    public Drug save(Drug drug) {
        return this.drugRepo.save(drug);
    }

    public List<Drug> searchDrugs(String query) {
    	
    	  
    	  
      //  return drugRepo.findTop50DrugsByNameStartingWith(query);
       return drugRepo.findDrugsByNameStartingWith(query);
      //  return drugRepo.findTop30DrugsByNameStartingWith(query);
        
       
    }

    public List<IDrug> searchByQuery(String query) {
        return drugRepo.findTop50DrugsByNameStartingWith(query);
    }

    public Page<Drug> findDrugs(Pageable pageable) {
        return drugRepo.findAll(pageable);
    }

    @Transactional
    public Drug save(com.asl.pms.api.v1.payload.Drug drug) {
        Generic generic = genericService.findGeneric(drug.getGeneric());
        DosageForm dosageForm = dosageFormService.findDosageForm(drug.getDosageForm());
        Company company = companyService.findCompany(drug.getCompany());
        
        Drug drug1 = new Drug(drug.getName(),
                generic,
                dosageForm,
                drug.getStrength(),
                company,
                drug.getPrice());
        Rack rack=rackRepo.getOne(drug.getRack());
        drug1.setRack(rack);

        return drugRepo.save(drug1);
    
    }

    @Transactional
    public Drug save(com.asl.pms.viewmodels.Drug drug) throws NumberFormatException {

        Map<String, Generic> drugGeneric = new HashMap<>();
        Map<String, DosageForm> drugDosageForm = new HashMap<>();
        Map<String, Company> drugCompany = new HashMap<>();
        Map<String, Location> drugLocation = new HashMap<>();

        Set<Generic> generics = new HashSet<>();
        Set<DosageForm> dosageForms = new HashSet<>();
        Set<Company> companies = new HashSet<>();
        Set<Location> locations = new HashSet<>();

        generics.add(new Generic(drug.getGeneric()));
//        dosageForms.add(new DosageForm(drug.getDosageForm()));
        companies.add(new Company(drug.getCompany()));

        Random random = new Random();
        int rack = random.nextInt(100);
        locations.add(new Location("foo", rack));

        Iterable<Generic> generics1 = genericService.save(generics);
        final String drugName = drug.getName();
        for (Generic generic: generics1)
            drugGeneric.put(drugName, generic);

        Iterable<DosageForm> dosageForms1 = dosageFormService.save(dosageForms);
        for (DosageForm dosageForm: dosageForms1)
            drugDosageForm.put(drugName, dosageForm);

        Iterable<Company> companies1 = companyService.save(companies);
        for (Company company: companies1)
            drugCompany.put(drugName, company);

        Iterable<Location> locations1 = locationRepo.saveAll(locations);
        for (Location location: locations1)
            drugLocation.put(drugName, location);

        double price = Double.parseDouble(drug.getPrice());
        Drug drug1 = new Drug(drugName,
                drugGeneric.get(drugName),
                drugDosageForm.get(drugName),
                drug.getStrength(),
                drugCompany.get(drugName),
                drugLocation.get(drugName),
                price);

        Drug saved = drugRepo.save(drug1);

        return saved;
    }

    public Drug findDrug(long id) {
        Optional<Drug> drug = drugRepo.findById(id);
        if (!drug.isPresent())
            throw new NotFoundException(Drug.class);

        return drug.get();
    }

    public List<Drug> findDrugsBy(Generic generic, DosageForm form) {

        Drug probe = new Drug(new Generic(generic.getId()),
                new DosageForm(form.getId()));
        Example<Drug> example = Example.of(probe);
        return drugRepo.findAll(example);
    }

    public List<IDrug> search(Search search) {
        return drugRepo.findTop50DrugsByNameStartingWith(search.getQuery());
    }

    public Drug update(long id, com.asl.pms.api.v1.payload.Drug drug) {

        Drug drug2 = findDrug(id);
        double price = drug2.getPrice();
        double price1 = drug.getPrice();
        if (price != price1) {
            drug2.setPrice(price1);
            return drugRepo.save(drug2);
        }

        return drug2;
    }

    public List<Drug> findDrugs(List<Long> drugIds) {
        return drugRepo.findAllById(drugIds);
    }
    
    public Drug update(Drug drug) {
        Drug updated=drugRepo.save(drug);
        return updated;
    }
    
    
    public Page<Drug> searchDrugs(String query, PageRequest pageable) {
    	
    	return  drugRepo.findAllByNameStartingWith(query, pageable);
    }

    public List<DrugStock> showDrugByMedicineName(String name) {
        List<DrugStock> drugs = drugRepo.showDrugByMedicineName(name);
        return drugs;
    }
	

}
