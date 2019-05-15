app.service("userService",function ($http) {
    //根据手机号得到验证码
    this.getCheckCode=function (phone) {
        return $http.get("./user/getCheckCode.do?phone="+phone);
    }
    //添加用户
    this.add=function (entity,checkCode) {
        return $http.post("./user/add.do?checkCode="+checkCode,entity);
    }
})
