app.controller("cartController",function ($scope,$controller,cartService) {
    $controller("baseController",{$scope:$scope});
    //添加商品到购物车
    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if (response.success){  //添加成功后刷新列表
                    $scope.findCartList();
                } else{
                    alert(response.message);
                }
            }
        )
    }
    //查询购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.total = cartService.sum($scope.cartList);
            }
        )
    }
    //根据登录用户id查询地址列表
    $scope.findListByUserId=function () {
        cartService.findListByUserId().success(function (response) {
            $scope.addressList = response;
            //定义地址对象
            $scope.address={"address":""};
            //定义支付类型
            $scope.payType = "1";
        })
    }
    $scope.selectPayType =function(type){
        $scope.payType = type;
    }
    //用户点击某个地址时
    $scope.selectAddress=function (address) {
        $scope.address=address;
    }
    //判断是否被选择
    $scope.isSelected =function (address) {
        //第一次时选择默认值
        if ($scope.address.address=="" && address.isDefault == "1"){
            $scope.address.address=address.address;
            return true;
        }
        //当用户点击某个地址后触发
        if ($scope.address.address==address.address  ){
            return true;
        }
        return false;
    }
    //提交订单
    $scope.submitOrder=function () {
        $scope.order = {};
        //1.组织数据
        $scope.order.paymentType=$scope.payType;                 //设置支付类型
        $scope.order.receiverAreaName=$scope.address.address;   //收件人地址
        $scope.order.reveiverMobile=$scope.address.mobile;      //收件人电话
        $scope.order.reveiver = $scope.address.contact;         //收件人
        $scope.order.userId=$scope.address.userId;
        //2.调用service完成订单的添加工作
        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success){
                    if ($scope.order.paymentType == 1){
                        location.href = "pay.html";
                    } else{
                        location.href = "paysuccess.html";
                    }
                } else{
                    alert(response.message);
                }

            }
        )

    }
})
