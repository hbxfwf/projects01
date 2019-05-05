app.controller("searchController",function ($scope,$controller,searchService) {
    $controller("baseController",{$scope:$scope});
    //定义搜索的实体对象
    $scope.searchMap={"keywords":'',"brand":'',"category":'','spec':{}};
    //搜索方法
    $scope.search =function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
        })
    }
    //添加搜索项到$scope.searchMap
    $scope.addSearchItem=function (key,value) {
        if (key == 'category' || key == 'brand'){
            $scope.searchMap[key] = value;
        } else{
            $scope.searchMap.spec[key] = value;
        }
        //调用后台的搜索方法
        $scope.search();
    }
    //移除选项
    $scope.removeSearchItem=function (key) {
        if (key == 'category' || key == 'brand'){
            $scope.searchMap[key] = '';
        } else{
            delete $scope.searchMap.spec[key];   //移除某个对象中的属性
        }
        //调用后台的搜索方法
        $scope.search();
    }
})
