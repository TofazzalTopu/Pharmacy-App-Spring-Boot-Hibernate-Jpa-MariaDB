package com.asl.pms.service;

import com.asl.pms.configuration.Constants;
import com.asl.pms.model.*;
import com.asl.pms.repository.SalesReturnDetailsRepository;
import com.asl.pms.repository.SalesReturnRepositoy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class SalesReturnService {

    @Autowired
    private SalesReturnRepositoy salesReturnRepositoy;

    @Autowired
    private SalesReturnDetailsRepository salesReturnDetailsRepository;

    @Autowired
    StockService stockService;

    @Transactional
    public SalesReturn saveSalesReturn(SalesReturn salesReturn) {
        SalesReturn saved = salesReturnRepositoy.save(salesReturn);
        return saved;
    }

    public List<SalesReturnDetails> saveSalesReturnDetails(List<SalesReturnDetails> salesReturnDetailsList) {
        salesReturnDetailsList.forEach(pDetails ->
        {
            salesReturnDetailsRepository.save(pDetails);
        });
        return salesReturnDetailsList;
    }


    public boolean updateTransactionAndStock(SalesReturn salesReturn, List<SalesReturnDetails> details) {

        try {
            TnxMaster tnxMaster = new TnxMaster();
            tnxMaster.setTnxType(Constants.TNX_PURCHASE_RETURN);
            tnxMaster.setTnxMode(Constants.CREDIT);
            tnxMaster.setCreatedBy(salesReturn.getCreatedBy());
            tnxMaster.setCreateTime(LocalDateTime.now());
            tnxMaster.setRefId(salesReturn.getId().toString());
            TnxMaster tnxMstSaved = stockService.saveTnxMaster(tnxMaster);

            details.forEach(pd -> {
                //System.out.println(item);
                TnxDetails tnxDtl = new TnxDetails();
                tnxDtl.setRefId(pd.getId().toString());
                tnxDtl.setTnxMaster(tnxMstSaved);
                tnxDtl.setDrug(pd.getDrug());
                tnxDtl.setTotalQty(pd.getSalesAmount());
                TnxDetails tnxDtlSaved = stockService.saveTnxDetails(tnxDtl);

                //Stock update
                List<Stock> stockList = stockService.findStockByDrug(tnxDtlSaved.getDrug());
                if (stockList != null && stockList.size() > 0) {
                    Stock stock = stockList.get(0);
                    int nQty = (int) tnxDtl.getTotalQty();
                    stock.setQty(stock.getQty() + nQty);
                    stockService.updateStock(stock);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<SalesReturn> getAll() {
        List<SalesReturn> salesReturnList = salesReturnRepositoy.findAll();
        return salesReturnList;
    }

    public List<SalesReturnDetails> findAllBySalesReturn(SalesReturn salesReturn) {
        List<SalesReturnDetails> salesReturnDetailsList = salesReturnDetailsRepository.findAllBySalesReturn(salesReturn);
        return salesReturnDetailsList;
    }


    public SalesReturn findById(Long id) {
        SalesReturn salesReturn = salesReturnRepositoy.findById(id).get();
        return salesReturn;
    }

}
