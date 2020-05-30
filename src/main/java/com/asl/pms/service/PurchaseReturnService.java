package com.asl.pms.service;

import com.asl.pms.configuration.Constants;
import com.asl.pms.model.*;
import com.asl.pms.repository.PurchaseReturnDetailsRepository;
import com.asl.pms.repository.PurchaseReturnRepository;
import com.asl.pms.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseReturnService {


    @Autowired
    PurchaseReturnDetailsRepository purchaseReturnDetailsRepository;

    @Autowired
    PurchaseReturnRepository purchaseReturnRepository;

    @Autowired
    StockService stockService;

    @Autowired
    JdbcTemplate jdbcTemplate;


    public List<PurchaseReturnDetails> findByPurchaseReturn(PurchaseReturn purchaseReturn) {
        return (List<PurchaseReturnDetails>) purchaseReturnDetailsRepository.findByPurchaseReturn(purchaseReturn);
    }

    @Transactional
    public PurchaseReturn savePurchaseReturn(PurchaseReturn purchaseReturn) {
        PurchaseReturn saved = purchaseReturnRepository.save(purchaseReturn);
        return saved;
    }

    public List<PurchaseReturnDetails> savePurchaseReturnDetails(List<PurchaseReturnDetails> purchaseReturnDetailsList) {
        purchaseReturnDetailsList.forEach(pDetails ->
        {
            purchaseReturnDetailsRepository.save(pDetails);
        });
        return purchaseReturnDetailsList;
    }

    public List<PurchaseReturn> findAll() {
        List<PurchaseReturn> purchaseReturnList = purchaseReturnRepository.findAll();
        return purchaseReturnList;
    }

    public List<PurchaseMaster> qryByReturnFlugAndPurchaseNoOrInvoiceId(String purchaseNo, String spInvId) {

        String purchaseNoQuery = "  AND master.purchase_no ='" + purchaseNo + "'";
        String invoiceQuery = "  AND master.sp_inv_id ='" + spInvId + "'";
        String queryForBoth = "  AND (master.purchase_no ='" + purchaseNo + "'  AND master.sp_inv_id ='" + spInvId + "')";


        String purchaseQuery = "SELECT * FROM purchase_master master WHERE master.return_flag =" + Constants.PURCHASE_RETURN;

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

    public PurchaseReturn getPurchaseReturnById(Long id) {
        return purchaseReturnRepository.findById(id).get();
    }

    public List<PurchaseReturn> getPurchaseReturnList(String purchaseNo, String spInvId) {
        List<PurchaseReturn> purchaseReturnList = purchaseReturnRepository.findAllByPurchaseMasterIn(qryByReturnFlugAndPurchaseNoOrInvoiceId(purchaseNo, spInvId));
        return purchaseReturnList;
    }

    public boolean updateTransactionAndStock(PurchaseReturn purchaseReturn, List<PurchaseReturnDetails> details) {

        try {
            TnxMaster tnxMaster = new TnxMaster();
            tnxMaster.setTnxType(Constants.TNX_PURCHASE_RETURN);
            tnxMaster.setTnxMode(Constants.CREDIT);
            tnxMaster.setCreatedBy(purchaseReturn.getCreatedBy());
            tnxMaster.setCreateTime(LocalDateTime.now());
            tnxMaster.setRefId(purchaseReturn.getId().toString());
            TnxMaster tnxMstSaved = stockService.saveTnxMaster(tnxMaster);

            details.forEach(pd -> {
                TnxDetails tnxDtl = new TnxDetails();
                tnxDtl.setRefId(pd.getId().toString());
                tnxDtl.setTnxMaster(tnxMstSaved);
                tnxDtl.setDrug(pd.getDrug());
                tnxDtl.setTotalQty(pd.getTotalQty());
                TnxDetails tnxDtlSaved = stockService.saveTnxDetails(tnxDtl);

                //Stock update
                List<Stock> stockList = stockService.findStockByDrug(tnxDtlSaved.getDrug());
                if (stockList != null && stockList.size() > 0) {
                    Stock stock = stockList.get(0);
                    int nQty = (int) tnxDtl.getTotalQty();
                    stock.setQty(stock.getQty() - nQty);
                    stockService.updateStock(stock);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
