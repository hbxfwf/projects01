app.controller("userController",function ($scope,$controller,userService) {
    $controller("baseController",{$scope:$scope});
    //发送验证码
    $scope.getCheckCode=function () {
        userService.getCheckCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        )
    }
    //添加用户
    $scope.add=function () {
        //1.验证两次输入的密码是否一致
        if ($scope.entity.password != $scope.password) {
            alert("两次密码不一致！");
            return;
        }
        //2.添加用户
        userService.add($scope.entity,$scope.checkCode).success(
            function (response) {
                if (response.success){
                    $scope.entity = {};
                    $scope.getCheckCode = "";
                    $scope.password = "";
                } else{
                    alert(response.message);
                }
            }
        )
    }
})
