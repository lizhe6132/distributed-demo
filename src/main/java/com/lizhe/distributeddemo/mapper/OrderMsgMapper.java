package com.lizhe.distributeddemo.mapper;

import com.lizhe.distributeddemo.vo.OrderMsg;
import org.apache.ibatis.annotations.Param;

public interface OrderMsgMapper {
    void saveMsg(OrderMsg orderMsg);
    OrderMsg getOne(@Param("uniqueId") String uniqueId);
}
