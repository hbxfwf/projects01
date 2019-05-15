app.service("secKillGoodsService",function ($http) {
    //1.查询秒杀商品列表
    this.findList=function () {
        return $http.get("./seckillGoods/findList.do");
    }
    //2.根据秒杀商品id查询商品
    this.findOne=function (id) {
        return $http.get("./seckillGoods/findOne.do?id="+id);
    }
    //3.后台下单，存放秒杀订单到redis中
    this.submitOrder=function (id) {
        return $http.get("./seckillGoods/submitOrder.do?seckillId="+id);
    }
})
