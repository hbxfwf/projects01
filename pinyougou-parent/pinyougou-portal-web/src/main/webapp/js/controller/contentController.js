app.controller("contentController",function ($scope,$controller,contentService) {
    $controller("baseController",{$scope:$scope});
    //0.将所有的分类的广告都放在下面定义的数组中
    $scope.contentList=[];
    //1.根据广告分类id查询此分类下的所有广告列表
    $scope.findContentByCategoryId=function (categoryId) {
        contentService.findContentByCategoryId(categoryId).success(
            function (response) {
            // $scope.list=response;
                $scope.contentList[categoryId]=response;
        })
    }
    //2.向搜索页面传递数据
    $scope.search=function () {
        location.href = "http://localhost:9104/search.html#?keywords=" + $scope.keywords;
    }
})
