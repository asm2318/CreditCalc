/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.util.Date;
import java.util.List;
import javax.persistence.OrderBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CrcRequestRepository extends JpaRepository<CrcRequest, String> {
    
    List<CrcRequest> findAllByOrderByDatetime();
    
    @Query("select a from CrcRequest a where a.keydate >= :keydateStart and a.keydate <= :keydateEnd order by datetime")
    List<CrcRequest> findAllWithRange(@Param("keydateStart")Date keydateStart, @Param("keydateEnd")Date keydateEnd);
    
    @Query("select a from CrcRequest a where a.keydate >= :keydate order by datetime")
    List<CrcRequest> findAllWithKeydateAfter(@Param("keydate") Date keydate);
    
    @Query("select a from CrcRequest a where a.keydate <= :keydate order by datetime")
    List<CrcRequest> findAllWithKeydateBefore(@Param("keydate") Date keydate);
}
