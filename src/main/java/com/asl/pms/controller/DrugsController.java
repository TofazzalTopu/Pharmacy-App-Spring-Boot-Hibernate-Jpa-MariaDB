package com.asl.pms.controller;

import java.util.List;

import javax.validation.Valid;

import com.asl.pms.viewmodels.DrugStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asl.pms.model.DosageForm;
import com.asl.pms.model.Drug;
import com.asl.pms.model.Generic;
import com.asl.pms.model.Rack;
import com.asl.pms.model.Shelf;
import com.asl.pms.service.CompanyService;
import com.asl.pms.service.DosageFormService;
import com.asl.pms.service.DrugService;
import com.asl.pms.service.GenericService;
import com.asl.pms.service.RackService;
import com.asl.pms.service.ShelfService;
import com.asl.pms.util.GlobalMethod;
import com.asl.pms.viewmodels.ICompany;
import com.asl.pms.viewmodels.IDosageForm;
import com.asl.pms.viewmodels.IGeneric;

@Controller
@RequestMapping("/drugs")
public class DrugsController {

	@Autowired
	GlobalMethod globalMethod;

	private final DrugService drugService;

	@Autowired
	GenericService genericService;

	@Autowired
	DosageFormService dosageFormService;

	@Autowired
	CompanyService companyService;

	@Autowired
	RackService rackService;

	@Autowired
	ShelfService shelfService;

