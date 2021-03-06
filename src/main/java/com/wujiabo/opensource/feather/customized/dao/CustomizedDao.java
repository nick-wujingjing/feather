package com.wujiabo.opensource.feather.customized.dao;

import java.util.List;
import java.util.Map;

import com.wujiabo.opensource.feather.customized.dao.CustomizedDaoImpl.PageBean;


public interface CustomizedDao {

	List<Map<String, Object>> queryForList(String sqlId, Object[] args);

	Map<String, Object> queryForMap(String sqlId, Object[] args);

	PageBean queryForListPage(String sqlId, Object[] args, int currentPage);

	List<Map<String, Object>> queryForListByReplaceCond(String getRoleidsByGroupid, List<Condition> conds, Object[] args);

}
