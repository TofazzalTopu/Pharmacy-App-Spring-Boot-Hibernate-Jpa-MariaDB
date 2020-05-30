package com.asl.pms.repository;

import com.asl.pms.model.Stock;
import com.asl.pms.viewmodels.DrugStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.asl.pms.model.Drug;
import java.util.List;
import java.lang.String;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
	List<Stock> findByDrug(Drug drug);
	

	@Query(value = "SELECT d.id, d.name, d.price, d.strength , c.name AS company , df.`name` AS dosageForm, g.name AS generic, s.qty AS stock , d.pack_size AS uom \r\n"
			+ "  FROM stock s, drug d , company c, dosage_form df, generic g\r\n"
			+ "    WHERE d.id=s.drug_id  AND d.company_id=c.id AND d.dosage_form_id=df.id AND d.generic_id=g.id AND d.name LIKE ?1% LIMIT	50", nativeQuery = true)
	List<DrugStock> showStockBymedicineName(String name);
	
	@Query(value = "SELECT d.id, d.name, d.price, d.strength , c.name AS company , df.`name` AS dosageForm, g.name AS generic, s.modify_time as modifyTime, \r\n"
			+ " s.qty AS stock , d.pack_size AS uom,  d.safety_margin AS safetyMargin  FROM stock s, drug d , company c, dosage_form df, generic g\r\n"
			+ "    WHERE d.id=s.drug_id  AND d.company_id=c.id AND d.dosage_form_id=df.id AND d.generic_id=g.id LIMIT	50", nativeQuery = true)
	List<DrugStock> showMedicineStock();
	
	@Query(value = "SELECT d.id, d.name, d.price, d.strength , c.name AS company , df.`name` AS dosageForm, g.name AS generic, s.qty AS stock , d.pack_size AS uom, \r\n"
			+ "   d.safety_margin AS safetyMargin FROM stock s, drug d , company c, dosage_form df, generic g\r\n"
			+ "    WHERE d.id=s.drug_id  AND d.company_id=c.id AND d.dosage_form_id=df.id AND d.generic_id=g.id and s.qty < d.safety_margin LIMIT	50", nativeQuery = true)
	List<DrugStock> showMedicineStockShortage();
	
	@Query(value = "SELECT count(*) FROM stock s, drug d , company c, dosage_form df, generic g\r\n"
			+ "    WHERE d.id=s.drug_id  AND d.company_id=c.id AND d.dosage_form_id=df.id AND d.generic_id=g.id and s.qty < d.safety_margin", nativeQuery = true)
	int showMedicineStockShortageCount();

}
