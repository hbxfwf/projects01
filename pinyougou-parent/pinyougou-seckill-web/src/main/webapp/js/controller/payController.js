app.controller("payController",function ($scope,$controller,$location,payService) {
    $controller("baseController",{$scope:$scope});
    //1.发送下单请求
    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {
                $scope.money=response.total_fee;
                $scope.outTradeNo=response.out_trade_no;
                //下单成功后，就不断查询订单,直到5分钟就退出
                queryPayStatus();
                //下面生成二维码
                new QRious({
                    element:document.getElementById("img"),
                    size:300,
                    level:'H',
                    value:response.code_url
                })
            }
        )
    }
    //2.查询订单方法
    queryPayStatus=function () {
        payService.queryPayStatus().success(function (response) {
            if(response.success){
                location.href="paysuccess.html#?money="+$scope.money;
            }else{
                if (response.message=="二维码超时"){
                    $scope.createNative();          //重新下单
                }
                location.href="payfail.html";
            }
        })
    }
    //3.显示支付成功页面上的金额
    $scope.showMoney=function () {
        return $location.search()['money'];
    }
})
