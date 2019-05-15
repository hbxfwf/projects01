app.service("payService",function ($http) {
    //1.发送下单请求
  this.createNative=function () {
      return $http.get("./pay/createNative.do");
  }
  //2.查询订单
    this.queryPayStatus=function () {
        return $http.get("./pay/queryPayStatus.do");
    }
})
