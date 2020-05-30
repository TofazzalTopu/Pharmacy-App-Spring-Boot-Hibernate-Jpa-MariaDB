package com.asl.pms.repository;

import com.asl.pms.model.Drug;
import com.asl.pms.viewmodels.DrugStock;
import com.asl.pms.viewmodels.IDrug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {

    Optional<Drug> findDrugByName(String name);

    List<IDrug> findTop50DrugsByNameStartingWith(String name);
    
    List<Drug> findTop30DrugsByNameStartingWith(String name);

    List<Drug> findDrugsByGenericIdAndDosageFormId(Long id, Long id2);
    List<Drug> findDrugsByGenericName(String generic);

    List<Drug> findDrugsByCompanyId(Long id);
    List<Drug> findDrugsByCompanyName(String name);
    
    //
    Page<Drug> findAllByNameStartingWith(String name, Pageable pageable);
    List<Drug> findDrugsByNameStartingWith(String name);


    @Query(value = "SELECT d.id, d.name, d.price, d.strength , c.name AS company , "
            + " df.`name` AS dosageForm, g.name AS generic, d.pack_size AS uom \r\n"
            + "  FROM  drug d , company c, dosage_form df, generic g\r\n"
            + "  WHERE d.company_id=c.id AND d.dosage_form_id=df.id AND d.generic_id=g.id  AND d.name LIKE ?1% "
            + " LIMIT	50", nativeQuery = true)
    List<DrugStock> showDrugByMedicineName(String name);

}
