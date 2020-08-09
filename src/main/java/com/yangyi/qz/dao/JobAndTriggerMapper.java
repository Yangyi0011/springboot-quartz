package com.yangyi.qz.dao;

import com.yangyi.qz.domain.dto.JobAndTriggerDTO;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/04/25
 */
@Repository("jobAndTriggerMapper")
public interface JobAndTriggerMapper {

    /**
     * @Description: 查询定时任务信息
     * @author lc
     */
    List<JobAndTriggerDTO> getJobAndTriggerDetails();
}
