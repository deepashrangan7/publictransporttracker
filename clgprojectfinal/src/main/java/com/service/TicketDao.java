package com.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.TicketBean;

@Repository
public interface TicketDao extends JpaRepository<TicketBean, Integer> {

//	@Query("select s from BusBean s where (s.stop1=?1||s.stop2=?1||s.stop3=?1||s.stop4=?1||s.stop5=?1)and(s.stop1=?2||s.stop2=?2||s.stop3=?2||s.stop4=?2||s.stop5=?2)")
//	public List<BusBean> findBus(String n1, String n2);
//public List<BusBean> findByStop1OrStop2OrStop3OrStop4OrStop5(String stop1);
//		public List<BusBean> findByStop1orStop2orStop3orStop4orStop5(String name);

}
