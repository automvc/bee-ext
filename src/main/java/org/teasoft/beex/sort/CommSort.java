///*
// * Copyright 2016-2022 the original author.All rights reserved.
// * Kingstar(honeysoft@126.com)
// * The license,see the LICENSE file.
// */
//
//package org.teasoft.beex.sort;
//
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import org.apache.commons.beanutils.BeanComparator;
//import org.apache.commons.collections.ComparatorUtils;
//import org.apache.commons.collections.comparators.ComparableComparator;
//import org.apache.commons.collections.comparators.ComparatorChain;
//import org.teasoft.bee.spi.BeanSort;
//import org.teasoft.bee.spi.entity.SortStruct;
//import org.teasoft.honey.util.StringUtils;
//
///**
// * @author AiTeaSoft
// * @since  2.0
// */
//public class CommSort implements BeanSort{
//
//	private static final long serialVersionUID = 91501L;
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public void sort(List<?> entityList, List<SortStruct> sortBeanList) {
//		if (entityList == null || sortBeanList == null) return;
//		if (entityList.size() == 0 || sortBeanList.size() == 0) return;
//
//		if (sortBeanList.size() == 1)  {
//			sort(entityList, sortBeanList.get(0));
//			return ;
//		}
//
//		// 组装排序链
//		String field = null;
//		ComparatorChain chain = new ComparatorChain();
//		for (int i = 0; i < sortBeanList.size(); i++) {
//			field = sortBeanList.get(i).getFieldName();
//			if (StringUtils.isNotBlank(field))
//				chain.addComparator(new BeanComparator(field), sortBeanList.get(i).isReverse());
//		}
//
//		Collections.sort(entityList, chain);// List<T> 或者是其他的集合
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private void sort(List<?> entityList, SortStruct sortStruct) {
//		String field = sortStruct.getFieldName();
//		if (StringUtils.isNotBlank(field)) {
//			Comparator<?> cmp = ComparableComparator.getInstance();
//			if (sortStruct.isReverse()) {
//				cmp = ComparatorUtils.reversedComparator(cmp); //倒序时,有字段的值为null,则报错.
//			} else {
//				cmp = ComparatorUtils.nullLowComparator(cmp);
//			}
//			Collections.sort(entityList, new BeanComparator(field, cmp));
//		}
//	}
//
//}
