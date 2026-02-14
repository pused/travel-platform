package com.example.backend.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.entity.Ticket;
import com.example.backend.entity.TicketOrder;
import com.example.backend.service.ITicketOrderService;
import com.example.backend.service.ITicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 订单过期处理定时任务
 * 
 * @author 
 * @since 2025-01-25
 */
@Slf4j
@Component
public class OrderExpirationTask {

    @Resource
    private ITicketOrderService ticketOrderService;
    
    @Resource
    private ITicketService ticketService;

    /**
     * 处理过期订单
     * 每小时执行一次，查询所有过期且未完成的订单，将其状态置为已取消
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行一次
    public void processExpiredOrders() {
        log.info("开始处理过期订单...");
        
        try {
            // 查询所有未完成的订单（状态为1-已付款的订单）
            QueryWrapper<TicketOrder> orderQueryWrapper = new QueryWrapper<>();
            orderQueryWrapper.eq("status", 1); // 只处理已付款状态的订单
            List<TicketOrder> pendingOrders = ticketOrderService.list(orderQueryWrapper);
            
            if (pendingOrders.isEmpty()) {
                log.info("没有需要处理的订单");
                return;
            }
            
            int expiredCount = 0;
            Date currentTime = new Date();
            
            for (TicketOrder order : pendingOrders) {
                // 获取订单对应的门票信息
                Ticket ticket = ticketService.getById(order.getTicketId());
                if (ticket == null) {
                    log.warn("订单 {} 对应的门票不存在，跳过处理", order.getOrderNo());
                    continue;
                }
                
                // 计算订单过期时间
                Date orderCreateTime = order.getCreateTime();
                Integer validityDays = ticket.getValidityPeriod();
                
                if (orderCreateTime == null || validityDays == null) {
                    log.warn("订单 {} 创建时间或门票有效期为空，跳过处理", order.getOrderNo());
                    continue;
                }
                
                // 计算过期时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(orderCreateTime);
                calendar.add(Calendar.DAY_OF_MONTH, validityDays);
                Date expirationTime = calendar.getTime();
                
                // 检查是否过期
                if (currentTime.after(expirationTime)) {
                    // 订单已过期，更新状态为已取消
                    UpdateWrapper<TicketOrder> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("id", order.getId())
                               .eq("status", 1) // 确保只更新已付款状态的订单
                               .set("status", 2) // 设置为已完成
                               .set("update_time", currentTime);
                    
                    boolean updateResult = ticketOrderService.update(updateWrapper);
                    if (updateResult) {
                        expiredCount++;
                        log.info("订单 {} 已过期，状态已更新为已取消。创建时间：{}，有效期：{}天，过期时间：{}", 
                                order.getOrderNo(), orderCreateTime, validityDays, expirationTime);
                    } else {
                        log.error("更新订单 {} 状态失败", order.getOrderNo());
                    }
                }
            }
            
            log.info("过期订单处理完成，共处理 {} 个过期订单", expiredCount);
            
        } catch (Exception e) {
            log.error("处理过期订单时发生异常", e);
        }
    }
    
    /**
     * 手动触发过期订单处理
     * 提供给管理员手动执行的接口
     */
    public void manualProcessExpiredOrders() {
        log.info("手动触发过期订单处理");
        processExpiredOrders();
    }
}