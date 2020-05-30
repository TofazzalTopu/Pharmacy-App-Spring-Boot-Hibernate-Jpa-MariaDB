package com.asl.pms.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.asl.pms.configuration.Constants;
import com.asl.pms.model.Drug;
import com.asl.pms.model.PurchaseDetails;
import com.asl.pms.model.PurchaseMaster;
import com.asl.pms.model.Stock;
import com.asl.pms.model.TnxDetails;
import com.asl.pms.model.TnxMaster;
import com.asl.pms.repository.PurchaseDetailsRepository;
import com.asl.pms.repository.PurchaseMasterRepository;
import com.asl.pms.repository.StockRepository;
import com.asl.pms.repository.TnxDetailsRepository;
import com.asl.pms.repository.TnxMasterRepository;

@Service
public class PurchaseService {
	@Autowired
	PurchaseMasterRepository purchaseMasterRepo;

	@Autowired
	PurchaseDetailsRepository purchaseDetailsRepo;

	@Autowired
	TnxMasterRepository tnxMasterRepo;

	@Autowired
	TnxDetailsRepository tnxDetailsRepo;

	@Autowired
	StockRepository stockRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	public PurchaseMaster getPurchaseMasterById(Long id) {
		return purchaseMasterRepo.findById(id).get();
	}

	public PurchaseDetails getPurchaseDetailsById(Long id) {
		return purchaseDetailsRepo.findById(id).get();
	}

	public List<PurchaseMaster> findAllPurchaseMaster() {
		return (List<PurchaseMaster>) purchaseMasterRepo.findAll(sortByIdDesc());
	}

	public List<PurchaseDetails> findAllPurchaseDetails() {
		return (List<PurchaseDetails>) purchaseDetailsRepo.findAll();
	}

	public PurchaseMaster savePurchaseMaster(PurchaseMaster purchaseMaster) {
		PurchaseMaster saved = purchaseMasterRepo.save(purchaseMaster);
		return saved;
	}

	public PurchaseDetails savePurchaseDetails(PurchaseDetails purchaseDetails) {
		PurchaseDetails saved = purchaseDetailsRepo.save(purchaseDetails);
		return saved;
	}

	public List<PurchaseDetails> findByPurchaseMaster(PurchaseMaster purchaseMaster) {
		return (List<PurchaseDetails>) purchaseDetailsRepo.findByPurchaseMaster(purchaseMaster);
	}

	private Sort sortByIdDesc() {
		return new Sort(Sort.Direction.DESC, "id");
	}

	public void deleteByPurchaseMasterId(Long id) {
		purchaseDetailsRepo.deletePurchaseDetailsByMstId(id);
		purchaseMasterRepo.deleteById(id);
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

	public List<PurchaseMaster> getAllByReturnFlag(String returnFlag) {
		List<PurchaseMaster> purchaseMasterList = purchaseMasterRepo.findAllByReturnFlag(returnFlag);
		return purchaseMasterList;
	}

	public List<PurchaseMaster> qryByReturnFlugAndPurchaseNoOrInvoiceId(String purchaseNo, String spInvId) {

		String purchaseNoQuery = "  AND master.purchase_no ='" + purchaseNo + "'";
		String invoiceQuery = "  AND master.sp_inv_id ='" + spInvId + "'";
		String queryForBoth = "  AND (master.purchase_no ='" + purchaseNo + "'  AND master.sp_inv_id ='" + spInvId
				+ "')";

		String purchaseQuery = "SELECT * FROM purchase_master master WHERE" + " (master.return_flag ="
				+ Constants.PURCHASE_NOT_RETURN + "  OR  master.return_flag IS NULL)";

		if (!purchaseNo.equals("") && spInvId.equals("")) {
			purchaseQuery += purchaseNoQuery;
		}

		if (purchaseNo.equals("") && !spInvId.equals("")) {
			purchaseQuery += invoiceQuery;
		}

		if (!purchaseNo.equals("") && !spInvId.equals("")) {
			purchaseQuery += queryForBoth;
		}

		List<PurchaseMaster> purchaseMasterList = jdbcTemplate.query(purchaseQuery,
				BeanPropertyRowMapper.newInstance(PurchaseMaster.class));
		return purchaseMasterList;
	}

	public List<PurchaseMaster> purchaseListByDateRange(String startDate, String endDate) {
		return (List<PurchaseMaster>) purchaseMasterRepo.purchaseListByDateRange(startDate, endDate);
	}

	public List<PurchaseMaster> findAllByCreateTimeBetween(String startDate, String endDate) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LocalDateTime dateTime1 = LocalDateTime.parse(startDate+" 00:00:00.000", formatter);
		LocalDateTime dateTime2 = LocalDateTime.parse(endDate+" 23:59:59.999", formatter);
		
		return (List<PurchaseMaster>) purchaseMasterRepo.findAllByCreateTimeBetween(dateTime1,dateTime2);
	}
	
	public List<PurchaseMaster> findAllByInvDateBetween(String startDate, String endDate) {
		return (List<PurchaseMaster>) purchaseMasterRepo.findAllByInvDateBetween(startDate, endDate);

	}

}
