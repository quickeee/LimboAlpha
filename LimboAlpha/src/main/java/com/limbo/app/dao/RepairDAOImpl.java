package com.limbo.app.dao;

import java.math.BigInteger;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import com.limbo.app.domain.DataTablesRequest;
import com.limbo.app.domain.DataTablesResponse;
import com.limbo.app.domain.DeletedRepairs;
import com.limbo.app.domain.Repair;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RepairDAOImpl extends HibernateTemplate implements RepairDAO {

	
	@Autowired 
    public RepairDAOImpl(@Qualifier("dynamicSessionFactory") SessionFactory sessionFactory) { 
     super(sessionFactory); 
    }

	// move to util
	private Repair modifyRepair(Repair repair){
		if (repair.getBaterySerialNumber() != null && !repair.getBaterySerialNumber().isEmpty()){
			repair.setBattery(true);
		} else {
			repair.setBattery(false);
		}
		if (repair.getPhoneManufacturer() != null && !repair.getPhoneManufacturer().isEmpty() ) {
			repair.setPhone(true);
		} else {
			repair.setPhone(false);
		}
		if (repair.getWarrantyPeriod() != null && repair.getWarrantyPeriod() > 0) {
			repair.setWarranty(true);
		} else {
			repair.setWarranty(false);
		}
		if (repair.getReceiptDate() == null){
			repair.setReceiptDate(getCurrentDat());
		}
		
		return repair;
	}
	public void addRepair(Repair repair, Integer userId) {		
		repair.setUserId(userId);
		repair = modifyRepair(repair);
		this.getSession().save(repair);
	}

	public void removeRepair(Integer id) {
		Session session = this.getSession();
		Repair repair = (Repair) session.load(Repair.class, id);
		if (null != repair) {
			DeletedRepairs delete = new DeletedRepairs();
			BeanUtils.copyProperties(repair, delete);
			delete.setRepairId(repair.getId());
			session.save(delete);
			session.delete(repair);
			session.flush();
		}
	}

	public Repair getRepair(Integer id){		
		Session session = this.getSession();		
		Query query =  session.createQuery("from Repair where id = :id");
		query.setParameter("id", id);
		Repair repair = (Repair)query.list().get(0);
		return repair;
	}
	
	public void updateRepair(Repair repair){		
		Session session = this.getSession();
		repair = modifyRepair(repair);
		session.merge(repair);
		session.flush();
	}	
	
	public void approveRepair(Integer id){
		Repair repair = getRepair(id);
		repair.setReturned(true);
		if (repair.getRepairDate() == null){
			repair.setRepairDate(getCurrentDat());
		}
		repair.setReturnDate(getCurrentDat());
		updateRepair(repair);
	}
		
	public boolean isReturned(Repair repair){
		return repair.isReturned();
	}
	
	@SuppressWarnings("unchecked")
	public List<Repair> listRepair() {
		Session session = this.getSession();
		return session.createQuery("from Repair order by id desc").list();
	}

	@SuppressWarnings("unchecked")
	public List<Repair> listReturnedRepair(boolean isReturned) {
		Session session = this.getSession();
		Query query =  session.createQuery("from Repair where returned = :returned order by id desc");
		query.setParameter("returned", isReturned);
		return query.list();
	}	
	
	@SuppressWarnings("unchecked")
	public List<Repair> listDoneOrNewRepairs(boolean isRepaired) {
		Session session = this.getSession();
		Criteria criteria = session.createCriteria(Repair.class);
		if (isRepaired) {
			criteria.add(Restrictions.isNotNull("repairDate"));
		} else {
			criteria.add(Restrictions.isNull("repairDate"));
		}
		criteria.add(Restrictions.eq("returned", false));
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	public boolean isRepaired(Repair repair) {
		if (repair.getRepairDate() != null){
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<DeletedRepairs> listDeletedRepairs() {
		Session session = this.getSession();
		return session.createQuery("from DeletedRepairs").list();
	}

	public void repairRepair(Integer id) {		
		Repair repair = getRepair(id);
		if (repair.getRepairDate() == null){
			repair.setRepairDate(getCurrentDat());
		}
		updateRepair(repair);
		
	}
	
	private Date getCurrentDat(){
		java.util.Date today = new java.util.Date();
		Date date = new Date(today.getTime());
		return date;
	}

	public DataTablesResponse<Repair> getDataTableResponse(
			DataTablesRequest dtReq, Boolean isReturned, Boolean isRepaired) {
		
		List<String> columnList =  dtReq.dataProp;
		List<String> stringSearch = Arrays.asList("clientFullName", "clientMobileNumber",
				"complains", "phoneManufacturer", "phoneModel");
		List<String> intSearch = Arrays.asList("id", "paymentAmount");
		
		Session session = this.getSession();
		Criteria criteria = session.createCriteria(Repair.class);
		
		try {
			if (isRepaired) {
				criteria.add(Restrictions.isNotNull("repairDate"));
			} else {
				criteria.add(Restrictions.isNull("repairDate"));
			}
			criteria.add(Restrictions.eq("returned", false));
		} catch (Exception e) {
			
		}
		if (isReturned != null) {
			criteria.add(Restrictions.eq("returned", isReturned));
		} 
			
		
		
		criteria.setFirstResult(dtReq.displayStart);
		criteria.setMaxResults(dtReq.displayLength);
		
		logger.info("Sorting: " + columnList.get(dtReq.sortedColumns.get(0)));
		if (dtReq.sortDirections.get(0).equals("asc")) {
			criteria.addOrder(Order.asc(columnList.get(dtReq.sortedColumns.get(0))));
		} else {
			criteria.addOrder(Order.desc(columnList.get(dtReq.sortedColumns.get(0))));
		}		
		Disjunction disjunction = Restrictions.disjunction();
		if (dtReq.searchQuery != null && !dtReq.searchQuery.isEmpty()) {			
			for (String column: stringSearch) {
				disjunction.add(Restrictions.like(column, "%" + dtReq.searchQuery + "%"));
			}
			try {
				for (String column: intSearch){
					disjunction.add(Restrictions.ge(column, Integer.parseInt(dtReq.searchQuery)));
				}
			} catch (Exception e) {
				logger.info("Unable to parse: " + dtReq.searchQuery);
			}		
		}
		Conjunction conjuction = Restrictions.conjunction();
		for (int i=0; i<dtReq.columnSearches.size(); i++) {
			String pattern = dtReq.columnSearches.get(i);
			if (pattern != null && !pattern.isEmpty()) {
				String column = columnList.get(i);
				if (stringSearch.contains(column)) {
					conjuction.add(Restrictions.like(column, "%" + pattern + "%"));
				} else if(intSearch.contains(column)) {
					conjuction.add(Restrictions.ge(column, Integer.parseInt(pattern)));
				}				
			}			
		}
		criteria.add(disjunction);
		criteria.add(conjuction);
		List<Repair> repairs = criteria.list();
		
		DataTablesResponse<Repair> dataTableResponse = new DataTablesResponse<Repair>();		
		dataTableResponse.data = repairs;
//		int foundRows = ((Long)session.createQuery("select found_rows()").uniqueResult()).intValue();
		int foundRows = ((BigInteger)session.createSQLQuery("select found_rows();").uniqueResult()).intValue();
		logger.info("Found rows: " + foundRows);
		int rowCount = ((Long)session.createQuery("select count(*) from Repair").uniqueResult()).intValue();
		logger.info("Total rows: " + rowCount);
		dataTableResponse.totalDisplayRecords = rowCount;
		dataTableResponse.totalRecords = rowCount;
		dataTableResponse.echo = dtReq.echo;
		
		return dataTableResponse;
	}

	
}