app.service("cartService",function ($http) {
    //添加商品到购物车中
    this.addGoodsToCartList=function (itemId,num) {
        return $http.get("./cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
    }
    //查询购物车列表
    this.findCartList=function () {
        return $http.get("./cart/findCartList.do");
    }
    //计算总数量及总金额
    this.sum=function (cartList) {
        var total = {"totalNum":0,"totalMoney":0};
        //遍历购物车
        for (var i = 0;i <cartList.length;i++){
            var cart = cartList[i];
            for (var j=0;j<cart.orderItemList.length;j++){
                var orderItem = cart.orderItemList[j];
                total.totalNum += orderItem.num;
                total.totalMoney += orderItem.totalFee;
            }
        }
        return total;
    }
    //根据用户id查询地址列表
    this.findListByUserId=function () {
        return $http.get("./address/findListByUserId.do");
    }
    //提交订单
    this.submitOrder=function (order) {
        return $http.post("./order/add.do",order);
    }
})