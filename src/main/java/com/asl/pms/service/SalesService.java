package com.asl.pms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.asl.pms.model.Drug;
import com.asl.pms.model.SalesDetails;
import com.asl.pms.model.SalesMaster;
import com.asl.pms.model.Stock;
import com.asl.pms.model.TnxDetails;
import com.asl.pms.model.TnxMaster;
import com.asl.pms.repository.SalesDetailsRepository;
import com.asl.pms.repository.SalesMasterRepository;
import com.asl.pms.repository.StockRepository;
import com.asl.pms.repository.TnxDetailsRepository;
import com.asl.pms.repository.TnxMasterRepository;

@Service
public class SalesService {
	@Autowired
	SalesMasterRepository salesMasterRepo;

	@Autowired
	SalesDetailsRepository salesDetailsRepo;

	@Autowired
	TnxMasterRepository tnxMasterRepo;

	@Autowired
	TnxDetailsRepository tnxDetailsRepo;

	@Autowired
	StockRepository stockRepository;

	public SalesMaster getSalesMasterById(Long id) {
		return salesMasterRepo.findById(id).get();
	}

	public SalesDetails getSalesDetailsById(Long id) {
		return salesDetailsRepo.findById(id).get();
	}

	public List<SalesMaster> findAllSalesMaster() {
		return (List<SalesMaster>) salesMasterRepo.findAll(sortByIdDesc());
	}

	public List<SalesDetails> findAllSalesDetails() {
		return (List<SalesDetails>) salesDetailsRepo.findAll();
	}

	public SalesMaster saveSalesMaster(SalesMaster salesMaster) {
		SalesMaster saved = salesMasterRepo.save(salesMaster);
		return saved;
	}

	public SalesDetails saveSalesDetails(SalesDetails salesDetails) {
		SalesDetails saved = salesDetailsRepo.save(salesDetails);
		return saved;
	}

	public List<SalesDetails> findBySalesMaster(SalesMaster salesMaster) {
		return (List<SalesDetails>) salesDetailsRepo.findBySalesMaster(salesMaster);
	}

	private Sort sortByIdDesc() {
		return new Sort(Sort.Direction.DESC, "id");
	}

	public void deleteBySalesMasterId(Long id) {
		salesDetailsRepo.deleteSalesDetailsByMstId(id);
		salesMasterRepo.deleteById(id);
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

	public List<SalesMaster> findAllBySalesDateBetween(String startDate, String endDate) {
		return (List<SalesMaster>) salesMasterRepo.findAllBySalesDateBetween(startDate, endDate);
	}

}
