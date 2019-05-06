app.controller("searchController",function ($scope,$controller,$location,searchService) {
    $controller("baseController",{$scope:$scope});
    //定义搜索的实体对象
    $scope.searchMap={"keywords":'',"brand":'',"category":'','spec':{},'price':'','page':'1','pagesize':'40',"sort":'',"sortField":''};
    //搜索方法
    $scope.search =function () {
        //取得从首页传递过来的搜索关键字
        var keywords = $location.search()['keywords'];
        if (keywords){
            $scope.searchMap.keywords=keywords;
        }
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            buildPageLabel();   //生成分页的导航条
        })
    }
    //添加搜索项到$scope.searchMap
    $scope.addSearchItem=function (key,value) {
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = value;
        } else{
            $scope.searchMap.spec[key] = value;
        }
        //调用后台的搜索方法
        $scope.search();
    }
    //移除选项
    $scope.removeSearchItem=function (key) {
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = '';
        } else{
            delete $scope.searchMap.spec[key];   //移除某个对象中的属性
        }
        //调用后台的搜索方法
        $scope.search();
    }
    //动态生成导航条
    buildPageLabel=function () {
        //0.定义从第一页到最后一页的所有分页的分页号
        $scope.pageLabel = [];
        //1.定义开始页与结束页
        var firstPage = 1;                          //开始页
        var lastPage = $scope.resultMap.totalPage;  //总页数
        //定义开始与结束的省略号对应的两个变量
        $scope.startDot = true;        //true:显示省略号，false：不显示
        $scope.endDot = true;          //true:显示省略号，false：不显示
        //2.根据当前页得到最新的首页与尾页
        if ($scope.resultMap.totalPage > 5){
            if ($scope.searchMap.page < 3){
                lastPage = 5;
                $scope.startDot = false;
            } else if ($scope.searchMap.page > $scope.resultMap.totalPage-2){
                firstPage = $scope.resultMap.totalPage - 4;
                $scope.endDot = false;
            } else{
                firstPage = $scope.searchMap.page - 2;
                lastPage = $scope.searchMap.page + 2;
            }
        }else{
            $scope.startDot = false;
            $scope.endDot = false;
        }
        //3.向pageLabel中添加分页码
        for (var i = firstPage;i <= lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }
    //跳转到指定页
    $scope.skipPage=function (p) {
        $scope.searchMap.page = p;
        $scope.search();
    }
    //跳转到上一页、下一页
    $scope.go =function (p) {
        $scope.searchMap.page = parseInt($scope.searchMap.page) + p;
        $scope.search();
    }
    //排序
    $scope.sortItem=function (sort,sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }
    //在搜索关键字中查找是否有品牌，如果有返回true,否则，返回false
    $scope.searchBrand=function () {
        //1,取得品牌列表
        var brandList = $scope.resultMap.brandLists;
        //2.遍历此列表，看是否在关键字中有此品牌
        for (var i = 0;i < brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf(brandList[i].text) >= 0){
                return true;
            }
        }
        return false;
    }
})
