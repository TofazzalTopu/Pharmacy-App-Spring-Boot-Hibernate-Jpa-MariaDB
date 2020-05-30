package com.asl.pms.service;

import com.asl.pms.exception.NotFoundException;
import com.asl.pms.model.DosageForm;
import com.asl.pms.model.Search;
import com.asl.pms.repository.DosageFormRepository;
import com.asl.pms.viewmodels.IDosageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DosageFormService {

	private final DosageFormRepository dosageFormRepo;

	@Autowired
	public DosageFormService(DosageFormRepository dosageFormRepo) {
		this.dosageFormRepo = dosageFormRepo;
	}

	public DosageForm findDosageForm(long id) {
		Optional<DosageForm> dosageForm = dosageFormRepo.findById(id);
		if (!dosageForm.isPresent())
			throw new NotFoundException(DosageForm.class);

		return dosageForm.get();
	}

	public Iterable<DosageForm> save(Set<DosageForm> dosageForms) {
		return dosageFormRepo.saveAll(dosageForms);
	}

	public List<IDosageForm> search(Search search) {
		String query = search.getQuery();
		return dosageFormRepo.findTop50ByNameStartingWith(query);
	}

	public List<IDosageForm> findAll() {
		return dosageFormRepo.findAllByOrderByNameAsc();
	}
}
