package com.asl.pms.service;

import com.asl.pms.model.Drug;
import com.asl.pms.model.Stock;
import com.asl.pms.model.TnxDetails;
import com.asl.pms.model.TnxMaster;
import com.asl.pms.repository.StockRepository;
import com.asl.pms.repository.TnxDetailsRepository;
import com.asl.pms.repository.TnxMasterRepository;
import com.asl.pms.viewmodels.DrugStock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

	@Autowired
	StockRepository stockRepository;
	@Autowired
	TnxMasterRepository tnxMasterRepo;

	@Autowired
	TnxDetailsRepository tnxDetailsRepo;

	public List<Stock> getStockList() {
		List<Stock> stockList = new ArrayList<>();

		stockList = stockRepository.findAll().stream()
				.filter(stock -> stock.getDrug().getSafetyMargin() > stock.getQty()).collect(Collectors.toList());

		return stockList;
	}

	public List<Stock> showStockList() {
		List<Stock> stockList = new ArrayList<>();

		stockList = stockRepository.findAll();

		return stockList;
	}

	public List<DrugStock> searchStockByMedicineName(String name) {
		List<DrugStock> drugs = stockRepository.showStockBymedicineName(name);
		return drugs;
	}

	public TnxMaster saveTnxMaster(TnxMaster tnxMaster) {
		TnxMaster saved = tnxMasterRepo.save(tnxMaster);
		return saved;
	}

	public TnxDetails saveTnxDetails(TnxDetails tnxDetails) {
		TnxDetails saved = tnxDetailsRepo.save(tnxDetails);
		return saved;
	}

	public List<Stock> findStockByDrug(Drug drug) {
		List<Stock> stockList = stockRepository.findByDrug(drug);
		return stockList;
	}

	public Stock updateStock(Stock stock) {
		Stock updated = stockRepository.save(stock);
		return updated;
	}

	public int showMedicineStockShortageCount() {
		int count = stockRepository.showMedicineStockShortageCount();
		return count;
	}

	public List<DrugStock> showMedicineStock() {
		List<DrugStock> drugs = stockRepository.showMedicineStock();
		return drugs;
	}
	
	
	public List<DrugStock>  medicineInOutHistory(String drugId) {
		List<DrugStock> drugs = tnxMasterRepo.medicineInOutHistory(drugId);
		return drugs;
	}

}