	@Autowired
	public DrugsController(DrugService drugService) {
		this.drugService = drugService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getDrugList(Model model, @RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "50") Integer total,
			@RequestParam(required = false, defaultValue = "name") String sort) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());	
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());

		Sort.Order order;
		if (sort.startsWith("-"))
			order = Sort.Order.asc(sort.replace("-", ""));
		else
			order = Sort.Order.desc(sort);

		Sort sortBy = Sort.by(order);
		PageRequest pageable = PageRequest.of(page, total, sortBy);
		Page<Drug> drugs = drugService.findDrugs(pageable);
		List<Drug> content = drugs.getContent();
		model.addAttribute("drugs", content);

		model.addAttribute("total", drugs.getTotalElements());
		model.addAttribute("pages", drugs.getTotalPages());
		model.addAttribute("current", drugs.getNumber());
		model.addAttribute("size", drugs.getSize());
		model.addAttribute("sortBy", sort);

		int previous = drugs.hasPrevious() ? drugs.previousPageable().getPageNumber() : 0;
		model.addAttribute("previous", previous);

		int next = drugs.hasNext() ? drugs.nextPageable().getPageNumber() : drugs.getTotalPages();
		model.addAttribute("next", next);
		
		return "/dashboard/drugs/drug";
	}

	@RequestMapping(method = RequestMethod.GET, value = "{id}")
	public String drug(@PathVariable Long id, Model model) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());

		Drug drug = drugService.findDrug(id);
		model.addAttribute("drug", drug);
		Generic generic = drug.getGeneric();
		DosageForm form = drug.getDosageForm();
		List<Drug> drugs = drugService.findDrugsBy(generic, form);
		model.addAttribute("drugs", drugs);

		return "dashboard/drugs/drug";
	}

	@RequestMapping(method = RequestMethod.GET, params = { "action=new" })
	public String addDrug(Model model) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		return "dashboard/drugs/drug-new";
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/create" })
	public String addDrugForm(Model model) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		Drug drug = new Drug();
		List<IGeneric> generics = genericService.findAll();
		List<IDosageForm> dosageForms = dosageFormService.findAll();
		List<ICompany> companies = companyService.findAll();

		// List<Rack> racks = rackService.findAll();
		List<Shelf> shelfs = shelfService.findAll();

		model.addAttribute("drug", drug);
		model.addAttribute("generics", generics);
		model.addAttribute("dosageForms", dosageForms);
		model.addAttribute("companies", companies);
		// model.addAttribute("racks", racks);
		model.addAttribute("shelfs", shelfs);
		return "dashboard/drugs/drug-new";
	}

	@RequestMapping(value = { "/create" }, method = RequestMethod.POST)
	public String saveDrug(Model model, @Valid @ModelAttribute("drug") Drug drug, BindingResult result) {
		com.asl.pms.api.v1.payload.Drug drugg = new com.asl.pms.api.v1.payload.Drug();
		drugg.setName(drug.getName());
		drugg.setGeneric(drug.getGeneric().getId());
		drugg.setDosageForm(drug.getDosageForm().getId());
		drugg.setCompany(drug.getCompany().getId());
		drugg.setStrength(drug.getStrength());
		drugg.setPrice(drug.getPrice());
		drugg.setRack(drug.getRack().getId());
		Drug drugSaved = drugService.save(drugg);

		System.out.println(drugSaved);
		return "redirect:/drugs";
	}

	@RequestMapping(method = RequestMethod.GET, value = "{id}", params = { "action=edit" })
	public String editDrug(@PathVariable Long id, Model model) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());

		Drug drug = drugService.findDrug(id);
		model.addAttribute(drug);

		List<IGeneric> generics = genericService.findAll();
		List<IDosageForm> dosageForms = dosageFormService.findAll();
		List<ICompany> companies = companyService.findAll();
		List<Shelf> shelfs = shelfService.findAll();

		model.addAttribute("generics", generics);
		model.addAttribute("dosageForms", dosageForms);
		model.addAttribute("companies", companies);
		model.addAttribute("shelfs", shelfs);

		if (drug.getRack() != null) {
			Long rid = drug.getRack().getShelf().getId();
			List<Rack> racks = rackService.findRackByShelfId(rid);
			model.addAttribute("racks", racks);
		}

		return "dashboard/drugs/drug-edit";
	}

	@RequestMapping(method = RequestMethod.GET, value = "{id}/locations", params = { "action=new" })
	public String addLocation(@PathVariable Long id, Model model) {
		Drug drug = drugService.findDrug(id);
		model.addAttribute(drug);

		return "location-new";
	}

	@RequestMapping(value = "/getRack/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)

	@ResponseBody
	public List<Rack> rackListPost(@PathVariable Long id) {
		List<Rack> racks = rackService.findRackByShelfId(id);
		System.out.println("racks size: " + racks.size());
		return racks;
	}

	@RequestMapping(value = "/getRack/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<Rack> rackListGet(@PathVariable Long id) {
		List<Rack> racks = rackService.findRackByShelfId(id);
		return racks;
	}

	@RequestMapping(value = { "/update" }, method = RequestMethod.POST)
	public String updateDrug(Model model, @Valid @ModelAttribute("drug") Drug drug, BindingResult result) {
		Drug drugUpdated = drugService.update(drug);
		System.out.println(drugUpdated);
		return "redirect:/drugs";
	}

	// @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
	@RequestMapping(value = { "/search/{query}" }, method = RequestMethod.GET)
	public String searchDrug(Model model, @RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "50") Integer total,
			@RequestParam(required = false, defaultValue = "name") String sort, @PathVariable("query") String query) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		//
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());

		Sort.Order order;
		if (sort.startsWith("-"))
			order = Sort.Order.asc(sort.replace("-", ""));
		else
			order = Sort.Order.desc(sort);

		Sort sortBy = Sort.by(order);
		PageRequest pageable = PageRequest.of(page, total, sortBy);

		Page<Drug> drugs = drugService.searchDrugs(query, pageable);
		List<Drug> content = drugs.getContent();
		model.addAttribute("drugs", content);

		int pages = drugs.getTotalPages();
		int current = drugs.getNumber();
		current = current < pages ? current + 1 : current;

		model.addAttribute("total", drugs.getTotalElements());
		model.addAttribute("pages", pages);
		model.addAttribute("current", current);
		model.addAttribute("size", drugs.getSize());
		model.addAttribute("sortBy", sort);

		int previous = drugs.hasPrevious() ? drugs.previousPageable().getPageNumber() : 0;
		model.addAttribute("previous", previous);

		int next = drugs.hasNext() ? drugs.nextPageable().getPageNumber() : drugs.getTotalPages();
		model.addAttribute("next", next);

		return "/dashboard/drugs/drug";
	}

	@RequestMapping(value = "/searchByName", method = RequestMethod.GET)
	@ResponseBody
	public List<DrugStock> searchByName(@RequestParam(required = false) String name) {
		List<DrugStock> drugs = drugService.showDrugByMedicineName(name);
		return drugs;

	}
	

}
