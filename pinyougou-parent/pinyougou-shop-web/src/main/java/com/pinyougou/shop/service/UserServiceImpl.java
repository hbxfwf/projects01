package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/25 11:18
 */
public class UserServiceImpl implements UserDetailsService {
    private SellerService service;
    //此处的这个服务从远程zookeeper中取得
    public void setService(SellerService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.通过username查询出某个商家用户
        TbSeller seller = service.findSellerBySellerId(username);
        System.out.println("seller:" + seller);
        //2.定义存放角色的集合对象
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(null != seller){
            if(seller.getStatus().equals("1")){  //必须是审核通过的用户才可登录
                //3.得到我们需要的用户（由此用户完成认证）
                return new User(username,seller.getPassword(),authorities);
            }
            return null;
        }
        return  null;
    }
}
