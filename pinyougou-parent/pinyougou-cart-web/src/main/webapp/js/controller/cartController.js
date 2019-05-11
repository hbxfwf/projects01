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

})
