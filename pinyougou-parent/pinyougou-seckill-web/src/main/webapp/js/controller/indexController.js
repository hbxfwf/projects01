app.controller("indexController",function ($scope,$controller,loginService) {
    $controller("baseController",{$scope:$scope});
    //显示登录名
    $scope.loginName=function () {
            loginService.loginName().success(
                function (response) {
                    $scope.name=response.message;
                }
            )
    }
})
