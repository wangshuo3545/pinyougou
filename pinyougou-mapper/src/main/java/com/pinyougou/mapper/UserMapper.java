package com.pinyougou.mapper;

import com.pinyougou.pojo.User;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * UserMapper 数据访问接口
 * @date 2019-01-11 09:53:21
 * @version 1.0
 */
public interface UserMapper extends Mapper<User>{


    boolean modifyPassword(@Param("userName") String userName
                            ,@Param("passwords") String password);

    boolean savePhone(String loginName, String phone);

    String getPhone(@Param("userName") String remoteUser);
}