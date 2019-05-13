package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.domain.Employee;
import cn.itsource.aigou.mapper.EmployeeMapper;
import cn.itsource.aigou.service.IEmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author solargen
 * @version V1.0
 * @className EmployeeServiceImpl
 * @description TODO
 * @date 2019/5/13
 */
@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements IEmployeeService {

    @Override
    public Employee login(String username, String password) {
        return baseMapper.selectOne(new QueryWrapper<Employee>()
                .eq("username",username)
                .eq("password",password));
    }
}
