package com.pinyougou.shop.service;

import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的用户服务认证类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-15<p>
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username: " + username);
        System.out.println("sellerService: " + sellerService);

        // 调用服务层查询tb_seller表中的数据
        Seller seller = sellerService.findOne(username);

        // 判断seller对象是否为空，判断商家是否审核通过
        if (seller != null && "1".equals(seller.getStatus())){
            // 定义集合封装角色
            List<GrantedAuthority> authorities = new ArrayList<>();
            // 添加角色
            authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

            return new User(username, seller.getPassword(), authorities);
        }

        return null;
    }


    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}
